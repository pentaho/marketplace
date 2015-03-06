package org.pentaho.marketplace.domain.model.entities.serialization;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public interface IMarketplaceXmlSerializer {

  Collection<IPlugin> getPlugins( InputStream xmlInputStream );

  Collection<IPlugin> getPlugins( String xml );

  Collection<IPlugin> getPlugins( Document marketplaceMetadataDocument ) throws
    XPathExpressionException;

  IPluginVersion getInstalledVersion( String xml );

  IPluginVersion getInstalledVersion( InputSource inputDocument );

  IPluginVersion getInstalledVersion( Document installedVersionDocument );
}
