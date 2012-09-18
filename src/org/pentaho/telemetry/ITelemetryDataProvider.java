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

import java.util.Map;


/**
 * Describes the interface that needs to be provided to TelemetryHelper in order
 * to guarantee that telemetry events are correctly published to a remote server
 * @author pedrovale
 */
public interface ITelemetryDataProvider {
  
  /**
   * @return the platform version
   */
  public String getPlatformVersion();
  
  /**
   * @return the plugin name that originated the telemetry event
   */
  public String getPluginName();
  
  /**
   * @return the plugin version that originated the telemetry event
   */
  public String getPluginVersion();
  
  /**
   * @return Extra info to be published alongside the event. For instance, in the marketplace
   * plugin, extra info is the name of the plugin being installed or uninstalled.
   */
  
  public Map<String, String> getExtraInformation();
  
  /**
   * 
   * @return Event type to be published
   */
  public TelemetryHelper.TelemetryEventType getEventType();

  /**
   * 
   * @return true if telemetry is enabled for this particular context
   */
  public boolean isTelemetryEnabled();
  
  
  /**
   * 
   * @return the base url for telemetry events to be posted
   */
  public String getBaseUrl();
  
  
}
