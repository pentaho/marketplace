/*
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

/**
 * Provides an easy way to toggle a checkboxes indeterminate property
 *
 * @example <input type="checkbox" data-indeterminate="isUnknown">
 */
define( [ 'marketplaceApp',
          'angular',
          'marketplace-lib/Logger' ],
    function ( app, angular, logger ) {
      logger.log("Required directives/indeterminate/indeterminateDirective.js");

      app.directive('indeterminate', function () {
          return {
            compile: function( tElm, tAttrs ) {
              if ( !tAttrs.type || tAttrs.type.toLowerCase() !== 'checkbox' ) {
                return angular.noop;
              }

              return function ($scope, elm, attrs) {
                $scope.$watch(attrs.indeterminate, function( newVal ) {
                  elm[0].indeterminate = !!newVal;
                });
              };
            }
          };
        }
      );
    }
);
