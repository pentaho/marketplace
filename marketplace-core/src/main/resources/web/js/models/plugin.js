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
      'marketplaceApp',
      'underscore',
      'marketplace-lib/Logger'
    ],
    function ( app, _, logger ) {

      logger.log("Required models/plugin.js");

      app.factory('Plugin',
          [
            function () {

              function Plugin() {}

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

              };

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
              };

              // TODO: this function has many assumptions that must be reviewed in a later version
              Plugin.Version.prototype.moreRecentThan = function ( version ) {
                if ( version === undefined || version === null ) {
                  var exception = new Error("Invalid version to compare");
                  exception.version = version;
                  throw exception;
                }

                if( this.branch !== version.branch ) { return false; }

                if ( this.version !== version.version ) { return true; }

                // same branch and version
                return this.buildIdMoreRecentThan( version.buildId );
              };

              // TODO: this function must be reviewed in a later version
              Plugin.Version.prototype.buildIdMoreRecentThan = function ( buildId ) {
                if ( this.buildId ) {
                  return this.buildId !== buildId;
                }

                // if this.build is undefined/null/"" then the buildId is the lowest
                return false;
              };

              Plugin.Version.prototype.clone = function () {
                return new Plugin.Version( this.branch, this.version, this.buildId );
              };

              return Plugin;
            }

          ]);
    }
);
