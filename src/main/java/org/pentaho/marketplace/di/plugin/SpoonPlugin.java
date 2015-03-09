package org.pentaho.marketplace.di.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.gui.SpoonFactory;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;

@org.pentaho.di.ui.spoon.SpoonPlugin( id = "PdiMarketplaceOSGI", image = "" )
@SpoonPluginCategories( { "spoon" } )
public class SpoonPlugin implements SpoonPluginInterface {

  private static final String RESOURCE_PATH = "org/pentaho/marketplace/di/plugin/res";
  private static final String OVERLAY_FILE_PATH =  RESOURCE_PATH + "/spoon_overlay.xul";
  private static final String SPOON_CATEGORY = "spoon";

  // region Properties
  private XulDomContainer container;

  public SpoonLifecycleListener getLifecycleListener() {
    return null;
  }

  // May be called more than once, don't construct your perspective here.
  public SpoonPerspective getPerspective() {
    return null;
  }

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

    // TODO: dependency injection
    this.setSpoon( Spoon.getInstance() );
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
    this.container = container;
    container.registerClassLoader( getClass().getClassLoader() );
    if ( category.equals( SPOON_CATEGORY ) ) {
      container.loadOverlay( OVERLAY_FILE_PATH );
      container.addEventHandler( this.getMenuHandler() );
    }
  }

  // Called by OSGI on remove
  public void removeFromContainer() throws XulException {
    if ( container == null ) {
      return;
    }

    Spoon spoon = this.getSpoon();
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
        // TODO Close marketplace tab
        container.deRegisterClassLoader( SpoonPlugin.class.getClassLoader() );

      }
    } );
  }
}

