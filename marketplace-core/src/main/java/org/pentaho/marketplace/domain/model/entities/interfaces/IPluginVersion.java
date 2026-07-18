/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.marketplace.domain.model.entities.interfaces;

public interface IPluginVersion {

  //region Properties

  //branch
  String getBranch();
  void setBranch( String value );

  //name
  String getName();
  void setName( String value );

  //version
  String getVersion();
  void setVersion( String value );

  //downloadUrl
  String getDownloadUrl();
  void setDownloadUrl( String value );

  //samplesDownloadUrl
  String getSamplesDownloadUrl();
  void setSamplesDownloadUrl( String value );

  //description
  String getDescription();
  void setDescription( String value );

  //changelog
  String getChangelog();
  void setChangelog( String value );

  //buildId
  String getBuildId();
  void setBuildId( String value );

  //releaseDate
  String getReleaseDate();
  void setReleaseDate( String value );

  /**
   * @return the minimum server / spoon version which is compatible with this version.
   */
  String getMinParentVersion();
  void setMinParentVersion( String value );

  /**
   * @return the maximum server / spoon version which is compatible with this version.
   */
  String getMaxParentVersion();
  void setMaxParentVersion( String value );

  //development Stage
  IDevelopmentStage getDevelopmentStage();
  IPluginVersion setDevelopmentStage( IDevelopmentStage stage );

  /**
   * @return true if it is an OSGi plugin; false otherwise.
   */
  boolean isOsgi();
  IPluginVersion setIsOsgi( boolean isOsgi );

  //endregion
}
