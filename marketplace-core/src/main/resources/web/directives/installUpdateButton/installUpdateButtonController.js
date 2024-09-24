/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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