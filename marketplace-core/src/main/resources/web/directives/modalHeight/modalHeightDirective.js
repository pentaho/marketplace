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
                var $modalContainer = $modal.find('.modal-container');
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
