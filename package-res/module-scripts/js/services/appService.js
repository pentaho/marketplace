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
    [ '$resource',
    function( $resource ) {

        // TODO provide pluginDTO to plugin mapper as an angular service
        var mapPlugin = function( pluginDTO ) {
            var plugin = {};

            plugin.id = pluginDTO.id;
            plugin.name = pluginDTO.name;
            plugin.image = pluginDTO.img;
            plugin.smallImage = pluginDTO.small_img;
            // description i8ln
            plugin.description = pluginDTO.description;

            return plugin;
        }

        var pluginsPromise = null;
        var pluginsResource = $resource('/pentaho/plugin/marketplace/api/plugins');

        return {
            //service methods and properties
            getHello : "Hello World!",
            getInitialCount: 0,
            addCount: function( currentCount ) {
                currentCount++;
                return currentCount;
            },

            refreshPlugins: function() {
                pluginsPromise = null;
                return this.getPlugins();
            },

            getPlugins: function() {
                if ( pluginsPromise == null ) {
                    pluginsPromise = pluginsResource.get().$promise.then(
                        function (data) {
                            return _.map( data.plugins, function ( pluginDTO ) { return mapPlugin( pluginDTO ); } );
                            return this.plugins;
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
            }
        }
    }
]);
