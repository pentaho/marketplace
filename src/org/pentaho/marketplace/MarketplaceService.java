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
 * Created Oct 10th, 2011
 * @author Will Gorman (wgorman@pentaho.com)
 */
package org.pentaho.marketplace;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.util.web.HttpUtil;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import flexjson.JSONSerializer;

public class MarketplaceService {
  
  private Log logger = LogFactory.getLog(MarketplaceService.class);
  public static final String PLUGIN_NAME = "marketplace";

  public static class MarketplaceSecurityException extends Exception {
    private static final long serialVersionUID = -1852471739131561628L;
  }
  
  public Plugin[] getPlugins() throws MarketplaceSecurityException {
    // return a unauthorized exception if unauthorized?
    if (!hasMarketplacePermission()) {
      throw new MarketplaceSecurityException();
    }
    
    // load plugins from url
    Plugin plugins[] = loadPluginsFromSite();

    // determine if any of the plugins are installed and what version they are
    IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, PentahoSessionHolder.getSession());
    List<String> installedPlugins = pluginManager.getRegisteredPlugins();
    if (installedPlugins.size() > 0) {
      Map<String, Plugin> marketplacePlugins = new HashMap<String, Plugin>();
      for (Plugin plugin : plugins) {
        marketplacePlugins.put(plugin.getId(), plugin);
      }
      
      for (String installedPlugin : installedPlugins) {
        Plugin plugin = marketplacePlugins.get(installedPlugin);
        if(plugin != null) {
          plugin.setInstalled(true);
          plugin.setInstalledVersion(getInstalledVersion(plugin.getId()));
        }
      }
    }
    return plugins;
  }
  
  public StatusMessage uninstallPlugin(String id) throws MarketplaceSecurityException {
    Plugin plugins[] = getPlugins();
    Plugin toUninstall = null;
    for (Plugin plugin : plugins) {
      if (plugin.getId().equals(id)) {
        toUninstall = plugin;
      }
    }
    if (toUninstall == null) {
      return new StatusMessage("NO_PLUGIN","Plugin Not Found");
    }
    
    // get plugin path
    
    String jobPath = PentahoSystem.getApplicationContext().getSolutionPath("system/" + PLUGIN_NAME + "/processes/uninstall_plugin.kjb");
    try {
      JobMeta jobMeta = new JobMeta(jobPath, null);
      Job job = new Job(null, jobMeta);
      
      File file = new File(PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/backups"));
      file.mkdirs();
      
      job.getJobMeta().setParameterValue("uninstallLocation",PentahoSystem.getApplicationContext().getSolutionPath("system/"+ toUninstall.getId()));
      job.getJobMeta().setParameterValue("uninstallBackup",PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/backups/" + toUninstall.getId() + "_" + new Date().getTime()));
      
      job.copyParametersFrom(job.getJobMeta());
      job.activateParameters();
      job.start();
      job.waitUntilFinished();
      Result result = job.getResult(); // Execute the selected job.
      
      if (result == null || result.getNrErrors() > 0) {
        return new StatusMessage("FAIL", "Failed to execute uninstall, see log for details.");
      }
    } catch (KettleException e) {
      logger.error(e.getMessage(), e);
    }
    
    return new StatusMessage("PLUGIN_UNINSTALLED", toUninstall.getName() + " was successfully uninstalled.  Please restart your BI Server.");

  }
  

  /**
   * this method installs a specified plugin based on id.  
   * 
   * @param id the plugin to install
   * @return a status mesasge to display the user
   */
  public StatusMessage installPlugin(String id) throws MarketplaceSecurityException {
    Plugin plugins[] = getPlugins();
    Plugin toInstall = null;
    for (Plugin plugin : plugins) {
      if (plugin.getId().equals(id)) {
        toInstall = plugin;
      }
    }
    if (toInstall == null) {
      return new StatusMessage("NO_PLUGIN","Plugin Not Found");
    }

    // this checks to make sure the plugin metadata isn't attempting to overwrite a folder on the system.
    // TODO: Test a .. encoded in UTF8, etc to see if there is a way to thwart this check
    if (toInstall.getId().indexOf(".") >= 0) { 
      return new StatusMessage("NO_PLUGIN","Plugin ID contains an illegal character");
    }
    
    // get plugin path
    
    String jobPath = PentahoSystem.getApplicationContext().getSolutionPath("system/" + PLUGIN_NAME + "/processes/download_and_install_plugin.kjb");
    try {
      JobMeta jobMeta = new JobMeta(jobPath, null);
      Job job = new Job(null, jobMeta);
      
      File file = new File(PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/downloads"));
      file.mkdirs();
      file = new File(PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/backups"));
      file.mkdirs();
      file = new File(PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/staging"));
      file.mkdirs();
      
      job.getJobMeta().setParameterValue("downloadUrl", toInstall.getDownloadUrl());
      job.getJobMeta().setParameterValue("downloadDestination",PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/downloads/" + toInstall.getId() + "-" + toInstall.getAvailableVersion() + ".zip"));
      job.getJobMeta().setParameterValue("stagingDestination",PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/staging"));
      job.getJobMeta().setParameterValue("stagingDestinationAndDir",PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/staging/" + toInstall.getId()));
      job.getJobMeta().setParameterValue("targetDestination",PentahoSystem.getApplicationContext().getSolutionPath("system/"+ toInstall.getId()));
      job.getJobMeta().setParameterValue("targetBackup",PentahoSystem.getApplicationContext().getSolutionPath("system/plugin-cache/backups/" + toInstall.getId() + "_" + new Date().getTime()));
      
      job.copyParametersFrom(job.getJobMeta());
      job.activateParameters();
      job.start();
      job.waitUntilFinished();
      Result result = job.getResult(); // Execute the selected job.
      
      if (result == null || result.getNrErrors() > 0) {
        return new StatusMessage("FAIL", "Failed to execute install, see log for details.");
      }
    } catch (KettleException e) {
      logger.error(e.getMessage(), e);
    }
    
    return new StatusMessage("PLUGIN_INSTALLED", toInstall.getName() + " was successfully installed.  Please restart your BI Server.");
  }
  

  /**
   *  This method wraps the installPlugin method, returning JSON instead of XML.
   */
  public String installPluginJson(String pluginId) {
    try {
      StatusMessage msg = installPlugin(pluginId);
      JSONSerializer serializer = new JSONSerializer(); 
      String json = serializer.deepSerialize( msg );
      return json;
    } catch (MarketplaceSecurityException e) {
      logger.debug(e.getMessage(), e);
      return createJsonMessage("Unauthorized Access", "ERROR_0002_UNAUTHORIZED_ACCESS");
    }
  }
  
  public String uninstallPluginJson(String pluginId) {
    try {
      StatusMessage msg = uninstallPlugin(pluginId);
      JSONSerializer serializer = new JSONSerializer(); 
      String json = serializer.deepSerialize( msg );
      return json;
    } catch (MarketplaceSecurityException e) {
      logger.debug(e.getMessage(), e);
      return createJsonMessage("Unauthorized Access", "ERROR_0002_UNAUTHORIZED_ACCESS");
    }
  }
  
  public String getPluginsJson() {
    try {
      Plugin pluginArray[] = getPlugins();
      JSONSerializer serializer = new JSONSerializer(); 
      String json = serializer.deepSerialize( pluginArray );
      return json;
    } catch (MarketplaceSecurityException e) {
      logger.debug(e.getMessage(), e);
      // error(Messages.getErrorString("UserSettingService.ERROR_0002_SETTINGS_READ", e.getLocalizedMessage()), e); //$NON-NLS-1$
      // return createJsonMessage(Messages.getString("UserSettingService.ERROR_0002_SETTINGS_READ", e.getLocalizedMessage()), "ERROR_0002_SETTINGS_READ"); //$NON-NLS-1$ //$NON-NLS-2$
      return createJsonMessage("Unauthorized Access", "ERROR_0002_UNAUTHORIZED_ACCESS"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  
  protected boolean hasMarketplacePermission() {
    Authentication auth = SecurityHelper.getAuthentication(PentahoSessionHolder.getSession(), true);
      IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
      String roles = null;
      String users = null;
            
      try {
        roles = resLoader.getPluginSetting(getClass(), "settings/marketplace-roles" ); //$NON-NLS-1$
        users = resLoader.getPluginSetting(getClass(), "settings/marketplace-users" ); //$NON-NLS-1$
      } catch (Exception e) {
        logger.debug("Error getting data access plugin settings", e);
      }

      if (roles == null) {
        roles = "Admin";
      }

      String roleArr[] = roles.split(","); //$NON-NLS-1$

      for (String role : roleArr) {
        for (GrantedAuthority userRole : auth.getAuthorities()) {
          if (role != null && role.trim().equals(userRole.getAuthority())) {
            return true;
          }
        }
      }
      if (users != null) {
        String userArr[] = users.split(","); //$NON-NLS-1$
        for (String user : userArr) {
          if (user != null && user.trim().equals(auth.getName())) {
            return true;
          }
        }
      }
      return false;
    }
  

  protected StatusMessage createMessage( String message, String code ) {
    StatusMessage msg = new StatusMessage();
    msg.setCode(code);
    msg.setMessage(message);
    return msg;
  }
  
  protected String createJsonMessage( String message, String code ) {
    StatusMessage msg = createMessage(message, code);
    JSONSerializer serializer = new JSONSerializer(); 
    String json = serializer.deepSerialize( msg );
    return json;
  }

  protected String getMarketplaceSiteContent() {
    IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
    String site = null;
    try {
      site = resLoader.getPluginSetting(getClass(), "settings/marketplace-site" ); //$NON-NLS-1$
    } catch (Exception e) {
      logger.debug("Error getting data access plugin settings", e);
    }

    if (site == null) {
      site = "http://wiki.pentaho.com/download/attachments/23528994/availableplugins.xml";
    }
    return HttpUtil.getURLContent(site);
  }

  /**
   * This method determines the installed version of a plugin.  If the plugin doesn't define a version correctly, it returns "Unknown".
   * This method makes the assumption that the plugin id also equals the plugin folder name.
   *
   * @param pluginId the plugin id related to the version.
   */
  protected String getInstalledVersion(String pluginId) {
    String versionPath = PentahoSystem.getApplicationContext().getSolutionPath("system/" + pluginId + "/version.xml");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    FileReader reader = null;
    try {
      File file = new File(versionPath);
      if (!file.exists()) return "Unknown";
      DocumentBuilder db = dbf.newDocumentBuilder();
      reader =  new FileReader(versionPath);
      Document dom = db.parse(new InputSource(reader));
      NodeList versionElements = dom.getElementsByTagName("version");
      if (versionElements.getLength() >= 1) {
        Element versionElement = (Element)versionElements.item(0);
        return versionElement.getTextContent();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception e) {}
    }
    return "Unknown";    
  }


  protected Plugin[] loadPluginsFromSite() {
    String content = getMarketplaceSiteContent();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.parse(new InputSource(new StringReader(content)));
        NodeList plugins = dom.getElementsByTagName("plugin");
        Plugin pluginArr[] = new Plugin[plugins.getLength()];
        for (int i = 0; i < plugins.getLength(); i++) {
          Element element = (Element)plugins.item(i);
          Plugin plugin = new Plugin();
          plugin.setId(element.getAttribute("id"));
          plugin.setAvailableVersion(getElementChildValue(element, "availableVersion"));
          plugin.setCompany(getElementChildValue(element, "company"));
          plugin.setCompanyUrl(getElementChildValue(element, "companyUrl"));
          plugin.setDescription(getElementChildValue(element, "description"));
          plugin.setDownloadUrl(getElementChildValue(element, "downloadUrl"));
          plugin.setImg(getElementChildValue(element, "img"));
          plugin.setLearnMoreUrl(getElementChildValue(element, "learnMoreUrl"));
          plugin.setName(getElementChildValue(element, "name"));
          pluginArr[i] = plugin;
        }
        return pluginArr;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  protected String getElementChildValue(Element element, String child) {
    NodeList list = element.getElementsByTagName(child);
    if (list.getLength() >= 1) {
      return list.item(0).getTextContent();
    } else {
      return null;
    }
  }
}
