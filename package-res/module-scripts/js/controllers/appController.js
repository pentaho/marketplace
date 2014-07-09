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

app.controller('appController',
    ['$scope', 'appService', 'navigationService', '$routeParams',
    function ($scope, appService, navigationService, $routeParams) {

        //VM initialization
        $scope.hello = appService.getHello;
        $scope.count = appService.getInitialCount;

        //get all plugins
        appService.getPlugins().then (
            function ( plugins ) {
                $scope.pluginList = plugins;
            }
        );

        //get selected plugin
        appService.getPlugin( $routeParams.pluginId ).then(
            function ( plugin ) {
                $scope.plugin = plugin;
            }
        );

        //application logic
        $scope.addCount = function () {
            //call service layer to execute business logic
            $scope.count = appService.addCount($scope.count);
        };

        //navigation

        //constant navigation via navigation service
        $scope.pluginsRoute = navigationService.pluginsRoute;
        $scope.pluginRoute = navigationService.pluginRoute;

        //programmatic navigation via navigation service
        $scope.getPluginsRoute = function() {

            //Have navigation controller handle the navigation
            navigationService.getPluginsRoute();
        }

        $scope.getPluginRoute = function(pluginId) {

            //Have navigation controller handle the navigation
            navigationService.getPluginRoute(pluginId);
        }
    }
]);
