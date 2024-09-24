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

package org.pentaho.marketplace.domain.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.serialization.IMarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;
import org.pentaho.marketplace.util.web.HttpUtil;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RemoteMetadataPluginProvider implements IRemotePluginProvider {

  // region Properties
  private static final String MARKETPLACE_ENTRIES_URL_FALLBACK = "https://raw.github.com/pentaho/marketplace-metadata/master/marketplace.xml";

  private Log logger = LogFactory.getLog( this.getClass() );
  private Log getLogger() {
    return this.logger;
  }

  @Override
  public URL getUrl() {
    return this.metadataUrl;
  }
  @Override
  public void setUrl( URL metadataUrl ) {
    this.metadataUrl = metadataUrl;
  }
  private URL metadataUrl;

  public IMarketplaceXmlSerializer getXmlSerializer() {
    return this.xmlPluginsSerializer;
  }
  protected RemoteMetadataPluginProvider setXmlSerializer( IMarketplaceXmlSerializer serializer ) {
    this.xmlPluginsSerializer = serializer;
    return this;
  }
  private IMarketplaceXmlSerializer xmlPluginsSerializer;

  // endregion

  // region Constructors
  public RemoteMetadataPluginProvider( IMarketplaceXmlSerializer xmlSerializer ) {
    this.setXmlSerializer( xmlSerializer );

    try {
      this.setUrl( new URL( MARKETPLACE_ENTRIES_URL_FALLBACK ) );
    } catch ( MalformedURLException e ) {
      this.getLogger().error( "Invalid metadata url: " + MARKETPLACE_ENTRIES_URL_FALLBACK, e );
    }
  }
  // endregion

  // region Methods
  public Map<String, IPlugin> getPlugins() {
    // TODO: make dependency explicit
    InputStream inputStream = HttpUtil.getURLInputStream( this.getUrl() );
    Map<String, IPlugin> plugins = this.getXmlSerializer().getPlugins( inputStream );
    return plugins;
  }
  // endregion
}
