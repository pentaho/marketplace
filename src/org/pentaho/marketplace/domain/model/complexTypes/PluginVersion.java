package org.pentaho.marketplace.domain.model.complexTypes;

import org.pentaho.marketplace.domain.model.complexTypes.interfaces.IPluginVersion;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PluginVersion implements IPluginVersion {

  //region Attributes
  private String branch;
  private String name;
  private String version;
  private String downloadUrl;
  private String samplesDownloadUrl;
  private String description;
  private String changelog;
  private String buildId;
  private String releaseDate;
  private String minParentVersion;
  private String maxParentVersion;
  //endregion

  //region IPluginVersion implementation
  @Override
  public String getBranch() {
    return this.branch;
  }

  @Override
  public void setBranch( String value ) {
    this.branch = value;
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
  public String getVersion() {
    return this.version;
  }

  @Override
  public void setVersion( String value ) {
    this.version = value;
  }

  @Override
  public String getDownloadUrl() {
    return this.downloadUrl;
  }

  @Override
  public void setDownloadUrl( String value ) {
    this.downloadUrl = value;
  }

  @Override
  public String getSamplesDownloadUrl() {
    return this.samplesDownloadUrl;
  }

  @Override
  public void setSamplesDownloadUrl( String value ) {
    this.samplesDownloadUrl = value;
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
  public String getChangelog() {
    return this.changelog;
  }

  @Override
  public void setChangelog( String value ) {
    this.changelog = value;
  }

  @Override
  public String getBuildId() {
    return this.buildId;
  }

  @Override
  public void setBuildId( String value ) {
    this.buildId = value;
  }

  @Override
  public String getReleaseDate() {
    return this.releaseDate;
  }

  @Override
  public void setReleaseDate( String value ) {
    this.releaseDate = value;
  }

  @Override
  public String getMinParentVersion() {
    return this.minParentVersion;
  }

  @Override
  public void setMinParentVersion( String value ) {
    this.minParentVersion = value;
  }

  @Override
  public String getMaxParentVersion() {
    return this.maxParentVersion;
  }

  @Override
  public void setMaxParentVersion( String value ) {
    this.maxParentVersion = value;
  }
  //endregion
}
