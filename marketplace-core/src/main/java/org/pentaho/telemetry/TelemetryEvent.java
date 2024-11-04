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

import flexjson.JSONSerializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a telemetry event. Will be serialized to the file system (before publishing) and to JSON on publish
 * time
 */
public class TelemetryEvent implements Serializable {

  // region Inner Definitions

  /**
   * Represents the type of telemetry event
   */
  public enum Type {
    INSTALLATION, REMOVAL, UPGRADE, USAGE, OTHER
  }

  // endregion Inner Definitions

  // region Constants

  private static final long serialVersionUID = 1930549805476439703L;

  // endregion Constants

  // region Properties

  /**
   * @return the type of telemetry event
   */
  public TelemetryEvent.Type getEventType() {
    return this.eventType;
  }

  protected void setEventType( TelemetryEvent.Type eventType ) {
    this.eventType = eventType;
  }

  private TelemetryEvent.Type eventType;

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
   * @return the URL of the target remote endpoint
   */
  public String getUrlToCall() {
    return this.urlToCall;
  }

  protected void setUrlToCall( String urlToCall ) {
    this.urlToCall = urlToCall;
  }

  private String urlToCall;

  /**
   * @return a map with event specific information
   */
  public Map<String, String> getExtraInfo() {
    return this.extraInfo;
  }

  protected void setExtraInfo( Map<String, String> extraInfo ) {
    this.extraInfo = extraInfo;
  }

  private Map<String, String> extraInfo;

  /**
   * @return the timestamp for when the event was published
   */
  public long getEventTimestamp() {
    return this.eventTimestamp;
  }

  protected void setEventTimestamp( long eventTimestamp ) {
    this.eventTimestamp = eventTimestamp;
  }

  private long eventTimestamp;

  // endregion

  // region Constructors

  protected TelemetryEvent( TelemetryEvent.Type eventType,
                            String pluginName,
                            String pluginVersion,
                            String platformVersion,
                            String urlToCall ) {
    this.setEventType( eventType );
    this.setPluginName( pluginName );
    this.setPluginVersion( pluginVersion );
    this.setPlatformVersion( platformVersion );
    this.setUrlToCall( urlToCall );
    this.setExtraInfo( new HashMap<String, String>( 1 ) );
    this.setEventTimestamp( System.currentTimeMillis() );
  }

  // endregion

  // region Methods

  protected String encodeToJSON() {
    JSONSerializer serializer = new JSONSerializer();
    return serializer.deepSerialize( this );
  }

  // endregion
}
