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

define(
    [
      'marketplaceApp',
      'underscore',
      'marketplace-lib/Logger'
    ],
    function ( app, _, logger ) {
      logger.log("Required services/appService.js");

      app.factory('appService',
          [ '$http', 'dtoMapperService', '$q', 'BASE_URL',
            function( $http, dtoMapper, $q, BASE_URL ) {

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
                  return $http.post( installPluginBaseUrl + '/' + plugin.id + '/' + version.branch)
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