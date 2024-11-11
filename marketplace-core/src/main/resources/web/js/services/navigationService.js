/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


'use strict';

app.factory('navigationService',
    ['$location',
    function ($location) {
        var pluginsRouteConstant = "/plugins";
        var pluginRouteConstant = "/plugin";

        return {
            //navigation logic using contants
            pluginsRoute: "#" + pluginsRouteConstant,
            pluginRoute: "#" + pluginRouteConstant,

            //programatic navigation logic
            gotoPluginsRoute: function() {
                $location.url(pluginsRouteConstant);
            },

            getPluginRoute: function( pluginId ) {
                $location.url(pluginRouteConstant + "/" + pluginId);
            }
        };
    }
]);