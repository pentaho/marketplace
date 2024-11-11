/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
    return getRepoURL( WEB_CLIENT_PATH );
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
    try {
      Spoon spoon = this.getSpoon();
      URL url = new URL( getMarketplaceURL() );

      spoon.addSpoonBrowser( getMarketplaceTabLabel(), url.toString(), false );

    } catch ( MalformedURLException e ) {
      this.getLogger().error( "Error on marketplace URL: " + WEB_CLIENT_PATH, e );
    }
  }

  // endregion
}
