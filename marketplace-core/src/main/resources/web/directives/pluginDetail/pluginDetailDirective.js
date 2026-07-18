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

define( [ 'marketplaceApp', 'marketplace-lib/Logger' ],
    function ( app, logger ) {

      logger.log("Required pluginDetail/pluginDetailDirective.js");

      app.directive('pluginDetail', function() {
        return {
          restrict: 'A', // 'A' must be used for IE8 compatibility
          replace: true, //replaces the custom directive element with the corresponding expanded HTML, to be HTML-compliant.
          templateUrl: 'directives/pluginDetail/pluginDetailTemplate.html',
          controller: 'pluginDetailController',
          //isolate scope
          scope: {
            plugin: "="
          }
        };
      });

    }
);

