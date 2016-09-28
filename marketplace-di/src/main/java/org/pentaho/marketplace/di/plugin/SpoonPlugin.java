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
 * Copyright (c) 2016 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.marketplace.di.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.di.ui.spoon.TabMapEntry;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;

@org.pentaho.di.ui.spoon.SpoonPlugin( id = "pentaho-marketplace-di", image = "" )
@SpoonPluginCategories( { "spoon" } )
public class SpoonPlugin implements SpoonPluginInterface {

  private static final String RESOURCE_PATH = "org/pentaho/marketplace/di/plugin/res";
  private static final String OVERLAY_FILE_PATH =  RESOURCE_PATH + "/spoon_overlay.xul";
  private static final String SPOON_CATEGORY = "spoon";

  // region Properties
  private XulDomContainer container;

  public void setUrl( String url ) {
    MenuHandler.WEB_CLIENT_PATH = url;
  }

  public SpoonLifecycleListener getLifecycleListener() {
    return new SpoonLifecycleListener() {
      public void onEvent( SpoonLifeCycleEvent evt ) {
        if ( evt.equals( SpoonLifeCycleEvent.STARTUP ) ) {
          try {
            getSpoon().setMarketMethod( MenuHandler.class.getMethod( "openMarketplace" ) );
          } catch ( Throwable e ) {
            // under no circumstance allow the failure to prevent market load
            logger.warn( e.getMessage(), e );
          }
        }
      }
    };
  }

  // May be called more than once, don't construct your perspective here.
  public SpoonPerspective getPerspective() {
    return null;
  }

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

  protected MenuHandler getMenuHandler() {
    return this.menuHandler;
  }
  protected void setMenuHandler( MenuHandler menuHandler ) {
    this.menuHandler = menuHandler;
  }
  private MenuHandler menuHandler;
  // endregion

  // region Constructors
  public SpoonPlugin() {
    this.setMenuHandler( new MenuHandler() );
  }
  // endregion

  /**
   * This call tells the Spoon Plugin to make it's modification to the particular area in Spoon (category). The current
   * possible areas are: trans-graph, job-graph, database_dialog and spoon.
   *
   * @param category  Area to modify
   * @param container The XUL-document for the particular category.
   * @throws XulException
   */
  public void applyToContainer( String category, XulDomContainer container ) throws XulException {
    if ( category.equals( SPOON_CATEGORY ) ) {
      this.container = container;
      container.registerClassLoader( getClass().getClassLoader() );
      container.loadOverlay( OVERLAY_FILE_PATH );
      container.addEventHandler( this.getMenuHandler() );

      // refresh menus
      this.getSpoon().enableMenus();
    }
  }

  // Called by OSGI on remove
  public void removeFromContainer() throws XulException {
    if ( container == null ) {
      return;
    }

    final Spoon spoon = this.getSpoon();
    final Log logger = this.getLogger();
    final String menuHandlerName = this.getMenuHandler().getName();
    spoon.getDisplay().syncExec( new Runnable() {
      public void run() {
        try {
          container.removeOverlay( OVERLAY_FILE_PATH );
        } catch ( XulException e ) {
          logger.error( "Error removing overlay: " + OVERLAY_FILE_PATH, e );
        }
        container.getEventHandlers().remove( menuHandlerName );
        container.deRegisterClassLoader( SpoonPlugin.class.getClassLoader() );

        closeMarketplaceTab();

        // refresh menus
        spoon.enableMenus();
      }

      private void closeMarketplaceTab() {
        TabMapEntry marketplaceTab = getMarketplaceTab();
        if ( marketplaceTab != null ) {
          spoon.delegates.tabs.removeTab( marketplaceTab );
        }
      }

      private TabMapEntry getMarketplaceTab() {
        TabMapEntry marketplaceTab = null;
        for ( TabMapEntry tabMapEntry : spoon.delegates.tabs.getTabs() ) {
          if ( tabMapEntry.getTabItem().getId().equalsIgnoreCase( "Marketplace" ) ) {
            marketplaceTab = tabMapEntry;
            break;
          }
        }
        return marketplaceTab;
      }
    } );
  }
}

