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

package org.pentaho.marketplace.domain.services;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.kar.KarService;
import org.osgi.framework.Bundle;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.marketplace.domain.model.entities.MarketEntryType;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.serialization.IMarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.helpers.Util;
import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;

import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.ISecurityHelper;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;

import org.pentaho.telemetry.ITelemetryService;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Plugin service implementation for the BA server
 */
public class BaPluginService extends BasePluginService {

  //region Constants

  private static final String CLOSE_METHOD_NAME = "close";

  private static final String PROPERTY_COLLECTION_SEPARATOR = ",";

  private static final String INSTALL_JOB_NAME = "download_and_install_plugin.kjb";
  private static final String UNINSTALL_JOB_NAME = "uninstall_plugin.kjb";

  private static final String CACHE_FOLDER = "system/plugin-cache/";
  private static final String DOWNLOAD_CACHE_FOLDER = CACHE_FOLDER + "downloads/";
  private static final String BACKUP_CACHE_FOLDER = CACHE_FOLDER + "backups/";
  private static final String STAGING_CACHE_FOLDER = CACHE_FOLDER + "staging/";

  private static final String MARKETPLACE_FOLDER = "system/marketplace";

  private static final String SYSTEM_FOLDER = "system/";
  private static final String PLUGIN_XML_FILE = "plugin.xml";
  //endregion

  //region Properties

  public IMarketplaceXmlSerializer getXmlSerializer() {
    return this.xmlPluginsSerializer;
  }
  protected BaPluginService setXmlSerializer( IMarketplaceXmlSerializer serializer ) {
    this.xmlPluginsSerializer = serializer;
    return this;
  }
  private IMarketplaceXmlSerializer xmlPluginsSerializer;

  public ISecurityHelper getSecurityHelper() {
    return this.securityHelper;
  }
  protected BaPluginService setSecurityHelper( ISecurityHelper securityHelper ) {
    this.securityHelper = securityHelper;
    return this;
  }
  private ISecurityHelper securityHelper;


  @Override
  protected String getServerVersion() {
    if ( super.getServerVersion() == null ) {
      VersionInfo versionInfo = VersionHelper.getVersionInfo( PentahoSystem.class );
      this.setServerVersion( versionInfo.getVersionNumber() );
    }

    return super.getServerVersion();
  }

  // TODO: see if there is a better way to encapsulate this
  public IPluginManager getPluginManager( IPentahoSession session ) {
    return PentahoSystem.get( IPluginManager.class, session );
  }

  // TODO: see if there is a better way to encapsulate this
  protected IApplicationContext getApplicationContext() {
    if ( this.applicationContext == null ) {
      return PentahoSystem.getApplicationContext();
    }

    return this.applicationContext;
  }
  protected BaPluginService setApplicationContext( IApplicationContext applicationContext ) {
    this.applicationContext = applicationContext;
    return this;
  }
  private IApplicationContext applicationContext;


  // TODO: see if there is a better way to encapsulate this.
  // Probably just pass in the session in the methods that require it.
  protected IPentahoSession getCurrentSession() {
    return PentahoSessionHolder.getSession();
  }
  protected BaPluginService setCurrentSession( IPentahoSession session ) {
    PentahoSessionHolder.setSession( session );
    return this;
  }

  /**
   * Gets the Roles which are authorized to install / unintall plugins
   * @return
   */
  public Collection<String> getAuthorizedRoles() {
    return this.authorizedRoles;
  }
  /**
   * Sets the Roles which are authorized to install / unintall plugins
   * @return
   */
  public void setAuthorizedRoles( Collection<String> authorizedRoles ) {
    if ( authorizedRoles == null ) {
      authorizedRoles = Collections.emptyList();
    }
    this.authorizedRoles = authorizedRoles;
  }
  private Collection<String> authorizedRoles = Collections.emptyList();

  /**
   * Sets roles which authorized to install / uninstall plugins from a string of comma separated values.
   * @param authorizedRolesString Comma separated string of roles ( e.g.: "roleA, roleB, roleC" )
   */
  public void setAuthorizedRoles( String authorizedRolesString ) {
    this.setAuthorizedRoles( this.parseStringCollection( authorizedRolesString, PROPERTY_COLLECTION_SEPARATOR ) );
  }

  /**
   * Gets the user names which are authorized to install / unintall plugins
   * @return
   */
  public Collection<String> getAuthorizedUsernames() {
    return authorizedUsernames;
  }
  /**
   * Sets the user names which are authorized to install / unintall plugins
   * @return
   */
  public void setAuthorizedUsernames( Collection<String> authorizedUsernames ) {
    if ( authorizedUsernames == null ) {
      authorizedUsernames = Collections.emptyList();
    }
    this.authorizedUsernames = authorizedUsernames;
  }
  private Collection<String> authorizedUsernames = Collections.emptyList();

  /**
   * Sets the user names which authorized to install / uninstall plugins from a string of comma separated values.
   * @param authorizedUsernamesString Comma separated string of user names ( e.g.: "Jack, Lilly, Joe" )
   */  public void setAuthorizedUsernames( String authorizedUsernamesString ) {
    this.setAuthorizedUsernames( this.parseStringCollection( authorizedUsernamesString, PROPERTY_COLLECTION_SEPARATOR ) );
  }

  /**
   * Gets the OSGI bundle this service belongs to
   * @return
   */
  public Bundle getBundle() {
    return this.bundle;
  }
  /**
   * Sets the OSGI bundle this service belongs to
   * @return
   */
  public void setBundle( Bundle bundle ) {
    this.bundle = bundle;
  }
  private Bundle bundle;

  /**
   * Gets the relative path of the folder where the marketplace kettle transformations / jobs are stored. This path is
   * relative to the bundle base folder supplied by {@link Bundle#getLocation()}
   *
   * @return
   */
  public String getRelativeKettleExecutionFolderPath() {
    return relativeKettleExecutionFolderPath;
  }
  /**
   * Sets the relative path of the folder where the marketplace kettle transformations / jobs are stored This path is
   * relative to the bundle base folder supplied by {@link Bundle#getLocation()}
   *
   * @param folderPath
   */
  public void setRelativeKettleExecutionFolderPath( String folderPath ) {
    this.relativeKettleExecutionFolderPath = folderPath;
  }
  private String relativeKettleExecutionFolderPath;

  /**
   * Gets the absolute path to the folder where the marketplace transformations / jobs are stored and executed.*
   * @return
   */
  public Path getAbsoluteKettleExecutionFolderPath() {
    return this.getMarketplaceFolder()
      .resolve( this.getRelativeKettleExecutionFolderPath() );
  }

  /**
   * Gets the path to where the kettle files are within the Bundle. This path is relative to the bundle root.
   * @return
   */
  public String getAbsoluteKettleResourcesSourcePath() {
    return this.absoluteKettleResourcesSourcePath;
  }
  public void setAbsoluteKettleResourcesSourcePath( String path ) {
    this.absoluteKettleResourcesSourcePath = path;
  }
  private String absoluteKettleResourcesSourcePath;


  protected Path getMarketplaceFolder() {
    String marketplacePath = this.getApplicationContext().getSolutionPath( MARKETPLACE_FOLDER );
    return Paths.get( marketplacePath ).toAbsolutePath();
  }

  private JobMeta getInstallJobMeta() {
    return this.getJobMeta( INSTALL_JOB_NAME );
  }

  private JobMeta getUninstallJobMeta() {
    return this.getJobMeta( UNINSTALL_JOB_NAME );
  }

  private JobMeta getJobMeta( String jobFileName ) {
    String jobFilePath = this.getAbsoluteKettleExecutionFolderPath().resolve( jobFileName ).toString();
    JobMeta meta = null;
    try {
      meta = new JobMeta( jobFilePath, null );
    } catch ( KettleXMLException e ) {
      this.getLogger().error( "Unable to create job meta from file path " + jobFilePath, e );
    }

    return meta;
  }

  //endregion

  //region Constructors
  public BaPluginService( IRemotePluginProvider metadataPluginsProvider,
                          IVersionDataFactory versionDataFactory,
                          IPluginVersionFactory pluginVersionFactory,
                          KarService karService, FeaturesService featuresService,
                          ITelemetryService telemetryService, IDomainStatusMessageFactory domainStatusMessageFactory,
                          IMarketplaceXmlSerializer pluginsSerializer,
                          ISecurityHelper securityHelper,
                          Bundle bundle ) {
    super( metadataPluginsProvider, versionDataFactory, pluginVersionFactory, karService, featuresService,
      telemetryService, domainStatusMessageFactory
    );

    //initialize dependencies
    this.setXmlSerializer( pluginsSerializer );
    this.setSecurityHelper( securityHelper );
    this.setBundle( bundle );
  }

  /**
   * Called after class is instantiated by DI
   */
  public void init() {
    this.copyKettleFilesToExecutionFolder();
  }

  /**
   * Called on object destruction by DI
   */
  public void destroy() {
    this.deleteKettleFilesFromExecutionFolder();
  }
  //endregion


  //region Methods
  @Override
  public Map<String, IPlugin> getPlugins() {
    Map<String, IPlugin> plugins = super.getPlugins();

    // remove non BA plugins
    CollectionUtils.filter( plugins.entrySet(), new Predicate() {
      @Override public boolean evaluate( Object mapEntry ) {
        Map.Entry<String, IPlugin> mapEntryCasted = (Map.Entry<String, IPlugin>) mapEntry;
        return mapEntryCasted.getValue().getType() == MarketEntryType.Platform;
      }
    } );

    return plugins;
  }


  @Override
  protected boolean hasMarketplacePermission() {
    Collection<String> authorizedRoles = this.getAuthorizedRoles();
    Collection<String> authorizedUsernames = this.getAuthorizedUsernames();

    if ( authorizedRoles.isEmpty() && authorizedUsernames.isEmpty() ) {
      // If it's true, we'll just check if the user is admin
      return this.getSecurityHelper().isPentahoAdministrator( this.getCurrentSession() );
    }

    Authentication authentication = this.getSecurityHelper().getAuthentication( this.getCurrentSession(), true );
    Collection<String> userRoles = this.getRoles( authentication );
    String userName = authentication.getName();

    return authorizedUsernames.contains( userName )
      || CollectionUtils.containsAny( authorizedRoles, userRoles );
  }

  private Collection<String> getRoles( Authentication authentication ) {
    Collection<String> roles = new ArrayList<>();
    for ( GrantedAuthority grantedAuthority : authentication.getAuthorities() ) {
      roles.add( grantedAuthority.getAuthority() );
    }
    return roles;
  }

  @Override
  protected IPluginVersion getInstalledPluginVersion( IPlugin plugin ) {
    String versionPath = this.getApplicationContext().getSolutionPath( "system/" + plugin.getId()
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


  private boolean isLegacyPlugin( String pluginId ) {
    String pluginConfigPath = this.getApplicationContext().getSolutionPath(
      SYSTEM_FOLDER + File.separator + pluginId + File.separator + PLUGIN_XML_FILE );
    return ( new File( pluginConfigPath ).isFile() );
  }

  @Override
  protected Collection<String> getInstalledPluginIds() {
    // get ids of OSGi plugins
    Collection<String> plugins = super.doGetInstalledPluginIds();

    // search and add ids for non-OSGi legacy plugins
    File systemDir = new File( this.getApplicationContext().getSolutionPath( SYSTEM_FOLDER ) );
    String[] dirs = systemDir.list( DirectoryFileFilter.INSTANCE );
    for ( String dir : dirs ) {
      if ( isLegacyPlugin( dir ) && !plugins.contains( dir ) ) {
        plugins.add( dir );
      }
    }

    return plugins;
  }


  @Override
  protected void unloadPlugin( String pluginId ) {
    IPluginManager pluginManager = this.getPluginManager( this.getCurrentSession() );
    ClassLoader cl = pluginManager.getClassLoader( pluginId );
    if ( cl != null && cl instanceof URLClassLoader ) {
      try {
        URLClassLoader cl1 = (URLClassLoader) cl;
        Util.closeURLClassLoader( cl1 );
        Method closeMethod = cl1.getClass().getMethod( BaPluginService.CLOSE_METHOD_NAME );
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

  @Override
  protected boolean doInstall( IPlugin plugin, IPluginVersion versionToInstall ) {
    return super.doInstall( plugin, versionToInstall )
      || executeInstall( plugin, versionToInstall );
  }

  @Override
  protected boolean executeInstall( IPlugin plugin, IPluginVersion version ) {
    try {
      Result result =
          this.executeInstallPluginJob( plugin.getId(), version.getDownloadUrl(), version.getSamplesDownloadUrl(),
            version.getVersion() );

      if ( result == null || result.getNrErrors() > 0 ) {
        return false;
      }
    } catch ( KettleException e ) {
      logger.error( e.getMessage(), e );
      return false;
    }

    return true;
  }

  @Override
  protected boolean doUninstall( IPlugin plugin, IPluginVersion installedVersion ) {
    return super.doUninstall( plugin, installedVersion )
      || executeUninstall( plugin );
  }

  @Override
  protected boolean executeUninstall( IPlugin plugin ) {
    try {
      Result result = this.executeUninstallPluginJob( plugin.getId() );

      if ( result == null || result.getNrErrors() > 0 ) {
        return false;
      }
    } catch ( KettleException e ) {
      logger.error( e.getMessage(), e );
      return false;
    }

    return true;
  }

  private Result executeInstallPluginJob( String pluginId, String downloadUrl, String samplesDownloadUrl,
                                          String availableVersion )
    throws UnknownParamException {

    JobMeta installMeta = this.getInstallJobMeta();
    if ( installMeta == null ) {
      this.getLogger().error( "Unable to find install job meta." );
      return null;
    }

    Job job = new Job( null, installMeta );

    File file = new File( this.getApplicationContext().getSolutionPath( DOWNLOAD_CACHE_FOLDER ) );
    file.mkdirs();
    file = new File( this.getApplicationContext().getSolutionPath( BACKUP_CACHE_FOLDER ) );
    file.mkdirs();
    file = new File( this.getApplicationContext().getSolutionPath( STAGING_CACHE_FOLDER ) );
    file.mkdirs();

    job.getJobMeta().setParameterValue( "downloadUrl", downloadUrl );

    if ( samplesDownloadUrl != null ) {
      job.getJobMeta().setParameterValue( "samplesDownloadUrl", samplesDownloadUrl );
      job.getJobMeta().setParameterValue( "samplesDir", "/public/plugin-samples" );
      job.getJobMeta().setParameterValue( "samplesTargetDestination", this.getApplicationContext()
          .getSolutionPath( "plugin-samples/" + pluginId ) );
      job.getJobMeta().setParameterValue( "samplesTargetBackup", this.getApplicationContext()
          .getSolutionPath( BACKUP_CACHE_FOLDER + pluginId + "_samples_" + new Date()
          .getTime() ) );
      job.getJobMeta().setParameterValue( "samplesDownloadDestination", this.getApplicationContext()
          .getSolutionPath( DOWNLOAD_CACHE_FOLDER + pluginId + "-samples-" + availableVersion
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
        .getSolutionPath( STAGING_CACHE_FOLDER ) );
    job.getJobMeta().setParameterValue( "stagingDestinationAndDir", this.getApplicationContext()
        .getSolutionPath( STAGING_CACHE_FOLDER + pluginId ) );
    job.getJobMeta().setParameterValue( "targetDestination", this.getApplicationContext()
        .getSolutionPath( "system/" + pluginId ) );
    job.getJobMeta().setParameterValue( "targetBackup", this.getApplicationContext()
        .getSolutionPath( BACKUP_CACHE_FOLDER + pluginId + "_" + new Date().getTime() ) );

    job.copyParametersFrom( job.getJobMeta() );
    job.setLogLevel( LogLevel.DETAILED );
    job.activateParameters();
    job.start();
    job.waitUntilFinished();
    Result result = job.getResult(); // Execute the selected job.

    return result;
  }

  private Result executeUninstallPluginJob( String pluginId )
    throws UnknownParamException {

    JobMeta uninstallJobMeta = this.getUninstallJobMeta();
    if ( uninstallJobMeta == null ) {
      this.getLogger().error( "Unable to find uninstall job meta." );
      return null;
    }

    Job job = new Job( null, uninstallJobMeta );

    File file = new File( this.getApplicationContext().getSolutionPath( BACKUP_CACHE_FOLDER ) );
    file.mkdirs();

    String uninstallBackup = this.getApplicationContext().getSolutionPath( BACKUP_CACHE_FOLDER
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

  private void copyKettleFilesToExecutionFolder() {
    Path kettleExecutionFolderPath = this.getAbsoluteKettleExecutionFolderPath();
    File targetKettleFilesFolder = new File( kettleExecutionFolderPath.toUri() );
    if ( !targetKettleFilesFolder.exists()
          && !targetKettleFilesFolder.mkdirs() ) {
      this.getLogger().error( "Failed to create temporary folder for marketplace kettle transformations at "
          + targetKettleFilesFolder.toString() );
    }

    String kettleResourcesSourcePath = this.getAbsoluteKettleResourcesSourcePath();
    Iterable<String> kettleResourcePaths = Collections.list( bundle.getEntryPaths( kettleResourcesSourcePath ) );
    for ( String kettleResourcePath : kettleResourcePaths ) {
      this.writeResourceToFolder( kettleResourcePath, kettleExecutionFolderPath );
    }
  }

  private void deleteKettleFilesFromExecutionFolder() {
    URI kettleExecutionFolder = this.getAbsoluteKettleExecutionFolderPath().toUri();
    File targetKettleFilesFolder = new File( kettleExecutionFolder );
    if ( targetKettleFilesFolder.exists() ) {
      try {
        FileUtils.deleteDirectory( targetKettleFilesFolder );
      } catch ( IOException e ) {
        this.getLogger().error( "Unable to delete marketplace temporary kettle execution folder: "
            + targetKettleFilesFolder.toString(), e );
      }
    }
  }

  private void writeResourceToFolder( URL resourceUrl, Path destinationFolder ) {
    try {
      InputStream inputStream = resourceUrl.openConnection().getInputStream();
      String fileName = FilenameUtils.getName( resourceUrl.toString() );
      Path destinationFile = destinationFolder.resolve( fileName );
      Files.copy( inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING );
    } catch ( IOException e ) {
      this.getLogger()
        .error( "Error copying " + resourceUrl.toString() + " to destination folder " + destinationFolder, e );
    }
  }

  private void writeResourceToFolder( String resourceUrl, Path destinationFolder ) {
    URL url = this.getBundle().getResource( resourceUrl );
    this.writeResourceToFolder( url, destinationFolder );
  }

  private Collection<String> parseStringCollection( String string, String valueSeparator ) {
    String[] splitString = string.split( valueSeparator );
    Collection<String> parsedValues = new ArrayList<>( splitString.length );
    for ( String authorizedRole : splitString ) {
      if ( authorizedRole != null ) {
        authorizedRole = authorizedRole.trim();
        if ( !authorizedRole.isEmpty() ) {
          parsedValues.add( authorizedRole );
        }
      }
    }
    return parsedValues;
  }


  //endregion


}

