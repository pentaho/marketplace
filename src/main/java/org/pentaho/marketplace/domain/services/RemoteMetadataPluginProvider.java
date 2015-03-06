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
import java.util.Collection;

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
  @Override
  public Collection<IPlugin> getPlugins() {
    InputStream inputStream = HttpUtil.getURLInputStream( this.getUrl() );
    Collection<IPlugin> plugins = this.getXmlSerializer().getPlugins( inputStream );
    return plugins;
  }
  // endregion
}
