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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.util.IVersionHelper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;

public class BaPluginTelemetry extends TelemetryService {

  private static Log logger = LogFactory.getLog(BaPluginTelemetry.class);

  // region Constructors

  public BaPluginTelemetry(String pluginName,
                           ITelemetryHandler telemetryHandler,
                           String telemetryUrl,
                           boolean telemetryEnabled) {
    super(telemetryHandler, telemetryUrl, telemetryEnabled);

    this.setPluginName( pluginName );
    this.setPluginVersion( this.getBaPluginVersion(pluginName) );
    this.setPlatformVersion( this.getBaPlatformVersion() );
  }

  // endregion

  // region Methods

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

  private String getBaPluginVersion( String pluginName ) {

    IApplicationContext context = PentahoSystem.getApplicationContext();

    String versionPath = PentahoSystem.getApplicationContext().getSolutionPath("system/" + pluginName + "/version.xml");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    FileReader reader = null;
    try {
      File file = new File(versionPath);
      if (!file.exists()) {
        return "Unknown";
      }
      DocumentBuilder db = dbf.newDocumentBuilder();
      reader = new FileReader(versionPath);
      Document dom = db.parse(new InputSource(reader));
      NodeList versionElements = dom.getElementsByTagName("version");
      if (versionElements.getLength() >= 1) {
        Element versionElement = (Element) versionElements.item(0);
        return versionElement.getAttribute("branch") + "-" + versionElement.getTextContent();
      }
    } catch (Exception e) {
      logger.error("Error while trying to read plugin version for " + pluginName, e);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception e) {
      }
    }
    return "Unknown";
  }

  // endregion

  /*
  public boolean isPluginTelemetryEnabled() {
    return Boolean.parseBoolean( PentahoSystem.getSystemSetting( "telemetry", "true" ) );
  }

  protected String getTelemetryBaseUrlFromPlugin() {
    IPluginResourceLoader resLoader = PentahoSystem.get( IPluginResourceLoader.class, null );
    String baseUrl = null;
    try {
      baseUrl = resLoader.getPluginSetting( getClass(), "settings/telemetry-site" ); //$NON-NLS-1$
    } catch ( Exception e ) {
      logger.debug( "Error getting data access plugin settings", e );
    }

    if ( baseUrl == null || "".equals( baseUrl ) ) {
      logger.warn( "Telemetry url is not set for plugin " + pluginName + ". Defaulting to a bogus local url" );
      baseUrl = "https://localhost:8080/pentaho/telemetry";
    }

    return baseUrl;
  }
  */
}
