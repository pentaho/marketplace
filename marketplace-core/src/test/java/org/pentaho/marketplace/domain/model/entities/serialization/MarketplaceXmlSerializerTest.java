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

package org.pentaho.marketplace.domain.model.entities.serialization;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.marketplace.domain.model.entities.DevelopmentStage;
import org.pentaho.marketplace.domain.model.entities.MarketEntryType;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.factories.CategoryFactory;
import org.pentaho.marketplace.domain.model.factories.PluginFactory;
import org.pentaho.marketplace.domain.model.factories.PluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.VersionDataFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public abstract class MarketplaceXmlSerializerTest<TSerializer extends IMarketplaceXmlSerializer> {

  private IPluginFactory pluginFactory;
  private IPluginVersionFactory pluginVersionFactory;
  private IVersionDataFactory versionDataFactory;
  private ICategoryFactory categoryFactory;

  protected abstract TSerializer create( IPluginFactory pluginFactory, IPluginVersionFactory pluginVersionFactory,
                                         IVersionDataFactory versionDataFactory, ICategoryFactory categoryFactory );

  private TSerializer createSerializer() {
    return this.create( this.pluginFactory, this.pluginVersionFactory, this.versionDataFactory, this.categoryFactory );
  }


  // region auxiliary methods
  private Collection<String> getPluginIds( Iterable<IPlugin> plugins ) {
    Collection<String> pluginIds = new ArrayList<String>();
    for ( IPlugin plugin : plugins ) {
      pluginIds.add( plugin.getId() );
    }
    return pluginIds;
  }
  // endregion

  // region metadata.xml test resource information
  /**
   * metadata.xml market entry ids of type "platform"
   */
  private List<String> metadataXmlPlatformPluginIds =
    new ArrayList<String>( Arrays.asList( "marketplace", "pentaho-cdf", "cda", "languagePack_ja", "pdi-mysql-plugin",
      "ApplePushNotification", "KFF", "BucketPartitioner", "CiviCrmOutput", "idh23", "TableauExtractRefresh",
      "GroovyConsoleSpoonPlugin"  ) );

  /**
   * Programatically creates the marketplace plugin that is in the metadata.xml test resource
   */
  private IPlugin getMetadataXmlMarketplacePlugin() {
    IPlugin metadataXmlMarketplacePlugin = this.pluginFactory.create();
    metadataXmlMarketplacePlugin.setId( "marketplace" );
    metadataXmlMarketplacePlugin.setType( MarketEntryType.Platform );
    metadataXmlMarketplacePlugin.setName( "Pentaho Marketplace" );
    metadataXmlMarketplacePlugin.setImg( "http://pentaho.com/sites/all/themes/pentaho/_media/logo-pentaho.svg" );
    metadataXmlMarketplacePlugin.setSmallImg( "http://www.webdetails.pt/ficheiros/mk_plugin.png" );
    metadataXmlMarketplacePlugin
      .setDocumentationUrl( "http://wiki.pentaho.com/display/PMOPEN/Pentaho+BI+Server+Marketplace+Plugin" );
    metadataXmlMarketplacePlugin.setDescription( "\n"
      + "      Pentaho Marketplace allows users to explore and test the plugins\n"
      + "      that are most relevant to them. This means high quality and useful\n"
      + "      plugins that users can use to get the most out of their business.\n"
      + "    " );
    metadataXmlMarketplacePlugin.setAuthorName( "Pentaho" );
    metadataXmlMarketplacePlugin.setAuthorUrl( "http://pentaho.com" );
    metadataXmlMarketplacePlugin.setAuthorLogo( "http://pentaho.com/sites/all/themes/pentaho/_media/logo-pentaho.svg" );
    metadataXmlMarketplacePlugin.setInstallationNotes( "These are the installation notes." );
    metadataXmlMarketplacePlugin.setDependencies( "No dependencies." );
    metadataXmlMarketplacePlugin.setLicense( "GLPL v2" );
    metadataXmlMarketplacePlugin.setLicenseName( "License name glpl v2." );
    metadataXmlMarketplacePlugin.setLicenseText( "You are allowed to do anything you like." );

    ICategory parentCategory = categoryFactory.create( "Apps" );
    ICategory category = categoryFactory.create( "Admin", parentCategory );
    metadataXmlMarketplacePlugin.setCategory( category );

    IPluginVersion trunk4XVersion = pluginVersionFactory.create();
    trunk4XVersion.setBranch( "TRUNK" );
    trunk4XVersion.setVersion( "TRUNK-SNAPSHOT" );
    trunk4XVersion.setBuildId( "1" );
    trunk4XVersion.setName( "Latest snapshot build" );
    trunk4XVersion.setDownloadUrl(
      "http://ci.pentaho.com/job/marketplace-4.8/lastSuccessfulBuild/artifact/dist/marketplace-plugin-TRUNK-SNAPSHOT"
        + ".zip" );
    trunk4XVersion.setDescription( "The latest development snapshot build." );
    trunk4XVersion.setMinParentVersion( "1.0" );
    trunk4XVersion.setMaxParentVersion( "4.9" );
    IPluginVersion trunk5XVersion = pluginVersionFactory.create();
    trunk5XVersion.setBranch( "TRUNK" );
    trunk5XVersion.setVersion( "TRUNK-SNAPSHOT" );
    trunk5XVersion.setBuildId( "49" );
    trunk5XVersion.setName( "Latest snapshot build" );
    trunk5XVersion.setDownloadUrl(
      "http://repository.pentaho.org/artifactory/pentaho/pentaho/marketplace/5.1-SNAPSHOT/marketplace-5.1-SNAPSHOT"
        + ".zip" );
    trunk5XVersion.setSamplesDownloadUrl( "http://testing.pentaho.com/mySamples.zip" );
    trunk5XVersion.setDescription( "Build for Pentaho 5.0" );
    trunk5XVersion.setMinParentVersion( "5.0" );
    trunk5XVersion.setMaxParentVersion( "5.1.99" );
    trunk5XVersion.setDevelopmentStage( new DevelopmentStage( "Customer", "2" ) );
    trunk5XVersion.setChangelog( "Lots of stuff changed." );

    Collection<IPluginVersion> versions = new ArrayList<IPluginVersion>();
    versions.add( trunk4XVersion );
    versions.add( trunk5XVersion );
    metadataXmlMarketplacePlugin.setVersions( versions );

    String[] screenshots = new String[] {
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-01.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-02.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-03.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-04.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-05.png"
    };
    metadataXmlMarketplacePlugin.setScreenshots( screenshots );

    // xsd properties not used in plugins (Platform type market entries)
    //plugin.setSupportLevel( "" );
    //plugin.setSupportOrganization( "" );
    //plugin.setForumUrl( "" );
    //plugin.setCasesUrl( "" );

    return metadataXmlMarketplacePlugin;

  }

  // endregion

  // region installedVersion.xml test resource information
  private IPluginVersion getInstalledVersionXmlInstalledVersion() {
    IPluginVersion version = this.pluginVersionFactory.create();
    version.setBranch( "testBranch" );
    version.setVersion( "testVersionName" );
    version.setBuildId( "123" );

    return version;
  }

  // endregion

  @Before
  public void setup() {
    this.pluginFactory = new PluginFactory();
    this.pluginVersionFactory = new PluginVersionFactory();
    this.versionDataFactory = new VersionDataFactory();
    this.categoryFactory = new CategoryFactory();
  }


  @Test
  public void oldTestGetPlugins() throws IOException {

    String pluginsXml = IOUtils.toString( new FileInputStream( "availableplugins.xml" ) );
    IMarketplaceXmlSerializer serializer = this.createSerializer();

    Map<String, IPlugin> plugins = serializer.getPlugins( pluginsXml );

    assertThat( plugins.size(), is( equalTo( 3 ) ) );

    IPlugin cdePlugin = plugins.get( "cde" );
    IPluginVersion cdeVersion = cdePlugin.getVersions().iterator().next();

    assertThat( cdePlugin.getId(), is( equalTo( "cde" ) ) );
    assertThat( cdePlugin.getImg(), is( equalTo( "wt_transparent.png" ) ) );
    assertThat( cdePlugin.getSmallImg(), is( equalTo( "wt_transparent_small.png" ) ) );
    assertThat( cdePlugin.getName(), is( equalTo( "Community Dashboard Editor" ) ) );
    assertThat( cdePlugin.getDocumentationUrl(), is( equalTo( "http://cde.webdetails.org" ) ) );
    assertThat( cdePlugin.getDescription().trim(), is( equalTo(
      "The Community Dashboard Editor (CDE) is the outcome of real-world needs: It was born to greatly simplify the "
        + "creation, edition and rendering of dashboards.\n\nCDE and the technology underneath (CDF, CDA and CCC) "
        + "allows to develop and deploy dashboards in the Pentaho platform in a fast and effective way." ) ) );
    assertThat( cdePlugin.getAuthorName(), is( equalTo( "WebDetails" ) ) );
    assertThat( cdePlugin.getAuthorUrl(), is( equalTo( "http://webdetails.pt" ) ) );
    assertThat( cdeVersion.getDownloadUrl(),
      is( equalTo( "http://www.webdetails.pt/ficheiros/CDE-bundle-1.0-RC3.tar.bz2" ) ) );
    assertThat( cdeVersion.getVersion(), is( equalTo( "1.0-RC3" ) ) );
    assertThat( cdePlugin.getInstallationNotes(), is( nullValue() ) );

    IPlugin cdaPlugin = plugins.get( "cda" );
    IPluginVersion cdaVersion = cdaPlugin.getVersions().iterator().next();
    assertThat( cdaVersion.getChangelog(), is( nullValue() ) );

    IPlugin cdfPlugin = plugins.get( "cdf" );
    IPluginVersion cdfVersion = cdfPlugin.getVersions().iterator().next();
    assertThat( cdfVersion.getChangelog(), is( equalTo( "Changelog" ) ) );
    assertThat( cdfVersion.getSamplesDownloadUrl(), is( equalTo( "http://localhost:8080/cdf-1.0.samples.zip" ) ) );
    assertThat( cdfPlugin.getInstallationNotes(), is( equalTo( "Notes after install" ) ) );

  }

  @Test
  public void oldTestGetPluginsAlternativeVersions() throws IOException {

    String pluginsXml = IOUtils.toString( new FileInputStream( "availableplugins_differentversions.xml" ) );
    IMarketplaceXmlSerializer serializer = this.createSerializer();

    Collection<IPlugin> plugins = serializer.getPlugins( pluginsXml ).values();

    assertThat( plugins.size(), is( equalTo( 1 ) ) );

    IPlugin plugin = plugins.iterator().next();

    Collection<IPluginVersion> alternativeVersions = plugin.getVersions();
    assertThat( alternativeVersions.size(), is( equalTo( 2 ) ) );

    Iterator<IPluginVersion> versionIterator = plugin.getVersions().iterator();
    IPluginVersion releaseCandidateVersion = versionIterator.next();

    assertThat( releaseCandidateVersion.getBranch(), is( equalTo( "RC" ) ) );
    assertThat( releaseCandidateVersion.getName(), is( equalTo( "Release Candidate" ) ) );
    assertThat( releaseCandidateVersion.getChangelog(), is( equalTo( "ChangeLog for RC" ) ) );
    assertThat( releaseCandidateVersion.getDescription(),
      is( equalTo( "This is RC1 - pretty cool version but still not quite there" ) ) );
    assertThat( releaseCandidateVersion.getDownloadUrl(),
      is( equalTo( "http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3.tar.bz2" ) ) );
    assertThat( releaseCandidateVersion.getSamplesDownloadUrl(),
      is( equalTo( "http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3-samples.tar.bz2" ) ) );

    assertThat( releaseCandidateVersion.getBuildId(), is( nullValue() ) );

    IPluginVersion trunkVersion = plugin.getVersionByBranch( "TRUNK" );
    assertThat( trunkVersion, is( notNullValue() ) );
    assertThat( trunkVersion.getBranch(), is( equalTo( "TRUNK" ) ) );
    assertThat( trunkVersion.getName(), is( equalTo( "Trunk" ) ) );
    assertThat( trunkVersion.getChangelog(), is( equalTo( "Change Log for TRUNK" ) ) );
    assertThat( trunkVersion.getBuildId(), is( equalTo( "135" ) ) );
  }


  /**
   * Tests that plugins are deserialized in the same order as they appear in the xml
   */
  @Test
  public void testGetPluginsSameOrderAsXml() throws IOException {
    // arrange
    FileInputStream inputStream = new FileInputStream( "metadata.xml" );
    String pluginsXml = IOUtils.toString( inputStream );
    IMarketplaceXmlSerializer serializer = this.createSerializer();

    List<String> expectedIds = this.metadataXmlPlatformPluginIds;

    // act
    List<IPlugin> actualPlugins = new ArrayList<>( serializer.getPlugins( pluginsXml ).values() );
    Collections.sort( actualPlugins, new Comparator<IPlugin>() {
        @Override public int compare( IPlugin plugin1, IPlugin plugin2 ) {
          return plugin1.getRank() - plugin2.getRank();
        }
      }
    );

    // assert
    String[] actualPluginIds = this.getPluginIds( actualPlugins ).toArray( new String[ actualPlugins.size() ] );
    for ( int i = 0; i < actualPluginIds.length; i++ ) {
      assertThat( actualPluginIds[ i ], is( equalTo( expectedIds.get( i ) ) ) );
    }

    inputStream.close();
  }

  /**
   * Tests that only market entries of type platform are deserialized into plugins
   */
  @Test
  public void testGetPluginsOnlyParsePlatformMarketEntries() throws IOException {
    // arrange
    FileInputStream inputStream = new FileInputStream( "metadata.xml" );
    String pluginsXml = IOUtils.toString( inputStream );
    IMarketplaceXmlSerializer serializer = this.createSerializer();

    Collection<String> expectedPluginIds = this.metadataXmlPlatformPluginIds;

    // act
    List<IPlugin> actualPlugins = new ArrayList<>( serializer.getPlugins( pluginsXml ).values() );
    Collections.sort( actualPlugins, new Comparator<IPlugin>() {
        @Override public int compare( IPlugin plugin1, IPlugin plugin2 ) {
          return plugin1.getRank() - plugin2.getRank();
        }
      }
    );

    // assert
    Collection<String> actualPluginIds = this.getPluginIds( actualPlugins );
    assertThat( actualPluginIds, hasSize( expectedPluginIds.size() ) );
    assertThat( actualPluginIds, is( equalTo( expectedPluginIds ) ) );

    inputStream.close();
  }

  /**
   * Tests the deserialization of a plugin with all properties set
   */
  @Test
  public void testGetPluginsPluginDeserialization() throws IOException {
    // arrange
    FileInputStream inputStream = new FileInputStream( "metadata.xml" );
    String pluginsXml = IOUtils.toString( inputStream );
    IMarketplaceXmlSerializer serializer = this.createSerializer();

    IPlugin expectedPlugin = this.getMetadataXmlMarketplacePlugin();

    // act
    Map<String, IPlugin> plugins = serializer.getPlugins( pluginsXml );
    IPlugin actualPlugin = plugins.get( expectedPlugin.getId() );

    // assert
    assertThat( actualPlugin, is( equalTo( expectedPlugin ) ) );

    inputStream.close();
  }


  /**
   * Tests that an installed version is properly serialized
   */
  @Test
  public void testGetInstalledVersion() throws IOException {
    // arrange
    FileInputStream inputStream = new FileInputStream( "installedVersion.xml" );
    String installedVersionXml = IOUtils.toString( inputStream );
    IMarketplaceXmlSerializer serializer = this.createSerializer();

    IPluginVersion expectedVersion = this.getInstalledVersionXmlInstalledVersion();

    // act
    IPluginVersion actualVersion = serializer.getInstalledVersion( installedVersionXml );

    // assert
    assertThat( actualVersion, is( equalTo( expectedVersion ) ) );

    inputStream.close();
  }


}
