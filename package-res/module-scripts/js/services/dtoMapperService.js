/*
 * Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */


 'use strict';

define(
    [
      'marketplace',
      'common-ui/underscore'
    ],
    function ( app, _ ) {
      console.log("Required services/dtoMapperService.js");

      var service = app.factory( 'dtoMapperService',
          [ 'Plugin', 'developmentStageService', '$translate',
            function( Plugin, devStages, $translate ) {

              function toPlugin( pluginDTO ) {
                // region TODO: TEMPORARY array transformation due to bug in server side serialization of single element collections
                pluginDTO.screenshots = toArray( pluginDTO.screenshots );
                pluginDTO.versions = toArray( pluginDTO.versions );
                // endregion

                var plugin = new Plugin();

                plugin.id = pluginDTO.id;
                plugin.name = pluginDTO.name;
                plugin.image = pluginDTO.img;
                plugin.smallImage = pluginDTO.small_img;
                // TODO check this property with XSD
                plugin.documentationUrl = pluginDTO.documentationUrl;

                // TODO description i18n
                plugin.description = pluginDTO.description;

                plugin.author = {};
                plugin.author.name = pluginDTO.authorName;
                plugin.author.siteUrl = pluginDTO.authorUrl;
                plugin.author.logoUrl = pluginDTO.authorLogo;

                // TODO change to function that checks for installed version info?
                plugin.isInstalled = (pluginDTO.installed.toUpperCase() === 'TRUE');

                plugin.versions = _.map( pluginDTO.versions, toVersion );

                plugin.installedVersion = getInstalledVersion( plugin.versions, pluginDTO );

                plugin.installationNotes = pluginDTO.installationNotes;

                plugin.screenshotUrls = pluginDTO.screenshots;

                // TODO improve dependencies
                plugin.dependencies = pluginDTO.dependencies;

                // TODO check license on server DTO
                plugin.license = {};
                plugin.license.name = pluginDTO.license_name;
                // pluginDTO.license takes precedence over pluginDTO.license_name
                if( pluginDTO.license !== null ||
                    pluginDTO.license !== undefined ) {
                  plugin.license.name = pluginDTO.license;
                }
                plugin.license.text = pluginDTO.license_text;

                //plugin.category = categories[ Math.floor( Math.random() * categories.length ) ];;
                plugin.category = toCategory( pluginDTO.category );

                return plugin;
              };

              function getInstalledVersion ( installableVersions, pluginDTO ) {
                var installedVersion = _.find( installableVersions, function ( version ) {
                  return version.branch == pluginDTO.installedBranch &&
                      version.version == pluginDTO.installedVersion;
                } );
                if ( installedVersion ) {
                  return installedVersion;
                } else {
                  installedVersion = new Plugin.Version();
                  installedVersion.branch = pluginDTO.installedBranch;
                  installedVersion.version = pluginDTO.installedVersion;
                  installedVersion.buildId = pluginDTO.installedBuildId;

                  // NOTE: Huge assumption here: if the installed version is not in
                  // the installable versions list, assume that the devStage of the installed version
                  // is the same as the installable version with the same branch
                  var sameBranchVersion = _.find( installableVersions, function (version) {
                    return version.branch == pluginDTO.installedBranch;
                  });
                  if ( sameBranchVersion ) {
                    installedVersion.devStage = sameBranchVersion.devStage;
                  }

                  return installedVersion;
                }
              }

              function Category( main, sub ) {
                var that = this;
                that.main = main;
                that.mainName = main;
                that.mainTranslateId = 'marketplace.categories.' + main.toLowerCase() + '.name';

                $translate( that.mainTranslateId )
                    .then( function ( translatedName ) {
                      if ( translatedName != that.mainTranslateId ) {
                        that.mainName = translatedName;
                      }
                    });

                if( sub ) {
                  that.sub = sub;
                  that.subName = sub;
                  that.subTranslateId = 'marketplace.categories.' + sub.toLowerCase() + '.name';

                  $translate( that.subTranslateId )
                      .then( function (translatedName ) {
                        if ( translatedName != that.subTranslateId ) {
                          that.subName = translatedName;
                        }
                      });
                }
              }

              Category.prototype.getId = function () {
                return this.main + this.sub;
              }

              function toCategory ( categoryDTO ) {
                if ( categoryDTO === null || categoryDTO === undefined ) {
                  return undefined;
                }

                if ( categoryDTO.parentName === undefined ) {
                  return new Category( categoryDTO.name );
                }

                return new Category( categoryDTO.parentName, categoryDTO.name );
              }

              function toVersion ( versionDTO ) {
                var version = new Plugin.Version();

                version.branch = versionDTO.branch;
                version.version = versionDTO.version;
                version.buildId = versionDTO.buildId;
                version.name = versionDTO.name;
                version.downloadUrl = versionDTO.downloadUrl;
                version.samplesDownloadUrl = versionDTO.samplesDownloadUrl;
                // TODO description i8ln;
                version.description = versionDTO.description;
                // TODO changeLog internationalization?
                version.changeLog = versionDTO.changeLog;
                // TODO: use Date type
                version.releaseDate = versionDTO.releaseDate;

                version.compatiblePentahoVersion = {};
                version.compatiblePentahoVersion.minimum = versionDTO.minParentVersion;
                version.compatiblePentahoVersion.maximum = versionDTO.maxParentVersion;

                var lane = versionDTO.developmentStageLane;
                var phase = versionDTO.developmentStagePhase;

                version.devStage = devStages.getStage( lane, phase );

                return version;
              };

              function getRandomStage() {
                var lanes = devStages.getLanes();
                var lane = lanes[ Math.floor( Math.random() * lanes.length ) ];
                var phase = Math.floor((Math.random() * 4) + 1);
                return devStages.getStage( lane.id, phase );
              }

              /**
               * This function only exists because of a "bug" on json serialization server side.
               * Single element arrays are serialized as an object instead of a single element array.
               * @param potentialArray :
               * @returns {Array} the potential array as a proper Array.
               */
              function toArray ( potentialArray ) {
                // already an array, return it
                if( potentialArray instanceof Array ) {
                  return potentialArray;
                }

                // null or undefined, return empty array
                if ( potentialArray === null || potentialArray === undefined ) {
                  return [];
                }

                // single element, put it in an array
                return [ potentialArray ];
              }

              return {
                toPlugin: toPlugin,

                toVersion: toVersion

              }
            }
          ]);

      return service;
    }
);

