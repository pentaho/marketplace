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
          [ 'Plugin', 'developmentStageService',
            function( Plugin, devStages ) {

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

                var installedVersion = _.find( plugin.versions, function ( version ) {
                  return version.branch == pluginDTO.installedBranch &&
                      version.version == pluginDTO.installedVersion;// &&
                      version.buildId == pluginDTO.installedBuildId;
                } );
                if ( installedVersion ) {
                  plugin.installedVersion = installedVersion;
                } else {
                  plugin.installedVersion = new Plugin.Version();
                  plugin.installedVersion.branch = pluginDTO.installedBranch;
                  plugin.installedVersion.version = pluginDTO.installedVersion;
                  plugin.installedVersion.buildId = pluginDTO.installedBuildId;
                }


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

                return plugin;
              };

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

                // TODO: fill in development stage from DTO

                /*
                var lanes = devStages.getLanes();
                var lane = lanes[ Math.floor( Math.random() * lanes.length ) ];
                var phase = Math.floor((Math.random() * 4) + 1);
                */
                var lane = versionDTO.developmentStageLane;
                var phase = versionDTO.developmentStagePhase;
                version.devStage = devStages.getStage( lane, phase );

                return version;
              };

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

