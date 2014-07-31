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


define( [
      'marketplace',
      'common-ui/underscore'
    ],
    function ( app, _ ) {

      console.log("Required pluginDetail/pluginDetailController.js");

      app.controller( 'PluginDetailController',
          ['$scope', 'appService',
            function ( $scope, appService ) {
              // Add trim to String if it is not defined
              if (!('trim' in String.prototype)) {
                String.prototype.trim= function() {
                  return this.replace(/^\s+/, '').replace(/\s+$/, '');
                };
              }

              function isEmptyString( string ) {
                return string == undefined ||
                    string == null ||
                    string.trim() == '';
              }

              function Info (label, description, url) {
                this.label = label;
                this.description = description;
                this.url = url;
              }

              Info.prototype.hasDescription = function () {
                return !isEmptyString( this.description );
              }

              Info.prototype.hasUrl = function () {
                return !isEmptyString( this.url );
              }

              function CreateInfo ( label, description, url ) {
                return new Info( label,description, url );
              }


              // TODO: i18n for Info labels!
              function getPluginInformation( plugin ) {
                var selectedVersion = $scope.selectedPluginVersion;

                var versionInfo = [];
                if ( selectedVersion ) {
                  versionInfo = [
                    CreateInfo( "Branch", selectedVersion.branch),
                    CreateInfo( "Version", selectedVersion.version),
                    CreateInfo( "Build", selectedVersion.build),
                    CreateInfo( "Release Date", selectedVersion.releaseDate ),
                    // TODO: These infos aren't in the mock
                    // newInfo( "Name", selectedVersion.name ),
                    // newInfo( "Samples", "samples", selectedVersion.samplesDownloadUrl ),
                    // newInfo( "Version Description",  selectedVersion.description ),
                    // newInfo( "ChangeLog", version.changeLog ),
                  ];
                }

                var infos = [
                    CreateInfo( "Developer", plugin.author.name, plugin.author.siteUrl ),
                    CreateInfo( "License", plugin.license.name ),
                    CreateInfo( "Dependencies", plugin.dependencies)
                ];

                // remove information with empty description as they should not be in the view
                return _.filter( versionInfo.concat(infos), function( info ) {  return info.hasDescription(); } );

              }

              function uninstallPlugin ( plugin ) {
                appService.uninstallPlugin( plugin.id );
              }


              $scope.uninstallPlugin = uninstallPlugin;
              $scope.pluginInformation = getPluginInformation( $scope.plugin );

              $scope.selectedPluginVersion = $scope.plugin.versions[0];

              // update version info when a new version is selected
              $scope.$watch( 'selectedPluginVersion', function () {
                    $scope.pluginInformation = getPluginInformation( $scope.plugin );
                  }
              );

            }
          ]);
    }
);
