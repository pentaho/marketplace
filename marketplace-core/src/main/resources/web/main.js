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

require(
    [
      'angular',
      'angular-sanitize',
      'underscore',

      'marketplaceApp',

      'marketplace/js/constants',

      'marketplace/js/controllers/applicationController',
      'marketplace/js/models/plugin',

      'marketplace/js/services/appService',
      'marketplace/js/services/installFlowService/installFlowService',
      'marketplace/js/services/dtoMapperService',
      'marketplace/js/services/developmentStageService',
      'marketplace/js/services/categoryService',

      'marketplace/directives/installUpdateButton/installUpdateButtonController',
      'marketplace/directives/installUpdateButton/installUpdateButtonDirective',

      'marketplace/directives/multiselectDropdown/multiselectDropdownController',
      'marketplace/directives/multiselectDropdown/multiselectDropdownDirective',

      'marketplace/directives/stagesInfo/stagesInfoController',
      'marketplace/directives/stagesInfo/stagesInfoDirective',

      'marketplace/directives/pluginDetail/pluginDetailController',
      'marketplace/directives/pluginDetail/pluginDetailDirective',
      'marketplace/directives/pluginList/pluginListController',
      'marketplace/directives/pluginList/pluginListDirective',
      'marketplace/directives/pluginListItem/pluginListItemController',
      'marketplace/directives/pluginListItem/pluginListItemDirective',
      'marketplace/directives/devStageIcon/devStageIconController',
      'marketplace/directives/devStageIcon/devStageIconDirective',

      'marketplace/directives/stopEvent/stopEventDirective',
      'marketplace/directives/indeterminate/indeterminateDirective',
      'marketplace/directives/modalHeight/modalHeightDirective'

    ] ,

    function( angular ) {
      angular.bootstrap( document, [ 'marketplace' ]);
    }
);

