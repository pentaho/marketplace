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
                      // Option was deselected
                      else {
                        var index = _.indexOf( selectedCollection, that.selectionValue );
                        if ( index > -1 ) {
                          selectedCollection.splice( index, 1 );
                        }
                      }

                      // update group selection (ALL / SOME / NONE)
                      that.group.updateSelected();
                    }
                );

              }

              function Group ( name, scope ) {
                var that = this;
                that.name = name;
                that.selected = Group.selectEnum.NONE;
                that.options = [];

                scope.$watch(
                    function() { return that.selected; },
                    function ( selectedNewValue ) {
                      switch ( selectedNewValue ) {
                        case Group.selectEnum.ALL:
                          _.each( that.options, function ( option ) { option.isSelected = true; } );
                          break;
                        case Group.selectEnum.NONE:
                          _.each( that.options, function ( option ) { option.isSelected = false; } );
                          break;
                      }
                    }
                );
              }

              Group.selectEnum = {
                ALL: "All",
                SOME: "Some",
                NONE: "None"
              }

              Group.prototype.updateSelected = function() {
                var selected = Group.selectEnum.NONE;
                if ( _.all( this.options, function ( option ) { return option.isSelected; } ) ) {
                  selected = Group.selectEnum.ALL;
                }
                else if ( _.any( this.options, function ( option ) { return option.isSelected; } ) ) {
                  selected = Group.selectEnum.SOME;
                }

                this.selected = selected;
              }

              Group.prototype.getCssClass = function () {
                switch ( this.selected ) {
                  case Group.selectEnum.ALL:
                    return 'all-selected';
                  case Group.selectEnum.SOME:
                    return 'some-selected';
                  case Group.selectEnum.NONE:
                    return 'none-selected';
                }
              }



              if ( !$scope.selectedOptions ) {
                $scope.selectedOptions = [];
              }

              $scope.groups = _.chain( $scope.options )
                  .groupBy( $scope.groupBy )
                  .map( function ( options, groupName ) {
                    var group = new Group ( groupName, $scope );
                    group.options = _.map( options,
                          function ( option ) {
                            var name = $scope.display ? option[$scope.display] : option;
                            var selectionValue = $scope.select ? option[$scope.select] : option;
                            return new Option( name, selectionValue, group, $scope, $scope.selectedOptions );
                          });
                    return group;
                  })
                  .value();

              $scope.toggleSelection = function ( selectable ) {
                selectable.isSelected = !selectable.isSelected;
              }

              $scope.groupClicked = function ( group ) {
                switch ( group.selected ) {
                  case Group.selectEnum.ALL:
                    group.selected = Group.selectEnum.NONE;
                    break;
                  case Group.selectEnum.NONE:
                  case Group.selectEnum.SOME:
                    group.selected = Group.selectEnum.ALL;
                    break;
                }
              }

            }
          ]);
    }
);