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
      'marketplace',
      'common-ui/underscore'
    ],
    function ( app, _ ) {

      console.log("Required models/plugin.js");

      app.factory('Plugin',
          [
            function () {

              function Plugin() {};

              Plugin.prototype = {
                getInstallationStatus: function () {
                  if (!this.isInstalled) {
                    return Plugin.InstallationStatusEnum.notInstalled;
                  }

                  // installed, up to date
                  if (this.isUpToDate()) {
                    return Plugin.InstallationStatusEnum.upToDate;
                  }

                  // installed, update available
                  return Plugin.InstallationStatusEnum.updateAvailable;
                },

                isUpToDate: function () {
                  if (!this.isInstalled) {
                    return false;
                  }

                  return this.getVersionToUpdate() === undefined;
                },

                getVersionToUpdate: function () {
                  return _.find(
                      this.versions,
                      function (version) {
                        return version.moreRecentThan( this.installedVersion ) },
                      this);
                }

              }

              Plugin.InstallationStatusEnum = {
                notInstalled: "NOT_INSTALLED",
                upToDate: "UP_TO_DATE",
                updateAvailable: "UPDATE_AVAILABLE"
              };

              Plugin.Version = function ( branch, version, buildId ) {
                this.branch = branch;
                this.version = version;
                this.buildId = buildId;
              };

              Plugin.Version.prototype.equals = function ( version ) {
                return version !== undefined && version !== null &&
                    this.branch ==  version.branch &&
                    this.version == version.version &&
                    this.buildId == version.buildId;
              }

              Plugin.Version.prototype.moreRecentThan = function ( version ) {
                if ( version === undefined || version === null ) {
                  var exception = new Error("Invalid version to compare");
                  exception.version = version;
                  throw exception;
                }

                // TODO: at the moment a version is considered more recent if the version or buildId is different
                return this.branch === version.branch &&
                    ( this.version !== version.version || this.buildId !== version.buildId )
              }

              return Plugin;
            }

          ]);
    }
);
