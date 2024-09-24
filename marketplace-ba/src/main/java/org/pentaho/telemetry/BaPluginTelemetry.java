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

package org.pentaho.telemetry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.pentaho.marketplace.util.XmlParserFactoryProducer;
import org.pentaho.platform.api.util.IVersionHelper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Creates a telemetry service for a specific BA plugin (supports both OSGi or non-OSGi plugins).
 */
public class BaPluginTelemetry extends TelemetryService {

  private static Log logger = LogFactory.getLog( BaPluginTelemetry.class );
  private static String UNKNOWN = "unknown";

  public BaPluginTelemetry( String pluginName,
                            String telemetryUrl,
                            boolean telemetryEnabled,
                            ITelemetryHandler telemetryHandler ) {
    super( pluginName, getPluginVersion( pluginName ), getBaVersion(), telemetryUrl, telemetryEnabled,
      telemetryHandler );
  }

  public BaPluginTelemetry( Bundle bundle,
                            String telemetryUrl,
                            boolean telemetryEnabled,
                            ITelemetryHandler telemetryHandler ) {
    super( bundle.getSymbolicName(), getPluginVersion( bundle ), getBaVersion(), telemetryUrl, telemetryEnabled,
      telemetryHandler );
  }

  private static String getPluginVersion( String pluginName ) {
    try {
      String versionPath =
          PentahoSystem.getApplicationContext().getSolutionPath( "system/" + pluginName + "/version.xml" );
      InputStream versionFile = new FileInputStream( new File( versionPath ) );
      return parsePluginVersion( versionFile );
    } catch ( FileNotFoundException fnfe ) {
      logger.debug( "Could not find file version.xml", fnfe );
    }
    return UNKNOWN;
  }

  private static String getPluginVersion( Bundle bundle ) {
    try {
      URL versionFileUrl = bundle.getResource( "version.xml" );
      InputStream versionFile = versionFileUrl.openConnection().getInputStream();
      return parsePluginVersion( versionFile );
    } catch ( NullPointerException npe ) {
      logger.debug( "Could not find resource file version.xml", npe );
    } catch ( IOException ioe ) {
      logger.debug( "Could not open file version.xml", ioe );
    }
    return UNKNOWN;
  }

  private static String parsePluginVersion( InputStream versionFile ) {
    try {
      DocumentBuilderFactory dbf = XmlParserFactoryProducer.createSecureDocBuilderFactory();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse( versionFile );
      NodeList nodes = doc.getElementsByTagName( "version" );
      if ( nodes.getLength() >= 1 ) {
        Element versionElement = (Element) nodes.item( 0 );
        return versionElement.getAttribute( "branch" ) + "-" + versionElement.getTextContent();
      }
    } catch ( Exception e ) {
      logger.debug( "Could not parse plugin version from version.xml", e );
    }
    return UNKNOWN;
  }

  private static String getBaVersion() {
    String platformVersion;
    VersionInfo versionInfo;
    IVersionHelper versionHelper = PentahoSystem.get( IVersionHelper.class, null );
    if ( versionHelper != null ) {
      versionInfo = VersionHelper.getVersionInfo( versionHelper.getClass() );
    } else {
      versionInfo = VersionHelper.getVersionInfo( PentahoSystem.class );
    }
    platformVersion = versionInfo.getProductID() + "-" + versionInfo.getVersionNumber();
    return platformVersion;
  }
}
