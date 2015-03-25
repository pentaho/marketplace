/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2015 Pentaho Corporation. All rights reserved.
*/

package org.pentaho.telemetry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaPluginTelemetry implements ITelemetryService {

    private static Log logger = LogFactory.getLog( BaPluginTelemetry.class );

    // region Constants

    private static final String BASE_TELEMETRY_SERVICE_NOT_DEFINED_MESSAGE =
            "Base telemetry service is not defined for plugin: ";
    private static final String TELEMETRY_NOT_ENABLED_MESSAGE =
            "Telemetry is not enabled for plugin: ";

    // endregion

    // region Properties

    /**
     * @return the base telemetry service
     */
    public ITelemetryHandler getTelemetryHandler() {
        return this.telemetryHandler;
    }
    protected void setTelemetryHandler(ITelemetryHandler telemetryHandler) {
        this.telemetryHandler = telemetryHandler;
    }
    private ITelemetryHandler telemetryHandler;

    /**
     * @return the platform version
     */
    public String getPlatformVersion() {
        return this.platformVersion;
    }
    protected void setPlatformVersion( String platformVersion ) {
        this.platformVersion = platformVersion;
    }
    private String platformVersion;

    /**
     * @return the plugin name
     */
    public String getPluginName() {
        return this.pluginName;
    }
    protected void setPluginName( String pluginName ) {
        this.pluginName = pluginName;
    }
    private String pluginName;

    /**
     * @return the plugin version
     */
    public String getPluginVersion() {
        return this.pluginVersion;
    }
    protected void setPluginVersion( String pluginVersion ) {
        this.pluginVersion = pluginVersion;
    }
    private String pluginVersion;

    /**
     * @return the base url for telemetry events to be posted
     */
    public String getBaseUrl() {
        return this.baseUrl;
    }
    protected void setBaseUrl( String baseUrl ) {
        this.baseUrl = baseUrl;
    }
    private String baseUrl;

    /**
     * @return true if telemetry is enabled for this plugin
     */
    public boolean isTelemetryEnabled() {
        return this.telemetryEnabled;
    }
    protected void setTelemetryEnabled( boolean telemetryEnabled ) {
        this.telemetryEnabled = telemetryEnabled;
    }
    private boolean telemetryEnabled;

    // endregion

    // region Constructors

    public BaPluginTelemetry(ITelemetryHandler telemetryHandler,
                              String telemetryUrl,
                              boolean telemetryEnabled) {
        this.setTelemetryHandler(telemetryHandler);
        this.setPlatformVersion( "BA 1.0" );
        this.setPluginName( "Unknwown" );
        this.setPluginVersion( "1.0.0" );
        this.setBaseUrl( telemetryUrl );
        this.setTelemetryEnabled(telemetryEnabled);
    }

    // endregion

    // region Methods

    @Override
    public boolean publishEvent( TelemetryEvent event ) {

        if ( !this.isTelemetryEnabled() ) {
            logger.info( TELEMETRY_NOT_ENABLED_MESSAGE + this.getPluginName() );
            return false;
        }

        if ( this.getTelemetryHandler() == null ) {
            logger.warn( BASE_TELEMETRY_SERVICE_NOT_DEFINED_MESSAGE + this.getPluginName() );
            return false;
        }

        // add provider info to telemetry event
        event.setPlatformVersion(this.getPlatformVersion());
        event.setPluginName(this.getPluginName());
        event.setPluginVersion(this.getPluginVersion());
        event.setUrlToCall(this.getBaseUrl());

        // call base service to publish event
        return this.getTelemetryHandler().publishEvent(event);
    }

    // endregion
}
