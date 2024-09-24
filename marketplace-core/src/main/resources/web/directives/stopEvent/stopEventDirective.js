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
define( [ 'marketplaceApp', 'marketplace-lib/Logger' ],
    function ( app, logger ) {
      logger.log("Required stopEvent/stopEventDirective.js");

      app.directive('stopEvent', function () {
        return {
          restrict: 'A',
          link: function (scope, element, attr) {
            element.bind(attr.stopEvent, function (e) {
              e.stopPropagation();
            });
          }
        };
      });

    }
);
