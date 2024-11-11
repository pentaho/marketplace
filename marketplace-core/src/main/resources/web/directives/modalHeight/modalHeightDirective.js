/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


'use strict';

define( [
      'marketplaceApp',
      'angular',
      'marketplace-lib/Logger'
    ],
    function ( app, angular, logger ) {
      logger.log("Required modalHeightDirective.js");

      app.directive('modalHeight', ['$timeout', '$window',
          function( timer, window ) {
            return {
              restrict: 'A',
              link: function( scope, element, attrs ) {

                function getVerticalPad ( $element ) {
                  return parseInt( $element.css('padding-top')) +
                      parseInt( $element.css( 'padding-bottom' ));
                }

                function changeModalHeight() {
                  var modalHeight = parseInt( $modalBody.height() ) +
                      getVerticalPad( $modalBody ) +
                      getVerticalPad( $modalContainer );

                  $modal.height( modalHeight );
                }

                function isBrowserIE8 () {
                  return element.parents('body').hasClass('IE8');
                }

                function changeModalHeightIE8 () {
                  var modalHeight = parseInt( $modalBody.height() )+
                      getVerticalPad( $modalBody ) +
                      getVerticalPad( $modalContainer );

                  var viewportHeight = $window.height();
                  var availableViewportHeight = viewportHeight * 0.8;

                  var finalModalHeight = $modal.height();
                  if( modalHeight > availableViewportHeight ) {
                    finalModalHeight = availableViewportHeight;
                  } else {
                    finalModalHeight = modalHeight;
                  }

                  $modal.css('height', finalModalHeight );
                  $modalContainer.css('height', finalModalHeight );
                  $modalContainer.css('max-height', '100%');
                }


                // Controller Logic
                var $modal = element.parents('.modal');
                var $modalBody = $modal.find('.modal-body');
                var $modalContainer = $modal.find('.modal-content');
                var $window = angular.element( window );

                if ( isBrowserIE8() ) {
                  $window.resize( changeModalHeightIE8 );
                  // timer is necessary in order to run changeModalHeight after render
                  timer( changeModalHeightIE8, 0 );
                }
                else {
                  // timer is necessary in order to run changeModalHeight after render
                  timer( changeModalHeight, 0 );
                }
              }
            };
          }]
      );

    }
);
