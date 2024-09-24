/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

'use strict';

define( [ 'angular',
          'angular-route',
          'angular-bootstrap',
          'angular-translate',
          'marketplace-lib/Logger',
          'marketplace-lib/angular-translate-static-loader'
    ],

    function ( angular, angularRoute, uiBootstrap, angularTranslate, logger ) {

      logger.log("Required app.js ");

      // define application module
      var app = angular.module( 'marketplace', [ 'ngRoute', 'ui.bootstrap', 'ngSanitize', 'ngAnimate', 'pascalprecht.translate' ] );

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

