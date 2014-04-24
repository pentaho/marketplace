package org.pentaho.marketplace.domain.services;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.marketplace.domain.model.complexTypes.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.interfaces.IPluginService;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;
import org.pentaho.platform.util.web.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginService implements IPluginService {

  //region Attributes
  private Log logger = LogFactory.getLog( this.getClass() );
  private XPath xpath;
  private IPluginFactory pluginFactory;
  private IPluginVersionFactory pluginVersionFactory;
  private IVersionDataFactory versionDataFactory;
  //endregion

  //region Constructors
  @Autowired
  public PluginService( IPluginFactory pluginFactory, IPluginVersionFactory pluginVersionFactory,
                        IVersionDataFactory versionDataFactory ) {

    //initialize dependencies
    this.pluginFactory = pluginFactory;
    this.pluginVersionFactory = pluginVersionFactory;
    this.versionDataFactory = versionDataFactory;

    this.xpath = XPathFactory.newInstance().newXPath();
  }
  //endregion

  //region Methods
  protected String discoverInstalledVersion( IPlugin plugin ) {

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

  protected boolean withinParentVersion( IPluginVersion pv ) {
    // need to compare plugin version min and max parent with system version.
    // replace the version of the xml url path with the current release version:
    VersionInfo versionInfo = VersionHelper.getVersionInfo( PentahoSystem.class );
    String v = versionInfo.getVersionNumber();
    return this.versionDataFactory.create( v ).within( this.versionDataFactory.create( pv.getMinParentVersion() ),
      this.versionDataFactory.create( pv.getMaxParentVersion() ) );
  }

  protected String getElementChildValue( Element element, String child ) {
    NodeList list = element.getElementsByTagName( child );
    if ( list.getLength() >= 1 ) {
      return list.item( 0 ).getTextContent();
    } else {
      return null;
    }
  }

  protected String getMarketplaceSiteContent() {
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

  protected Collection<IPlugin> loadPluginsFromSite() {
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

        plugin.setCompany( getElementChildValue( element, "author" ) );
        plugin.setCompanyUrl( getElementChildValue( element, "author_url" ) );
        plugin.setCompanyLogo( getElementChildValue( element, "author_logo" ) );
        plugin.setImg( getElementChildValue( element, "img" ) );

        plugin.setSmallImg( getElementChildValue( element, "small_img" ) );
        plugin.setLearnMoreUrl( getElementChildValue( element, "documentation_url" ) );
        plugin.setInstallationNotes( getElementChildValue( element, "installation_notes" ) );
        plugin.setLicense( getElementChildValue( element, "license" ) );
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
  public IStatusMessage installPlugin( String pluginId, String versionBranch ) {
    return null;
  }

  @Override
  public IStatusMessage uninstallPlugin( String pluginId ) {
    return null;
  }
  //endregion
}
