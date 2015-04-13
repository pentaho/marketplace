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
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

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
