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
 * Copyright (c) 2016 - 2017 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.marketplace.domain.model.entities.serialization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.marketplace.domain.model.entities.DevelopmentStage;
import org.pentaho.marketplace.domain.model.entities.MarketEntryType;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IDevelopmentStage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.di.core.xml.XMLParserFactoryProducer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MarketplaceXmlSerializer implements IMarketplaceXmlSerializer {

  // region constants
  private static final String[] EMPTY_STRING_ARRAY = new String[ 0 ];
  protected static final String OSGI_URL_SUFFIX = ".kar";
  // endregion

  // region Properties
  private IPluginFactory pluginFactory;
  private IPluginVersionFactory pluginVersionFactory;
  private ICategoryFactory categoryFactory;

  private Log logger = LogFactory.getLog( this.getClass() );

  private Log getLogger() {
    return this.logger;
  }

  private DocumentBuilderFactory getDocumentBuilderFactory() {
    return this.documentBuilderFactory;
  }

  private DocumentBuilderFactory documentBuilderFactory;

  private XPath xpath;
  // endregion

  public MarketplaceXmlSerializer( IPluginFactory pluginFactory,
                                   IPluginVersionFactory pluginVersionFactory,
                                   ICategoryFactory categoryFactory ) {

    this.pluginFactory = pluginFactory;
    this.pluginVersionFactory = pluginVersionFactory;
    this.categoryFactory = categoryFactory;

    this.xpath = XPathFactory.newInstance().newXPath();

    this.documentBuilderFactory = createDocumentBuilderFactory();
  }

  private DocumentBuilderFactory createDocumentBuilderFactory() {
    DocumentBuilderFactory secureDocBuilderFactory = null;
    try {
      secureDocBuilderFactory = XMLParserFactoryProducer.createSecureDocBuilderFactory();
    } catch ( ParserConfigurationException e ) {
      this.getLogger().error( e );
    }
    return secureDocBuilderFactory;
  }

  @Override public Map<String, IPlugin> getPlugins( InputStream xmlStream ) {
    Map<String, IPlugin> plugins = Collections.emptyMap();
    try {
      DocumentBuilder db = this.getDocumentBuilderFactory().newDocumentBuilder();
      Document document = db.parse( xmlStream );

      plugins = this.getPlugins( document );

    } catch ( Exception e ) {
      this.getLogger().error( "Error getting plugins from marketplace xml.", e );
      // TODO: throw app exception in order to return error in endpoint
    }

    return plugins;
  }

  @Override public Map<String, IPlugin> getPlugins( String xml ) {
    Map<String, IPlugin> plugins = Collections.emptyMap();
    try {
      DocumentBuilder db = this.getDocumentBuilderFactory().newDocumentBuilder();
      Document document = db.parse( new InputSource( new StringReader( xml ) ) );

      plugins = this.getPlugins( document );

    } catch ( Exception e ) {
      this.getLogger().error( "Error getting plugins from marketplace xml: " + xml, e );
      // TODO: throw app exception in order to return error in endpoint
    }

    return plugins;
  }

  @Override public Map<String, IPlugin> getPlugins( Document marketplaceMetadataDocument ) throws
    XPathExpressionException {
    NodeList plugins = marketplaceMetadataDocument.getElementsByTagName( "market_entry" );
    Map<String, IPlugin> pluginMap = new HashMap<>();
    for ( int i = 0; i < plugins.getLength(); i++ ) {
      Element pluginElement = (Element) plugins.item( i );

      IPlugin plugin = getPlugin( pluginElement );
      pluginMap.put( plugin.getId(), plugin );
    }

    return pluginMap;
  }

  private IPlugin getPlugin( Element pluginElement ) throws XPathExpressionException {
    IPlugin plugin = this.pluginFactory.create();

    plugin.setId( getElementChildValue( pluginElement, "id" ) );
    plugin.setName( getElementChildValue( pluginElement, "name" ) );
    plugin.setDescription( getElementChildValue( pluginElement, "description" ) );

    plugin.setAuthorName( getElementChildValue( pluginElement, "author" ) );
    plugin.setAuthorUrl( getElementChildValue( pluginElement, "author_url" ) );
    plugin.setAuthorLogo( getElementChildValue( pluginElement, "author_logo" ) );
    plugin.setImg( getElementChildValue( pluginElement, "img" ) );

    plugin.setSmallImg( getElementChildValue( pluginElement, "small_img" ) );
    plugin.setDocumentationUrl( getElementChildValue( pluginElement, "documentation_url" ) );
    plugin.setInstallationNotes( getElementChildValue( pluginElement, "installation_notes" ) );
    plugin.setLicense( getElementChildValue( pluginElement, "license" ) );
    plugin.setLicenseName( getElementChildValue( pluginElement, "license_name" ) );
    plugin.setLicenseText( getElementChildValue( pluginElement, "license_text" ) );
    plugin.setDependencies( getElementChildValue( pluginElement, "dependencies" ) );
    plugin.setType( MarketEntryType.valueOf( getElementChildValue( pluginElement, "type" ) ) );
    plugin.setCategory( this.getCategory( pluginElement ) );

    NodeList availableVersionsNode =
        (NodeList) this.xpath.evaluate( "versions/version", pluginElement, XPathConstants.NODESET );
    Collection<IPluginVersion> versions = this.getPluginVersions( availableVersionsNode );
    plugin.setVersions( versions );

    NodeList availableScreenshotsNode = (NodeList) xpath.evaluate( "screenshots/screenshot", pluginElement,
        XPathConstants.NODESET );
    String[] screenshots = getScreenshots( availableScreenshotsNode );
    plugin.setScreenshots( screenshots );

    return plugin;
  }

  private IPluginVersion getPluginVersion( Element versionElement ) throws XPathExpressionException {
    IPluginVersion version = this.pluginVersionFactory.create();

    version.setBranch( getElementChildValue( versionElement, "branch" ) );
    version.setName( getElementChildValue( versionElement, "name" ) );
    version.setVersion( getElementChildValue( versionElement, "version" ) );
    String downloadUrl = getElementChildValue( versionElement, "package_url" );
    if ( downloadUrl != null ) {
      downloadUrl = downloadUrl.trim();
      version.setDownloadUrl( downloadUrl );
      version.setIsOsgi( downloadUrl.endsWith( OSGI_URL_SUFFIX ) );
    }
    version.setSamplesDownloadUrl( getElementChildValue( versionElement, "samples_url" ) );
    version.setDescription( getElementChildValue( versionElement, "description" ) );
    version.setChangelog( getElementChildValue( versionElement, "changelog" ) );
    version.setBuildId( getElementChildValue( versionElement, "build_id" ) );
    version.setReleaseDate( getElementChildValue( versionElement, "releaseDate" ) );
    version.setMinParentVersion( getElementChildValue( versionElement, "min_parent_version" ) );
    version.setMaxParentVersion( getElementChildValue( versionElement, "max_parent_version" ) );
    version.setDevelopmentStage( getDevelopmentStage( versionElement ) );

    return version;
  }

  private Collection<IPluginVersion> getPluginVersions( NodeList versionsElement ) throws XPathExpressionException {
    if ( versionsElement.getLength() == 0 ) {
      return Collections.emptyList();
    }

    Collection<IPluginVersion> versions = new ArrayList<>();
    for ( int j = 0; j < versionsElement.getLength(); j++ ) {
      Element versionElement = (Element) versionsElement.item( j );
      IPluginVersion pv = this.getPluginVersion( versionElement );
      versions.add( pv );
    }

    return versions;
  }

  private String[] getScreenshots( NodeList screenshotsElement ) {
    if ( screenshotsElement.getLength() == 0 ) {
      return EMPTY_STRING_ARRAY;
    }

    String[] screenshots = new String[ screenshotsElement.getLength() ];
    for ( int j = 0; j < screenshotsElement.getLength(); j++ ) {
      Element screenshotElement = (Element) screenshotsElement.item( j );
      screenshots[ j ] = screenshotElement.getTextContent();
    }

    return screenshots;
  }

  private String getElementChildValue( Element element, String child ) throws XPathExpressionException {
    Element childElement = (Element) xpath.evaluate( child, element, XPathConstants.NODE );

    if ( childElement != null ) {
      return childElement.getTextContent();
    } else {
      return null;
    }
  }

  private ICategory getCategory( Element pluginElement ) throws XPathExpressionException {
    final String CATEGORY_ELEMENT_NAME = "category";

    Element categoryElement = (Element) xpath.evaluate( CATEGORY_ELEMENT_NAME, pluginElement, XPathConstants.NODE );
    if ( categoryElement == null ) {
      return null;
    }

    return this.getCategoryFromCategoryElement( categoryElement );
  }

  private ICategory getCategoryFromCategoryElement( Element categoryElement )
    throws XPathExpressionException {

    final String PARENT_ELEMENT_NAME = "parent";
    final String NAME_ELEMENT_NAME = "name";

    ICategory parent = null;
    Element parentElement = (Element) xpath.evaluate( PARENT_ELEMENT_NAME, categoryElement, XPathConstants.NODE );
    if ( parentElement != null ) {
      parent = getCategoryFromCategoryElement( parentElement );
    }

    String name = getElementChildValue( categoryElement, NAME_ELEMENT_NAME );
    ICategory category = this.categoryFactory.create( name, parent );
    return category;
  }

  /**
   * Parses the version element to get the development stage
   *
   * @param versionElement where the development stage element is contained
   * @return the parsed development stage
   */
  private IDevelopmentStage getDevelopmentStage( Element versionElement ) throws XPathExpressionException {
    final String DEVELOPMENT_STAGE_ELEMENT_NAME = "development_stage";
    final String DEVELOPMENT_STAGE_LANE_ELEMENT_NAME = "lane";
    final String DEVELOPMENT_STAGE_PHASE_ELEMENT_NAME = "phase";

    Element devStageElement =
        (Element) xpath.evaluate( DEVELOPMENT_STAGE_ELEMENT_NAME, versionElement, XPathConstants.NODE );
    if ( devStageElement == null ) {
      return null;
    }

    String lane = this.getElementChildValue( devStageElement, DEVELOPMENT_STAGE_LANE_ELEMENT_NAME );
    String phase = this.getElementChildValue( devStageElement, DEVELOPMENT_STAGE_PHASE_ELEMENT_NAME );

    // TODO: switch to factory to allow DI?
    return new DevelopmentStage( lane, phase );
  }

  @Override public IPluginVersion getInstalledVersion( String xml ) {
    return this.getInstalledVersion( new InputSource( new StringReader( xml ) ) );
  }

  @Override public IPluginVersion getInstalledVersion( InputSource inputDocument ) {
    IPluginVersion version = null;
    try {
      DocumentBuilder db = this.getDocumentBuilderFactory().newDocumentBuilder();
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
}
