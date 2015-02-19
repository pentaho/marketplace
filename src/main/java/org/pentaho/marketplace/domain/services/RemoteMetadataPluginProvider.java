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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.serialization.MarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.services.interfaces.IRemotePluginProvider;

//import org.pentaho.platform.api.engine.IPluginResourceLoader;

//import org.pentaho.platform.util.web.HttpUtil;
import org.pentaho.marketplace.util.web.HttpUtil;

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

  public URL getUrl() {

    // TODO: Temporary
    String urlPath = MARKETPLACE_ENTRIES_URL_FALLBACK;
    try {
      return new URL( urlPath );
    } catch ( MalformedURLException e ) {
      this.logger.error( "Invalid metadata url: " + urlPath, e );
      return null;
    }

    //return this.metadataUrl;
  }
  public RemoteMetadataPluginProvider setUrl( URL metadataUrl ) {
    this.metadataUrl = metadataUrl;
    return this;
  }
  private URL metadataUrl;

  public MarketplaceXmlSerializer getXmlSerializer() {
    return this.xmlPluginsSerializer;
  }
  protected RemoteMetadataPluginProvider setXmlSerializer( MarketplaceXmlSerializer serializer ) {
    this.xmlPluginsSerializer = serializer;
    return this;
  }
  private MarketplaceXmlSerializer xmlPluginsSerializer;

  protected String getUrlContent( String url ) {
    return HttpUtil.getURLContent( url );
  }
  // endregion


  // region Constructors
  public RemoteMetadataPluginProvider( MarketplaceXmlSerializer xmlSerializer ) {
    this.setXmlSerializer( xmlSerializer );
  }
  // endregion

  /*
  // TODO check dependency...
  private URL getMetadataUrl( IPluginResourceLoader resLoader ) {
      String urlPath = null;
      try {
          urlPath = resLoader.getPluginSetting( getClass(), "settings/marketplace-site" ); //$NON-NLS-1$
      } catch ( Exception e ) {
          logger.debug( "Error getting data access plugin settings", e );
      }

      if ( urlPath == null || "".equals( urlPath ) ) {
          urlPath = MARKETPLACE_ENTRIES_URL_FALLBACK;
      }

      try {
          return new URL( urlPath );
      } catch ( MalformedURLException e ) {
          this.logger.error( "Invalid metadata url: " + urlPath, e );
          return null;
      }
  }
  */


  // region Methods
  @Override
  public Collection<IPlugin> getPlugins() {
    String url = this.getUrl().toString();

    String content = this.getUrlContent( url );
    //Sometimes this call fails. Second attempt is always successful
    if ( StringUtils.isEmpty( content ) ) {
      content = this.getUrlContent( url );
    }

    return this.getXmlSerializer().getPlugins( content );
  }
  // endregion
}
