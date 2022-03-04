/*
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
      'marketplaceApp',
      'underscore',
      'marketplace-lib/Logger'
    ],
    function ( app, _, logger ) {
      logger.log("Required services/appService.js");

      app.factory('appService',
          [ '$http', 'dtoMapperService', 'csrfService', '$q', 'BASE_URL',
            function( $http, dtoMapper, csrfService, $q, BASE_URL ) {

              var pluginsUrl =  BASE_URL + '/plugins';
              var installPluginBaseUrl = BASE_URL + '/plugin';
              var pluginsPromise = null;

              var PENTAHO_MARKETPLACE_ID = "pentaho-marketplace";
              var PDI_MARKETPLACE_ID = "pdi-marketplace";

              function isResponseError( response ) {
                return response.data.statusMessage.code.substring(0,5).toLowerCase() == 'error';
              }
              return {
                refreshPluginsFromServer: function() {
                  pluginsPromise = null;
                  return this.getPlugins();
                },

                getPlugins: function() {
                  if ( pluginsPromise == null ) {
                    pluginsPromise = $http.get( pluginsUrl ).then(
                        function ( response ) {
                          var dto = response.data;
                          if ( isResponseError( response ) ) {
                            logger.log( "Failed getting plugins from server." );
                            return $q.reject( dto.statusMessage );
                          }
                          return _.map( dto.plugins, dtoMapper.toPlugin );
                        }
                    );
                  }

                  return pluginsPromise
                },

                getPlugin: function( pluginId ) {
                  return this.getPlugins().then(
                      function ( plugins ) {
                        return _.find( plugins, function ( plugin ) { return plugin.id === pluginId; } );
                      }
                  );
                },

                installPlugin: function ( plugin, version ) {
                  logger.log("Installing " + plugin.id + " " + version.branch );
                  var protectedUrl = installPluginBaseUrl + '/' + plugin.id + '/' + version.branch;
                  var csrfToken = csrfService.getToken(protectedUrl);
                  var headers = {};
                  // Add the CSRF token, if needed.
                  if(csrfToken !== null) {
                    headers[csrfToken.header] = csrfToken.token;
                  }

                  return $http.post( protectedUrl, null, {headers: headers})
                      .then( function ( response ) {
                        if ( isResponseError( response ) ) {
                          logger.log("Install NOT OK. plugin Id: " + plugin.id + " branch: " + version.branch);
                          return $q.reject(response.data.statusMessage);
                        }
                        // TODO: verify in response if everything is actually ok
                        logger.log("Install OK. plugin Id: " + plugin.id + " branch: " + version.branch);
                        plugin.isInstalled = true;
                        plugin.installedVersion = version;

                      },
                      function ( response ) {
                        // FIXME: marketplace upgrade raises exceptions due to serialization issues on the server side
                        // even when the upgrade is OK
                        if ( plugin.id === PENTAHO_MARKETPLACE_ID || plugin.id === PDI_MARKETPLACE_ID ) {
                            logger.debug("Got error while upgrading marketplace but everything should be ok. " );
                            return;
                        }
                        logger.log("Install NOT OK. plugin Id: " + plugin.id + " branch: " + version.branch);
                        return $q.reject( response );
                      });
                },

                uninstallPlugin: function ( plugin ) {
                  // TODO: change to log when dialogs are handled
                  logger.log( "Uninstalling " + plugin.id );
                  // Not using the shortcut method $http.delete because it does not work in IE8
                  return $http( { method: 'DELETE', url: installPluginBaseUrl + '/' + plugin.id } )
                      .then( function ( response ) {
                        if ( isResponseError( response ) ) {
                          logger.log( "Uninstall NOT OK. plugin Id: " + plugin.id );
                          return $q.reject(response.data.statusMessage);
                        }
                        // TODO: verify in response if everything is actually ok
                        logger.log( "Uninstall OK. plugin Id: " + plugin.id );
                        plugin.isInstalled = false;
                        plugin.installedVersion = undefined;
                      },
                      function ( response ) {
                        // FIXME: marketplace uninstall raises exceptions due to serialization issues on the server side
                        // even when the uninstall is OK.
                        if ( plugin.id === PENTAHO_MARKETPLACE_ID || plugin.id === PDI_MARKETPLACE_ID ) {
                            logger.debug("Got error while uninstalling marketplace but everything should be ok. " );
                            return;
                        }
                        logger.log( "Uninstall NOT OK. plugin Id: " + plugin.id );
                        return $q.reject( response );
                      });
                }

              }
            }
          ]);
    }
);