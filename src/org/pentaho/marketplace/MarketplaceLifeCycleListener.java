/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.pentaho.marketplace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import org.pentaho.telemetry.BaPluginTelemetry;
import org.pentaho.telemetry.TelemetryHelper.TelemetryEventType;

/**
 * Responsible for setting up distributed cache from configuration.
 */
public class MarketplaceLifeCycleListener implements IPluginLifecycleListener
{
  
  
  static Log logger = LogFactory.getLog(MarketplaceLifeCycleListener.class);

  @Override
  public void init() throws PluginLifecycleException
  {
    logger.debug("Marketplace init");
  }

  @Override
  public void loaded() throws PluginLifecycleException
  {
    logger.debug("Marketplace loaded.");
    BaPluginTelemetry telemetryPublisher = new BaPluginTelemetry("marketplace");
    telemetryPublisher.sendTelemetryRequest(TelemetryEventType.USAGE, null);
  }
  
  @Override
  public void unLoaded() throws PluginLifecycleException
  {
    logger.debug("Marketplace unloaded");
  }


  
  
}

