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
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.marketplace.di.plugin;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.platform.settings.ServerPort;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import org.pentaho.platform.settings.ServerPortRegistry;

import java.net.MalformedURLException;
import java.net.URL;

public class MenuHandler extends AbstractXulEventHandler {
  public static final String MARKETPLACE_MENU_EVENT_HANDLER = "marketplaceMenuEventHandler";
  private static final String WEB_CLIENT_PATH =  "/marketplace/web/main.html";
  private static final String OSGI_SERVICE_PORT = "OSGI_SERVICE_PORT";

  private static Class<?> PKG = MenuHandler.class; // for i18n purposes, needed by Translator2!!


  // region Properties
  public Spoon getSpoon() {
    if ( this.spoon == null ) {
      return Spoon.getInstance();
    }
    return this.spoon;
  }
  public void setSpoon( Spoon spoon ) {
    this.spoon = spoon;
  }
  private Spoon spoon;

  public Integer getOsgiServicePort() {
    // if no service port is specified try getting it from
    ServerPort osgiServicePort = ServerPortRegistry.getPort( OSGI_SERVICE_PORT );
    if ( osgiServicePort != null ) {
      return osgiServicePort.getValue();
    }
    return null;
  }

  protected Log getLogger() {
    return this.logger;
  }
  private Log logger = LogFactory.getLog( this.getClass() );
  // endregion

  // region Constructors
  public MenuHandler() {
    this.setName( MARKETPLACE_MENU_EVENT_HANDLER );
  }
  // endregion

  public String getMarketplaceURL() {
    return "http://localhost:" + this.getOsgiServicePort() + WEB_CLIENT_PATH;
  }

  public String getMarketplaceTabLabel() {
    // TODO: i18n
    //String tabLabel = BaseMessages.getString( PKG, "marketplace_tab_label" );
    String tabLabel = "Marketplace";
    return tabLabel;
  }

  // region Methods
  public void openMarketplace() {
    try {
      Spoon spoon = this.getSpoon();
      URL url = new URL( getMarketplaceURL() );

      spoon.addSpoonBrowser( getMarketplaceTabLabel(), url.toString(), false );

    } catch ( MalformedURLException e ) {
      this.getLogger().error( "Error on marketplace URL: " + WEB_CLIENT_PATH, e );
    }
  }

  public void openPluginInformation() {
    this.getSpoon().showPluginInfo();
  }
  // endregion
}
