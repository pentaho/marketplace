package org.pentaho.marketplace.domain.model.entities.serialization;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.util.Map;

public interface IMarketplaceXmlSerializer {

  Map<String, IPlugin> getPlugins( InputStream xmlInputStream );

  Map<String, IPlugin> getPlugins( String xml );

  Map<String, IPlugin> getPlugins( Document marketplaceMetadataDocument ) throws
    XPathExpressionException;

  IPluginVersion getInstalledVersion( String xml );

  IPluginVersion getInstalledVersion( InputSource inputDocument );

  IPluginVersion getInstalledVersion( Document installedVersionDocument );
}
