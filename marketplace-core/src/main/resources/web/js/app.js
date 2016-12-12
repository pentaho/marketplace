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

define( [ 'angular',
          'angular-route',
          'ui-bootstrap',
          'angular-translate',
          'marketplace-lib/Logger'
    ],

    function ( angular, angularRoute, uiBootstrap, angularTranslate, logger ) {

      logger.log("Required app.js ");

      // define application module
      var app = angular.module( 'marketplace', [ 'ngRoute', 'ui.bootstrap', 'ngSanitize', 'pascalprecht.translate' ] );

      app.config(['$routeProvider', function( $routeProvider ) {

        $routeProvider.when('/',
            {
              templateUrl: 'partials/plugin-list.html',
              controller: 'applicationController'
            });

        $routeProvider.otherwise(
            {
              redirectTo: '/'
            });

      }]);

      app.config(['$translateProvider', function ($translateProvider) {

        $translateProvider.useStaticFilesLoader({
          prefix: 'lang/messages_',
          suffix: '.properties',
          fileFormat: 'properties'

        });
        // TODO: SESSION_LOCALE AS INJECTED VARIABLE INSTEAD OF GLOBAL
        var SESSION_LOCALE = 'en';
        $translateProvider.preferredLanguage(SESSION_LOCALE)
            .fallbackLanguage('en');

      }]);

      // Disabling history in order to work with Firefox. See [MARKET-184] for more info.
      app.config( ['$provide', function ($provide){
        $provide.decorator('$sniffer', ['$delegate', function ($delegate) {
          $delegate.history = false;
          return $delegate;
        }]);
      }]);

      //enable CORS in Angular http requests
      /*app.config(['$httpProvider', function($httpProvider) {
       $httpProvider.defaults.useXDomain = true;
       delete $httpProvider.defaults.headers.common['X-Requested-With'];
       }]);*/

      app.filter('encodeURI', function() {
        return encodeURI;
      } );

      return app;
    }
);

