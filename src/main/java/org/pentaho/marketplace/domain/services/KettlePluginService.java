package org.pentaho.marketplace.domain.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.PluginTypeInterface;
import org.pentaho.marketplace.domain.model.entities.MarketEntryType;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;
import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;

public class KettlePluginService extends BasePluginService {

  // region Constructor
  public KettlePluginService( IRemotePluginProvider metadataPluginsProvider,
                              IVersionDataFactory versionDataFactory,
                              IDomainStatusMessageFactory domainStatusMessageFactory ) {
    super( metadataPluginsProvider, versionDataFactory, domainStatusMessageFactory );
  }
  // endregion

  @Override
  protected boolean hasMarketplacePermission() {
    return true;
  }

  @Override
  protected void unloadPlugin( String pluginId ) {

  }

  @Override
  protected IPluginVersion getInstalledPluginVersion( String pluginId ) {

    return null;

    /*
    String pluginFolder = buildPluginsFolderPath( marketEntry ) + File.separator + pluginId;
    File pluginFolderFile = new File( pluginFolder );

    if ( !pluginFolderFile.exists() ) {
      return null;
    }

    String versionPath = pluginFolder + File.separator + "version.xml";
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    FileReader reader = null;
    try {
      File file = new File( versionPath );
      if ( !file.exists() ) {
        return null;
      }
      DocumentBuilder db = dbf.newDocumentBuilder();
      reader = new FileReader( versionPath );
      Document dom = db.parse( new InputSource( reader ) );
      NodeList versionElements = dom.getElementsByTagName( "version" );
      if ( versionElements.getLength() >= 1 ) {
        Element versionElement = (Element) versionElements.item( 0 );

        // TODO: instantiate version
        IPluginVersion version;
        version.setBuildId( versionElement.getAttribute( "buildId" ) );
        version.setBranch( versionElement.getAttribute( "branch" ) );
        version.setVersion( versionElement.getTextContent() );

        return version;
      }

    } catch ( Exception e ) {
      e.printStackTrace();
    } finally {
      try {
        if ( reader != null ) {
          reader.close();
        }
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }

    return null;
    */
  }

  @Override
  protected Collection<String> getInstalledPluginIds() {

    return Collections.EMPTY_LIST;

    //return null;
  }

  @Override
  protected boolean executeInstall( IPlugin plugin, IPluginVersion version ) {
    return false;
  }

  @Override
  protected boolean executeUninstall( IPlugin plugin ) {
    return false;
  }

  @Override
  public Collection<IPlugin> getPlugins() {
    Collection<IPlugin> plugins = super.getPlugins();

    // remove non PDI plugins
    CollectionUtils.filter( plugins, new Predicate() {
      @Override public boolean evaluate( Object plugin ) {
        if ( !(plugin instanceof IPlugin) ) {
          return false;
        }
        IPlugin castedPlugin = (IPlugin) plugin;
        return castedPlugin.getType() != MarketEntryType.Platform;
      }
    });

    return plugins;
  }



  /**
   * Builds and returns the path to the plugins folder.
   *
   * @param marketEntry
   * @return String the path to the plugins folder.
   */
  public static String buildPluginsFolderPath( final IPlugin marketEntry ) throws Exception {

    throw new Exception( "Not implemented" );
    /*
    PluginInterface plugin = getPluginObject( marketEntry.getId() );
    if ( plugin != null && plugin.getPluginDirectory() != null ) {
      return new File( plugin.getPluginDirectory().getFile() ).getParent();
    } else {
      String subfolder = getInstallationSubfolder( marketEntry );

      // Use current directory (should be the Kettle distribution directory) as the root folder to install plugins
      // This is because plugin types are not guaranteed to search the ~/.kettle folder for plugins.
      return "plugins" + ( subfolder == null ? "" : Const.FILE_SEPARATOR + subfolder );
    }
    */
  }

  /**
   * Find the plugin object related to a pluginId.
   *
   * @param pluginId
   *          id of plugin
   * @return plugin object
   */
  private static PluginInterface getPluginObject( String pluginId ) {
    for ( Class<? extends PluginTypeInterface> pluginType : PluginRegistry.getInstance().getPluginTypes() ) {
      if ( PluginRegistry.getInstance().findPluginWithId( pluginType, pluginId ) != null ) {
        return PluginRegistry.getInstance().findPluginWithId( pluginType, pluginId );
      }
    }
    return null;
  }

  /**
   * Returns the folder name for the MarketEntries type.
   *
   * @param marketEntry
   * @return
   */
  public static String getInstallationSubfolder( IPlugin marketEntry ) throws Exception {
    //TODO IMPLEMENT

    throw new Exception( "Not Implemented" );

    /*
    String subfolder = null;
    switch ( marketEntry.getType() ) {
      case Step:
        subfolder = "steps";
        break;
      case JobEntry:
        subfolder = "jobentries";
        break;
      case Partitioner:
        subfolder = "steps";
        break;
      case SpoonPlugin:
        subfolder = "spoon";
        break;
      case Database:
        subfolder = "databases";
        break;
      case Repository:
        subfolder = "repositories";
        break;
      case HadoopShim:
        subfolder = "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
        break;
      case Mixed:
      case General:
        subfolder = "";
        break;

      default:
        subfolder = null;
    }
    return subfolder;
    */
  }

}
