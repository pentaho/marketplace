package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;

import java.util.Collection;

public class Plugin implements IPlugin {

  //region Attributes
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
  private Collection<IPluginVersion> versions;
  private String[] screenshots;
  private String dependencies;
  private String license;
  //endregion

  //region IPlugin implementation
  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId( String value ) {
    this.id = value;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName( String value ) {
    this.name = value;
  }

  @Override
  public String getImg() {
    return this.img;
  }

  @Override
  public void setImg( String value ) {
    this.img = value;
  }

  @Override
  public String getSmallImg() {
    return this.smallImg;
  }

  @Override
  public void setSmallImg( String value ) {
    this.smallImg = value;
  }

  @Override
  public String getLearnMoreUrl() {
    return this.learnMoreUrl;
  }

  @Override
  public void setLearnMoreUrl( String value ) {
    this.learnMoreUrl = value;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public void setDescription( String value ) {
    this.description = value;
  }

  @Override
  public String getCompany() {
    return this.company;
  }

  @Override
  public void setCompany( String value ) {
    this.company = value;
  }

  @Override
  public String getCompanyUrl() {
    return this.companyUrl;
  }

  @Override
  public void setCompanyUrl( String value ) {
    this.companyUrl = value;
  }

  @Override
  public String getCompanyLogo() {
    return this.companyLogo;
  }

  @Override
  public void setCompanyLogo( String value ) {
    this.companyLogo = value;
  }

  @Override
  public String getInstalledBranch() {
    return this.installedBranch;
  }

  @Override
  public void setInstalledBranch( String value ) {
    this.installedBranch = value;
  }

  @Override
  public String getInstalledVersion() {
    return this.installedVersion;
  }

  @Override
  public void setInstalledVersion( String value ) {
    this.installedVersion = value;
  }

  @Override
  public String getInstalledBuildId() {
    return this.installedBuildId;
  }

  @Override
  public void setInstalledBuildId( String value ) {
    this.installedBuildId = value;
  }

  @Override
  public String getInstallationNotes() {
    return this.installationNotes;
  }

  @Override
  public void setInstallationNotes( String value ) {
    this.installationNotes = value;
  }

  @Override
  public boolean isInstalled() {
    return this.installed;
  }

  @Override
  public void setInstalled( boolean value ) {
    this.installed = value;
  }

  @Override
  public Collection<IPluginVersion> getVersions() {
    return this.versions;
  }

  @Override
  public void setVersions( Collection<IPluginVersion> value ) {
    this.versions = value;
  }

  @Override
  public String[] getScreenshots() {
    return this.screenshots;
  }

  @Override
  public void setScreenshots( String[] value ) {
    this.screenshots = value;
  }

  @Override
  public String getDependencies() {
    return this.dependencies;
  }

  @Override
  public void setDependencies( String value ) {
    this.dependencies = value;
  }

  @Override
  public String getLicense() {
    return this.license;
  }

  @Override
  public void setLicense( String value ) {
    this.license = value;
  }

  @Override
  public IPluginVersion getVersionByBranch( String branch ) {

    if ( this.versions == null ) {
      return null;
    }

    for ( IPluginVersion v : this.versions ) {
      if ( branch != null && branch.equals( v.getBranch() ) ) {
        return v;
      }
    }
    return null;
  }
  //endregion
}
