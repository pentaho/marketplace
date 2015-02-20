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
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
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
import java.util.ArrayList;
import java.util.Collection;

public class KettlePluginService extends BasePluginService {

  // region Properties
  private PluginRegistry getPluginRegistry() {
    return PluginRegistry.getInstance();
  }

  public IPluginVersionFactory getPluginVersionFactory() {
    return this.pluginVersionFactory;
  }
  public KettlePluginService setPluginVersionFactory( IPluginVersionFactory factory ) {
    this.pluginVersionFactory = factory;
    return this;
  }
  private IPluginVersionFactory pluginVersionFactory;
  // endregion

  // region Constructor
  public KettlePluginService( IRemotePluginProvider metadataPluginsProvider,
                              IVersionDataFactory versionDataFactory,
                              IDomainStatusMessageFactory domainStatusMessageFactory,
                              IPluginVersionFactory pluginVersionFactory ) {
    super( metadataPluginsProvider, versionDataFactory, domainStatusMessageFactory );

    this.setPluginVersionFactory( pluginVersionFactory );
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
  protected IPluginVersion getInstalledPluginVersion( IPlugin plugin ) {
    String pluginFolder = buildPluginsFolderPath( plugin ) + File.separator + plugin.getId();
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

        IPluginVersion version = this.getPluginVersionFactory().create();
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
  }

  @Override
  protected Collection<String> getInstalledPluginIds() {
    Collection<String> pluginIds = new ArrayList<>();
    PluginRegistry pluginRegistry = this.getPluginRegistry();

    for ( Class<? extends PluginTypeInterface> pluginType : pluginRegistry.getPluginTypes() ) {
      for( PluginInterface plugin : pluginRegistry.getPlugins( pluginType) ) {
        for( String pluginId : plugin.getIds() ) {
          pluginIds.add( pluginId );
        }
      }
    }

    return pluginIds;
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
        if ( !( plugin instanceof IPlugin ) ) {
          return false;
        }
        IPlugin castedPlugin = (IPlugin) plugin;
        return castedPlugin.getType() != MarketEntryType.Platform;
      }
    } );

    return plugins;
  }



  /**
   * Builds and returns the path to the plugins folder.
   *
   * @param marketEntry
   * @return String the path to the plugins folder.
   */
  public static String buildPluginsFolderPath( final IPlugin marketEntry ) {
    PluginInterface plugin = getPluginObject( marketEntry.getId() );
    if ( plugin != null && plugin.getPluginDirectory() != null ) {
      return new File( plugin.getPluginDirectory().getFile() ).getParent();
    } else {
      String subfolder = getInstallationSubfolder( marketEntry );

      // Use current directory (should be the Kettle distribution directory) as the root folder to install plugins
      // This is because plugin types are not guaranteed to search the ~/.kettle folder for plugins.
      return "plugins" + ( subfolder == null ? "" : Const.FILE_SEPARATOR + subfolder );
    }
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
  public static String getInstallationSubfolder( IPlugin marketEntry ) {
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
      /*
      case Repository:
        subfolder = "repositories";
        break;
        */
      case HadoopShim:
        subfolder = "pentaho-big-data-plugin" + File.separator + "hadoop-configurations";
        break;
      /*
      case General:
      */
      case Mixed:
        subfolder = "";
        break;

      default:
        subfolder = null;
    }
    return subfolder;
  }

}
