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
      'marketplaceApp',
      'underscore'
    ],
    function ( app, _ ) {

      console.log("Required controllers/applicationController.js");

      app.controller('applicationController',
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
                                selectedStage.lane.id == version.devStage.lane.id &&
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
                  filterCategory( plugin ) &&
                  filterText(plugin, $scope.searchText );
              };

              function contains ( string, subString ) {
                if ( !string || !subString ) {
                  return false;
                }

                // make sure these are strings
                string = string.toString().toLowerCase();
                subString = string.toString().toLowerCase();

                return string.indexOf( subString ) > -1;
              };

              function filterText ( plugin, text ) {
                if ( !text ) {
                  return true;
                }

                return contains ( plugin.name, text ) ||
                    contains( plugin.description, text ) ||
                    contains( plugin.author.name, text ) ||
                    contains( plugin.dependencies, text ) ||
                    contains( plugin.license.name, text ) ||
                    _.any( plugin.versions, function ( version ) {
                      return contains( version.branch, text ) ||
                          contains( version.version, text ) ||
                          contains( version.buildId, text ) ||
                          contains( version.name , text ) ||
                          contains( version.description, text );
                    });
              };


              function applyPluginFilter() {
                appService.getPlugins().then( filterAndSetPlugins );
              };

              function filterAndSetPlugins ( plugins ) {
                $scope.filteredPlugins = $filter('filter')( plugins, pluginFilter );
              };

              function refreshPluginsFromServer () {
                $scope.filteredPlugins = null;
                $scope.errorMessageId = null;
                $scope.isGettingPluginsFromServer = true;
                appService.refreshPluginsFromServer()
                    .then( function ( plugins ) {
                      updateRegistersForInstallStatusChanges( plugins );
                      // TODO: move update to dev stage filter to init.
                      // This update is here due to some Firefox issues with translation.
                      updateDevStagesFilter();
                      updateCategoryFilter( plugins );
                      filterAndSetPlugins( plugins );
                    }
                    ,function () {
                      $scope.errorMessageId = 'marketplace.errorMessages.failureGettingPlugins';
                    })
                    // Calling method this way for IE8 compatibilty because finnaly is a reserved word
                    ['finally']( function () {
                      $scope.isGettingPluginsFromServer = false;
                    });
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
                    .uniq( function ( category ) { return category.getId(); } )
                    .sortBy( function ( category ) { return category.mainName + category.subName; })
                    .value();

                return categories;
              }

              function openStagesInfoModal () {
                function modalController( $scope, $modalInstance ) {
                  $scope.closeModal = function () {
                    $modalInstance.close();
                  }
                }

                var stagesInfoModal = $modal.open( {
                  templateUrl: 'partials/stagesInfoModal.html',
                  controller: modalController,
                  windowClass: "stagesInfoDialog"
                });
              }

              function updateDevStagesFilter () {
                $scope.developmentStages = _.map( devStagesService.getStages(), function ( stage ) {
                  var filterStageOption = { lane: stage.lane.name, name: stage.name, stage: stage };
                  // NOTE: These watches are necessary because of translation issues in FireFox
                  $scope.$watch( function () { return stage.name; }, function () { filterStageOption.name = stage.name; } );
                  $scope.$watch( function () { return stage.lane.name; }, function () { filterStageOption.lane = stage.lane.name; } );
                  return filterStageOption;
                });
              }

              $scope.onWhatAreStagesClicked = openStagesInfoModal;

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
                  templateUrl: 'partials/pluginDetailModal.html',
                  scope: modalScope,
                  windowClass: "pluginDetailDialog",
                  size: 'lg'
                });

                modalScope.closeModal = function () {
                  pluginDetailModal.close();
                }

                // clean up created modal scope
                pluginDetailModal.result.then(
                    function() { modalScope.$destroy(); },
                    function() { modalScope.$destroy(); }
                );
              };

              $scope.$watchCollection( "selectedStages", applyPluginFilter );
              $scope.$watchCollection( "selectedTypes", applyPluginFilter );
              $scope.$watch( "searchText", applyPluginFilter );

              $scope.$watch( "selectedTab", applyPluginFilter );

              $scope.selectedStages = [];
              $scope.developmentStages = [];

              $scope.selectedTypes = [];
              $scope.pluginTypes = [];

              $scope.selectTab( availableTab );


              // Get plugins from server
              refreshPluginsFromServer();

            }
          ]);


    }
);

