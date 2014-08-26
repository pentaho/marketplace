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
                               scope, selectedValues, selectedDisplays ) {
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
                        selectedValues.push( that.selectionValue );
                        selectedDisplays.push( that.name );
                      }
                      // Option was deselected
                      else {
                        var index = _.indexOf( selectedValues, that.selectionValue );
                        if ( index > -1 ) {
                          // these two arrays should be "synchronized"
                          selectedValues.splice( index, 1 );
                          selectedDisplays.splice( index, 1);
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

              function getOptionsDisplayString ( selectedOptionsDisplay ) {
                if (selectedOptionsDisplay.length == 0 ) {
                  // TODO: i18n
                  return "All";
                }
                return selectedOptionsDisplay.join(', ');
              };

              function getGroups ( scope, options, groupBy, display, select ) {
                return _.chain( options )
                    .groupBy( groupBy )
                    .map( function ( options, groupName ) {
                      var group = new Group ( groupName, scope );
                      group.options = _.map( options,
                          function ( option ) {
                            var name = display ? option[display] : option;
                            var selectionValue = select ? option[select] : option;
                            // TODO: remove scope? => selectedOptionsValue, selectedOptionsDisplay
                            return new Option( name, selectionValue, group, scope, scope.selectedOptionsValue, scope.selectedOptionsDisplay );
                          });
                      return group;
                    })
                    .value();
              }

              $scope.selectedOptionsDisplay = [];

              $scope.getOptionsDisplayString = function () { return getOptionsDisplayString( $scope.selectedOptionsDisplay ); };

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

              $scope.$watch( 'options', function () {
                $scope.groups = getGroups( $scope, $scope.options, $scope.groupBy, $scope.display, $scope.select );
              });

            }
          ]);
    }
);