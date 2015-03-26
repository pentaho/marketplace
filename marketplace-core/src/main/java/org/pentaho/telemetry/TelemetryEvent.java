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

import flexjson.JSONSerializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a telemetry event. Will be serialized to file system (to store before publishing) and to JSON on publish
 * time
 */
public class TelemetryEvent implements Serializable {


  public enum Type {
    INSTALLATION, REMOVAL, USAGE, OTHER
  }


  // region Properties

  public String getPlatformVersion() {
    return this.platformVersion;
  }

  protected void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  private String platformVersion;

  public String getPluginName() {
    return this.pluginName;
  }

  protected void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }

  private String pluginName;

  public String getPluginVersion() {
    return this.pluginVersion;
  }

  protected void setPluginVersion(String pluginVersion) {
    this.pluginVersion = pluginVersion;
  }

  private String pluginVersion;

  public String getUrlToCall() {
    return this.urlToCall;
  }

  protected void setUrlToCall(String urlToCall) {
    this.urlToCall = urlToCall;
  }

  private String urlToCall;

  public TelemetryEvent.Type getEventType() {
    return this.eventType;
  }

  protected void setEventType(TelemetryEvent.Type eventType) {
    this.eventType = eventType;
  }

  private TelemetryEvent.Type eventType;

  public Map<String, String> getExtraInfo() {
    return this.extraInfo;
  }

  protected void setExtraInfo(Map<String, String> extraInfo) {
    this.extraInfo = extraInfo;
  }

  private Map<String, String> extraInfo;

  public long getEventTimestamp() {
    return this.eventTimestamp;
  }

  protected void setEventTimestamp(long eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }

  private long eventTimestamp;

  // endregion

  // region Constructors

  protected TelemetryEvent(TelemetryEvent.Type eventType,
                        String pluginName,
                        String pluginVersion,
                        String platformVersion,
                        String urlToCall) {
    this(eventType, pluginName, pluginVersion, platformVersion, urlToCall, new HashMap<String, String>(1));
  }

  protected TelemetryEvent(TelemetryEvent.Type eventType,
                        String pluginName,
                        String pluginVersion,
                        String platformVersion,
                        String urlToCall,
                        Map<String, String> eventInfo) {
    this.setEventType(eventType);
    this.setPluginName(pluginName);
    this.setPluginVersion(pluginVersion);
    this.setPlatformVersion(platformVersion);
    this.setUrlToCall(urlToCall);
    this.setExtraInfo(eventInfo);
  }

  // endregion

  // region Methods

  public void addInfo(String key, String value) {
    this.getExtraInfo().put(key, value);
  }

  protected void updateTimestamp() {
    this.setEventTimestamp(System.currentTimeMillis());
  }

  protected String encodeEvent() {
    JSONSerializer serializer = new JSONSerializer();
    return serializer.deepSerialize(this);
  }

  // endregion
}
