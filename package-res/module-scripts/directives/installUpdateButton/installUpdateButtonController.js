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

define( [ 'marketplace' ],
    function ( app ) {
      console.log("Required installUpdateButton/installUpdateButton.js");

      app.controller('installUpdateButtonController',
          ['$scope', 'appService',
            function ( $scope , appService ) {
              var button = {};
              var plugin = $scope.plugin;
              var installedVersion = plugin.installedVersion;

              function updateButton( selectedVersion ) {
                // No version selected to install or update
                if ( !selectedVersion ) {
                  // TODO: i18n
                  button.text = "Invalid selected version";
                  button.disabled = true;
                  button.cssClass = "invalid";
                  button.onClick = function () {};
                  return;
                }

                if ( plugin.isInstalled ) {
                  if ( selectedVersion.equals( installedVersion ) ) {
                    // TODO i18n
                    button.text = "Reinstall";
                    button.cssClass = "install";
                    button.disabled = false;
                    button.onClick = function () { appService.installPlugin( plugin.id, selectedVersion.branch ); };
                    return
                  }
                  else if ( selectedVersion.moreRecentThan( installedVersion ) ) {
                    // TODO: i18n
                    button.text = "Update";
                    button.cssClass = "update";
                    button.disabled = false;
                    button.onClick = function () { appService.installPlugin( plugin.id, selectedVersion.branch ) } ;
                    return;
                  }
                }

                // TODO: i18n
                button.text = "Install";
                button.cssClass = "install";
                button.disabled = false;
                button.onClick = function () { appService.installPlugin( plugin.id, selectedVersion.branch ); };
                return;
              }

              $scope.button = button;
              $scope.$watch( 'selectedVersion', function ( newSelectedVersion ) { updateButton( newSelectedVersion ); } );

            }
          ]);

    }
);