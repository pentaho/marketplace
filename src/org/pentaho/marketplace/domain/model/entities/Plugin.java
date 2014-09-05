package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;

import java.util.Collection;

public class Plugin implements IPlugin {

  //region Attributes
  private String id;
  private String name;
  private String img;
  private String smallImg;
  private String documentationUrl;
  private String description;
  private String authorName;
  private String authorUrl;
  private String authorLogo;
  private String installedBranch;
  private String installedVersion;
  private String installedBuildId;
  private String installationNotes;
  private boolean installed;
  private Collection<IPluginVersion> versions;
  private String[] screenshots;
  private String dependencies;
  private String license;
  private String licenseName;
  private String licenseText;
  private ICategory category;
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
  public String getDocumentationUrl() {
    return this.documentationUrl;
  }

  @Override
  public void setDocumentationUrl( String value ) {
    this.documentationUrl = value;
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
  public String getAuthorName() {
    return this.authorName;
  }

  @Override
  public void setAuthorName( String value ) {
    this.authorName = value;
  }

  @Override
  public String getAuthorUrl() {
    return this.authorUrl;
  }

  @Override
  public void setAuthorUrl( String value ) {
    this.authorUrl = value;
  }

  @Override
  public String getAuthorLogo() {
    return this.authorLogo;
  }

  @Override
  public void setAuthorLogo( String value ) {
    this.authorLogo = value;
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

  @Override public String getLicenseName() { return this.licenseName; }
  @Override public void setLicenseName( String value ) { this.licenseName = value; }

  @Override public String getLicenseText() { return this.licenseText; }
  @Override public void setLicenseText( String value ) { this.licenseText = value; }

  @Override public ICategory getCategory() { return this.category; }
  @Override public void setCategory( ICategory category ) { this.category = category; }

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
