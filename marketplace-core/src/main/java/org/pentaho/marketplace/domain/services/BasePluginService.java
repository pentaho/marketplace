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
 * Copyright (c) 2015 - 2017 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.marketplace.domain.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.kar.KarService;

import org.apache.commons.io.FileUtils;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.interfaces.IPluginProvider;
import org.pentaho.marketplace.domain.services.interfaces.IPluginService;
import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;
import org.pentaho.telemetry.ITelemetryService;
import org.pentaho.telemetry.TelemetryEvent;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class BasePluginService implements IPluginService {

  //region Inner Definitions
  protected static class MarketplaceSecurityException extends Exception {

    private static final long serialVersionUID = -1852471739131561628L;
  }
  //endregion

  //region Constants

  // Error messages codes should begin with ERROR
  protected static final String UNAUTHORIZED_ACCESS_MESSAGE = "Unauthorized Access.";
  protected static final String UNAUTHORIZED_ACCESS_ERROR_CODE = "ERROR_0002_UNAUTHORIZED_ACCESS";
  protected static final String NO_PLUGIN_ERROR_CODE = "ERROR_0001_NO_PLUGIN";
  protected static final String FAIL_ERROR_CODE = "ERROR_0003_FAIL";
  protected static final String PLUGIN_INSTALLED_CODE = "PLUGIN_INSTALLED";
  protected static final String PLUGIN_UNINSTALLED_CODE = "PLUGIN_UNINSTALLED";

  protected static final String KARAF_FEATURES_CONFIG_PID = "org.apache.karaf.features";
  protected static final String KARAF_FEATURES_BOOT_PROPERTY_ID = "featuresBoot";
  protected static final String PENTAHO_FEATURES_CONFIG_PID = "org.pentaho.features";
  protected static final String PENTAHO_RUNTIME_FEATURES_PROPERTY_ID = "runtimeFeatures";

  //endregion

  //region Properties

  //region logger
  protected Log getLogger() {
    return this.logger;
  }
  protected Log logger = LogFactory.getLog( this.getClass() );
  //endregion

  //region metadataPluginsProvider
  public IPluginProvider getMetadataPluginsProvider() {
    return this.metadataPluginsProvider;
  }

  protected BasePluginService setMetadataPluginsProvider( IPluginProvider provider ) {
    this.metadataPluginsProvider = provider;
    return this;
  }

  private IPluginProvider metadataPluginsProvider;
  //endregion

  //region versionDataFactory
  public IVersionDataFactory getVersionDataFactory() {
    return this.versionDataFactory;
  }

  protected void setVersionDataFactory(
    IVersionDataFactory versionDataFactory ) {
    this.versionDataFactory = versionDataFactory;
  }

  private IVersionDataFactory versionDataFactory;
  //endregion

  //region pluginVersionFactory
  public IPluginVersionFactory getPluginVersionFactory() {
    return pluginVersionFactory;
  }

  protected void setPluginVersionFactory( IPluginVersionFactory pluginVersionFactory ) {
    this.pluginVersionFactory = pluginVersionFactory;
  }

  private IPluginVersionFactory pluginVersionFactory;
  //endregion

  //region karService
  public KarService getKarService() {
    return this.karService;
  }

  protected void setKarService( KarService karService ) {
    this.karService = karService;
  }

  private KarService karService;
  //endregion karService

  //region featureService
  public FeaturesService getFeaturesService() {
    return this.featuresService;
  }

  protected void setFeaturesService( FeaturesService featuresService ) {
    this.featuresService = featuresService;
  }

  private FeaturesService featuresService;
  //endregion

  //region telemetryService
  public ITelemetryService getTelemetryService() {
    return this.telemetryService;
  }

  protected void setTelemetryService( ITelemetryService telemetryService ) {
    this.telemetryService = telemetryService;
  }

  private ITelemetryService telemetryService;
  //endregion

  //region getDomainStatusMessageFactory
  public IDomainStatusMessageFactory getDomainStatusMessageFactory() {
    return this.domainStatusMessageFactory;
  }

  protected BasePluginService setDomainStatusMessageFactory( IDomainStatusMessageFactory domainStatusMessageFactory ) {
    this.domainStatusMessageFactory = domainStatusMessageFactory;
    return this;
  }

  private IDomainStatusMessageFactory domainStatusMessageFactory;
  //endregion

  //region serverVersion
  protected String getServerVersion() {
    return this.serverVersion;
  }

  protected BasePluginService setServerVersion( String serverVersion ) {
    this.serverVersion = serverVersion;
    return this;
  }

  private String serverVersion;
  //endregion

  protected ConfigurationAdmin getConfigurationAdmin() {
    return this.configurationAdmin;
  }
  protected BasePluginService setConfigurationAdmin( ConfigurationAdmin configurationAdmin ) {
    this.configurationAdmin = configurationAdmin;
    return this;
  }
  private ConfigurationAdmin configurationAdmin;
  //endregion

  //region Constructors
  protected BasePluginService( IRemotePluginProvider metadataPluginsProvider,
                               IVersionDataFactory versionDataFactory,
                               IPluginVersionFactory pluginVersionFactory,
                               KarService karService,
                               FeaturesService featuresService,
                               ConfigurationAdmin configurationAdmin,
                               ITelemetryService telemetryService,
                               IDomainStatusMessageFactory domainStatusMessageFactory
  ) {
    //initialize dependencies
    this.setMetadataPluginsProvider( metadataPluginsProvider );
    this.setVersionDataFactory( versionDataFactory );
    this.setPluginVersionFactory( pluginVersionFactory );
    this.setKarService( karService );
    this.setFeaturesService( featuresService );
    this.setConfigurationAdmin( configurationAdmin );
    this.setTelemetryService( telemetryService );
    this.setDomainStatusMessageFactory( domainStatusMessageFactory );
  }
  //endregion

  //region Methods

  private boolean withinParentVersion( IPluginVersion pv ) {
    // need to compare plugin version min and max parent with system version.
    // replace the version of the xml url path with the current release version:
    String v = this.getServerVersion();
    IVersionData pvMax = this.getVersionDataFactory().create( pv.getMaxParentVersion() );
    IVersionData pvMin = this.getVersionDataFactory().create( pv.getMinParentVersion() );
    IVersionData version = this.getVersionDataFactory().create( v );

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
   */
  private Map<String, IPlugin> removeNonCompatibleVersions( Iterable<IPlugin> plugins ) {
    Map<String, IPlugin> pluginsWithCompatibleVersions = new HashMap<>();

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

  public IPlugin getPlugin( String id ) {
    return this.getPlugins().get( id );
  }

  // TODO: only allows one version per branch
  public IPluginVersion getPluginVersion( IPlugin plugin, String versionBranch ) {
    if ( versionBranch != null && versionBranch.length() > 0 ) {
      return plugin.getVersionByBranch( versionBranch );
    }
    return null;
  }

  /**
   * Filters plugins by plugin id
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
   * @param plugins the plugins to be marked as installed
   */
  private void setPluginsAsInstalled( Collection<IPlugin> plugins ) {
    for ( IPlugin plugin : plugins ) {
      plugin.setInstalled( true );
      IPluginVersion installedVersion = getInstalledPluginVersion( plugin );
      if ( installedVersion != null ) {
        plugin.setInstalledBranch( installedVersion.getBranch() );
        plugin.setInstalledVersion( installedVersion.getVersion() );
        plugin.setInstalledBuildId( installedVersion.getBuildId() );
      }
    }
  }


  private void publishTelemetryEvent( TelemetryEvent.Type eventType, IPlugin plugin, IPluginVersion version ) {
    try {
      ITelemetryService telemetryService = this.getTelemetryService();
      TelemetryEvent event = telemetryService.createEvent( eventType );
      event.getExtraInfo().put( "installedPlugin", plugin.getId() );
      event.getExtraInfo().put( "installedBranch", version.getBranch() );
      event.getExtraInfo().put( "installedVersion", version.getVersion() );
      telemetryService.publishEvent( event );
    } catch ( NoClassDefFoundError e ) {
      this.getLogger().debug( "Failed to find class definitions. Most likely reason is reinstalling marketplace.", e );
    }
  }

  private IDomainStatusMessage upgradePluginAux( String pluginId, String versionBranch ) {

    if ( !hasMarketplacePermission() ) {
      return this.getDomainStatusMessageFactory()
        .create( UNAUTHORIZED_ACCESS_ERROR_CODE, UNAUTHORIZED_ACCESS_MESSAGE );
    }

    if ( !isPluginIdValid( pluginId ) ) {
      return this.getDomainStatusMessageFactory()
        .create( NO_PLUGIN_ERROR_CODE, "Invalid plugin id" );
    }

    IPlugin plugin = this.getPlugin( pluginId );
    if ( plugin == null ) {
      return this.getDomainStatusMessageFactory()
        .create( NO_PLUGIN_ERROR_CODE, "Plugin not found" );
    }

    IPluginVersion pluginVersionToInstall = plugin.getVersionByBranch( versionBranch );
    if ( pluginVersionToInstall == null ) {
      return this.getDomainStatusMessageFactory()
        .create( NO_PLUGIN_ERROR_CODE, "Plugin version for branch " + versionBranch + " not found" );
    }

    // Perhaps we are reinstalling the marketplace.
    // Create telemetry event and messages before closing class loader just in case.
    ITelemetryService telemetryService = this.getTelemetryService();
    TelemetryEvent event = telemetryService.createEvent( TelemetryEvent.Type.UPGRADE );
    event.getExtraInfo().put( "installedPlugin", plugin.getId() );
    event.getExtraInfo().put( "installedVersion", pluginVersionToInstall.getVersion() );
    event.getExtraInfo().put( "installedBranch", versionBranch );

    IDomainStatusMessage successMessage =
            this.domainStatusMessageFactory.create( PLUGIN_INSTALLED_CODE, plugin.getName()
                    + " was successfully Upgraded.  Please restart. \n" + plugin.getInstallationNotes() );

    IDomainStatusMessage upgradeInstallFailureMessage = this.domainStatusMessageFactory
            .create(FAIL_ERROR_CODE, "Failed to install on plugin upgrade, see log for details.");

    IDomainStatusMessage upgradeUninstallFailureMessage = this.domainStatusMessageFactory
            .create(FAIL_ERROR_CODE, "Failed to uninstall on plugin upgrade, see log for details.");


    // it's an upgrade, uninstall old version first
    if ( !this.executeUninstall(plugin) ) {
      return upgradeUninstallFailureMessage;
    }

    // install new version
    if ( !this.executeInstall( plugin, pluginVersionToInstall ) ) {
      return upgradeInstallFailureMessage;
    }

    try {
      telemetryService.publishEvent( event );
    } catch ( NoClassDefFoundError e ) {
      this.getLogger().debug( "Failed to find class definitions. Most likely reason is reinstalling marketplace.", e );
    }

    return successMessage;
  }

  private IDomainStatusMessage installPluginAux( String pluginId, String versionBranch )
    throws MarketplaceSecurityException {

    if ( !hasMarketplacePermission() ) {
      throw new MarketplaceSecurityException();
    }

    if ( !isPluginIdValid( pluginId ) ) {
      return this.domainStatusMessageFactory.create( NO_PLUGIN_ERROR_CODE, "Invalid Plugin Id." );
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

    // Perhaps we are reinstalling the marketplace.
    // Create telemetry event and messages before closing class loader just in case.
    ITelemetryService telemetryService = this.getTelemetryService();
    TelemetryEvent event = telemetryService.createEvent( TelemetryEvent.Type.INSTALLATION );
    event.getExtraInfo().put( "installedPlugin", toInstall.getId() );
    event.getExtraInfo().put( "installedVersion", versionToInstall.getVersion() );
    event.getExtraInfo().put( "installedBranch", versionBranch );

    IDomainStatusMessage successMessage =
      this.domainStatusMessageFactory.create( PLUGIN_INSTALLED_CODE, toInstall.getName()
        + " was successfully installed.  Please restart your BI Server. \n" + toInstall.getInstallationNotes() );

    IDomainStatusMessage failureMessage = this.domainStatusMessageFactory
      .create( FAIL_ERROR_CODE, "Failed to execute install, see log for details." );

    if ( !this.executeInstall( toInstall, versionToInstall ) ) {
      return failureMessage;
    }

    try {
      telemetryService.publishEvent( event );
    } catch ( NoClassDefFoundError e ) {
      this.getLogger().debug( "Failed to find class definitions. Most likely reason is reinstalling marketplace.", e );
    }

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

    // Perhaps we are uninstalling the marketplace.
    // Create telemetry event and messages before closing class loader just in case.
    ITelemetryService telemetryService = this.getTelemetryService();
    TelemetryEvent event = telemetryService.createEvent( TelemetryEvent.Type.INSTALLATION );
    event.getExtraInfo().put( "uninstalledPlugin", toUninstall.getId() );
    event.getExtraInfo().put( "uninstalledPluginVersion", toUninstall.getInstalledVersion() );
    event.getExtraInfo().put( "uninstalledPluginBranch", toUninstall.getInstalledBranch() );

    IDomainStatusMessage successMessage =
      this.domainStatusMessageFactory.create( PLUGIN_UNINSTALLED_CODE, toUninstall.getName()
        + " was successfully uninstalled.  Please restart your BI Server." );

    IDomainStatusMessage failureMessage = this.domainStatusMessageFactory
      .create( FAIL_ERROR_CODE, "Failed to execute uninstall, see log for details." );

    if ( !this.executeUninstall( toUninstall ) ) {
      return failureMessage;
    }

    try {
      telemetryService.publishEvent( event );
    } catch ( NoClassDefFoundError e ) {
      this.getLogger().debug( "Failed to find class definitions. Most likely reason is uninstalling marketplace.", e );
    }

    return successMessage;
  }
  //endregion

  //region IPluginService implementation
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
      IPlugin plugin = this.getPlugin( pluginId );

      if ( plugin != null && plugin.isInstalled() ) {
        return this.upgradePluginAux( pluginId, versionBranch );
      } else {
        return this.installPluginAux( pluginId, versionBranch );
      }

    } catch ( MarketplaceSecurityException e ) {
      this.getLogger().debug( e.getMessage(), e );
      return this.domainStatusMessageFactory.create( UNAUTHORIZED_ACCESS_ERROR_CODE, UNAUTHORIZED_ACCESS_MESSAGE );
    }
  }

  @Override
  public IDomainStatusMessage uninstallPlugin( String pluginId ) {
    try {
      return uninstallPluginAux( pluginId );
    } catch ( MarketplaceSecurityException e ) {
      this.getLogger().debug( e.getMessage(), e );
      return this.domainStatusMessageFactory.create( UNAUTHORIZED_ACCESS_ERROR_CODE, UNAUTHORIZED_ACCESS_MESSAGE );
    }
  }
  //endregion

  protected boolean executeOsgiInstallViaKarService( IPlugin plugin, IPluginVersion versionToInstall ) {
    if ( versionToInstall.isOsgi() ) {
      this.getLogger().debug( "## Install Osgi Plugin ##" );
      try {
        URI uri = new URL( versionToInstall.getDownloadUrl() ).toURI();
        this.getKarService().install( uri );
        // TODO: check if it was successful or not
        return true;
      } catch ( Exception e ) {
        this.getLogger().warn( "Failed to install OSGi plugin.", e );
        return false;
      }
    }
    return false;
  }

  private String getKarafDeployFolder() {
    //TODO: hardcoded deploy folder. Check for better alternative
    return System.getProperty( "karaf.base" ) + File.separator + "deploy";
  }

  protected boolean executeOsgiInstall( IPlugin plugin, IPluginVersion versionToInstall ) {
    if ( versionToInstall.isOsgi() ) {
      this.getLogger().debug( "Installing Osgi Plugin " + plugin.getId() );
      try {
        String deployFolderName = this.getKarafDeployFolder();
        String downloadUrl = versionToInstall.getDownloadUrl();
        //String karName = FilenameUtils.getName( downloadUrl );
        File dlKarFile = new File( deployFolderName + File.separator + plugin.getId() + ".kar" );
        FileUtils.copyURLToFile( new URL( downloadUrl ), dlKarFile );

        // TODO: check if it was successful or not
        return true;
      } catch ( Exception e ) {
        this.getLogger().warn( "Failed to install OSGi plugin " + plugin.getId(), e );
        return false;
      }
    }
    return false;
  }


  protected boolean executeOsgiUninstallViaKarService( IPlugin plugin ) {
    this.getLogger().debug( "Uninstalling Osgi Plugin " + plugin.getId() );
    try {
      this.getKarService().uninstall( plugin.getId() );
      // TODO: check if it was successful or not
      return true;
    } catch ( Exception e ) {
      this.getLogger().warn( "Failed to uninstall OSGi plugin " + plugin.getId(), e );
      return false;
    }
  }

  protected boolean executeOsgiUninstall( IPlugin plugin ) {
    String pluginId = plugin.getId();
    this.removeFeatureFromKarafBoot( pluginId );
    try {
      this.getFeaturesService().uninstallFeature( pluginId );
    } catch ( Exception e ) {
      this.getLogger().debug( "No installed feature found with name " + pluginId + " when uninstalling OSGI plugin." );
    }

    // remove KAR file from deploy folder if it exists
    String deployFolder = this.getKarafDeployFolder();
    File karFile = new File( deployFolder + File.separator + pluginId + ".kar" );
    if( karFile.exists() && karFile.isFile() ) {
      return FileUtils.deleteQuietly( karFile );
    }
    return true;
  }

  private void removeFeatureFromKarafBoot( String featureName ) {
    this.removeFeatureFromKarafBoot( featureName, KARAF_FEATURES_CONFIG_PID, KARAF_FEATURES_BOOT_PROPERTY_ID );
    this.removeFeatureFromKarafBoot( featureName, PENTAHO_FEATURES_CONFIG_PID, PENTAHO_RUNTIME_FEATURES_PROPERTY_ID );
  }
  
  private void removeFeatureFromKarafBoot( String featureName, String configurationPid, String propertyId ) {
    ConfigurationAdmin configurationAdmin = this.getConfigurationAdmin();
    Log logger = this.getLogger();

    try {
      Configuration configuration = configurationAdmin.getConfiguration( configurationPid );
      Dictionary<String, Object> properties = configuration.getProperties();
      if( properties == null ) {
        logger.debug( "Configuration " + configurationPid + " has no properties." );
        return;
      }
      String propertyValue = (String) properties.get( propertyId );
      if( propertyValue == null ) {
        logger.debug( "Property " + propertyId + " not set in configuration " + configurationPid + "." );
        return;
      }

      String newPropertyValue = propertyValue.replaceFirst("," + featureName, "");
      if( !propertyValue.equals( newPropertyValue ) ) {
        properties.put( propertyId, newPropertyValue );
        configuration.update( properties );
      }
    } catch ( IOException e ) {
      logger.debug( "Unable to access configuration " + configurationPid + "." );
    }
  }

  private IPluginVersion getInstalledOsgiPluginVersion( IPlugin plugin ) {
    this.getLogger().debug( "Infer Version from installed Osgi Plugin" );
    // search installed features for plugin id
    IPluginVersion installedOsgiPluginVersion = this.getInstalledOsgiPluginVersionFromFeatures( plugin );
    if( installedOsgiPluginVersion == null ) {
      // If no feature with the plugin id is found, check installed KARs
      installedOsgiPluginVersion = this.getInstalledOsgiPluginVersionFromKars( plugin );
    }
    return installedOsgiPluginVersion;
  }

  private IPluginVersion getInstalledOsgiPluginVersionFromKars( IPlugin plugin ) {
    try {
      if ( this.getKarService().list().contains( plugin.getId() ) ) {
        IPluginVersion installedPluginVersion = this.getPluginVersionFactory().create();
        installedPluginVersion.setIsOsgi( true );
        // TODO: add branch / version / buildId information that currently is not available
        return installedPluginVersion;
      }
    } catch ( Exception e ) {
      return null;
    }
    return null;
  }

    private IPluginVersion getInstalledOsgiPluginVersionFromFeatures( IPlugin plugin ) {
    this.getLogger().debug( "## Infer Version from Karaf features ##" );
    try {
      String pluginId = plugin.getId();
      Feature feature = this.getFeaturesService().getFeature( pluginId );
      if ( feature != null ) {
        IPluginVersion installedPluginVersion = this.getPluginVersionFactory().create();
        installedPluginVersion.setIsOsgi( true );
        // installedPluginVersion.setName( feature.getName() );
        installedPluginVersion.setVersion( feature.getVersion() );
        installedPluginVersion.setBranch( null );
        installedPluginVersion.setBuildId( null );

        return installedPluginVersion;
      }
    } catch ( Exception e ) {
      this.getLogger().warn( "Failed to infer version of installed OSGi plugin from features.", e );
      return null;
    }
    return null;
  }


  private IPluginVersion getInstalledPluginVersion( IPlugin plugin ) {
    IPluginVersion osgiPluginVersion = this.getInstalledOsgiPluginVersion( plugin );
    if( osgiPluginVersion != null ) {
        return osgiPluginVersion;
    } else {
        return this.getInstalledNonOsgiPluginVersion( plugin );
    }
  }

  private Collection<String> getInstalledOsgiPluginIds() {
    Collection<String> potentialOsgiPluginIds = new HashSet<>();

    try {
      for( String installedKar : this.getKarService().list() ) {
       potentialOsgiPluginIds.add( installedKar );
      }
    } catch ( Exception e ) { }

    for( Feature feature : this.getFeaturesService().listInstalledFeatures() ) {
      potentialOsgiPluginIds.add( feature.getName() );
    }
    return potentialOsgiPluginIds;
  }

  private Collection<String> getInstalledPluginIds() {
    Collection<String> installedPluginIds = this.getInstalledOsgiPluginIds();
    installedPluginIds.addAll( this.getInstalledNonOsgiPluginIds() );

    return installedPluginIds;
  }


  private boolean executeInstall( IPlugin plugin, IPluginVersion version ) {
    if ( version.isOsgi() ) {
      return this.executeOsgiInstall( plugin, version );
    } else {
      // before install, close class loader in case it's a reinstall
      this.unloadPlugin( plugin );
      return this.executeNonOsgiInstall( plugin, version );
    }
  }

  private boolean executeUninstall( IPlugin plugin ) {
    IPluginVersion version =  this.getInstalledPluginVersion( plugin );
    if ( version == null ) {
      this.getLogger().debug( "Did not find plugin version for installed plugin: " + plugin.getId() );
      return false;
    }

    if ( version.isOsgi() ) {
      return this.executeOsgiUninstall( plugin );
    } else {
      // before install, close class loader in case it's a reinstall
      this.unloadPlugin( plugin );
      return this.executeNonOsgiUninstall( plugin );
    }
  }


  protected abstract boolean hasMarketplacePermission();

  protected abstract void unloadPlugin( IPlugin pluginId );

  protected abstract boolean executeNonOsgiInstall( IPlugin plugin, IPluginVersion version );

  protected abstract boolean executeNonOsgiUninstall( IPlugin plugin );

  protected abstract IPluginVersion getInstalledNonOsgiPluginVersion( IPlugin plugin );

  protected abstract Collection<String> getInstalledNonOsgiPluginIds();
}
