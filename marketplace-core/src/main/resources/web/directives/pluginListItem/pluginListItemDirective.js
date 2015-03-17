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

define( [ 'marketplaceApp' ],
    function ( app ) {
      console.log("Required pluginListItem/pluginListItemDirective.js");

      app.directive('pluginListItem',
          function() {
            return {
              restrict: 'A', // 'A' must be used for IE8 compatibility
              replace: true, //replaces the custom directive element with the corresponding expanded HTML, to be HTML-compliant.
              templateUrl: 'directives/pluginListItem/pluginListItemTemplate.html',
              controller: 'pluginListItemController',
              //isolate scope
              scope: {
                //'&' evaluates in the parent scope
                //'@' evaluates as a string
                //'=' evaluates in the isolate scope
                plugin: "="
              }
            };
          }
      );

    }
);
