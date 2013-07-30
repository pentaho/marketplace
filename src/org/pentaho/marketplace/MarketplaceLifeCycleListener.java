/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.pentaho.marketplace;

import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.UserSession;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalogHelper;

import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.pentaho.telemetry.BaPluginTelemetry;
import org.pentaho.telemetry.TelemetryHelper.TelemetryEventType;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

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

