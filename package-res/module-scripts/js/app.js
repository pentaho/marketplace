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

define( [ 'common-ui/angular',
          'common-ui/angular-route',
          'common-ui/angular-ui-bootstrap',
          'angular-translate'
    ],

    function ( angular, angularRoute, uiBootstrap ) {

      console.log("Required app.js ");

      // define application module
      var app = angular.module( 'marketplace', [ 'ngRoute', 'ui.bootstrap', 'ngSanitize', 'pascalprecht.translate' ] );

      app.config(['$routeProvider', function( $routeProvider ) {

        $routeProvider.when('/',
            {
              templateUrl: 'partials/plugin-list.html',
              controller: 'PluginListController'
            });

        /*
         $routeProvider.when('/',
         {
         controller: 'appController'
         });

         $routeProvider.when('/plugins',
         {
         templateUrl: 'partials/plugin-list.html',
         controller: 'appController'
         });

         $routeProvider.when('/plugin/:pluginId',
         {
         templateUrl: 'partials/plugin-detail.html',
         controller: 'appController'
         });
         */


        $routeProvider.otherwise(
            {
              redirectTo: '/'
            });

        /*
         $routeProvider.when("/", {
         templateUrl: "partials/testView.html",
         controller: "TestController"
         });
         */

      }]);

      app.config(['$translateProvider', function ($translateProvider) {

        $translateProvider.useStaticFilesLoader({
          prefix: 'i18n/messages-',
          suffix: '.json'
        });
        // TODO: SESSION_LOCALE AS INJECTED VARIABLE INSTEAD OF GLOBAL
        $translateProvider.preferredLanguage(SESSION_LOCALE)
            .fallbackLanguage('en');

      }]);

      //enable CORS in Angular http requests
      /*app.config(['$httpProvider', function($httpProvider) {
       $httpProvider.defaults.useXDomain = true;
       delete $httpProvider.defaults.headers.common['X-Requested-With'];
       }]);*/

      return app;
    }
);

