/*
 * Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package org.pentaho.marketplace.domain.services;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.Before;

import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.serialization.MarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.model.factories.DomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.PluginFactory;
import org.pentaho.marketplace.domain.model.factories.PluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.VersionDataFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.interfaces.IPluginProvider;
import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;

import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.api.engine.ISecurityHelper;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginServiceTest {

  private static final String SETTINGS_ROLES = "settings/marketplace-roles";
  private static final String SETTINGS_USERS = "settings/marketplace-users";
  private static final String SETTINGS_MARKETPLACE_SITE_ = "settings/marketplace-site";

  private String getSolutionPath() {
    return System.getProperty( "user.dir" ) + "/test-res/pentaho-solutions/";
  }

  IDomainStatusMessageFactory domainStatusMessageFactory;
  IVersionDataFactory versionDataFactory;

  IPluginFactory pluginFactory = new PluginFactory();
  IPluginVersionFactory pluginVersionFactory = new PluginVersionFactory();

  //region auxiliary methods

  /**
   * Creates a new plugin service.
   * Solution folder is set to "test-res/pentaho-solutions/"
   * @return
   */
  private BAPluginService createPluginService() {
    IDomainStatusMessageFactory domainStatusMessageFactory = this.domainStatusMessageFactory;
    IVersionDataFactory versionDataFactory = this.versionDataFactory;

    IRemotePluginProvider pluginProvider = mock( IRemotePluginProvider.class );
    MarketplaceXmlSerializer serializer = mock( MarketplaceXmlSerializer.class );
    ISecurityHelper securityHelper = mock( ISecurityHelper.class );
    IPluginResourceLoader resourceLoader = mock( IPluginResourceLoader.class );

    BAPluginService service = new BAPluginService( pluginProvider, serializer, versionDataFactory, domainStatusMessageFactory, securityHelper, resourceLoader );

    IApplicationContext applicationContext = mock( IApplicationContext.class );
    final String solutionPath = this.getSolutionPath();
    when( applicationContext.getSolutionPath( anyString() ) ).thenAnswer( new Answer<String>() {
      @Override public String answer( InvocationOnMock invocation ) throws Throwable {
        String path = (String) invocation.getArguments()[ 0 ];
        return solutionPath + path;
      }
    } );
    service.setApplicationContext( applicationContext );

    return service;
  }

  private Authentication createMockUserAuthentication( String role, String userName ) {
    GrantedAuthority userAuthority = mock( GrantedAuthority.class );
    when( userAuthority.getAuthority() ).thenReturn( role );
    Authentication userAuthentication = mock( Authentication.class );
    when( userAuthentication.getAuthorities() ).thenReturn( new GrantedAuthority[] { userAuthority } );
    when( userAuthentication.getName() ).thenReturn( userName );

    return  userAuthentication;
  }

  private IPlugin getPluginById( Iterable<IPlugin> plugins, String pluginId ) {
    for ( IPlugin plugin : plugins ) {
      if ( pluginId.equals( plugin.getId() ) ) {
        return plugin;
      }
    }

    return null;
  }

  // endregion

  @Before
  public void setup() {
    this.domainStatusMessageFactory = new DomainStatusMessageFactory();
    this.versionDataFactory = new VersionDataFactory();
  }

  // region Tests

  /**
   * Tests that when no allowed roles are defined and the user trying to install is not an administrator
   * then the installation is denied.
   */
  @Test
  public void testInstallDeniedNoRolesAndNonAdminUser( ) {
    // arrange
    BAPluginService service = this.createPluginService();

    // setup no roles
    IPluginResourceLoader resourceLoader = service.getPluginResourceLoader();
    when( resourceLoader.getPluginSetting( BAPluginService.class, SETTINGS_ROLES ) ).thenReturn( null );

    // setup security helper for non admin user
    ISecurityHelper securityHelper = service.getSecurityHelper();
    when( securityHelper.isPentahoAdministrator( Mockito.any( IPentahoSession.class ) ) ).thenReturn( false );

    // act
    IDomainStatusMessage result = service.installPlugin( "doesNotMatter", "doesNotMatter" );

    // assert
    assertThat( result.getCode(), is( equalTo( BasePluginService.UNAUTHORIZED_ACCESS_ERROR_CODE ) ) );
  }

  /**
   * Tests that when no allowed roles are defined and the user trying to uninstall is not an administrator
   * then the uninstall is denied.
   */
  @Test
  public void testUninstallDeniedNoRolesAndNonAdminUser( ) {
    // arrange
    BAPluginService service = this.createPluginService();

    // setup no roles
    IPluginResourceLoader resourceLoader = service.getPluginResourceLoader();
    when( resourceLoader.getPluginSetting( BAPluginService.class, SETTINGS_ROLES ) ).thenReturn( null );

    // setup security helper for non admin user
    ISecurityHelper securityHelper = service.getSecurityHelper();
    when( securityHelper.isPentahoAdministrator( Mockito.any( IPentahoSession.class ) ) ).thenReturn( false );

    // act
    IDomainStatusMessage result = service.uninstallPlugin( "doesNotMatter" );

    // assert
    assertThat( result.getCode(), is( equalTo( BasePluginService.UNAUTHORIZED_ACCESS_ERROR_CODE ) ) );
  }


  /**
   * Tests that when allowed roles are defined and the user trying to install does not have one of them
   * then the installation is denied.
   */
  @Test
  public void testInstallDeniedUserNotInRoles( ) {
    // region arrange
    BAPluginService service = this.createPluginService();

    // this is the role of the user which will be in the authorized roles
    String userRole = "peasant";
    String authorizedRoles = "manager,lackey";

    // setup roles
    IPluginResourceLoader resourceLoader = service.getPluginResourceLoader();
    when( resourceLoader.getPluginSetting( BAPluginService.class,  SETTINGS_ROLES ) ).thenReturn( authorizedRoles );

    Authentication userAuthentication = this.createMockUserAuthentication( userRole, null );
    // setup security helper
    ISecurityHelper securityHelper = service.getSecurityHelper();
    when( securityHelper.getAuthentication( Mockito.any( IPentahoSession.class ), eq( true ) ) ).thenReturn( userAuthentication );

    // endregion

    // act
    IDomainStatusMessage result = service.installPlugin( "doesNotMatter", "doesNotMatter" );

    // assert
    assertThat( result.getCode(), is( equalTo( BasePluginService.UNAUTHORIZED_ACCESS_ERROR_CODE ) ) );
  }

  /**
   * Tests that when allowed roles are defined and the user trying to uninstall does not have one of them
   * then the uninstall is denied.
   */
  @Test
  public void testUninstallDeniedUserNotInRoles( ) {
    // region arrange
    BAPluginService service = this.createPluginService();

    // this is the role of the user which will be in the authorized roles
    String userRole = "peasant";
    String authorizedRoles = "manager,lackey";

    // setup roles
    IPluginResourceLoader resourceLoader = service.getPluginResourceLoader();
    when( resourceLoader.getPluginSetting( BAPluginService.class,  SETTINGS_ROLES ) ).thenReturn( authorizedRoles );

    Authentication userAuthentication = this.createMockUserAuthentication( userRole, null );
    // setup security helper
    ISecurityHelper securityHelper = service.getSecurityHelper();
    when( securityHelper.getAuthentication( Mockito.any( IPentahoSession.class ), eq( true ) ) ).thenReturn( userAuthentication );

    // endregion

    // act
    IDomainStatusMessage result = service.uninstallPlugin( "doesNotMatter" );

    // assert
    assertThat( result.getCode(), is( equalTo( BasePluginService.UNAUTHORIZED_ACCESS_ERROR_CODE ) ) );
  }


  /**
   * Tests that when allowed users are defined and the user trying to install is not one of them
   * then the installation is denied.
   */
  @Test
  public void testInstallDeniedUserNotInUsers( ) {
    // arrange
    String userName = "Dennis";
    String authorizedUsers = "Joseph,David";
    String authorizedRoles = "";
    BAPluginService service = this.createPluginService();

    IPluginResourceLoader resourceLoader = service.getPluginResourceLoader();
    when( resourceLoader.getPluginSetting( BAPluginService.class,  SETTINGS_ROLES ) ).thenReturn( authorizedRoles );
    when( resourceLoader.getPluginSetting( BAPluginService.class,  SETTINGS_USERS ) ).thenReturn( authorizedUsers );

    Authentication userAuthentication = this.createMockUserAuthentication( null, userName );
    ISecurityHelper securityHelper = service.getSecurityHelper();
    when( securityHelper.getAuthentication( Mockito.any( IPentahoSession.class ), eq( true ) ) ).thenReturn( userAuthentication );

    // act
    IDomainStatusMessage result = service.installPlugin( "doesNotMatter", "doesNotMatter" );

    // assert
    assertThat( result.getCode(), is( equalTo( BasePluginService.UNAUTHORIZED_ACCESS_ERROR_CODE ) ) );
  }

  /**
   * Tests that when allowed users are defined and the user trying to uninstall is not one of them
   * then the uninstall is denied.
   */
  @Test
  public void testUninstallDeniedUserNotInUsers( ) {
    // arrange
    String userName = "Dennis";
    String authorizedUsers = "Joseph,David";
    String authorizedRoles = "";
    BAPluginService service = this.createPluginService();

    IPluginResourceLoader resourceLoader = service.getPluginResourceLoader();
    when( resourceLoader.getPluginSetting( BAPluginService.class,  SETTINGS_ROLES ) ).thenReturn( authorizedRoles );
    when( resourceLoader.getPluginSetting( BAPluginService.class,  SETTINGS_USERS ) ).thenReturn( authorizedUsers );

    Authentication userAuthentication = this.createMockUserAuthentication( null, userName );
    ISecurityHelper securityHelper = service.getSecurityHelper();
    when( securityHelper.getAuthentication( Mockito.any( IPentahoSession.class ), eq( true ) ) ).thenReturn( userAuthentication );

    // act
    IDomainStatusMessage result = service.uninstallPlugin( "doesNotMatter" );

    // assert
    assertThat( result.getCode(), is( equalTo( BasePluginService.UNAUTHORIZED_ACCESS_ERROR_CODE ) ) );
  }

  //TODO: "(un)install allowed" test cases

  /**
   * Tests that only compatible versions (with ba server) of plugins are returned
   */
  @Test
  public void testGetPluginsOnlyCompatibleVersions( ) {
    // arrange
    BAPluginService service = this.createPluginService();
    service.setServerVersion( "5.2" );

    IPluginVersion compatibleVersion = this.pluginVersionFactory.create();
    compatibleVersion.setMinParentVersion( "1.0" );
    compatibleVersion.setMaxParentVersion( "6.9.99" );

    IPluginVersion notCompatibleVersion = this.pluginVersionFactory.create();
    notCompatibleVersion.setMaxParentVersion( "1.0" );
    notCompatibleVersion.setMinParentVersion( "1.0" );

    Collection<IPluginVersion> versions = new ArrayList<IPluginVersion>();
    versions.add( compatibleVersion );
    versions.add( notCompatibleVersion );

    Map<String, IPlugin> plugins = new HashMap<>();
    IPlugin plugin = this.pluginFactory.create();
    String pluginId = "myPlugin";
    plugin.setId( pluginId );
    plugin.setVersions( versions );
    plugins.put(plugin.getId(), plugin );

    IPluginProvider pluginProvider = service.getMetadataPluginsProvider();
    when( pluginProvider.getPlugins() ).thenReturn( plugins );

    // act
    Map<String, IPlugin> actualPlugins = service.getPlugins();

    // assert
    IPlugin actualPlugin = actualPlugins.get( pluginId );
    Collection<IPluginVersion> actualVersions = actualPlugin.getVersions();

    assertThat( actualPlugin, is( equalTo( plugin ) ) );
    assertThat( actualVersions, hasItem( compatibleVersion ) );
    assertThat( actualVersions, not( hasItem( notCompatibleVersion ) ) );
  }

  /**
   * Tests that plugins which are installed (in the system folder) are marked as installed
   */
  @Test
  public void testGetPluginsInstalledPluginsAreIdentified() {
    // arrange
    BAPluginService service = this.createPluginService();
    service.setServerVersion( "5.2" );

    IPluginVersion compatibleVersion = this.pluginVersionFactory.create();
    compatibleVersion.setMinParentVersion( "1.0" );
    compatibleVersion.setMaxParentVersion( "6.9.99" );

    IPlugin installedPlugin = this.pluginFactory.create();
    // plugin name is the same as the plugin folder inside "test-res/pentaho-solutions/system"
    String installedPluginId = "installedPlugin";
    installedPlugin.setId( installedPluginId );
    installedPlugin.setInstalled( false );
    // have at least one compatible version so its not filtered out
    installedPlugin.getVersions().add( compatibleVersion );

    IPlugin notInstalledPlugin = this.pluginFactory.create();
    String notInstalledPluginId = "notInstalledPlugin";
    notInstalledPlugin.setId( notInstalledPluginId );
    notInstalledPlugin.setInstalled( false );
    // have at least one compatible version so its not filtered out
    notInstalledPlugin.getVersions().add( compatibleVersion );

    Map<String, IPlugin> plugins = new HashMap<>();
    plugins.put( installedPlugin.getId(), installedPlugin );
    plugins.put( notInstalledPlugin.getId(), notInstalledPlugin );

    IPluginProvider pluginProvider = service.getMetadataPluginsProvider();
    when( pluginProvider.getPlugins() ).thenReturn( plugins );

    // act
    Map<String, IPlugin> actualPlugins = service.getPlugins();

    // assert
    IPlugin actualInstalledPlugin = actualPlugins.get( installedPluginId );
    IPlugin actualNotInstalledPlugin = actualPlugins.get( notInstalledPluginId );
    assertThat( actualInstalledPlugin, is( notNullValue() ) );
    assertThat( actualNotInstalledPlugin, is( notNullValue() ) );
    assertThat( actualInstalledPlugin.isInstalled(), is( true ) );
    assertThat( actualNotInstalledPlugin.isInstalled(), is( false ) );
  }

  /**
   * Tests that the metadata plugin provider fed into the PluginService is initialized
   * with the URL specified in the settings.xml provided by the resource loader
   */
  @Test
  public void testUsesUrlSpecifiedByPluginResourceLoader() throws MalformedURLException {
    // arrange
    IDomainStatusMessageFactory domainStatusMessageFactory = this.domainStatusMessageFactory;
    IVersionDataFactory versionDataFactory = this.versionDataFactory;

    IRemotePluginProvider pluginProvider = mock( IRemotePluginProvider.class );
    MarketplaceXmlSerializer serializer = mock( MarketplaceXmlSerializer.class );
    ISecurityHelper securityHelper = mock( ISecurityHelper.class );

    IPluginResourceLoader resourceLoader = mock( IPluginResourceLoader.class );
    String resourceMetadataUrl = "http://myresource.com/metadata.xml";
    when(resourceLoader.getPluginSetting( BAPluginService.class, SETTINGS_MARKETPLACE_SITE_ ) )
      .thenReturn( resourceMetadataUrl );

    // act
    BAPluginService service = new BAPluginService( pluginProvider, serializer, versionDataFactory,
        domainStatusMessageFactory, securityHelper, resourceLoader );

    // assert
    verify( pluginProvider, times( 1 ) ).setUrl( new URL( resourceMetadataUrl )  );
  }

  // endregion

}
