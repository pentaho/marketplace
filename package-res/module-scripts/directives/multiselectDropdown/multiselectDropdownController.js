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

              // todo get from service
              var pluginTypes = [
                { name: 'Analysis', group: 'Apps'},
                { name: 'Dashboards', group: 'Apps'},
                { name: 'Reporting', group: 'Apps'},
                { name: 'Lifecycle', group: 'Apps'},
                { name: 'Admin', group: 'Apps'},
                { name: 'Visualizations', group: 'Other' },
                { name: 'Themes', group: 'Other'},
                { name: 'Language Packs', group: 'Other'}
              ];

              var groupBy = 'group';
              var nameAttribute = 'name';

              var typesGrouped = _.groupBy( pluginTypes, groupBy );

              function Option( name, group,
                               scope, selectedCollection ) {
                var option = {
                  name: name,
                  isSelected: false,
                  group: group
                };

                // when an option is (de)selected, (remove)add it (from)to the selected model
                scope.$watch( function() { return option.isSelected; },
                    function ( isSelectedNewValue ) {
                      if ( isSelectedNewValue ) {
                        selectedCollection.push( option.name );
                      }
                      else {
                        var index = _.indexOf( selectedCollection, option.name );
                        if ( index > -1 ) {
                          selectedCollection.splice( index, 1 );
                        }
                      }

                      option.group.isSelected = _.all( group.options, function ( option ) { return option.isSelected; } );
                    });

                return option;
              }

              function Group ( name, scope ) {
                var group =  {
                  name: name,
                  isSelected: false,
                  options: []
                };

                scope.$watch(
                    function() { return group.isSelected; },
                    function ( isSelectedNewValue ) {
                      _.each( group.options,
                          function ( option ) {
                            option.isSelected = isSelectedNewValue;
                          });
                      }
                );


                return group;
              }

              $scope.selected = [];
              $scope.groups = _.map( typesGrouped,
                  function ( options, groupName ) {
                    var group = new Group ( groupName, $scope );
                    group.options = _.map( options,
                          function ( option ) {
                            return new Option( option[nameAttribute], group, $scope, $scope.selected );
                          });
                    return group;
                  });

              $scope.toggleSelection = function ( selectable ) {
                selectable.isSelected = !selectable.isSelected;
              }

            }


          ]);

    }
);