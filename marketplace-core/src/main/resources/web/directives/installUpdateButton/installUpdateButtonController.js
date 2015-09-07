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
      'marketplaceApp', 'marketplace-lib/Logger' ],
    function ( app, logger ) {
      logger.log("Required installUpdateButton/installUpdateButton.js");

      app.controller('installUpdateButtonController',
          ['$scope', 'installFlowService',
            function ( $scope, installService ) {
              var button = {};
              var plugin = $scope.plugin;

              function noOperation() {}

              function updateButton( selectedVersion ) {
                var installedVersion = plugin.installedVersion;

                // No version selected to install or update
                if ( !selectedVersion ) {
                  button.textId = "marketplace.installationButton.invalidMessage";
                  button.disabled = true;
                  button.cssClass = "invalid";
                  button.onClick = noOperation;
                  return;
                }

                if ( plugin.isInstalled ) {
                  if ( selectedVersion.equals( installedVersion ) ) {
                    if( $scope.allowReinstall ) {
                      button.textId = "marketplace.installationButton.reinstall";
                      button.cssClass = "install";
                      button.disabled = false;
                      button.onClick = function () { installService.installPlugin( plugin, selectedVersion ); };
                      return;
                    }
                    else {
                      button.textId = "marketplace.installationButton.upToDate";
                      button.cssClass = "upToDate";
                      // buttom must be enabled so that click event propagation may be stopped
                      button.disabled = false;
                      button.onClick = noOperation;
                      return;
                    }
                  }
                  else if ( selectedVersion.moreRecentThan( installedVersion ) ) {
                    button.textId = "marketplace.installationButton.update";
                    button.cssClass = "update";
                    button.disabled = false;
                    button.onClick = function () {
                      installService.updatePlugin( plugin, selectedVersion );
                    };
                    return;
                  }
                }

                button.textId = "marketplace.installationButton.install";
                button.cssClass = "install";
                button.disabled = false;
                button.onClick = function () {
                  installService.installPlugin( plugin, selectedVersion );
                };
                return;
              }

              $scope.button = button;
              $scope.$watch( 'selectedVersion', function ( newSelectedVersion ) { updateButton( newSelectedVersion ); } );
              $scope.$watch( function () { return plugin.installedVersion; },
                  function ( newInstalledVersion ) {
                    updateButton ( $scope.selectedVersion );
                  } );

            }
          ]);

    }
);