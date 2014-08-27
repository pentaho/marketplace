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
          ['$scope', 'appService', '$modal', 'developmentStageService', '$filter',
            function ( $scope, appService, $modal, devStagesService, $filter ) {

              var installedTab = 'installedTab';
              var availableTab = 'availableTab';

              /**
               * Functions that clear the listeners that refresh
               * the filtered plugins whenever a plugin is installed / uninstalled
               * @type {Array}
               */
              var pluginVersionWatcherUnregisterFunctions = [];

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
                return plugin.isInstalled;
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
                      return _.any( plugin.versions, function ( version )  {
                            return version.devStage &&
                                selectedStage.lane == version.devStage.lane &&
                                selectedStage.phase == version.devStage.phase;
                      } );
                    }
                );
              };

              function filterCategory ( plugin ) {
                if ( $scope.selectedTypes.length == 0 ) {
                  return true;
                }

                // plugin does not have a category
                if ( !plugin.category ) {
                  return false;
                }

                // plugin is in one of the selected development stages
                return _.any( $scope.selectedTypes,
                    function ( selectedType ) {
                      return selectedType.main == plugin.category.main &&
                          selectedType.sub == plugin.category.sub;
                    }
                );
              }


              /**
               * Checks if a plugin passes all the conditions set in the view
               * @param {Plugin} plugin
               * @returns {Boolean} True if the plugin passes the filter
               */
              function pluginFilter ( plugin ) {
                return filterInstalled( plugin ) &&
                  filterStage ( plugin ) &&
                  filterCategory( plugin );
              };

              function applyPluginFilter() {
                appService.getPlugins().then( filterAndSetPlugins );
              };

              function filterAndSetPlugins ( plugins ) {
                var pluginsFromDropDown = $filter('filter')( plugins, pluginFilter );
                $scope.filteredPlugins = $filter('filter')( pluginsFromDropDown, $scope.searchText );
              };

              function refreshPluginsFromServer () {
                $scope.filteredPlugins = null;
                $scope.isGettingPluginsFromServer = true;
                appService.refreshPluginsFromServer().then( function ( plugins ) {
                      updateRegistersForInstallStatusChanges( plugins );
                      updateCategoryFilter( plugins );
                      filterAndSetPlugins( plugins );
                      $scope.isGettingPluginsFromServer = false;
                    }
                    // TODO: i18n
                    //,function () { alert("Error getting plugins."); }
                );
              };

              function updateRegistersForInstallStatusChanges ( newPlugins ) {
                // stop watching old plugin objects
                _.each( pluginVersionWatcherUnregisterFunctions, function ( stopWatching ) {
                  stopWatching();
                } );
                pluginVersionWatcherUnregisterFunctions = [];

                // start watching new plugin objects
                _.each( newPlugins, function ( plugin ) {
                  var stopWatching = $scope.$watch( function () { return plugin.installedVersion; },
                      function () { filterAndSetPlugins( newPlugins ); } );
                  pluginVersionWatcherUnregisterFunctions.push( stopWatching );
                } );
              };

              function updateCategoryFilter( plugins ) {
                $scope.pluginTypes = getCategories( plugins );
              };

              function getCategories ( plugins ) {
                var categories = _.chain( plugins )
                    .filter( function ( plugin ) { return plugin.category !== undefined && plugin.category !== null; } )
                    .map( function( plugin ) { return plugin.category; } )
                    .uniq( function ( category ) { return category.main + category.sub; } )
                    .sortBy( function ( category ) { return category.main + category.sub; })
                    .value();

                return categories;
              }


              /**
               * Refreshes the plugin list from the server
               */
              $scope.refreshPluginsFromServer = refreshPluginsFromServer;


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
                  template: '<div data-plugin-detail data-plugin="plugin"></div>',
                  scope: modalScope,
                  windowClass: "pluginDetailDialog"
                });

                // clean up created modal scope
                pluginDetailModal.result.then(
                    function() { modalScope.$destroy(); },
                    function() { modalScope.$destroy(); }
                );
              };

              /*
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
              */

              // TODO: i18n
              $scope.$watchCollection( "selectedStages", applyPluginFilter );
              $scope.$watchCollection( "selectedTypes", applyPluginFilter );
              $scope.$watch( "searchText", applyPluginFilter );

              $scope.$watch( "selectedTab", applyPluginFilter );

              $scope.selectedStages = [];
              $scope.developmentStages = devStagesService.getStages();

              $scope.selectedTypes = [];
              $scope.pluginTypes = [];

              $scope.selectTab( availableTab );


              // Get plugins from server
              refreshPluginsFromServer();

            }
          ]);


    }
);

