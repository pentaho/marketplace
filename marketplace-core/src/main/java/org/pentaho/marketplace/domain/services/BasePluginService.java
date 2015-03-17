package org.pentaho.marketplace.domain.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.interfaces.IPluginProvider;
import org.pentaho.marketplace.domain.services.interfaces.IPluginService;

import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;

// TODO: turn on telemetry
/*
import org.pentaho.telemetry.BaPluginTelemetry;
import org.pentaho.telemetry.TelemetryHelper;
*/

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePluginService implements IPluginService {

  //region Inner Definitions
  protected static class MarketplaceSecurityException extends Exception {

    private static final long serialVersionUID = -1852471739131561628L;
  }
  //endregion

  //region Constants

  // region Message codes

  // Error messages codes should begin with ERROR

  protected static final String UNAUTHORIZED_ACCESS_MESSAGE =
    "Unauthorized Access.";
  protected static final String UNAUTHORIZED_ACCESS_ERROR_CODE =
    "ERROR_0002_UNAUTHORIZED_ACCESS";
  protected static final String NO_PLUGIN_ERROR_CODE = "ERROR_0001_NO_PLUGIN";
  protected static final String FAIL_ERROR_CODE = "ERROR_0003_FAIL";
  protected static final String PLUGIN_INSTALLED_CODE = "PLUGIN_INSTALLED";
  protected static final String PLUGIN_UNINSTALLED_CODE = "PLUGIN_UNINSTALLED";

  // endregion

  public static final String PLUGIN_NAME = "marketplace";


  //endregion

  //region Attributes
  protected Log getLogger() {
    return this.logger;
  }
  protected Log logger = LogFactory.getLog( this.getClass() );

  private IVersionDataFactory versionDataFactory;
  private IDomainStatusMessageFactory domainStatusMessageFactory;

  public IPluginProvider getMetadataPluginsProvider() {
    return this.metadataPluginsProvider;
  }
  protected BasePluginService setMetadataPluginsProvider( IPluginProvider provider ) {
    this.metadataPluginsProvider = provider;
    return this;
  }
  private IPluginProvider metadataPluginsProvider;


  protected String getServerVersion() {
    return this.serverVersion;
  }
  protected BasePluginService setServerVersion( String serverVersion ) {
    this.serverVersion = serverVersion;
    return this;
  }
  private String serverVersion;

  //endregion

  //region Constructors
  protected BasePluginService( IRemotePluginProvider metadataPluginsProvider,
                               IVersionDataFactory versionDataFactory,
                               IDomainStatusMessageFactory domainStatusMessageFactory
  ) {
    //initialize dependencies
    this.versionDataFactory = versionDataFactory;
    this.domainStatusMessageFactory = domainStatusMessageFactory;

    this.setMetadataPluginsProvider( metadataPluginsProvider );
  }
  //endregion

  //region Methods

  private boolean withinParentVersion( IPluginVersion pv ) {
    // need to compare plugin version min and max parent with system version.
    // replace the version of the xml url path with the current release version:
    String v = this.getServerVersion();
    IVersionData pvMax = this.versionDataFactory.create( pv.getMaxParentVersion() );
    IVersionData pvMin = this.versionDataFactory.create( pv.getMinParentVersion() );
    IVersionData version = this.versionDataFactory.create( v );

    return version.within( pvMin, pvMax );
  }

  private Collection<IPluginVersion> getCompatibleVersionsWithParent( Iterable<IPluginVersion> versions ) {
    Collection<IPluginVersion> compatibleVersions = new ArrayList<>();
    for ( IPluginVersion version : versions ) {
      if ( withinParentVersion( version ) ) {
        compatibleVersions.add( version );
      }
    }

    return compatibleVersions;
  }

  /**
   * Filters out plugins without a compatible version to server. Removes non compatible versions for the plugins which
   * pass the filter.
   *
   * @param plugins
   * @return
   */
  private Map<String, IPlugin> removeNonCompatibleVersions( Iterable<IPlugin> plugins ) {
    Map<String, IPlugin> pluginsWithCompatibleVersions = new HashMap<>(  );

    for ( IPlugin plugin : plugins ) {
      // filter out plugin versions that are not compatible with parent version
      Collection<IPluginVersion> compatibleVersions = this.getCompatibleVersionsWithParent( plugin.getVersions() );
      // only include plugins that have versions within this release
      if ( compatibleVersions.size() > 0 ) {
        // change available version to only compatible ones
        plugin.setVersions( compatibleVersions );
        pluginsWithCompatibleVersions.put( plugin.getId(), plugin );
      }
    }

    return pluginsWithCompatibleVersions;
  }

  private boolean isPluginIdValid( String pluginId ) {
    return pluginId != null
      && pluginId.length() > 0
      // this checks to make sure the plugin metadata isn't attempting to overwrite a folder on the system.
      // TODO: Test a .. encoded in UTF8, etc to see if there is a way to thwart this check
      && pluginId.indexOf( "." ) < 0;
  }

  /**
   * Filters plugins by plugin id
   *
   * @param plugins
   * @param pluginIds
   * @return
   */
  private Map<String, IPlugin> filterPlugins( Map<String, IPlugin> plugins, Collection<String> pluginIds ) {
    if ( pluginIds.size() < 1 ) {
      return Collections.emptyMap();
    }

    Map<String, IPlugin> filteredPlugins = new HashMap<>();

    for ( String pluginId : pluginIds ) {
      IPlugin plugin = plugins.get( pluginId );
      if ( plugin != null ) {
        filteredPlugins.put( pluginId, plugin );
      }
    }

    return filteredPlugins;
  }

  /**
   * Sets plugins as installed as well as the installed version
   *
   * @param plugins
   */
  private void setPluginsAsInstalled( Collection<IPlugin> plugins ) {
    for ( IPlugin plugin : plugins ) {
      plugin.setInstalled( true );
      // TODO: assumes plugin is installed in a folder named with the plugin id
      IPluginVersion installedVersion = getInstalledPluginVersion( plugin );
      if ( installedVersion != null ) {
        plugin.setInstalledBranch( installedVersion.getBranch() );
        plugin.setInstalledVersion( installedVersion.getVersion() );
        plugin.setInstalledBuildId( installedVersion.getBuildId() );
      }
    }
  }

  private IDomainStatusMessage installPluginAux( String pluginId, String versionBranch )
    throws MarketplaceSecurityException {

    if ( !hasMarketplacePermission() ) {
      throw new MarketplaceSecurityException();
    }

    if ( !isPluginIdValid( pluginId ) ) {
      return this.domainStatusMessageFactory.create( NO_PLUGIN_ERROR_CODE,
        "Invalid Plugin Id." );
    }

    IPlugin toInstall = this.getPlugin( pluginId );
    if ( toInstall == null ) {
      return this.domainStatusMessageFactory.create( NO_PLUGIN_ERROR_CODE, "Plugin Not Found" );
    }

    IPluginVersion versionToInstall = null;
    if ( versionBranch != null && versionBranch.length() > 0 ) {
      versionToInstall = toInstall.getVersionByBranch( versionBranch );
      if ( versionToInstall == null ) {
        return this.domainStatusMessageFactory.create( NO_PLUGIN_ERROR_CODE, "Plugin version not found" );
      }
    } else {
      return this.domainStatusMessageFactory
        .create( FAIL_ERROR_CODE, "Version " + versionBranch + " not found for plugin " + pluginId
          + ", see log for details." );
    }


    // TODO: turn on telemetry
    /*
    // Perhaps we are reinstalling the marketplace.
    // Create telemetry event and messages before closing class loader just in case.
    BaPluginTelemetry telemetryEvent = new BaPluginTelemetry( PLUGIN_NAME );
    Map<String, String> extraInfo = new HashMap<>( 1 );
    extraInfo.put( "installedPlugin", toInstall.getId() );
    extraInfo.put( "installedVersion", versionToInstall.getVersion() );
    extraInfo.put( "installedBranch", versionBranch );
    */

    IDomainStatusMessage successMessage =
      this.domainStatusMessageFactory.create( PLUGIN_INSTALLED_CODE, toInstall.getName()
        + " was successfully installed.  Please restart your BI Server. \n" + toInstall.getInstallationNotes() );

    IDomainStatusMessage failureMessage = this.domainStatusMessageFactory
      .create( FAIL_ERROR_CODE, "Failed to execute install, see log for details." );

    // before install, close class loader in case it's a reinstall
    this.unloadPlugin( toInstall.getId() );

    if ( !this.executeInstall( toInstall, versionToInstall ) ) {
      return failureMessage;
    }

    // TODO: turn on telemetry
    /*
    try {
      telemetryEvent.sendTelemetryRequest( TelemetryHelper.TelemetryEventType.INSTALLATION, extraInfo );
    } catch ( NoClassDefFoundError e ) {
      this.logger.debug( "Failed to find class definitions. Most likely reason is reinstalling marketplace.", e );
    }
    */

    return successMessage;
  }

  private IDomainStatusMessage uninstallPluginAux( String pluginId ) throws MarketplaceSecurityException {

    if ( !hasMarketplacePermission() ) {
      throw new MarketplaceSecurityException();
    }

    IPlugin toUninstall = this.getPlugin( pluginId );
    if ( toUninstall == null ) {
      return this.domainStatusMessageFactory.create( NO_PLUGIN_ERROR_CODE, "Plugin Not Found" );
    }

    // TODO: turn on telemetry
    /*
    // Perhaps we are uninstalling the marketplace.
    // Create telemetry event and messages before closing class loader just in case.
    BaPluginTelemetry telemetryEvent = new BaPluginTelemetry( PLUGIN_NAME );
    Map<String, String> extraInfo = new HashMap<>( 1 );
    extraInfo.put( "uninstalledPlugin", toUninstall.getId() );
    extraInfo.put( "uninstalledPluginVersion", toUninstall.getInstalledVersion() );
    extraInfo.put( "uninstalledPluginBranch", toUninstall.getInstalledBranch() );
    */

    IDomainStatusMessage successMessage =
      this.domainStatusMessageFactory.create( PLUGIN_UNINSTALLED_CODE, toUninstall.getName()
        + " was successfully uninstalled.  Please restart your BI Server." );

    IDomainStatusMessage failureMessage = this.domainStatusMessageFactory
      .create( FAIL_ERROR_CODE, "Failed to execute uninstall, see log for details." );

    // before deletion, close class loader
    this.unloadPlugin( toUninstall.getId() );

    if ( !this.executeUninstall( toUninstall ) ) {
      return failureMessage;
    }


    // TODO: turn on telemetry
    //telemetryEvent.sendTelemetryRequest( TelemetryHelper.TelemetryEventType.REMOVAL, extraInfo );

    return successMessage;
  }
  //endregion

  //region IPluginService implementation
  public IPlugin getPlugin( String id ) {
    return this.getPlugins().get( id );
  }

  @Override public Map<String, IPlugin> getPlugins() {
    Map<String, IPlugin> marketplacePlugins = this.getMetadataPluginsProvider().getPlugins();

    Map<String, IPlugin> compatiblePlugins = this.removeNonCompatibleVersions( marketplacePlugins.values() );

    Collection<String> installedPluginIds = getInstalledPluginIds();
    Map<String, IPlugin> installedPlugins = this.filterPlugins( compatiblePlugins, installedPluginIds );
    this.setPluginsAsInstalled( installedPlugins.values() );

    return compatiblePlugins;
  }

  @Override
  public IDomainStatusMessage installPlugin( String pluginId, String versionBranch ) {
    try {
      IDomainStatusMessage msg = this.installPluginAux( pluginId, versionBranch );
      return msg;
    } catch ( MarketplaceSecurityException e ) {
      this.getLogger().debug( e.getMessage(), e );
      return this.domainStatusMessageFactory.create( UNAUTHORIZED_ACCESS_ERROR_CODE,
        UNAUTHORIZED_ACCESS_MESSAGE );
    }
  }

  @Override
  public IDomainStatusMessage uninstallPlugin( String pluginId ) {
    try {
      IDomainStatusMessage msg = uninstallPluginAux( pluginId );
      return msg;
    } catch ( MarketplaceSecurityException e ) {
      this.getLogger().debug( e.getMessage(), e );
      return this.domainStatusMessageFactory.create( UNAUTHORIZED_ACCESS_ERROR_CODE,
        UNAUTHORIZED_ACCESS_MESSAGE );
    }
  }
  //endregion

  abstract protected boolean hasMarketplacePermission();

  abstract protected void unloadPlugin( String pluginId );

  abstract protected boolean executeInstall( IPlugin plugin, IPluginVersion version );

  abstract protected boolean executeUninstall( IPlugin plugin );

  abstract protected IPluginVersion getInstalledPluginVersion( IPlugin plugin );

  abstract protected Collection<String> getInstalledPluginIds();

}
