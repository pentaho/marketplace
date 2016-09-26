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

define(
    [
        'jquery', // load jquery before angular
        'angular',
        'angular-sanitize',
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

