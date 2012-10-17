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
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *
 * Created Set 17th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */
package org.pentaho.telemetry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.api.util.IVersionHelper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.VersionInfo;
import org.pentaho.telemetry.TelemetryHelper.TelemetryEventType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;




/**
 * Class that manages publishing telemetry events for BA server plugins.
 * Correct usage is:
 * <ol>
 * <li>create a new instance with the right plugin name</li>
 * <li>Call the <b>sendTelemetryRequest</b> method to publish events
 * </ol>
 * @author pedrovale
 */
public class BaPluginTelemetry {

  private static Log logger = LogFactory.getLog(TelemetryHelper.class);
  
  private ITelemetryDataProvider baPluginTelemetryDataProvider;
  final private String pluginName;
  final private String pluginVersion;

  
    /**
   * The version information for the pentaho platform is in the core jar - that
   * is the fallback position. The VersionHelper implementation however should be
   * in a .jar file with correct manifest.
   */
  protected static final VersionInfo versionInfo;

  
  static {
    //
    // Allow override of product id information
    //
    IVersionHelper versionHelper = PentahoSystem.get(IVersionHelper.class, null);
    if (versionHelper != null) {
      versionInfo = VersionHelper.getVersionInfo(versionHelper.getClass());
    } else {
      versionInfo = VersionHelper.getVersionInfo(PentahoSystem.class);
    }
  }  
  
  
  
  public BaPluginTelemetry(final String pluginName) {
    
    this.pluginName = pluginName;
    this.pluginVersion = getPluginVersion();       
  }

  
  
  /**
   * Sends a new telemetry request regarding the plugin to a remote server
   * @param eventType - type of event to be published
   * @param extraInfo - Extra info to be included in the event
   * @return <i>true</i> if the event was correctly published to the request queue, <i> false</i>
   * otherwise
   */
  public boolean sendTelemetryRequest(final TelemetryEventType eventType, 
          final Map<String, String> extraInfo) {
    this.baPluginTelemetryDataProvider = new ITelemetryDataProvider() {
      @Override
      public String getPlatformVersion() {
        return versionInfo.getVersionNumber();
      }

      @Override
      public String getPluginName() {
        return pluginName;
      }

      @Override
      public String getPluginVersion() {
        return pluginVersion;
      }

      @Override
      public Map<String, String> getExtraInformation() {
        return extraInfo;
      }

      @Override
      public TelemetryEventType getEventType() {
        return eventType;
      }

      @Override
      public boolean isTelemetryEnabled() {
        return isPluginTelemetryEnabled();
      }

      
      @Override
      public String getBaseUrl() {
        return getTelemetryBaseUrlFromPlugin();
      }
      
 
    };
   
    TelemetryHelper th = new TelemetryHelper();
    th.setDataProvider(baPluginTelemetryDataProvider);
    return th.publishTelemetryEvent();        
  }
  
  
        
  
  /**
   * Determines whether the current plugin is allowed to publish telemetry events
   * @return <i> true</i> if telemetry is enabled, <i>false</i> otherwise.
   */
  public boolean isPluginTelemetryEnabled() {        
    return Boolean.parseBoolean(PentahoSystem.getSystemSetting("telemetry", "true"));    
  }
  
  
  protected String getTelemetryBaseUrlFromPlugin() {
    IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
    String baseUrl = null;
    try {
      baseUrl = resLoader.getPluginSetting(getClass(), "settings/telemetry-site"); //$NON-NLS-1$
    } catch (Exception e) {
      logger.debug("Error getting data access plugin settings", e);
    }

    if (baseUrl == null || "".equals(baseUrl)) {      
      logger.warn("Telemetry url is not set for plugin " + pluginName + ". Defaulting to a bogus local url");
      baseUrl = "https://localhost:8080/pentaho/telemetry";
    }

    return baseUrl;
  }
  
  
  
  private String getPluginVersion() {
       String versionPath = PentahoSystem.getApplicationContext().getSolutionPath("system/" + this.pluginName + "/version.xml");
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
  
  
}
