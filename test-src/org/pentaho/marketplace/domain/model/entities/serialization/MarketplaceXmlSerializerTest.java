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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.pentaho.marketplace.domain.model.entities.DevelopmentStage;
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


public class MarketplaceXmlSerializerTest {
  /*
  public Collection<IPlugin> getPlugins( String xml )

  public Collection<IPlugin> getPlugins( Document marketplaceMetadataDocument )

  private IPlugin getPlugin( Element pluginElement )

  private IPluginVersion getPluginVersion( Element versionElement )

  private Collection<IPluginVersion> getPluginVersions( NodeList versionsElement )

  private String[] getScreenshots( NodeList screenshotsElement )

  private String getElementChildValue( Element element, String child )

  private ICategory getCategory( Element pluginElement )

  private ICategory getCategoryFromCategoryElement( Element categoryElement )



  private IDevelopmentStage getDevelopmentStage( Element versionElement )

  public IPluginVersion getInstalledVersion( String xml )

  public IPluginVersion getInstalledVersion( InputSource inputDocument )

  public IPluginVersion getInstalledVersion( Document installedVersionDocument )
  */

  private IPluginFactory pluginFactory;
  private IPluginVersionFactory pluginVersionFactory;
  private IVersionDataFactory versionDataFactory;
  private ICategoryFactory categoryFactory;


  @Before
  public void setup() {
    this.pluginFactory = new PluginFactory();
    this.pluginVersionFactory = new PluginVersionFactory();
    this.versionDataFactory = new VersionDataFactory();
    this.categoryFactory = new CategoryFactory();
  }

  private MarketplaceXmlSerializer createSerializer() {
    return new MarketplaceXmlSerializer( this.pluginFactory,  this.pluginVersionFactory, this.versionDataFactory, this.categoryFactory );
  }

  @Test
  public void oldTestGetPlugins( ) throws IOException {

    String pluginsXml = IOUtils.toString( new FileInputStream( "test-res/availableplugins.xml" ) );
    MarketplaceXmlSerializer serializer = this.createSerializer();

    Collection<IPlugin> plugins = serializer.getPlugins( pluginsXml );

    Assert.assertTrue( plugins.size() == 3 );

    Iterator<IPlugin> iterator = plugins.iterator();
    IPlugin cdePlugin = iterator.next();
    IPluginVersion cdeVersion = cdePlugin.getVersions().iterator().next();

    Assert.assertEquals( "cde", cdePlugin.getId() );
    Assert.assertEquals( "wt_transparent.png", cdePlugin.getImg() );
    Assert.assertEquals( "wt_transparent_small.png", cdePlugin.getSmallImg() );
    Assert.assertEquals( "Community Dashboard Editor", cdePlugin.getName() );
    Assert.assertEquals( "http://cde.webdetails.org", cdePlugin.getDocumentationUrl() );
    Assert.assertEquals( "The Community Dashboard Editor (CDE) is the outcome of real-world needs: It was born to greatly simplify the creation, edition and rendering of dashboards.\n\nCDE and the technology underneath (CDF, CDA and CCC) allows to develop and deploy dashboards in the Pentaho platform in a fast and effective way.", cdePlugin.getDescription().trim() );
    Assert.assertEquals( "WebDetails", cdePlugin.getAuthorName() );
    Assert.assertEquals( "http://webdetails.pt", cdePlugin.getAuthorUrl() );
    Assert.assertEquals( "http://www.webdetails.pt/ficheiros/CDE-bundle-1.0-RC3.tar.bz2", cdeVersion.getDownloadUrl() );
    Assert.assertEquals( "1.0-RC3", cdeVersion.getVersion() );
    Assert.assertNull( cdePlugin.getInstallationNotes() );

    IPlugin cdaPlugin = iterator.next();
    IPluginVersion cdaVersion = cdaPlugin.getVersions().iterator().next();
    Assert.assertNull( cdaVersion.getChangelog() );

    IPlugin cdfPlugin = iterator.next();
    IPluginVersion cdfVersion = cdfPlugin.getVersions().iterator().next();
    Assert.assertEquals( "Changelog", cdfVersion.getChangelog() );
    Assert.assertEquals( "http://localhost:8080/cdf-1.0.samples.zip", cdfVersion.getSamplesDownloadUrl() );
    Assert.assertEquals( "Notes after install", cdfPlugin.getInstallationNotes() );

  }

  @Test
  public void oldTestGetPluginsAlternativeVersions( ) throws IOException {

    String pluginsXml = IOUtils.toString( new FileInputStream( "test-res/availableplugins_differentversions.xml" ) );
    MarketplaceXmlSerializer serializer = this.createSerializer();

    Collection<IPlugin> plugins = serializer.getPlugins( pluginsXml );

    Assert.assertEquals( 1, plugins.size() );

    IPlugin plugin = plugins.iterator().next();

    Collection<IPluginVersion> alternativeVersions = plugin.getVersions();
    Assert.assertEquals( 2, alternativeVersions.size() );

    Iterator<IPluginVersion> versionIterator = plugin.getVersions().iterator();
    IPluginVersion releaseCandidateVersion =  versionIterator.next();

    Assert.assertEquals( "RC", releaseCandidateVersion.getBranch() );
    Assert.assertEquals( "Release Candidate", releaseCandidateVersion.getName() );
    Assert.assertEquals( "ChangeLog for RC", releaseCandidateVersion.getChangelog() );
    Assert.assertEquals( "This is RC1 - pretty cool version but still not quite there", releaseCandidateVersion.getDescription() );
    Assert.assertEquals( "http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3.tar.bz2", releaseCandidateVersion.getDownloadUrl() );
    Assert.assertEquals( "http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3-samples.tar.bz2", releaseCandidateVersion.getSamplesDownloadUrl() );
    Assert.assertNull( releaseCandidateVersion.getBuildId() );

    IPluginVersion trunkVersion = plugin.getVersionByBranch( "TRUNK" );
    Assert.assertNotNull( trunkVersion );
    Assert.assertEquals( "TRUNK", trunkVersion.getBranch() );
    Assert.assertEquals( "Trunk", trunkVersion.getName() );
    Assert.assertEquals( "Change Log for TRUNK", trunkVersion.getChangelog() );
    Assert.assertEquals( "135", trunkVersion.getBuildId() );
  }

  @Test
  public void testGetPluginsSameOrderAsXml() throws IOException {
    // arrange
    FileInputStream inputStream = new FileInputStream( "test-res/metadata.xml" );
    String pluginsXml = IOUtils.toString( inputStream );
    MarketplaceXmlSerializer serializer = this.createSerializer();

    String[] expectedIds = new String[] {  "marketplace", "pentaho-cdf", "cda", "languagePack_ja" };

    // act
    Collection<IPlugin>  pluginCollection = serializer.getPlugins( pluginsXml );

    // assert
    IPlugin[] plugins = pluginCollection.toArray( new IPlugin[ pluginCollection.size() ] );

    for ( int i = 0; i < expectedIds.length; i++ ) {
      Assert.assertEquals( expectedIds[i], plugins[i].getId() );
    }

    inputStream.close();
  }

  @Test
  public void testGetPluginsOnlyParsePlatformMarketEntries() throws IOException {
    // arrange
    FileInputStream inputStream = new FileInputStream( "test-res/metadata.xml" );
    String pluginsXml = IOUtils.toString( inputStream );
    MarketplaceXmlSerializer serializer = this.createSerializer();

    Collection<String> platformPlugins = new ArrayList<String>( Arrays.asList( "marketplace", "pentaho-cdf", "cda", "languagePack_ja" ) );

    // act
    Collection<IPlugin>  plugins = serializer.getPlugins( pluginsXml );

    // assert
    for ( IPlugin plugin : plugins ) {
      String pluginId = plugin.getId();
      Assert.assertTrue( platformPlugins.contains( pluginId ) );
    }

    inputStream.close();
  }

  @Test
  public void testGetPluginsPluginParsedOk() throws IOException {
    // arrange

    IPlugin expectedPlugin = pluginFactory.create();
    expectedPlugin.setId( "marketplace" );
    expectedPlugin.setName( "Pentaho Marketplace" );
    expectedPlugin.setImg( "http://pentaho.com/sites/all/themes/pentaho/_media/logo-pentaho.svg" );
    expectedPlugin.setSmallImg( "http://www.webdetails.pt/ficheiros/mk_plugin.png" );
    expectedPlugin.setDocumentationUrl( "http://wiki.pentaho.com/display/PMOPEN/Pentaho+BI+Server+Marketplace+Plugin" );
    expectedPlugin.setDescription( "\n"
      + "      Pentaho Marketplace allows users to explore and test the plugins\n"
      + "      that are most relevant to them. This means high quality and useful\n"
      + "      plugins that users can use to get the most out of their business.\n"
      + "    " );
    expectedPlugin.setAuthorName( "Pentaho" );
    expectedPlugin.setAuthorUrl( "http://pentaho.com" );
    expectedPlugin.setAuthorLogo( "http://pentaho.com/sites/all/themes/pentaho/_media/logo-pentaho.svg" );
    expectedPlugin.setInstallationNotes( "These are the installation notes." );
    expectedPlugin.setDependencies( "No dependencies." );
    expectedPlugin.setLicense( "GLPL v2" );
    expectedPlugin.setLicenseName( "License name glpl v2." );
    expectedPlugin.setLicenseText( "You are allowed to do anything you like." );

    ICategory parentCategory = categoryFactory.create( "Apps" );
    ICategory category = categoryFactory.create( "Admin", parentCategory );
    expectedPlugin.setCategory( category );

    IPluginVersion trunk4XVersion = pluginVersionFactory.create();
    trunk4XVersion.setBranch( "TRUNK" );
    trunk4XVersion.setVersion( "TRUNK-SNAPSHOT" );
    trunk4XVersion.setBuildId( "1" );
    trunk4XVersion.setName( "Latest snapshot build" );
    trunk4XVersion.setDownloadUrl( "http://ci.pentaho.com/job/marketplace-4.8/lastSuccessfulBuild/artifact/dist/marketplace-plugin-TRUNK-SNAPSHOT.zip" );
    trunk4XVersion.setDescription( "The latest development snapshot build." );
    trunk4XVersion.setMinParentVersion( "1.0" );
    trunk4XVersion.setMaxParentVersion( "4.9" );
    IPluginVersion trunk5XVersion = pluginVersionFactory.create();
    trunk5XVersion.setBranch( "TRUNK" );
    trunk5XVersion.setVersion( "TRUNK-SNAPSHOT" );
    trunk5XVersion.setBuildId( "49" );
    trunk5XVersion.setName( "Latest snapshot build" );
    trunk5XVersion.setDownloadUrl( "http://repository.pentaho.org/artifactory/pentaho/pentaho/marketplace/5.1-SNAPSHOT/marketplace-5.1-SNAPSHOT.zip" );
    trunk5XVersion.setSamplesDownloadUrl( "http://testing.pentaho.com/mySamples.zip" );
    trunk5XVersion.setDescription( "Build for Pentaho 5.0" );
    trunk5XVersion.setMinParentVersion( "5.0" );
    trunk5XVersion.setMaxParentVersion( "5.1.99" );
    trunk5XVersion.setDevelopmentStage( new DevelopmentStage( "Customer", "2" ) );
    trunk5XVersion.setChangelog( "Lots of stuff changed." );

    Collection<IPluginVersion> versions = new ArrayList<IPluginVersion>();
    versions.add( trunk4XVersion );
    versions.add( trunk5XVersion );
    expectedPlugin.setVersions( versions );

    String[] screenshots = new String[] {
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-01.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-02.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-03.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-04.png",
      "https://raw2.github.com/pentaho/marketplace/master/marketplace-resources/marketplace-05.png"
    };
    expectedPlugin.setScreenshots( screenshots );

    // xsd properties not used in plugins (Platform type market entries)
    //plugin.setSupportLevel( "" );
    //plugin.setSupportOrganization( "" );
    //plugin.setForumUrl( "" );
    //plugin.setCasesUrl( "" );

    FileInputStream inputStream = new FileInputStream( "test-res/metadata.xml" );
    String pluginsXml = IOUtils.toString( inputStream );
    MarketplaceXmlSerializer serializer = this.createSerializer();


    // act
    Collection<IPlugin>  plugins = serializer.getPlugins( pluginsXml );
    IPlugin actualPlugin = plugins.iterator().next();

    // assert
    Assert.assertEquals( expectedPlugin, actualPlugin );

    inputStream.close();
  }




}
