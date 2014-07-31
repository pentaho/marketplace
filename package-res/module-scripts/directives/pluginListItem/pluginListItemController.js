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

define( [ 'marketplace' ],
    function ( app ) {
      console.log("Required pluginListItem/PluginListItemController.js");

      app.controller('PluginListItemController',
          ['$scope', 'appService', 'Plugin',
            function ( $scope , appService, Plugin ) {
              var installButton = {};

              var plugin = $scope.plugin;
              var installationStatus = plugin.getInstallationStatus();
              switch ( installationStatus ) {
                case Plugin.InstallationStatusEnum.notInstalled:
                  var versionToInstall = plugin.versions[0];
                  // TODO i18n
                  installButton.text = "Install";
                  installButton.cssClass = "install";
                  installButton.disabled = false;
                  installButton.onClick = function () { appService.installPlugin( plugin, versionToInstall ) } ;
                  // TODO i18n
                  versionToInstall.statusMessage= "Available:"
                  $scope.inContextPluginVersion = versionToInstall;
                  break;
                case Plugin.InstallationStatusEnum.updateAvailable:
                  // TODO i18n
                  installButton.text = "Update";
                  installButton.cssClass = "updateAvailable";
                  installButton.disabled = false;
                  installButton.onClick = function () { appService.installPlugin( plugin, plugin.getVersionToUpdate() ) } ;
                  // TODO i18n
                  plugin.installedVersion.statusMessage= "Installed:"
                  $scope.inContextPluginVersion = plugin.installedVersion;
                  break;
                case Plugin.InstallationStatusEnum.upToDate:
                default:
                  // TODO i18n
                  installButton.text = "Up to Date";
                  installButton.cssClass = "upToDate";
                  installButton.disabled = true;
                  installButton.onClick = function () {} ; // No operation
                  // TODO i18n
                  plugin.installedVersion.statusMessage= "Installed:"
                  $scope.inContextPluginVersion = plugin.installedVersion;
              }

              $scope.installButton = installButton;

            }
          ]);

    }
);