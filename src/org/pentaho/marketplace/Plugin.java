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

import java.io.Serializable;

public class Plugin implements Serializable {
  private static final long serialVersionUID = 8252279235434152138L;
  private String id;
  private String name;
  private String img;
  private String learnMoreUrl;
  private String downloadUrl;
  private String samplesDownloadUrl;
  private String availableVersion;
  private String description;
  private String company;
  private String companyUrl;
  private String installedVersion;
  private String changelog;
  private String installationNotes;
  private boolean installed;
  
  public Plugin() {}
  
  public Plugin(String img, String name, String availableVersion, String description, String learnMoreUrl, String company, String companyUrl) {
    this.setImg(img);
    this.setName(name);
    this.setAvailableVersion(availableVersion);
    this.setDescription(description);
    this.setLearnMoreUrl(learnMoreUrl);
    this.setCompany(company);
    this.setCompanyUrl(companyUrl);
  }

  
  public String getInstallationNotes() {
      return installationNotes;
  }
  
  
  public void setInstallationNotes(String installationNotes) {
      this.installationNotes = installationNotes;
  }
  
  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLearnMoreUrl() {
    return learnMoreUrl;
  }

  public void setLearnMoreUrl(String learnMoreUrl) {
    this.learnMoreUrl = learnMoreUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getSamplesDownloadUrl() {
    return samplesDownloadUrl;
  }

  public void setSamplesDownloadUrl(String samplesDownloadUrl) {
    this.samplesDownloadUrl = samplesDownloadUrl;
  }
  
  
  
  public String getAvailableVersion() {
    return availableVersion;
  }

  public void setAvailableVersion(String availableVersion) {
    this.availableVersion = availableVersion;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getCompanyUrl() {
    return companyUrl;
  }

  public void setCompanyUrl(String companyUrl) {
    this.companyUrl = companyUrl;
  }

  public String getInstalledVersion() {
    return installedVersion;
  }

  public void setInstalledVersion(String installedVersion) {
    this.installedVersion = installedVersion;
  }

  public boolean isInstalled() {
    return installed;
  }

  public void setInstalled(boolean installed) {
    this.installed = installed;
  }
  
  
  public String getChangelog() {
    return changelog;
  }

  public void setChangelog(String changelog) {
    this.changelog = changelog;
  }
  
  
}
