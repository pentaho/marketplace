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

      logger.log("Required pluginList/pluginListController.js");

      app.controller('pluginListController',
          ['$scope', 'appService',
            function ($scope, appService ) {

              //gets plugins from directive

              /*
               $scope.viewPluginDetail = function ( pluginId ) {
               //Have navigation controller handle the navigation
               navigationService.getPluginRoute( pluginId );
               }
               */

            }
          ]);


    }
);
