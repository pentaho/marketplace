package org.pentaho.marketplace.domain.model.entities.interfaces;

import org.pentaho.marketplace.domain.model.complexTypes.interfaces.IPluginVersion;

import java.util.Collection;

public interface IPlugin {

  //region Properties

  //id
  String getId();

  void setId( String value );

  //name
  String getName();

  void setName( String value );

  //img
  String getImg();

  void setImg( String value );

  //smallImg
  String getSmallImg();

  void setSmallImg( String value );

  //learnModeUrl
  String getLearnMoreUrl();

  void setLearnMoreUrl( String value );

  //description
  String getDescription();

  void setDescription( String value );

  //company
  String getCompany();

  void setCompany( String value );

  //companyUrl
  String getCompanyUrl();

  void setCompanyUrl( String value );

  //companyLogo
  String getCompanyLogo();

  void setCompanyLogo( String value );

  //installedBranch
  String getInstalledBranch();

  void setInstalledBranch( String value );

  //installedVersion
  String getInstalledVersion();

  void setInstalledVersion( String value );

  //installedBuildId
  String getInstalledBuildId();

  void setInstalledBuildId( String value );

  //installationNotes
  String getInstallationNotes();

  void setInstallationNotes( String value );

  //installed
  boolean isInstalled();

  void setInstalled( boolean value );

  //versions
  Collection<IPluginVersion> getVersions();

  void setVersions( Collection<IPluginVersion> value );

  //screenshots
  String[] getScreenshots();

  void setScreenshots( String[] value );

  //dependencies
  String getDependencies();

  void setDependencies( String value );

  //license
  String getLicense();

  void setLicense( String value );

  //endregion
}
