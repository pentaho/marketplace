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

app.factory('Plugin',
    [
      function () {

        function Plugin() {

        };

        Plugin.InstallationStatusEnum = {
          notInstalled: "NOT_INSTALLED",
          upToDate: "UP_TO_DATE",
          updateAvailable: "UPDATE_AVAILABLE"
        };

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
                  // can only compare to other versions with the same installed version branch
                  this.installedVersion.branch === version.branch &&
                  ( this.installedVersion.version !== version.version ||
                      this.installedVersion.buildId != version.buildId )
                },
                this);
          }
        }

        return Plugin;
      }

    ]);
