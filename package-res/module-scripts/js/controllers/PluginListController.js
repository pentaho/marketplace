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

              var installedTab = 'installedTab';
              var availableTab = 'availableTab';

              /**
               *
               * @param plugin
               * @returns {Boolean}
               */
              function filterInstalled ( plugin ) {
                if ( !$scope.isTabSelected( installedTab ) ) {
                  return true;
                }

                // if installed tab is selected only accept installed plugins
                return plugin.isInstalled;;
              };

              /**
               *
               * @param plugin
               * @returns {Boolean}
               */
              function filterStage ( plugin ) {
                if ( $scope.selectedStages.length == 0 ) {
                  return true;
                }

                // plugin is in one of the selected development stages
                return _.any( $scope.selectedStages,
                    function ( selectedStage ) {
                      return ( selectedStage.lane == plugin.devStage.lane &&
                          selectedStage.level == plugin.devStage.level );
                    }
                )
              };


              /**
               * Checks if a plugin passes all the conditions set in the view
               * @param {Plugin} plugin
               * @returns {Boolean} True if the plugin passes the filter
               */
              function pluginFilter ( plugin ) {
                return filterInstalled( plugin ) &&
                  filterStage ( plugin );
              };


              function applyPluginFilter() {
                appService.getPlugins().then( filterAndSetPlugins );
              };

              function filterAndSetPlugins ( plugins ) {
                $scope.filteredPlugins = _.chain( plugins)
                    .filter( pluginFilter )
                    .sortBy( function ( plugin ) { return plugin.getInstallationStatus(); } )
                    .value();
              };

              /**
               * Refreshes the plugin list from the server
               */
              $scope.refreshPluginsFromServer = function() {
                $scope.filteredPlugins = null;
                appService.refreshPluginsFromServer().then( filterAndSetPlugins );
              };

              $scope.selectTab = function ( tab ) {
                $scope.selectedTab = tab;
              };

              $scope.isTabSelected = function ( tab ) {
                return $scope.selectedTab == tab;
              };


              $scope.pluginWasClicked = function ( plugin ) {
                var modalScope = $scope.$new( true ); // create new isolate scope
                modalScope.plugin = plugin;
                var pluginDetailModal = $modal.open( {
                  templateUrl: 'directives/pluginDetail/pluginDetailTemplate.html',
                  controller: 'PluginDetailController',
                  scope: modalScope
                  //windowClass: "pentaho-dialog"
                });

                // clean up created modal scope
                pluginDetailModal.result.then(
                    function() { modalScope.$destroy(); },
                    function() { modalScope.$destroy(); }
                );
              };

              /*
              appService.getPlugins().then( function ( plugins ) {
                    $scope.pluginTypes = _.chain( plugins )
                        .unique( function ( plugin ) { return plugin.type; } )
                        .map( function ( plugin ) { return { name: plugin.type, group: plugin.type }; } )
                        .value();
                  }
              );
              */

              // TODO: get from service
              $scope.pluginTypes = [
                { name: 'Analysis', group: 'Apps' },
                { name: 'Dashboards', group: 'Apps' },
                { name: 'Reporting', group: 'Apps' },
                { name: 'Lifecycle', group: 'Apps' },
                { name: 'Admin', group: 'Apps' },
                { name: 'Visualizations', group: 'Other' },
                { name: 'Themes', group: 'Other' },
                { name: 'Language Packs', group: 'Other' }
              ];

              // TODO: i18n
              // TODO: get from service
              $scope.developmentStages = [
                { lane: "customer", level: 1, shortDescription: 'Development Phase' },
                { lane: "customer", level: 2, shortDescription: 'Snapshot Release' },
                { lane: "customer", level: 3, shortDescription: 'Limited Support' },
                { lane: "customer", level: 4, shortDescription: 'Production Release' },
                { lane: "community", level: 1, shortDescription: 'Development Phase' },
                { lane: "community", level: 2, shortDescription: 'Snapshot Release' },
                { lane: "community", level: 3, shortDescription: 'Stable Release' },
                { lane: "community", level: 4, shortDescription: 'Mature Release' }
              ];


              $scope.$watch( "selectedTab", applyPluginFilter );
              $scope.selectTab( availableTab );

              $scope.selectedStages = [];
              $scope.selectedTypes = [];

              $scope.$watchCollection( "selectedStages", applyPluginFilter );


              // initialize plugins
              appService.getPlugins().then( filterAndSetPlugins );

            }
          ]);


    }
);

