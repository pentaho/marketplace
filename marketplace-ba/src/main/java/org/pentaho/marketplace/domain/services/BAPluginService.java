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


import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.serialization.MarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.helpers.Util;
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

import org.pentaho.telemetry.ITelemetryService;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
//import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class BAPluginService extends BasePluginService {

    @Override
    protected boolean hasMarketplacePermission() {
        return false;
    }

    @Override
    protected void unloadPlugin(String pluginId) {

    }

    @Override
    protected boolean executeInstall(IPlugin plugin, IPluginVersion version) {
        return false;
    }

    @Override
    protected boolean executeUninstall(IPlugin plugin) {
        return false;
    }

    @Override
    protected IPluginVersion getInstalledPluginVersion(IPlugin plugin) {
        return null;
    }

    @Override
    protected Collection<String> getInstalledPluginIds() {
        return Collections.emptyList();
    }

  //region Constants

  private static final String CLOSE_METHOD_NAME = "close";
  private static final String PROCESSES_FILES_FOLDER =  "system/" + PLUGIN_NAME + "/processes/";
  private static final String INSTALL_JOB_PATH = PROCESSES_FILES_FOLDER + "download_and_install_plugin.kjb";
  private static final String UNINSTALL_JOB_PATH = PROCESSES_FILES_FOLDER + "uninstall_plugin.kjb";

  private static final String CACHE_FOLDER = "system/plugin-cache/";
  private static final String DOWNLOAD_CACHE_FOLDER = CACHE_FOLDER + "downloads/";
  private static final String BACKUP_CACHE_FOLDER = CACHE_FOLDER + "backups/";
  private static final String STAGING_CACHE_FOLDER = CACHE_FOLDER + "staging/";

  //endregion

  //region Properties

  public MarketplaceXmlSerializer getXmlSerializer() {
    return this.xmlPluginsSerializer;
  }
  protected BAPluginService setXmlSerializer( MarketplaceXmlSerializer serializer ) {
    this.xmlPluginsSerializer = serializer;
    return this;
  }
  private MarketplaceXmlSerializer xmlPluginsSerializer;

  public ISecurityHelper getSecurityHelper() {
    return this.securityHelper;
  }
  protected BAPluginService setSecurityHelper( ISecurityHelper securityHelper ) {
    this.securityHelper = securityHelper;
    return this;
  }
  private ISecurityHelper securityHelper;


  public IPluginResourceLoader getPluginResourceLoader() {
    return this.pluginResourceLoader;
  }
  protected BAPluginService setPluginResourceLoader( IPluginResourceLoader pluginResourceLoader ) {
    this.pluginResourceLoader = pluginResourceLoader;
    return this;
  }
  private IPluginResourceLoader pluginResourceLoader;

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
  protected BAPluginService setApplicationContext( IApplicationContext applicationContext ) {
    this.applicationContext = applicationContext;
    return this;
  }
  private IApplicationContext applicationContext;


  // TODO: see if there is a better way to encapsulate this.
  // Probably just pass in the session in the methods that require it.
  protected IPentahoSession getCurrentSession() {
    return PentahoSessionHolder.getSession();
  }
  protected BAPluginService setCurrentSession( IPentahoSession session ) {
    PentahoSessionHolder.setSession( session );
    return this;
  }
  //endregion

  //region Constructors
  public BAPluginService(IRemotePluginProvider metadataPluginsProvider,
                         MarketplaceXmlSerializer pluginsSerializer,
                         IVersionDataFactory versionDataFactory,
                         IDomainStatusMessageFactory domainStatusMessageFactory,
                         ISecurityHelper securityHelper,
                         IPluginResourceLoader resourceLoader,
                         ITelemetryService telemetryService) {
    super( metadataPluginsProvider, versionDataFactory, domainStatusMessageFactory, telemetryService );

    //initialize dependencies
    MarketplaceXmlSerializer serializer = pluginsSerializer;
    this.setXmlSerializer( serializer );

    this.setSecurityHelper( securityHelper );
    this.setPluginResourceLoader( resourceLoader );
  }
  //endregion

  /*

  //region Methods
  @Override
  protected boolean hasMarketplacePermission() {
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

  @Override
  protected Collection<String> getInstalledPluginIds() {
    Collection<String> plugins = new ArrayList<>();

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

  @Override
  protected void unloadPlugin( String pluginId ) {
    IPluginManager pluginManager = this.getPluginManager( this.getCurrentSession() );
    ClassLoader cl = pluginManager.getClassLoader( pluginId );
    if ( cl != null && cl instanceof URLClassLoader ) {
      try {
        URLClassLoader cl1 = (URLClassLoader) cl;
        Util.closeURLClassLoader( cl1 );
        Method closeMethod = cl1.getClass().getMethod( BAPluginService.CLOSE_METHOD_NAME );
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
  protected boolean executeInstall( IPlugin plugin, IPluginVersion version ) {
    try {
      Result result =
        this.executeInstallPluginJob( plugin.getId(), version.getDownloadUrl(), version.getSamplesDownloadUrl(), version.getVersion() );

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
    throws KettleXMLException, UnknownParamException {

    // get marketplace path
    String jobPath = this.getApplicationContext().getSolutionPath( INSTALL_JOB_PATH );

    JobMeta installJobMeta = new JobMeta( jobPath, null );
    Job job = new Job( null, installJobMeta );

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
    throws KettleXMLException, UnknownParamException {
    // get plugin path
    String jobPath = this.getApplicationContext().getSolutionPath( UNINSTALL_JOB_PATH );

    JobMeta uninstallJobMeta = new JobMeta( jobPath, null );
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
  //endregion

  */

}

