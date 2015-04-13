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
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.net.MalformedURLException;
import java.net.URL;

public class MenuHandler extends AbstractXulEventHandler {
  public static final String MARKETPLACE_MENU_EVENT_HANDLER = "marketplaceMenuEventHandler";
  private static final String WEB_CLIENT_PATH =  "http://localhost:8181/marketplace/web/main.html";

  private static Class<?> PKG = MenuHandler.class; // for i18n purposes, needed by Translator2!!


  // region Properties
  public Spoon getSpoon() {
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

    // TODO: spoon instance should be injected
    this.setSpoon( Spoon.getInstance() );
  }
  // endregion

  // region Methods
  public void openMarketplace() {
    try {
      Spoon spoon = this.getSpoon();
      URL url = new URL( WEB_CLIENT_PATH );
      // TODO: i18n
      //String tabLabel = BaseMessages.getString( PKG, "marketplace_tab_label" );
      String tabLabel = "Marketplace";
      spoon.addSpoonBrowser( tabLabel, url.toString() );

    } catch ( MalformedURLException e ) {
      this.getLogger().error( "Error on marketplace URL: " + WEB_CLIENT_PATH, e );
    }
  }

  public void openPluginInformation() {
    this.getSpoon().showPluginInfo();
  }
  // endregion
}
