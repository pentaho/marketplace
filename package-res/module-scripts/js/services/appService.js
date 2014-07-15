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

app.factory('appService',
    [ '$http', 'dtoMapperService',
    function( $http, dtoMapper ) {

        var baseUrl = '/pentaho/plugin/marketplace/api'
        var pluginsUrl =  baseUrl + '/plugins';
        var installPluginBaseUrl = baseUrl + '/plugin';
        var pluginsPromise = null;

        return {
            refreshPluginsFromServer: function() {
                pluginsPromise = null;
                return this.getPlugins();
            },

            getPlugins: function() {
                if ( pluginsPromise == null ) {
                    pluginsPromise = $http.get( pluginsUrl ).then(
                        function ( response ) {
                            return _.map( response.data.plugins, dtoMapper.toPlugin );
                        });
                }

                return pluginsPromise
            },

            getPlugin: function( pluginId ) {
                return this.getPlugins().then(
                    function ( plugins ) {
                        return _.find( plugins, function ( plugin ) { return plugin.id === pluginId; } );
                    }
                );
            },

            installPlugin: function ( pluginId, versionBranch ) {
              return $http.post( installPluginBaseUrl + '/' + pluginId + '/' + versionBranch)
                  .then( function ( response ) {
                            alert("Install OK. plugin Id: " + pluginId + " branch: " + versionBranch)
                         },
                         function ( response ) {
                           alert("Install NOT OK. plugin Id: " + pluginId + " branch: " + versionBranch)
                         });
            },

            uninstallPlugin: function ( pluginId ) {
              return $http.delete( installPluginBaseUrl + '/' + pluginId )
                  .then( function ( response ) {
                    alert("Uninstall OK. plugin Id: " + pluginId )
                  },
                  function ( response ) {
                    alert("Uninstall NOT OK. plugin Id: " + pluginId )
                  });
            }
        }
    }
]);
