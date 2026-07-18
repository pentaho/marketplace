/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



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
