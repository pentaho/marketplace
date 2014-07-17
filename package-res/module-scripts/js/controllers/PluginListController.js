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

define(
    [
      'marketplace',
      'common-ui/underscore'
    ],
    function ( app, _ ) {

      console.log("Required controllers/PluginListController.js");

      app.controller('PluginListController',
          ['$scope', 'appService', '$modal',
            function ( $scope, appService, $modal ) {

              /**
               * Checks if a plugin passes all the conditions set in the view
               * @param {Plugin} plugin
               * @returns {Boolean} True if the plugin passes the filter
               */
              function pluginFilter ( plugin ) {
                if ( $scope.showOnlyInstalled ) {
                  return plugin.isInstalled;
                }
                return true;
              };

              function applyPluginFilter() {
                appService.getPlugins().then( filterAndSetPlugins );
              };

              function filterAndSetPlugins ( plugins ) {
                $scope.filteredPlugins = _.filter( plugins, pluginFilter );
              }

              /**
               * Refreshes the plugin list from the server
               */
              $scope.refreshPluginsFromServer = function() {
                $scope.filteredPlugins = null;
                appService.refreshPluginsFromServer().then( filterAndSetPlugins );
              };



              $scope.pluginWasClicked = function ( plugin ) {
                var modalScope = $scope.$new( true ); // create new isolate scope
                modalScope.plugin = plugin;
                var pluginDetailModal = $modal.open( {
                  templateUrl: 'directives/pluginDetail/pluginDetailTemplate.html',
                  controller: 'PluginDetailController',
                  scope: modalScope
                });

                // clean up created modal scope
                pluginDetailModal.result.then(
                    function() { modalScope.$destroy(); },
                    function() { modalScope.$destroy(); }
                );
              };







              // todo get from service
              $scope.pluginTypes = [
                { name: 'Analysis', group: 'Apps'},
                { name: 'Dashboards', group: 'Apps'},
                { name: 'Reporting', group: 'Apps'},
                { name: 'Lifecycle', group: 'Apps'},
                { name: 'Admin', group: 'Apps'},
                { name: 'Visualizations' },
                { name: 'Themes'},
                { name: 'Language Packs'}

              ]

              // initialize plugins
              appService.getPlugins().then( filterAndSetPlugins );

              // region Filters
              $scope.showOnlyInstalled = false;
              $scope.$watch( "showOnlyInstalled", applyPluginFilter );

              // endregion


            }
          ]);


    }
);

