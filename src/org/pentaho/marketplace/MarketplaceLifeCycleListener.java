/*!
* Copyright 2002 - 2013 Pentaho Corporation.  All rights reserved.
* 
* This software was developed by Pentaho Corporation and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Pentaho Corporation.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

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

