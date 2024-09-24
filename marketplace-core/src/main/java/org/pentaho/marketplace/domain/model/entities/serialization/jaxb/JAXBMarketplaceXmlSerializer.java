/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.marketplace.domain.model.entities.serialization.jaxb;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto.Market;
import org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto.Version;

import org.pentaho.marketplace.domain.model.entities.DevelopmentStage;
import org.pentaho.marketplace.domain.model.entities.MarketEntryType;
import org.pentaho.marketplace.domain.model.entities.interfaces.IDevelopmentStage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.serialization.IMarketplaceXmlSerializer;

import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.util.XmlParserFactoryProducer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPathExpressionException;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JAXBMarketplaceXmlSerializer implements IMarketplaceXmlSerializer {

  // region Constants
  private static final String OSGI_URL_SUFFIX = ".kar";
  // endregion

  // region Properties
  private JAXBContext jaxbContext;
  private Unmarshaller jaxbUnmarshaller;

  private ICategoryFactory categoryFactory;
  private IPluginFactory pluginFactory;
  private IPluginVersionFactory pluginVersionFactory;

  protected Log getLogger() {
    return this.logger;
  }
  private Log logger = LogFactory.getLog( this.getClass() );
  // endregion

  // region Constructors
  public JAXBMarketplaceXmlSerializer( IPluginFactory pluginFactory,
                                       IPluginVersionFactory pluginVersionFactory,
                                       ICategoryFactory categoryFactory ) {

    this.pluginFactory = pluginFactory;
    this.pluginVersionFactory = pluginVersionFactory;
    this.categoryFactory = categoryFactory;

    try {
      this.jaxbContext = JAXBContext.newInstance( Market.class );
      this.jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    } catch ( JAXBException e ) {
      this.getLogger().error( "Error initializing JAXBMarketplaceXmlSerializer.", e );
    }
  }
  // endregion

  // regions Methods
  @Override public Map<String, IPlugin> getPlugins( InputStream xmlInputStream ) {
    try {
      SAXParserFactory spf = XmlParserFactoryProducer.createSecureSAXParserFactory();
      Source xmlSource = new SAXSource( spf.newSAXParser().getXMLReader(), new InputSource( xmlInputStream ) );
      Market market = (Market) jaxbUnmarshaller.unmarshal( xmlSource );
      Map<String, IPlugin> plugins = this.toPlugins( market );
      return plugins;

    } catch ( JAXBException | SAXException | ParserConfigurationException e ) {
      this.getLogger().debug( "Failed trying to parse invalid marketplace metadata." );
      return Collections.emptyMap();
    }
  }

  @Override public Map<String, IPlugin> getPlugins( String xml ) {
    try {
      SAXParserFactory spf = XmlParserFactoryProducer.createSecureSAXParserFactory();
      Source xmlSource = new SAXSource( spf.newSAXParser().getXMLReader(), new InputSource( new StringReader( xml ) ) );
      Market market = (Market) jaxbUnmarshaller.unmarshal( xmlSource );
      Map<String, IPlugin> plugins = this.toPlugins( market );
      return plugins;

    } catch ( JAXBException | SAXException | ParserConfigurationException e ) {
      this.getLogger().debug( "Failed trying to parse invalid marketplace metadata." );
      return Collections.emptyMap();
    }
  }

  @Override public Map<String, IPlugin> getPlugins( Document marketplaceMetadataDocument )
    throws XPathExpressionException {
    try {
      Market market = (Market) jaxbUnmarshaller.unmarshal( marketplaceMetadataDocument );
      Map<String, IPlugin> plugins = this.toPlugins( market );
      return plugins;

    } catch ( JAXBException e ) {
      this.getLogger().debug( "Failed trying to parse invalid marketplace metadata." );
      return Collections.emptyMap();
    }
  }

  @Override public IPluginVersion getInstalledVersion( String xml ) {
    return this.getInstalledVersion( new InputSource( new StringReader( xml ) ) );
  }

  @Override public IPluginVersion getInstalledVersion( InputSource inputDocument ) {
    IPluginVersion version = null;
    try {
      DocumentBuilderFactory dbf = XmlParserFactoryProducer.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.parse( inputDocument );

      version = this.getInstalledVersion( document );

    } catch ( Exception e ) {
      this.getLogger().error( "Error getting plugin version from version xml.", e );
      // TODO: throw app exception in order to return error in endpoint
    }

    return version;
  }

  @Override public IPluginVersion getInstalledVersion( Document installedVersionDocument ) {
    NodeList versionElements = installedVersionDocument.getElementsByTagName( "version" );

    if ( versionElements.getLength() == 0 ) {
      return null;
    }

    Element versionElement = (Element) versionElements.item( 0 );

    IPluginVersion version = this.pluginVersionFactory.create();
    version.setBranch( versionElement.getAttribute( "branch" ) );
    version.setVersion( versionElement.getTextContent() );
    version.setBuildId( versionElement.getAttribute( "buildId" ) );

    return version;
  }

  // region DTO => Entity mapping

  private Map<String, IPlugin> toPlugins( Market market ) {
    Collection<Market.MarketEntry> marketEntries = market.getMarketEntry();
    Map<String, IPlugin> plugins = new HashMap<>( marketEntries.size() );

    int rank = 0;
    for ( Market.MarketEntry entry : marketEntries ) {
      plugins.put( entry.getId(), this.toPlugin( entry, rank++ ) );
    }

    return plugins;
  }

  private IPlugin toPlugin( Market.MarketEntry entry, int rank ) {
    IPlugin plugin = this.pluginFactory.create();

    //fill the instance
    plugin.setRank( rank );
    plugin.setId( entry.getId() );
    plugin.setName( entry.getName() );
    plugin.setType( this.toEntryType( entry.getType() ) );
    plugin.setImg( entry.getImg() );
    plugin.setSmallImg( entry.getSmallImg() );
    plugin.setDocumentationUrl( entry.getDocumentationUrl() );
    plugin.setDescription( entry.getDescription() );
    plugin.setAuthorName( entry.getAuthor() );
    plugin.setAuthorUrl( entry.getAuthorUrl() );
    plugin.setAuthorLogo( entry.getAuthorLogo() );
    plugin.setInstallationNotes( entry.getInstallationNotes() );
    plugin.setDependencies( entry.getDependencies() );
    plugin.setLicense( entry.getLicense() );
    plugin.setLicenseName( entry.getLicenseName() );
    plugin.setLicenseText( entry.getLicenseText() );

    List<Version> versions = ( entry.getVersions() != null )
        ? entry.getVersions().getVersion()
        : new ArrayList<Version>( 0 );
    plugin.setVersions( this.toVersions( versions ) );

    List<String> screenshots = ( entry.getScreenshots() != null )
        ? entry.getScreenshots().getScreenshot()
        : new ArrayList<String>( 0 );
    plugin.setScreenshots( screenshots.toArray( new String[ screenshots.size() ] ) );

    plugin.setCategory( this.toCategory( entry.getCategory() ) );

    return plugin;
  }

  private Collection<IPluginVersion> toVersions( List<Version> versions ) {
    Collection<IPluginVersion> pluginVersions = new ArrayList<>( versions.size() );

    for ( Version version : versions ) {
      pluginVersions.add( this.toVersion( version ) );
    }

    return pluginVersions;
  }

  private IPluginVersion toVersion( Version version ) {
    //get new pluginVersion instance
    IPluginVersion pluginVersion = this.pluginVersionFactory.create();

    //fill the instance
    pluginVersion.setBranch( version.getBranch() );
    pluginVersion.setName( version.getName() );
    String downloadUrl = version.getPackageUrl();
    if ( downloadUrl != null ) {
      downloadUrl = downloadUrl.trim();
      pluginVersion.setDownloadUrl( downloadUrl );
      pluginVersion.setIsOsgi( downloadUrl.endsWith( OSGI_URL_SUFFIX ) );
    }
    pluginVersion.setVersion( version.getVersion() );
    pluginVersion.setSamplesDownloadUrl( version.getSamplesUrl() );
    pluginVersion.setDescription( version.getDescription() );
    pluginVersion.setChangelog( version.getChangelog() );
    pluginVersion.setBuildId( version.getBuildId() );
    //pluginVersion.setReleaseDate( version.getReleaseDate() );
    pluginVersion.setMinParentVersion( version.getMinParentVersion() );
    pluginVersion.setMaxParentVersion( version.getMaxParentVersion() );

    // TODO: use factory for DI?
    org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto.DevelopmentStage dtoDevStage = version.getDevelopmentStage();
    if ( dtoDevStage != null ) {
      IDevelopmentStage devStage = new DevelopmentStage( dtoDevStage.getLane().value(),
          String.valueOf( dtoDevStage.getPhase() ) );
      pluginVersion.setDevelopmentStage( devStage );
    }

    //return the instance
    return pluginVersion;
  }

  private MarketEntryType toEntryType(
      org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto.MarketEntryType entryType ) {
    return MarketEntryType.getMarketEntryType( entryType.value() );
  }

  private ICategory toCategory( org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto.Category categoryDto ) {
    if ( categoryDto == null ) {
      return null;
    }

    ICategory parent = null;
    if ( categoryDto.getParent() != null ) {
      parent = this.toCategory( categoryDto.getParent() );
    }

    ICategory category = this.categoryFactory.create( categoryDto.getName(), parent );
    return category;
  }
  // endregion

  // endregion
}
