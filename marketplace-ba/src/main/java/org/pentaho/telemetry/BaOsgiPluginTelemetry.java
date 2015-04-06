/*!
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
* Copyright (c) 2002-2015 Pentaho Corporation. All rights reserved.
*/

package org.pentaho.telemetry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.util.IVersionHelper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class BaOsgiPluginTelemetry extends TelemetryService {

  private static Log logger = LogFactory.getLog( BaOsgiPluginTelemetry.class );

  // region Constructors

  public BaOsgiPluginTelemetry( Bundle bundle,
                                String telemetryUrl,
                                boolean telemetryEnabled,
                                ITelemetryHandler telemetryHandler ) {
    super( bundle.getSymbolicName(), telemetryUrl, telemetryEnabled, telemetryHandler );

    this.setPluginVersion( this.getPluginVersion( bundle ) );
    this.setPlatformVersion( this.getBaPlatformVersion() );
  }

  // endregion

  // region Methods

  private String getPluginVersion( Bundle bundle ) {

    URL versionFileUrl = bundle.getResource( "version.xml" );

    try {
      InputStream inputStream = versionFileUrl.openConnection().getInputStream();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document dom = db.parse( inputStream );
      NodeList versionElements = dom.getElementsByTagName( "version" );
      if ( versionElements.getLength() >= 1 ) {
        Element versionElement = (Element) versionElements.item( 0 );
        return versionElement.getAttribute( "branch" ) + "-" + versionElement.getTextContent();
      }
    } catch ( ParserConfigurationException e ) {
      e.printStackTrace();
    } catch ( SAXException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    return "Unknown";
  }

  private String getBaPlatformVersion() {
    String platformVersion;
    VersionInfo versionInfo;
    IVersionHelper versionHelper = PentahoSystem.get( IVersionHelper.class, null );
    if ( versionHelper != null ) {
      versionInfo = VersionHelper.getVersionInfo( versionHelper.getClass() );
    } else {
      versionInfo = VersionHelper.getVersionInfo( PentahoSystem.class );
    }
    platformVersion = versionInfo.getProductID() + "_" + versionInfo.getVersionNumber();
    return platformVersion;
  }

  // endregion

}
