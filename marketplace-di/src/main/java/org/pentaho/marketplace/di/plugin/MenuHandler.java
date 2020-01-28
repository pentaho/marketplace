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
 * Copyright (c) 2016 - 2020 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.marketplace.di.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.platform.settings.ServerPort;
import org.pentaho.platform.settings.ServerPortRegistry;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.net.MalformedURLException;
import java.net.URL;

public class MenuHandler extends AbstractXulEventHandler {
  public static final String MARKETPLACE_MENU_EVENT_HANDLER = "marketplaceMenuEventHandler";
  public static String WEB_CLIENT_PATH =  "/marketplace/web/main.html";
  private static final String OSGI_SERVICE_PORT = "OSGI_SERVICE_PORT";
  private static final String THIN_CLIENT_HOST = "THIN_CLIENT_HOST";
  private static final String THIN_CLIENT_PORT = "THIN_CLIENT_PORT";
  private static final String LOCALHOST = "127.0.0.1";

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
    return "osgi" + WEB_CLIENT_PATH;
  }

  private static String getRepoURL( String path ) {
    String host;
    Integer port;
    try {
      host = getKettleProperty( THIN_CLIENT_HOST );
      port = Integer.valueOf( getKettleProperty( THIN_CLIENT_PORT ) );
    } catch ( Exception e ) {
      host = LOCALHOST;
      port = getOsgiServicePort();
    }
    return "http://" + host + ":" + port + path;
  }

  private static String getKettleProperty( String propertyName ) {
    // loaded in system properties at startup
    return System.getProperty( propertyName );
  }

  private static Integer getOsgiServicePort() {
    // if no service port is specified try getting it from
    ServerPort osgiServicePort = ServerPortRegistry.getPort( OSGI_SERVICE_PORT );
    if ( osgiServicePort != null ) {
      return osgiServicePort.getAssignedPort();
    }
    return null;
  }

  public String getMarketplaceTabLabel() {
    // TODO: i18n
    //String tabLabel = BaseMessages.getString( PKG, "marketplace_tab_label" );
    String tabLabel = "Marketplace";
    return tabLabel;
  }

  // region Methods
  public void openMarketplace() {
    Spoon spoon = this.getSpoon();
    String url = getMarketplaceURL();

    spoon.addSpoonBrowser( getMarketplaceTabLabel(), url, false );
  }

  // endregion
}
