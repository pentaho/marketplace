package org.pentaho.marketplace.domain.services;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.helpers.Util;
import org.pentaho.marketplace.domain.services.interfaces.IPluginService;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;
import org.pentaho.platform.util.web.HttpUtil;
import org.pentaho.telemetry.BaPluginTelemetry;
import org.pentaho.telemetry.TelemetryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PluginService implements IPluginService {

  //region Inner Definitions
  private static class MarketplaceSecurityException extends Exception {

    private static final long serialVersionUID = -1852471739131561628L;
  }
  //endregion

  //region Constants
  private static final String UNAUTHORIZED_ACCESS_MESSAGE =
    "Unauthorized Access. Your Pentaho roles do not allow you to make changes to plugins.";
  private static final String UNAUTHORIZED_ACCESS_ERROR_CODE =
    "ERROR_0002_UNAUTHORIZED_ACCESS";
  private static final String NO_PLUGIN_ERROR_CODE = "NO_PLUGIN";
  private static final String FAIL_ERROR_CODE = "FAIL";
  private static final String PLUGIN_INSTALLED_CODE = "PLUGIN_INSTALLED";
  private static final String PLUGIN_UNINSTALLED_CODE = "PLUGIN_UNINSTALLED";
  private static final String CLOSE_METHOD_NAME = "close";
  private static final String PLUGIN_NAME = "marketplace";
  //endregion

  //region Attributes
  private Log logger = LogFactory.getLog( this.getClass() );
  private XPath xpath;
  private IPluginFactory pluginFactory;
  private IPluginVersionFactory pluginVersionFactory;
  private IVersionDataFactory versionDataFactory;
  private IDomainStatusMessageFactory domainStatusMessageFactory;
  //endregion

  //region Constructors
  @Autowired
  public PluginService( IPluginFactory pluginFactory,
                        IPluginVersionFactory pluginVersionFactory,
                        IVersionDataFactory versionDataFactory,
                        IDomainStatusMessageFactory domainStatusMessageFactory ) {

    //initialize dependencies
    this.pluginFactory = pluginFactory;
    this.pluginVersionFactory = pluginVersionFactory;
    this.versionDataFactory = versionDataFactory;
    this.domainStatusMessageFactory = domainStatusMessageFactory;

    this.xpath = XPathFactory.newInstance().newXPath();
  }
  //endregion

  //region Methods
  private void closeClassLoader( String pluginId ) {
    IPluginManager pluginManager = PentahoSystem.get( IPluginManager.class, PentahoSessionHolder.getSession() );
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

    Authentication auth = SecurityHelper.getInstance().getAuthentication( PentahoSessionHolder.getSession(), true );
    IPluginResourceLoader resLoader = PentahoSystem.get( IPluginResourceLoader.class, null );
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
      return SecurityHelper.getInstance().isPentahoAdministrator( PentahoSessionHolder.getSession() );
    }

    String[] roleArr = roles.split( "," ); //$NON-NLS-1$

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

  private String discoverInstalledVersion( IPlugin plugin ) {

    String versionPath = PentahoSystem.getApplicationContext().getSolutionPath( "system/" + plugin.getId()
      + "/version.xml" );
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    FileReader reader = null;
    try {
      File file = new File( versionPath );
      if ( !file.exists() ) {
        return "Unknown";
      }
      DocumentBuilder db = dbf.newDocumentBuilder();
      reader = new FileReader( versionPath );
      Document dom = db.parse( new InputSource( reader ) );
      NodeList versionElements = dom.getElementsByTagName( "version" );
      if ( versionElements.getLength() >= 1 ) {
        Element versionElement = (Element) versionElements.item( 0 );

        plugin.setInstalledBuildId( versionElement.getAttribute( "buildId" ) );
        plugin.setInstalledBranch( versionElement.getAttribute( "branch" ) );
        plugin.setInstalledVersion( versionElement.getTextContent() );

        return versionElement.getTextContent();
      }
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
    return "Unknown";
  }

  private Collection<String> getInstalledPluginsFromFileSystem() {

    Collection<String> plugins = new ArrayList<String>();

    File systemDir = new File( PentahoSystem.getApplicationContext().getSolutionPath( "system/" ) );

    String[] dirs = systemDir.list( DirectoryFileFilter.INSTANCE );

    for ( int i = 0; i < dirs.length; i++ ) {
      String dir = dirs[ i ];
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
    VersionInfo versionInfo = VersionHelper.getVersionInfo( PentahoSystem.class );
    String v = versionInfo.getVersionNumber();
    IVersionData pvMax = this.versionDataFactory.create( pv.getMaxParentVersion() );
    IVersionData pvMin = this.versionDataFactory.create( pv.getMinParentVersion() );
    IVersionData version = this.versionDataFactory.create( v );

    return version.within( pvMin, pvMax );
  }

  private String getElementChildValue( Element element, String child ) {
    NodeList list = element.getElementsByTagName( child );
    if ( list.getLength() >= 1 ) {
      return list.item( 0 ).getTextContent();
    } else {
      return null;
    }
  }

  private String getMarketplaceSiteContent() {
    IPluginResourceLoader resLoader = PentahoSystem.get( IPluginResourceLoader.class, null );
    String site = null;
    try {
      site = resLoader.getPluginSetting( getClass(), "settings/marketplace-site" ); //$NON-NLS-1$
    } catch ( Exception e ) {
      logger.debug( "Error getting data access plugin settings", e );
    }

    if ( site == null || "".equals( site ) ) {

      site = "https://raw.github.com/pentaho/marketplace-metadata/master/marketplace.xml";

    }

    return HttpUtil.getURLContent( site );
  }

  private Collection<IPlugin> loadPluginsFromSite() {
    String content = getMarketplaceSiteContent();
    //Sometimes this call fails. Second attemp is always succesfull
    if ( StringUtils.isEmpty( content ) ) {
      content = getMarketplaceSiteContent();
    }
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document dom = db.parse( new InputSource( new StringReader( content ) ) );
      NodeList plugins = dom.getElementsByTagName( "market_entry" );
      Collection<IPlugin> pluginList = new ArrayList<IPlugin>();
      for ( int i = 0; i < plugins.getLength(); i++ ) {
        Element element = (Element) plugins.item( i );
        String type = getElementChildValue( element, "type" );
        if ( !"Platform".equals( type ) ) {
          continue;
        }

        IPlugin plugin = this.pluginFactory.create();
        plugin.setId( getElementChildValue( element, "id" ) );
        plugin.setName( getElementChildValue( element, "name" ) );
        plugin.setDescription( getElementChildValue( element, "description" ) );

        plugin.setAuthorName( getElementChildValue( element, "author" ) );
        plugin.setAuthorUrl( getElementChildValue( element, "author_url" ) );
        plugin.setAuthorLogo( getElementChildValue( element, "author_logo" ) );
        plugin.setImg( getElementChildValue( element, "img" ) );

        plugin.setSmallImg( getElementChildValue( element, "small_img" ) );
        plugin.setDocumentationUrl( getElementChildValue( element, "documentation_url" ) );
        plugin.setInstallationNotes( getElementChildValue( element, "installation_notes" ) );
        plugin.setLicense( getElementChildValue( element, "license" ) );
        plugin.setLicenseName( getElementChildValue( element, "license_name" ) );
        plugin.setLicenseText( getElementChildValue( element, "license_text" ) );
        plugin.setDependencies( getElementChildValue( element, "dependencies" ) );


        //NodeList availableVersions = element.getElementsByTagName( "version" );
        NodeList availableVersions =
          (NodeList) this.xpath.evaluate( "versions/version", element, XPathConstants.NODESET );

        if ( availableVersions.getLength() > 0 ) {
          Collection<IPluginVersion> versions = new ArrayList<IPluginVersion>();
          for ( int j = 0; j < availableVersions.getLength(); j++ ) {
            Element versionElement = (Element) availableVersions.item( j );
            IPluginVersion pv = this.pluginVersionFactory.create();
            pv.setBranch( getElementChildValue( versionElement, "branch" ) );
            pv.setName( getElementChildValue( versionElement, "name" ) );
            pv.setVersion( getElementChildValue( versionElement, "version" ) );
            pv.setDownloadUrl( getElementChildValue( versionElement, "package_url" ) );
            pv.setSamplesDownloadUrl( getElementChildValue( versionElement, "samples_url" ) );
            pv.setDescription( getElementChildValue( versionElement, "description" ) );
            pv.setChangelog( getElementChildValue( versionElement, "changelog" ) );
            pv.setBuildId( getElementChildValue( versionElement, "build_id" ) );
            pv.setReleaseDate( getElementChildValue( versionElement, "releaseDate" ) );
            pv.setMinParentVersion( getElementChildValue( versionElement, "min_parent_version" ) );
            pv.setMaxParentVersion( getElementChildValue( versionElement, "max_parent_version" ) );
            pv.setStageLane( getElementChildValue( versionElement, "stage_lane" ) );
            pv.setStagePhase( getElementChildValue( versionElement, "stage_phase" ) );

            if ( withinParentVersion( pv ) ) {
              versions.add( pv );
            }

          }
          plugin.setVersions( versions );
        }

        NodeList availableScreenshots = (NodeList) xpath.evaluate( "screenshots/screenshot", element,
          XPathConstants.NODESET );
        if ( availableScreenshots.getLength() > 0 ) {
          String[] screenshots = new String[ availableScreenshots.getLength() ];

          for ( int j = 0; j < availableScreenshots.getLength(); j++ ) {
            Element screenshotElement = (Element) availableScreenshots.item( j );
            screenshots[ j ] = screenshotElement.getTextContent();
          }

          plugin.setScreenshots( screenshots );
        }

        // only include plugins that have versions within this release
        if ( plugin.getVersions() != null && plugin.getVersions().size() > 0 ) {
          pluginList.add( plugin );
        }
      }
      return pluginList;
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    return null;
  }

  private IDomainStatusMessage installPluginAux( String pluginId, String versionBranch )
    throws MarketplaceSecurityException {

    if ( !hasMarketplacePermission() ) {
      throw new MarketplaceSecurityException();
    }

    Iterable<IPlugin> plugins = getPlugins();
    IPlugin toInstall = null;
    for ( IPlugin plugin : plugins ) {
      if ( plugin.getId().equals( pluginId ) ) {
        toInstall = plugin;
      }
    }
    if ( toInstall == null ) {
      return this.domainStatusMessageFactory.create( PluginService.NO_PLUGIN_ERROR_CODE, "Plugin Not Found" );
    }

    // this checks to make sure the plugin metadata isn't attempting to overwrite a folder on the system.
    // TODO: Test a .. encoded in UTF8, etc to see if there is a way to thwart this check
    if ( toInstall.getId().indexOf( "." ) >= 0 ) {
      return this.domainStatusMessageFactory.create( PluginService.NO_PLUGIN_ERROR_CODE,
        "Plugin ID contains an illegal character" );
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

    // get plugin path
    String jobPath = PentahoSystem.getApplicationContext().getSolutionPath( "system/" + PluginService.PLUGIN_NAME
      + "/processes/download_and_install_plugin.kjb" );


    try {
      JobMeta installJobMeta = new JobMeta( jobPath, null );
      Job job = new Job( null, installJobMeta );

      File file = new File( PentahoSystem.getApplicationContext().getSolutionPath( "system/plugin-cache/downloads" ) );
      file.mkdirs();
      file = new File( PentahoSystem.getApplicationContext().getSolutionPath( "system/plugin-cache/backups" ) );
      file.mkdirs();
      file = new File( PentahoSystem.getApplicationContext().getSolutionPath( "system/plugin-cache/staging" ) );
      file.mkdirs();

      job.getJobMeta().setParameterValue( "downloadUrl", downloadUrl );
      if ( toInstall.getVersionByBranch( versionBranch ).getSamplesDownloadUrl() != null ) {
        job.getJobMeta().setParameterValue( "samplesDownloadUrl", samplesDownloadUrl );
        job.getJobMeta().setParameterValue( "samplesDir", "/public/plugin-samples" );
        job.getJobMeta().setParameterValue( "samplesTargetDestination", PentahoSystem.getApplicationContext()
          .getSolutionPath( "plugin-samples/" + toInstall.getId() ) );
        job.getJobMeta().setParameterValue( "samplesTargetBackup", PentahoSystem.getApplicationContext()
          .getSolutionPath( "system/plugin-cache/backups/" + toInstall.getId() + "_samples_" + new Date()
            .getTime() ) );
        job.getJobMeta().setParameterValue( "samplesDownloadDestination", PentahoSystem.getApplicationContext()
          .getSolutionPath( "system/plugin-cache/downloads/" + toInstall.getId() + "-samples-" + availableVersion
            + "_" + new Date().getTime() + ".zip" ) );
        job.getJobMeta().setParameterValue( "samplesStagingDestination", PentahoSystem.getApplicationContext()
          .getSolutionPath( "system/plugin-cache/staging_samples" ) );
        job.getJobMeta().setParameterValue( "samplesStagingDestinationAndDir", PentahoSystem.getApplicationContext()
          .getSolutionPath( "system/plugin-cache/staging_samples/" + toInstall.getId() ) );
      }
      job.getJobMeta().setParameterValue( "downloadDestination", PentahoSystem.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/downloads/" + toInstall.getId() + "-" + availableVersion + "_"
          + new Date().getTime() + ".zip" ) );
      job.getJobMeta().setParameterValue( "stagingDestination", PentahoSystem.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/staging" ) );
      job.getJobMeta().setParameterValue( "stagingDestinationAndDir", PentahoSystem.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/staging/" + toInstall.getId() ) );
      job.getJobMeta().setParameterValue( "targetDestination", PentahoSystem.getApplicationContext()
        .getSolutionPath( "system/" + toInstall.getId() ) );
      job.getJobMeta().setParameterValue( "targetBackup", PentahoSystem.getApplicationContext()
        .getSolutionPath( "system/plugin-cache/backups/" + toInstall.getId() + "_" + new Date().getTime() ) );

      job.copyParametersFrom( job.getJobMeta() );
      job.setLogLevel( LogLevel.DETAILED );
      job.activateParameters();
      job.start();
      job.waitUntilFinished();
      Result result = job.getResult(); // Execute the selected job.

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

    String versionBranch = toUninstall.getInstalledBranch();
    // get plugin path
    String jobPath = PentahoSystem.getApplicationContext().getSolutionPath( "system/" + PLUGIN_NAME
      + "/processes/uninstall_plugin.kjb" );

    try {
      JobMeta uninstallJobMeta = new JobMeta( jobPath, null );
      Job job = new Job( null, uninstallJobMeta );

      File file = new File( PentahoSystem.getApplicationContext().getSolutionPath( "system/plugin-cache/backups" ) );
      file.mkdirs();

      String uninstallBackup = PentahoSystem.getApplicationContext().getSolutionPath( "system/plugin-cache/backups/"
        + toUninstall.getId() + "_" + new Date().getTime() );
      job.getJobMeta().setParameterValue( "uninstallLocation", PentahoSystem.getApplicationContext()
        .getSolutionPath( "system/" + toUninstall.getId() ) );
      job.getJobMeta().setParameterValue( "uninstallBackup", uninstallBackup );
      job.getJobMeta().setParameterValue( "samplesDir", "/public/plugin-samples/" + toUninstall.getId() );

      job.copyParametersFrom( job.getJobMeta() );
      job.activateParameters();
      job.start();
      job.waitUntilFinished();
      Result result = job.getResult(); // Execute the selected job.

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
  //endregion

  //region IPluginService implementation
  @Override
  public Collection<IPlugin> getPlugins() {
    Collection<IPlugin> plugins = loadPluginsFromSite();


    // There are 2 methods of doing this.
    // 1 ) Plugin manager
    // 2 ) Scan file system
    // We'll use 2 ) because 1 ) gets totally screwed up after we do an install/uninstall operation

    // List<String> installedPlugins = getInstalledPluginsFromPluginManager(  );
    Collection<String> installedPlugins = getInstalledPluginsFromFileSystem();


    if ( installedPlugins.size() > 0 ) {
      Map<String, IPlugin> marketplacePlugins = new HashMap<String, IPlugin>();
      if ( plugins != null ) {
        for ( IPlugin plugin : plugins ) {
          marketplacePlugins.put( plugin.getId(), plugin );
        }
      }

      for ( String installedPlugin : installedPlugins ) {
        IPlugin plugin = marketplacePlugins.get( installedPlugin );
        if ( plugin != null ) {
          plugin.setInstalled( true );
          discoverInstalledVersion( plugin );
        }
      }
    }
    return plugins;
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
