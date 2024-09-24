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

package org.pentaho.telemetry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelemetryService implements ITelemetryService {

  // region Constants

  private static final String TELEMETRY_NOT_ENABLED_MESSAGE =
    "Telemetry is not enabled for plugin: ";
  private static final String TELEMETRY_HANDLER_NOT_DEFINED_MESSAGE =
    "Telemetry handler is not defined for plugin: ";

  // endregion

  // region Properties

  protected Log getLogger() {
    return logger;
  }

  private static final Log logger = LogFactory.getLog( TelemetryService.class );

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
   * @return the URL for telemetry events to be posted
   */
  public String getTelemetryUrl() {
    return this.telemetryUrl;
  }

  protected void setTelemetryUrl( String telemetryUrl ) {
    this.telemetryUrl = telemetryUrl;
  }

  private String telemetryUrl;

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

  /**
   * @return the telemetry handler
   */
  public ITelemetryHandler getTelemetryHandler() {
    return this.telemetryHandler;
  }

  protected void setTelemetryHandler( ITelemetryHandler telemetryHandler ) {
    this.telemetryHandler = telemetryHandler;
  }

  private ITelemetryHandler telemetryHandler;

  // endregion

  // region Constructors

  public TelemetryService( String pluginName,
                           String pluginVersion,
                           String platformVersion,
                           String telemetryUrl,
                           boolean telemetryEnabled,
                           ITelemetryHandler telemetryHandler ) {
    this.setPluginName( pluginName );
    this.setPluginVersion( pluginVersion );
    this.setPlatformVersion( platformVersion );
    this.setTelemetryUrl( telemetryUrl );
    this.setTelemetryEnabled( telemetryEnabled );
    this.setTelemetryHandler( telemetryHandler );
  }

  // endregion

  // region Methods

  @Override
  public TelemetryEvent createEvent( TelemetryEvent.Type eventType ) {
    return new TelemetryEvent( eventType, this.getPluginName(), this.getPluginVersion(), this.getPlatformVersion(),
      this.getTelemetryUrl() );
  }

  @Override
  public boolean publishEvent( TelemetryEvent event ) {

    if ( !this.isTelemetryEnabled() ) {
      this.getLogger().info( TELEMETRY_NOT_ENABLED_MESSAGE + this.getPluginName() );
      return false;
    }

    if ( this.getTelemetryHandler() == null ) {
      this.getLogger().warn( TELEMETRY_HANDLER_NOT_DEFINED_MESSAGE + this.getPluginName() );
      return false;
    }

    // update event timestamp to match the time it was published
    event.setEventTimestamp( System.currentTimeMillis() );

    // add event to the telemetry handler queue
    return this.getTelemetryHandler().queueEvent( event );
  }

  // endregion
}
