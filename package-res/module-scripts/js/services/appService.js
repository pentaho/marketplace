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
    [ '$resource', 'dtoMapperService',
    function( $resource, dtoMapper ) {

        var pluginsPromise = null;
        var pluginsResource = $resource('/pentaho/plugin/marketplace/api/plugins');

        return {
            refreshPluginsFromServer: function() {
                pluginsPromise = null;
                return this.getPlugins();
            },

            getPlugins: function() {
                if ( pluginsPromise == null ) {
                    pluginsPromise = pluginsResource.get().$promise.then(
                        function (data) {
                            return _.map( data.plugins, dtoMapper.toPlugin );
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

          /*
            installPluginFromVersion: function ( pluginVersion ) {
              installPlugin( pluginVersion.)

            },
          */

            installPlugin: function ( pluginId, versionBranch ) {
              // TODO
              alert( "Tried to install plugin Id: " + pluginId + " branch: " + versionBranch );
            }
        }
    }
]);
