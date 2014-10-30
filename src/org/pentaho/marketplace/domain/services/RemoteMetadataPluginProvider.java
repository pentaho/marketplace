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
import org.pentaho.marketplace.domain.services.interfaces.IPluginProvider;
import org.pentaho.platform.util.web.HttpUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class RemoteMetadataPluginProvider implements IPluginProvider {

  // region Properties
  private Log logger = LogFactory.getLog( this.getClass() );
  private Log getLogger() {
    return this.logger;
  }

  public URL getMetadataUrl() {
    return this.metadataUrl;
  }
  public RemoteMetadataPluginProvider setMetadataUrl( URL metadataUrl ) {
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

  private DocumentBuilderFactory getDocumentBuilderFactory() {
    return this.documentBuilderFactory;
  }
  protected RemoteMetadataPluginProvider setDocumentBuilderFactory( DocumentBuilderFactory factory ) {
    this.documentBuilderFactory = factory;
    return this;
  }
  private DocumentBuilderFactory documentBuilderFactory;
  // endregion


  // region Constructors
  public RemoteMetadataPluginProvider( MarketplaceXmlSerializer xmlSerializer ) {
    this.setXmlSerializer( xmlSerializer );

    this.setDocumentBuilderFactory( DocumentBuilderFactory.newInstance() );
  }
  // endregion

  // region Methods
  @Override
  public Collection<IPlugin> getPlugins() {
    String url = this.getMetadataUrl().toString();
    String content = HttpUtil.getURLContent( url );

    //Sometimes this call fails. Second attemp is always succesfull
    if ( StringUtils.isEmpty( content ) ) {
      content = HttpUtil.getURLContent( url );
    }
    DocumentBuilderFactory dbf = this.getDocumentBuilderFactory();
    Collection<IPlugin> plugins;
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document dom = db.parse( new InputSource( new StringReader( content ) ) );

      plugins = this.getXmlSerializer().getPlugins( dom );

    } catch ( Exception e ) {
      this.getLogger().error( "Error getting metadata from " + url, e );
      plugins = Collections.emptyList();
    }

    return plugins;
  }
  // endregion
}
