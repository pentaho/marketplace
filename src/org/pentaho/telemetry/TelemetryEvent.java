/*
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
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *
 * Created Set 17th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */
package org.pentaho.telemetry;

import flexjson.JSONSerializer;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

 
 /**
  * Internal class to be used in conjuntion with the telemetry event request queue.
  * Useful for setting up retry attempts
  * @author pedrovale
  */
public class TelemetryEvent implements Serializable {

  private String urlToCall;
  private String pluginName, 
          pluginVersion,
          platformVersion;
  private long eventTimestamp;
  private TelemetryHelper.TelemetryEventType eventType;

  private Map<String, String> extraInfo;
  
  public TelemetryEvent(ITelemetryDataProvider dataProvider) {
    this.urlToCall = dataProvider.getBaseUrl();

    this.eventType = dataProvider.getEventType();
    this.pluginName = dataProvider.getPluginName();
    this.pluginVersion = dataProvider.getPluginVersion();
    this.platformVersion = dataProvider.getPlatformVersion();
    this.extraInfo = dataProvider.getExtraInformation();
    this.eventTimestamp = System.currentTimeMillis();

  }
  
  public String encodeEvent() {
    JSONSerializer serializer = new JSONSerializer();
    return serializer.deepSerialize(this);
  }

  /**
   * @return the urlToCall
   */
  public String getUrlToCall() {
    return urlToCall;
  }

  /**
   * @param urlToCall the urlToCall to set
   */
  public void setUrlToCall(String urlToCall) {
    this.urlToCall = urlToCall;
  }

  /**
   * @return the pluginName
   */
  public String getPluginName() {
    return pluginName;
  }

  /**
   * @return the pluginVersion
   */
  public String getPluginVersion() {
    return pluginVersion;
  }

  /**
   * @return the platformVersion
   */
  public String getPlatformVersion() {
    return platformVersion;
  }

  /**
   * @return the eventTimestamp
   */
  public long getEventTimestamp() {
    return eventTimestamp;
  }

  /**
   * @return the eventType
   */
  public TelemetryHelper.TelemetryEventType getEventType() {
    return eventType;
  }

  /**
   * @return the extraInfo
   */
  public Map<String, String> getExtraInfo() {
    return extraInfo;
  }
  
}
