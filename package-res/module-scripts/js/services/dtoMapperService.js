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

app.factory('dtoMapperService',
    [
      function() {

        function Plugin() {};
        function PluginVersion () {};

        function toPlugin( pluginDTO ) {
          // region TEMPORARY array transformation due to bug in server side serialization of single element collections
          pluginDTO.screenshots = toArray( pluginDTO.screenshots );
          pluginDTO.versions = toArray( pluginDTO.versions );
          // endregion

          var plugin = new Plugin();

          plugin.id = pluginDTO.id;
          plugin.name = pluginDTO.name;
          plugin.image = pluginDTO.img;
          plugin.smallImage = pluginDTO.small_img;
          // TODO check this property with XSD
          plugin.documentationUrl = pluginDTO.documentationUrl;

          // TODO description i8ln
          plugin.description = pluginDTO.description;

          plugin.author = {};
          plugin.author.name = pluginDTO.author;
          plugin.author.siteUrl = pluginDTO.author_url;
          plugin.author.logoUrl = pluginDTO.author_logo;

          // TODO change to function that checks for installed version info?
          plugin.isInstalled = (pluginDTO.installed.toUpperCase() === 'TRUE');

          plugin.installedVersionInfo = {};
          plugin.installedVersionInfo.branch = pluginDTO.installedBranch;
          plugin.installedVersionInfo.version = pluginDTO.installedVersion;
          plugin.installedVersionInfo.buildId = pluginDTO.installedBuildId;

          plugin.installationNotes = pluginDTO.installationNotes;

          plugin.screenshotUrls = pluginDTO.screenshots;

          // TODO improve dependencies
          plugin.dependencies = pluginDTO.dependencies;

          // TODO check license on server DTO
          plugin.license = {};
          plugin.license.name = pluginDTO.license_name;
          // pluginDTO.license takes precedence over pluginDTO.license_name
          if( pluginDTO.license !== null ||
              pluginDTO.license !== undefined ) {
            plugin.license.name = pluginDTO.license;
          }
          plugin.license.text = pluginDTO.license_text;



          plugin.versions = _.map( pluginDTO.versions, toVersion );
          return plugin;
        };

        function toVersion ( versionDTO ) {
          var version = new PluginVersion();

          version.branch = versionDTO.branch;
          version.version = versionDTO.version;
          version.buildId = versionDTO.buildId;
          version.name = versionDTO.name;
          version.downloadUrl = versionDTO.downloadUrl;
          version.samplesDownloadUrl = versionDTO.samplesDownloadUrl;
          // TODO description i8ln;
          version.description = versionDTO.description;
          // TODO changeLog internationalization?
          version.changeLog = versionDTO.changeLog;
          // TODO: use Date type
          version.releaseDate = versionDTO.releaseDate;

          version.compatiblePentahoVersion = {};
          version.compatiblePentahoVersion.minimum = versionDTO.minParentVersion;
          version.compatiblePentahoVersion.maximum = versionDTO.maxParentVersion;

          return version;
        };

        /**
         * This function only exists because of a "bug" on json serialization server side.
         * Single element arrays are serialized as an object instead of a single element array.
         * @param potentialArray :
         * @returns {Array} the potential array as a proper Array.
         */
        function toArray ( potentialArray ) {
          // already an array, return it
          if( potentialArray instanceof Array ) {
            return potentialArray;
          }

          // null or undefined, return empty array
          if ( potentialArray === null || potentialArray === undefined ) {
            return [];
          }

          // single element, put it in an array
          return [ potentialArray ];
        }

        return {
          toPlugin: toPlugin,

          toVersion: toVersion

        }
      }
    ]);
