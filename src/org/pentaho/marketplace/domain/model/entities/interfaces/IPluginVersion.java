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

  //minParentVersion
  String getMinParentVersion();

  void setMinParentVersion( String value );

  //maxParentVersion
  String getMaxParentVersion();

  void setMaxParentVersion( String value );

  // stage lane
  String getStageLane();
  void setStageLane( String value );

  // stage phase
  String getStagePhase();
  void setStagePhase( String value );

  //endregion
}
