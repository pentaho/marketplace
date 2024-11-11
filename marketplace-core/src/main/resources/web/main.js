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


define(
    [
        'jquery', // load jquery before angular
        'angular',
        'angular-sanitize',
        'angular-animate',
        'underscore',

        'marketplaceApp',

        './js/constants',

        './js/controllers/applicationController',
        './js/models/plugin',

        './js/services/appService',
        './js/services/installFlowService/installFlowService',
        './js/services/dtoMapperService',
        './js/services/developmentStageService',
        './js/services/categoryService',

        './directives/installUpdateButton/installUpdateButtonController',
        './directives/installUpdateButton/installUpdateButtonDirective',

        './directives/multiselectDropdown/multiselectDropdownController',
        './directives/multiselectDropdown/multiselectDropdownDirective',

        './directives/stagesInfo/stagesInfoController',
        './directives/stagesInfo/stagesInfoDirective',

        './directives/pluginDetail/pluginDetailController',
        './directives/pluginDetail/pluginDetailDirective',
        './directives/pluginList/pluginListController',
        './directives/pluginList/pluginListDirective',
        './directives/pluginListItem/pluginListItemController',
        './directives/pluginListItem/pluginListItemDirective',
        './directives/devStageIcon/devStageIconController',
        './directives/devStageIcon/devStageIconDirective',

        './directives/stopEvent/stopEventDirective',
        './directives/indeterminate/indeterminateDirective',
        './directives/modalHeight/modalHeightDirective'

    ],

    function (jq, angular) {
        'use strict';

        var module = {
            name: 'marketplace',
            init: marketplaceInit
        };

        return module;

        function marketplaceInit(element) {
            angular.element(element).ready(function () {
                angular.bootstrap(element, [module.name]);
            });
        }
    }
);

