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

define( [ 'marketplaceApp', 'marketplace-lib/Logger' ],
    function ( app, logger ) {
      logger.log("Required pluginListItem/pluginListItemController.js");

      app.controller('pluginListItemController',
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
                  statusMessageId = "marketplace.list.item.installationStatus.installed";
                }
                else {
                  buttonVersion = plugin.versions[0];
                  infoVersion = buttonVersion;
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
