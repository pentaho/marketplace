/*
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
      'marketplaceApp',
      'underscore',
      'marketplace-lib/Logger'
    ],
    function ( app, _, logger ) {
      logger.log("Required services/dtoMapperService.js");

      var service = app.factory( 'dtoMapperService',
          [ 'Plugin', 'developmentStageService', 'categoryService',
            function( Plugin, devStages, categoryService ) {

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
                // TODO: toString is used because of difference between BA server and OSGI serializer
                plugin.isInstalled = (pluginDTO.installed.toString().toUpperCase() === 'TRUE');

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

                plugin.type = pluginDTO.type;

                //plugin.category = categories[ Math.floor( Math.random() * categories.length ) ];;
                plugin.category = toCategory( pluginDTO.category );

                return plugin;
              };

              function getInstalledVersion ( installableVersions, pluginDTO ) {
                var installedVersion = _.find( installableVersions, function ( version ) {
                  return version.branch == pluginDTO.installedBranch &&
                      version.version == pluginDTO.installedVersion;
                } );

                // if there is an installable version with same branch and version
                // use it as the installed version maintaining the build id from the DTO
                if ( installedVersion ) {
                  var cloneInstalledVersion = installedVersion.clone();
                  cloneInstalledVersion.buildId = pluginDTO.installedBuildId;

                  return cloneInstalledVersion;
                }
                // otherwise create new version
                else {
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
                  } else {
                    // NOTE: another huge assumption: if no version of the same branch is found
                    // then assume the devStage of the installed version
                    // is the lowest dev stage in an installable version
                    var sortedStages = _.chain( installableVersions )
                        .map( function ( version ) { return version.devStage; })
                        .filter( function ( devStage ) { return devStage; }) // remove null / undefined stages
                        .sortBy( function ( devStage ) { return devStage.getRank(); })
                        .value();

                    var lowestRankStage = sortedStages[0];
                    installedVersion.devStage = lowestRankStage;
                  }

                  return installedVersion;
                }
              }

              function toCategory ( categoryDTO ) {
                if ( categoryDTO === null || categoryDTO === undefined ) {
                  return undefined;
                }

                if ( categoryDTO.parentName === undefined ) {
                  return categoryService.getCategory( categoryDTO.name );
                }

                return categoryService.getCategory( categoryDTO.parentName, categoryDTO.name );
              }

              function toVersion ( versionDTO ) {
                function toString( object ) {
                  if ( object === undefined || object === null ) {
                    return object;
                  }
                  return object.toString();
                }
                var version = new Plugin.Version();

                version.branch = toString( versionDTO.branch );
                version.version = toString( versionDTO.version );
                version.buildId = versionDTO.buildId;
                version.name = toString( versionDTO.name );
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

