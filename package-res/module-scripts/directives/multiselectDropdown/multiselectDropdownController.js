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

define(
    [
      'marketplace',
      'common-ui/underscore'
    ],
    function ( app, _ ) {
      console.log("Required multiselectDropdown/multiselectDropdownController.js");

      app.controller('MultiselectDropdownController',
          ['$scope',
            function ( $scope ) {

              function Option( name, selectionValue, group,
                               scope, selectedCollection ) {
                this.name = name;
                this.isSelected = false;
                this.selectionValue = selectionValue;
                this.group = group;

                var that = this;

                // when an option is (de)selected, (remove)add it (from)to the selected model
                scope.$watch(
                    function() { return that.isSelected; },
                    function ( isSelectedNewValue ) {
                      if ( isSelectedNewValue ) {
                        selectedCollection.push( that.selectionValue );
                      }
                      else {
                        var index = _.indexOf( selectedCollection, that.selectionValue );
                        if ( index > -1 ) {
                          selectedCollection.splice( index, 1 );
                        }
                      }

                      that.group.isSelected = _.all( group.options, function ( option ) { return option.isSelected; } );
                    }
                );

              }

              function Group ( name, scope ) {
                this.name = name;
                this.isSelected = false;
                this.options = [];

                var that = this;

                scope.$watch(
                    function() { return that.isSelected; },
                    function ( isSelectedNewValue ) {
                      _.each( that.options,
                          function ( option ) {
                            option.isSelected = isSelectedNewValue;
                          }
                      );
                    }
                );
              }


              if ( !$scope.selected ) {
                $scope.selected = [];
              }

              $scope.groups = _.chain( $scope.options )
                  .groupBy( $scope.groupBy )
                  .map( function ( options, groupName ) {
                    var group = new Group ( groupName, $scope );
                    group.options = _.map( options,
                          function ( option ) {
                            var name = $scope.display ? option[$scope.display] : option;
                            var selectionValue = $scope.select ? option[$scope.select] : option;
                            return new Option( name, selectionValue, group, $scope, $scope.selected );
                          });
                    return group;
                  })
                  .value();

              $scope.toggleSelection = function ( selectable ) {
                selectable.isSelected = !selectable.isSelected;
              }

            }
          ]);
    }
);