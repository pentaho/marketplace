package org.pentaho.marketplace.domain.services;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;
import org.pentaho.marketplace.domain.model.entities.serialization.MarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.helpers.Util;
import org.pentaho.marketplace.domain.services.interfaces.IPluginProvider;
import org.pentaho.marketplace.domain.services.interfaces.IPluginService;

import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;
import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.api.engine.ISecurityHelper;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;

import org.pentaho.telemetry.BaPluginTelemetry;
import org.pentaho.telemetry.TelemetryHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class PluginService implements IPluginService {

  //region Inner Definitions
  private static class MarketplaceSecurityException extends Exception {

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

  private static final String CLOSE_METHOD_NAME = "close";
  private static final String PLUGIN_NAME = "marketplace";

  private static final String MARKETPLACE_ENTRIES_URL_FALLBACK = "https://raw.github.com/pentaho/marketplace-metadata/master/marketplace.xml";

  //endregion

  //region Attributes
  private Log logger = LogFactory.getLog( this.getClass() );
  private IVersionDataFactory versionDataFactory;
  private IDomainStatusMessageFactory domainStatusMessageFactory;

  public MarketplaceXmlSerializer getXmlSerializer() {
    return this.xmlPluginsSerializer;
  }
  protected PluginService setXmlSerializer( MarketplaceXmlSerializer serializer ) {
    this.xmlPluginsSerializer = serializer;
    return this;
  }
  private MarketplaceXmlSerializer xmlPluginsSerializer;

  public ISecurityHelper getSecurityHelper() {
    return this.securityHelper;
  }
  protected PluginService setSecurityHelper( ISecurityHelper securityHelper ) {
    this.securityHelper = securityHelper;
    return this;
  }
  private ISecurityHelper securityHelper;

  public IPluginProvider getMetadataPluginsProvider() {
    return this.metadataPluginsProvider;
  }
  protected PluginService setMetadataPluginsProvider( IPluginProvider provider ) {
    this.metadataPluginsProvider = provider;
    return this;
  }
  private IPluginProvider metadataPluginsProvider;

  public IPluginResourceLoader getPluginResourceLoader() { return this.pluginResourceLoader; }
  protected PluginService setPluginResourceLoader( IPluginResourceLoader pluginResourceLoader ) {
    this.pluginResourceLoader = pluginResourceLoader;
    return this;
  }
  private IPluginResourceLoader pluginResourceLoader;

  protected String getServerVersion() {
    if ( this.serverVersion == null ) {
      VersionInfo versionInfo = VersionHelper.getVersionInfo( PentahoSystem.class );
      return versionInfo.getVersionNumber();
    }

    return this.serverVersion;
  }
  protected PluginService setServerVersion( String serverVersion ) {
    this.serverVersion = serverVersion;
    return this;
  }
  private String serverVersion;

  // TODO: see if there is a better way to encapsulate this
  public IPluginManager getPluginManager ( IPentahoSession session ) {
    return PentahoSystem.get( IPluginManager.class, session );
  }

  // TODO: see if there is a better way to encapsulate this
  protected IApplicationContext getApplicationContext() {
    if ( this.applicationContext == null ) {
      return PentahoSystem.getApplicationContext();
    }

    return this.applicationContext;
  }
  protected PluginService setApplicationContext( IApplicationContext applicationContext ) {
    this.applicationContext = applicationContext;
    return this;
  }
  private IApplicationContext applicationContext;


  // TODO: see if there is a better way to encapsulate this.
  // Probably just pass in the session in the methods that require it.
  protected IPentahoSession getCurrentSession() {
    return PentahoSessionHolder.getSession();
  }
  protected PluginService setCurrentSession( IPentahoSession session ) {
    PentahoSessionHolder.setSession( session );
    return this;
  }
  //endregion

  //region Constructors
  @Autowired
  public PluginService( IRemotePluginProvider metadataPluginsProvider,
                        MarketplaceXmlSerializer pluginsSerializer,
                        IVersionDataFactory versionDataFactory,
                        IDomainStatusMessageFactory domainStatusMessageFactory,
                        ISecurityHelper securityHelper,
                        IPluginResourceLoader resourceLoader ) {

    //initialize dependencies
    this.versionDataFactory = versionDataFactory;
    this.domainStatusMessageFactory = domainStatusMessageFactory;

    MarketplaceXmlSerializer serializer = pluginsSerializer;
    this.setXmlSerializer( serializer );

    this.setSecurityHelper( securityHelper );
    this.setPluginResourceLoader( resourceLoader );

    URL metadataUrl = this.getMetadataUrl( resourceLoader );
    metadataPluginsProvider.setUrl( metadataUrl );
    this.setMetadataPluginsProvider( metadataPluginsProvider );

  }
  //endregion

  //region Methods
  private void closeClassLoader( String pluginId ) {
    IPluginManager pluginManager = this.getPluginManager( this.getCurrentSession() );
    ClassLoader cl = pluginManager.getClassLoader( pluginId );
    if ( cl != null && cl instanceof URLClassLoader ) {
      try {
        URLClassLoader cl1 = (URLClassLoader) cl;
        Util.closeURLClassLoader( cl1 );
        Method closeMethod = cl1.getClass().getMethod( PluginService.CLOSE_METHOD_NAME );
        closeMethod.invoke( cl1 );
      } catch ( Throwable e ) {
        if ( e instanceof NoSuchMethodException ) {
          logger.debug( "Probably running in java 6 so close method on URLClassLoader is not available" );
        } else if ( e instanceof IOException ) {
          logger.error( "Unable to close class loader for plugin. Will try uninstalling plugin anyway", e );
        } else {
          logger.error( "Error while closing class loader", e );
        }
      }
    }
  }

  private boolean hasMarketplacePermission() {
    IPluginResourceLoader resLoader = this.getPluginResourceLoader();
    String roles = null;
    String users = null;

    try {
      roles = resLoader.getPluginSetting( getClass(), "settings/marketplace-roles" ); //$NON-NLS-1$
      users = resLoader.getPluginSetting( getClass(), "settings/marketplace-users" ); //$NON-NLS-1$
    } catch ( Exception e ) {
      logger.debug( "Error getting data access plugin settings", e );
    }

    if ( roles == null ) {
      // If it's true, we'll just check if the user is admin
      return this.getSecurityHelper().isPentahoAdministrator( this.getCurrentSession() );
    }

    String[] roleArr = roles.split( "," ); //$NON-NLS-1$

    Authentication auth = this.getSecurityHelper().getAuthentication( this.getCurrentSession(), true );
    for ( String role : roleArr ) {
      for ( GrantedAuthority userRole : auth.getAuthorities() ) {
        if ( role != null && role.trim().equals( userRole.getAuthority() ) ) {
          return true;
        }
      }
    }
    if ( users != null ) {
      String[] userArr = users.split( "," ); //$NON-NLS-1$
      for ( String user : userArr ) {
        if ( user != null && user.trim().equals( auth.getName() ) ) {
          return true;
        }
      }
    }
    return false;
  }

  private IPluginVersion getInstalledPluginVersion( String pluginFolderName ) {
    String versionPath = this.getApplicationContext().getSolutionPath( "system/" + pluginFolderName
      + "/version.xml" );
    FileReader reader = null;
    try {
      File file = new File( versionPath );
      if ( !file.exists() ) {
        return null;
      }
      reader = new FileReader( versionPath );
      IPluginVersion version = this.getXmlSerializer().getInstalledVersion( new InputSource( reader ) );
      return version;

    } catch ( Exception e ) {
      e.printStackTrace();
    } finally {
      try {
        if ( reader != null ) {
          reader.close();
        }
      } catch ( Exception e ) {
        // do nothing
      }
    }
    return null;
  }

  private Collection<String> getInstalledPluginIdsFromFileSystem() {

    Collection<String> plugins = new ArrayList<String>();

    File systemDir = new File( this.getApplicationContext().getSolutionPath( "system/" ) );

    String[] dirs = systemDir.list( DirectoryFileFilter.INSTANCE );

    for ( String dir : dirs ) {
      if ( ( new File( systemDir.getAbsolutePath() + File.separator + dir + File.separator
          + "plugin.xml" ) ).isFile() ) {
        plugins.add( dir );
      }
    }

    return plugins;
  }

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
    Collection<IPluginVersion> compatibleVersions = new ArrayList<IPluginVersion>();
    for ( IPluginVersion version : versions ) {
      if ( withinParentVersion( version ) ) {
        compatibleVersions.add( version );
      }
    }

    return compatibleVersions;
  }

  private URL getMetadataUrl( IPluginResourceLoader resLoader ) {
    String urlPath = null;
    try {
      urlPath = resLoader.getPluginSetting( getClass(), "settings/marketplace-site" ); //$NON-NLS-1$
    } catch ( Exception e ) {
      logger.debug( "Error getting data access plugin settings", e );
    }

    if ( urlPath == null || "".equals( urlPath ) ) {
      urlPath = MARKETPLACE_ENTRIES_URL_FALLBACK;
    }

    try {
      return new URL( urlPath );
    } catch ( MalformedURLException e ) {
      this.logger.error( "Invalid metadata url: " + urlPath, e );
      return null;
    }
  }

  /**
   * Filters out plugins without a compatible version to server.
   * Removes non compatible versions for the plugins which pass the filter.
   * @param plugins
   * @return
   */
  private Collection<IPlugin> removeNonCompatibleVersions( Iterable<IPlugin> plugins ) {
    Collection<IPlugin> pluginsWithCompatibleVersions = new ArrayList<IPlugin>();

    for ( IPlugin plugin : plugins ) {
      // filter out plugin versions that are not compatible with parent version
      Collection<IPluginVersion> compatibleVersions = this.getCompatibleVersionsWithParent( plugin.getVersions() );
      // only include plugins that have versions within this release
      if ( compatibleVersions.size() > 0 ) {
        // change available version to only compatible ones
        plugin.setVersions( compatibleVersions );
        pluginsWithCompatibleVersions.add( plugin );
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
   * @param plugins
   * @param pluginIds
   * @return
   */
  private Collection<IPlugin> filterPlugins( Collection<IPlugin> plugins, Collection<String> pluginIds ) {
    if ( pluginIds.size() < 1 ) {
      return Collections.emptyList();
    }

    Collection<IPlugin> filteredPlugins = new ArrayList<IPlugin>();
    Map<String, IPlugin> pluginMap = new Hashtable<String, IPlugin>();
    for( IPlugin plugin : plugins ) {
      pluginMap.put( plugin.getId(), plugin );
    }

    for ( String pluginId : pluginIds ) {
      IPlugin plugin = pluginMap.get( pluginId );
      if ( plugin != null ) {
        filteredPlugins.add( plugin );
      }
    }

    return filteredPlugins;
  }

  /**
   * Sets plugins as installed as well as the installed version
   * @param plugins
   */
  private void setPluginsAsInstalled( Collection<IPlugin> plugins ) {
    for ( IPlugin plugin : plugins ) {
      plugin.setInstalled( true );
      // TODO: assumes plugin is installed in a folder named with the plugin id
      IPluginVersion installedVersion = getInstalledPluginVersion( plugin.getId() );
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
      return this.domainStatusMessageFactory.create( PluginService.NO_PLUGIN_ERROR_CODE,
        "Invalid Plugin Id." );
    }

    // TODO: should not be necessary to get all plugins. Just the one we want.
    Iterable<IPlugin> plugins = this.getPlugins();
    IPlugin toInstall = null;
    for ( IPlugin plugin : plugins ) {
      if ( plugin.getId().equals( pluginId ) ) {
        toInstall = plugin;
      }
    }
    if ( toInstall == null ) {
      return this.domainStatusMessageFactory.create( PluginService.NO_PLUGIN_ERROR_CODE, "Plugin Not Found" );
    }

    // before deletion, close class loader
    this.closeClassLoader( toInstall.getId() );


    String downloadUrl, samplesDownloadUrl, availableVersion;

    if ( versionBranch != null && versionBranch.length() > 0 ) {
      IPluginVersion v = toInstall.getVersionByBranch( versionBranch );
      if ( v == null ) {
        return this.domainStatusMessageFactory.create( PluginService.NO_PLUGIN_ERROR_CODE, "Plugin version not found" );
      }
      downloadUrl = v.getDownloadUrl();
      samplesDownloadUrl = v.getSamplesDownloadUrl();
      availableVersion = v.getVersion();
    } else {
      return this.domainStatusMessageFactory
        .create( PluginService.FAIL_ERROR_CODE, "Version " + versionBranch + " not found for plugin " + pluginId
          + ", see log for details." );
    }

    try {
      Result result = this.executeInstallPluginJob( toInstall.getId(), downloadUrl, samplesDownloadUrl, availableVersion );

      if ( result == null || result.getNrErrors() > 0 ) {
        return this.domainStatusMessageFactory
          .create( PluginService.FAIL_ERROR_CODE, "Failed to execute install, see log for details." );
      }
    } catch ( KettleException e ) {
      logger.error( e.getMessage(), e );
      return this.domainStatusMessageFactory
        .create( PluginService.FAIL_ERROR_CODE, "Failed to execute install, see log for details." );
    }


    BaPluginTelemetry telemetryEvent = new BaPluginTelemetry( PLUGIN_NAME );
    Map<String, String> extraInfo = new HashMap<String, String>( 1 );
    extraInfo.put( "installedPlugin", toInstall.getId() );
    extraInfo.put( "installedVersion", availableVersion );
    extraInfo.put( "installedBranch", versionBranch );

    telemetryEvent.sendTelemetryRequest( TelemetryHelper.TelemetryEventType.INSTALLATION, extraInfo );


    return this.domainStatusMessageFactory.create( PluginService.PLUGIN_INSTALLED_CODE, toInstall.getName()
      + " was successfully installed.  Please restart your BI Server. \n" + toInstall.getInstallationNotes() );
  }

  private Result executeInstallPluginJob( String pluginId, String downloadUrl, String samplesDownloadUrl, String availableVersion )
    throws KettleXMLException, UnknownParamException {

    // get marketplace path
    String jobPath = this.getApplicationContext().getSolutionPath( "system/" + PluginService.PLUGIN_NAME
      + "/processes/download_and_install_plugin.kjb" );

    JobMeta installJobMeta = new JobMeta( jobPath, null );
    Job job = new Job( null, installJobMeta );

    File file = new File( this.getApplicationContext().getSolutionPath( "system/plugin-cache/downloads" ) );
    file.mkdirs();
    file = new File( this.getApplicationContext().getSolutionPath( "system/plugin-cache/backups" ) );
    file.mkdirs();
    file = new File( this.getApplicationContext().getSolutionPath( "system/plugin-cache/staging" ) );
    file.mkdirs();

    job.getJobMeta().setParameterValue( "downloadUrl", downloadUrl );

    if ( samplesDownloadUrl != null ) {
      job.getJobMeta().setParameterValue( "samplesDownloadUrl", samplesDownloadUrl );
      job.getJobMeta().setParameterValue( "samplesDir", "/public/plugin-samples" );
      job.getJobMeta().setParameterValue( "samplesTargetDestination", this.getApplicationContext()
        .getSolutionPath( "plugin-samples/" + pluginId ) );
      job.getJobMeta().setParameterValue( "samplesTargetBackup", this.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/backups/" + pluginId + "_samples_" + new Date()
          .getTime() ) );
      job.getJobMeta().setParameterValue( "samplesDownloadDestination", this.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/downloads/" + pluginId + "-samples-" + availableVersion
          + "_" + new Date().getTime() + ".zip" ) );
      job.getJobMeta().setParameterValue( "samplesStagingDestination", this.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/staging_samples" ) );
      job.getJobMeta().setParameterValue( "samplesStagingDestinationAndDir", this.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/staging_samples/" + pluginId ) );
    }

    job.getJobMeta().setParameterValue( "downloadDestination", this.getApplicationContext()
      .getSolutionPath( "system/plugin-cache/downloads/" + pluginId + "-" + availableVersion + "_"
        + new Date().getTime() + ".zip" ) );
    job.getJobMeta().setParameterValue( "stagingDestination", this.getApplicationContext()
      .getSolutionPath( "system/plugin-cache/staging" ) );
    job.getJobMeta().setParameterValue( "stagingDestinationAndDir", this.getApplicationContext()
      .getSolutionPath( "system/plugin-cache/staging/" + pluginId ) );
    job.getJobMeta().setParameterValue( "targetDestination", this.getApplicationContext()
      .getSolutionPath( "system/" + pluginId ) );
    job.getJobMeta().setParameterValue( "targetBackup", this.getApplicationContext()
      .getSolutionPath( "system/plugin-cache/backups/" + pluginId + "_" + new Date().getTime() ) );

    job.copyParametersFrom( job.getJobMeta() );
    job.setLogLevel( LogLevel.DETAILED );
    job.activateParameters();
    job.start();
    job.waitUntilFinished();
    Result result = job.getResult(); // Execute the selected job.

    return result;
  }

  private IDomainStatusMessage uninstallPluginAux( String pluginId ) throws MarketplaceSecurityException {

    if ( !hasMarketplacePermission() ) {
      throw new MarketplaceSecurityException();
    }

    Iterable<IPlugin> plugins = getPlugins();
    IPlugin toUninstall = null;

    for ( IPlugin plugin : plugins ) {
      if ( plugin.getId().equals( pluginId ) ) {
        toUninstall = plugin;
      }
    }
    if ( toUninstall == null ) {
      return this.domainStatusMessageFactory.create( PluginService.NO_PLUGIN_ERROR_CODE, "Plugin Not Found" );
    }

    // before deletion, close class loader
    this.closeClassLoader( toUninstall.getId() );


    try {
      Result result = this.executeUninstallPluginJob( toUninstall.getId() );

      if ( result == null || result.getNrErrors() > 0 ) {
        return this.domainStatusMessageFactory
          .create( PluginService.FAIL_ERROR_CODE, "Failed to execute uninstall, see log for details." );
      }
    } catch ( KettleException e ) {
      logger.error( e.getMessage(), e );
    }


    BaPluginTelemetry telemetryEvent = new BaPluginTelemetry( PLUGIN_NAME );
    Map<String, String> extraInfo = new HashMap<String, String>( 1 );
    extraInfo.put( "uninstalledPlugin", toUninstall.getId() );
    extraInfo.put( "uninstalledPluginVersion", toUninstall.getInstalledVersion() );
    extraInfo.put( "uninstalledPluginBranch", toUninstall.getInstalledBranch() );
    telemetryEvent.sendTelemetryRequest( TelemetryHelper.TelemetryEventType.REMOVAL, extraInfo );

    return this.domainStatusMessageFactory.create( PluginService.PLUGIN_UNINSTALLED_CODE, toUninstall.getName()
      + " was successfully uninstalled.  Please restart your BI Server." );
  }

  private Result executeUninstallPluginJob( String pluginId )
    throws KettleXMLException, UnknownParamException {
    // get plugin path
    String jobPath = this.getApplicationContext().getSolutionPath( "system/" + PLUGIN_NAME
        + "/processes/uninstall_plugin.kjb" );

    JobMeta uninstallJobMeta = new JobMeta( jobPath, null );
    Job job = new Job( null, uninstallJobMeta );

    File file = new File( this.getApplicationContext().getSolutionPath( "system/plugin-cache/backups" ) );
    file.mkdirs();

    String uninstallBackup = this.getApplicationContext().getSolutionPath( "system/plugin-cache/backups/"
      + pluginId + "_" + new Date().getTime() );
    job.getJobMeta().setParameterValue( "uninstallLocation", this.getApplicationContext()
        .getSolutionPath( "system/" + pluginId ) );
    job.getJobMeta().setParameterValue( "uninstallBackup", uninstallBackup );
    job.getJobMeta().setParameterValue( "samplesDir", "/public/plugin-samples/" + pluginId );

    job.copyParametersFrom( job.getJobMeta() );
    job.activateParameters();
    job.start();
    job.waitUntilFinished();
    Result result = job.getResult(); // Execute the selected job.

    return result;
  }
  //endregion

  //region IPluginService implementation
  @Override
  public Collection<IPlugin> getPlugins() {
    Collection<IPlugin> marketplacePlugins = this.getMetadataPluginsProvider().getPlugins();

    Collection<IPlugin> compatiblePlugins = this.removeNonCompatibleVersions( marketplacePlugins );

    // There are 2 methods of getting installed plugins.
    // 1 ) Plugin manager
    // 2 ) Scan file system
    // We'll use 2 ) because 1 ) gets totally screwed up after we do an install/uninstall operation

    // List<String> installedPlugins = getInstalledPluginsFromPluginManager(  );
    Collection<String> installedPluginIds = getInstalledPluginIdsFromFileSystem();
    Collection<IPlugin> installedPlugins = this.filterPlugins( compatiblePlugins, installedPluginIds );
    this.setPluginsAsInstalled( installedPlugins );

    return compatiblePlugins;
  }

  @Override
  public IDomainStatusMessage installPlugin( String pluginId, String versionBranch ) {

    try {
      IDomainStatusMessage msg = this.installPluginAux( pluginId, versionBranch );
      return msg;
    } catch ( MarketplaceSecurityException e ) {
      logger.debug( e.getMessage(), e );
      return this.domainStatusMessageFactory.create( PluginService.UNAUTHORIZED_ACCESS_ERROR_CODE,
        PluginService.UNAUTHORIZED_ACCESS_MESSAGE );
    }
  }

  @Override
  public IDomainStatusMessage uninstallPlugin( String pluginId ) {

    try {
      IDomainStatusMessage msg = uninstallPluginAux( pluginId );
      return msg;
    } catch ( MarketplaceSecurityException e ) {
      logger.debug( e.getMessage(), e );
      return this.domainStatusMessageFactory.create( PluginService.UNAUTHORIZED_ACCESS_ERROR_CODE,
        PluginService.UNAUTHORIZED_ACCESS_MESSAGE );
    }
  }
  //endregion

}
