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
      logger.log("Required devStageIcon/devStageIconController.js");

      app.controller('devStageIconController',
          ['$scope', 'developmentStageService',
            function ( $scope, devStages ) {

              function update() {
                if ( $scope.lane && $scope.phase ) {
                  var laneTemplate = $scope.lane.toLowerCase() + "-";
                  var phaseTemplate = "0" + $scope.phase;
                  $scope.devStageClass = "dev-stage-" + laneTemplate + phaseTemplate;

                  $scope.stage = devStages.getStage( $scope.lane, $scope.phase );
                }
              }

              $scope.$watch('lane',  update );
              $scope.$watch('phase', update );

            }
          ]);

    }
);
