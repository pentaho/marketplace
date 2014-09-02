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
      console.log("Required pluginListItem/PluginListItemController.js");

      app.controller('PluginListItemController',
          ['$scope',
            function ( $scope ) {
              var plugin = $scope.plugin;

              function updateItemInfo() {
                var buttonVersion;
                var infoVersion;
                var statusMessageId;

                if ( plugin.isInstalled ) {
                  buttonVersion = plugin.isUpToDate() ? plugin.installedVersion : plugin.getVersionToUpdate();
                  infoVersion = plugin.installedVersion;
                  // TODO i18n
                  statusMessageId = "marketplace.list.item.installationStatus.installed";
                }
                else {
                  buttonVersion = plugin.versions[0];
                  infoVersion = buttonVersion;
                  // TODO i18n
                  statusMessageId = "marketplace.list.item.installationStatus.available";
                }

                $scope.buttonVersion = buttonVersion;
                $scope.infoVersion = infoVersion;
                $scope.infoVersionStatusMessageId= statusMessageId;

              }

              updateItemInfo();
              $scope.$watch( function () { return plugin.installedVersion; },
                  function () { updateItemInfo(); } );

            }
          ]);

    }
);