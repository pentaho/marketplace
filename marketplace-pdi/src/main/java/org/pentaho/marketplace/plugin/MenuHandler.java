package org.pentaho.marketplace.di.plugin;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.pentaho.di.i18n.BaseMessages;
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
