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
    private String smallImg;
    private String learnMoreUrl;
    private String description;
    private String company;
    private String companyUrl;
    private String companyLogo;
    private String installedBranch;
    private String installedVersion;
    private String installedBuildId;    
    private String installationNotes;
    private boolean installed;
    private PluginVersion[] versions;

    public Plugin() {
    }

    public Plugin(String img, String name, String description, String learnMoreUrl, String company, String companyUrl) {
        this.setImg(img);
        this.setName(name);
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

    
    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getInstalledBranch() {
        return installedBranch;
    }

    public void setInstalledBranch(String installedBranch) {
        this.installedBranch = installedBranch;
    }

    /**
     * @return the versions
     */
    public PluginVersion[] getVersions() {
        return versions;
    }

    /**
     * @param versions the versions to set
     */
    public void setVersions(PluginVersion[] versions) {
        this.versions = versions;
    }

    public PluginVersion getVersionByBranch(String branch) {
        if (this.versions == null) {
            return null;
        }
        for (int i = 0; i < this.versions.length; i++) {
            if (branch != null && branch.equals(this.versions[i].getBranch())) {
                return this.versions[i];
            }
        }
        return null;
    }

    /**
     * @return the installedBuildId
     */
    public String getInstalledBuildId() {
        return installedBuildId;
    }

    /**
     * @param installedBuildId the installedBuildId to set
     */
    public void setInstalledBuildId(String installedBuildId) {
        this.installedBuildId = installedBuildId;
    }

    /**
     * @return the smallImg
     */
    public String getSmallImg() {
        return smallImg;
    }

    /**
     * @param smallImg the smallImg to set
     */
    public void setSmallImg(String smallImg) {
        this.smallImg = smallImg;
    }
}
