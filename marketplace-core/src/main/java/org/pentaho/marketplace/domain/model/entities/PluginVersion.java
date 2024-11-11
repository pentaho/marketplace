/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDevelopmentStage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;

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
  private boolean isOsgi = false;
  private IDevelopmentStage developmentStage;
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
    if( this.minParentVersion == null || this.minParentVersion.equals( "" ) ) {
      // if no min value is set, assume minimum value possible
      this.minParentVersion = "0.0.0";
    }
    return this.minParentVersion;
  }

  @Override
  public void setMinParentVersion( String value ) {
    this.minParentVersion = value;
  }

  @Override
  public String getMaxParentVersion() {
    if( this.maxParentVersion == null || this.maxParentVersion.equals( "" ) ) {
      // if no max value is set, assume largest value possible
      this.maxParentVersion = Integer.MAX_VALUE + "." + Integer.MAX_VALUE + "." + Integer.MAX_VALUE;
    }
    return this.maxParentVersion;
  }

  @Override
  public void setMaxParentVersion( String value ) {
    this.maxParentVersion = value;
  }

  @Override public IDevelopmentStage getDevelopmentStage() { return this.developmentStage; }
  @Override public PluginVersion setDevelopmentStage ( IDevelopmentStage stage ) {
    this.developmentStage = stage;
    return this;
  }

  @Override public boolean isOsgi() {
    return this.isOsgi;
  }
  @Override public PluginVersion setIsOsgi( boolean isOsgi ) {
    this.isOsgi = isOsgi;
    return this;
  }
  //endregion


  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    PluginVersion that = (PluginVersion) o;

    if ( branch != null ? !branch.equals( that.branch ) : that.branch != null ) {
      return false;
    }
    if ( buildId != null ? !buildId.equals( that.buildId ) : that.buildId != null ) {
      return false;
    }
    if ( changelog != null ? !changelog.equals( that.changelog ) : that.changelog != null ) {
      return false;
    }
    if ( description != null ? !description.equals( that.description ) : that.description != null ) {
      return false;
    }
    if ( developmentStage != null ? !developmentStage.equals( that.developmentStage ) :
      that.developmentStage != null ) {
      return false;
    }
    if ( downloadUrl != null ? !downloadUrl.equals( that.downloadUrl ) : that.downloadUrl != null ) {
      return false;
    }
    if ( maxParentVersion != null ? !maxParentVersion.equals( that.maxParentVersion ) :
      that.maxParentVersion != null ) {
      return false;
    }
    if ( minParentVersion != null ? !minParentVersion.equals( that.minParentVersion ) :
      that.minParentVersion != null ) {
      return false;
    }
    if ( name != null ? !name.equals( that.name ) : that.name != null ) {
      return false;
    }
    if ( releaseDate != null ? !releaseDate.equals( that.releaseDate ) : that.releaseDate != null ) {
      return false;
    }
    if ( samplesDownloadUrl != null ? !samplesDownloadUrl.equals( that.samplesDownloadUrl ) :
      that.samplesDownloadUrl != null ) {
      return false;
    }
    if ( version != null ? !version.equals( that.version ) : that.version != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = branch != null ? branch.hashCode() : 0;
    result = 31 * result + ( name != null ? name.hashCode() : 0 );
    result = 31 * result + ( version != null ? version.hashCode() : 0 );
    result = 31 * result + ( downloadUrl != null ? downloadUrl.hashCode() : 0 );
    result = 31 * result + ( samplesDownloadUrl != null ? samplesDownloadUrl.hashCode() : 0 );
    result = 31 * result + ( description != null ? description.hashCode() : 0 );
    result = 31 * result + ( changelog != null ? changelog.hashCode() : 0 );
    result = 31 * result + ( buildId != null ? buildId.hashCode() : 0 );
    result = 31 * result + ( releaseDate != null ? releaseDate.hashCode() : 0 );
    result = 31 * result + ( minParentVersion != null ? minParentVersion.hashCode() : 0 );
    result = 31 * result + ( maxParentVersion != null ? maxParentVersion.hashCode() : 0 );
    result = 31 * result + ( developmentStage != null ? developmentStage.hashCode() : 0 );
    return result;
  }
}
