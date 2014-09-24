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

define( [ 'marketplace' ],
    function ( app ) {
      console.log("Required modalHeightDirective.js");

      app.directive('modalHeight', ['$timeout',
          function( timer ) {
            return {
              restrict: 'A',
              link: function( scope, element, attrs ) {
                function getVerticalPad ( $element ) {
                  return parseInt( $element.css('padding-top')) +
                      parseInt( $element.css( 'padding-bottom' ));
                }

                function changeModalHeight() {
                  var $body = $('body');
                  var $modal = element.parents('.modal');
                  var $modalBody = $modal.find('.modal-body');
                  var $modalContainer = $modal.find('.modal-container');

                  var modalHeight = $modalBody.height() +
                      getVerticalPad( $modalBody ) +
                      getVerticalPad( $modalContainer );

                  $modal.height( modalHeight );

                  if($body.hasClass('IE8')) {
                    var viewportHeight = $(window).height();
                    var availableViewportHeight = viewportHeight * 0.8;
                    
                    if(parseInt(modalHeight) > parseInt(availableViewportHeight)) {
                      $modal.css( 'height', availableViewportHeight );
                    }

                    var finalModalHeight = $modal.height();
                    $modalContainer.height(finalModalHeight);
                    $modalContainer.css('max-height', '100%');

                    $(window).resize(function() {
                      var viewportHeight = $(window).height();
                      var availableViewportHeight = viewportHeight * 0.8;
                    
                      if(parseInt(modalHeight) > parseInt(availableViewportHeight)) {
                        $modal.css( 'height', availableViewportHeight );
                      } else {
                        $modal.css( 'height', modalHeight );                        
                      }

                      var finalModalHeight = $modal.height();
                      $modalContainer.height(finalModalHeight);
                      $modalContainer.css('max-height', '100%');
                    });
                  }
                }

                // timer is necessary in order to run changeModalHeight after render
                timer( changeModalHeight, 0 );
              }
            };
          }]
      );

    }
);
