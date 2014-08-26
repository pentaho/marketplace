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
      console.log("Required services/developmentStageService.js");

      var service = app.factory( 'developmentStageService',
          [
            function ( ) {

              var customerLane = "Customer";
              var communityLane = "Community";

              /**
               * Possible development stages
               * @type {{}}
               */
              var developmentStages = {};

              /**
               *
               * @param lane
               * @param phase
               * @param name
               * @param description
               * @constructor
               */
              function DevelopmentStage ( lane, phase, name, description ) {
                this.lane = lane;
                this.phase = phase;

                // TODO: needs support for i18n
                this.name = name;
                this.description = description;

              }

              function setDevelopmentStage ( devStage ) {
                if( developmentStages[devStage.lane] === undefined ) {
                  developmentStages[devStage.lane] = [];
                }
                developmentStages[devStage.lane][devStage.phase] = devStage;
              }

              // TODO: check if these should be obtained from metadata
              // TODO: i18n
              function intializeDevelopmentStages() {
                setDevelopmentStage( new DevelopmentStage( customerLane, 1, 'Development Phase', "Start up phase of an internal project. Usually a Labs experiment." ));
                setDevelopmentStage( new DevelopmentStage( customerLane, 2, 'Snapshot Release', "Unstable and unsupported branch, not recommended for production use." ));
                setDevelopmentStage( new DevelopmentStage( customerLane, 3, 'Limited Support', "Assistence given by Services Development with no contractual support for production environments." ));
                setDevelopmentStage( new DevelopmentStage( customerLane, 4, 'Production Release', "Production release with PM assigned, fully supported as part of the Pentaho release cycle." ));
                setDevelopmentStage( new DevelopmentStage( communityLane, 1, 'Development Phase', "Start up phase of an internal project. Usually a Labs experiment." ));
                setDevelopmentStage( new DevelopmentStage( communityLane, 2, 'Snapshot Release', "Unstable and unsupported branch, not recommended for production use." ));
                setDevelopmentStage( new DevelopmentStage( communityLane, 3, 'Stable Release', "Adoption is ramping up and product could be used in production environments." ));
                setDevelopmentStage( new DevelopmentStage( communityLane, 4, 'Mature Release', "Indicates a successfully adopted project in a mature state." ));
              }

              function getStage( lane, phase ) {
                if ( !lane || !phase ) {
                  return undefined;
                }
                return developmentStages[lane][phase];
              }

              function getStages () {
                return _.chain( developmentStages )
                    .flatten()
                    // IE8 requires that undefined and null values are explicitly removed
                    .filter( function ( stage ) { return !(stage === undefined || stage === null); } )
                    .value();
              }

              function getLanes () {
                return _.map( developmentStages, function ( value, key ) { return key; } );
              }

              intializeDevelopmentStages();
              return {
                getStage: getStage,

                getStages: getStages,

                getLanes: getLanes

              }
            }
          ]);

      return service;
    }
);

