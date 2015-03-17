package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;

import java.util.ArrayList;
import java.util.Arrays;
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

  private MarketEntryType type;
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
    if ( this.versions == null ) {
      this.versions = new ArrayList<IPluginVersion>();
    }
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
  public MarketEntryType getType() {
    return this.type;
  }
  @Override
  public void setType( MarketEntryType type ) {
    this.type = type;
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

  @Override
  public int getRank() {
    return this.rank;
  }
  public void setRank( int rank ) {
    this.rank = rank;
  }
  private int rank;
  //endregion


  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    Plugin plugin = (Plugin) o;

    if ( installed != plugin.installed ) {
      return false;
    }
    if ( authorLogo != null ? !authorLogo.equals( plugin.authorLogo ) : plugin.authorLogo != null ) {
      return false;
    }
    if ( authorName != null ? !authorName.equals( plugin.authorName ) : plugin.authorName != null ) {
      return false;
    }
    if ( authorUrl != null ? !authorUrl.equals( plugin.authorUrl ) : plugin.authorUrl != null ) {
      return false;
    }
    if ( category != null ? !category.equals( plugin.category ) : plugin.category != null ) {
      return false;
    }
    if ( dependencies != null ? !dependencies.equals( plugin.dependencies ) : plugin.dependencies != null ) {
      return false;
    }
    if ( description != null ? !description.equals( plugin.description ) : plugin.description != null ) {
      return false;
    }
    if ( documentationUrl != null ? !documentationUrl.equals( plugin.documentationUrl ) :
      plugin.documentationUrl != null ) {
      return false;
    }
    if ( id != null ? !id.equals( plugin.id ) : plugin.id != null ) {
      return false;
    }
    if ( img != null ? !img.equals( plugin.img ) : plugin.img != null ) {
      return false;
    }
    if ( installationNotes != null ? !installationNotes.equals( plugin.installationNotes ) :
      plugin.installationNotes != null ) {
      return false;
    }
    if ( installedBranch != null ? !installedBranch.equals( plugin.installedBranch ) :
      plugin.installedBranch != null ) {
      return false;
    }
    if ( installedBuildId != null ? !installedBuildId.equals( plugin.installedBuildId ) :
      plugin.installedBuildId != null ) {
      return false;
    }
    if ( installedVersion != null ? !installedVersion.equals( plugin.installedVersion ) :
      plugin.installedVersion != null ) {
      return false;
    }
    if ( license != null ? !license.equals( plugin.license ) : plugin.license != null ) {
      return false;
    }
    if ( licenseName != null ? !licenseName.equals( plugin.licenseName ) : plugin.licenseName != null ) {
      return false;
    }
    if ( licenseText != null ? !licenseText.equals( plugin.licenseText ) : plugin.licenseText != null ) {
      return false;
    }
    if ( name != null ? !name.equals( plugin.name ) : plugin.name != null ) {
      return false;
    }
    if ( !Arrays.equals( screenshots, plugin.screenshots ) ) {
      return false;
    }
    if ( smallImg != null ? !smallImg.equals( plugin.smallImg ) : plugin.smallImg != null ) {
      return false;
    }
    if ( type != plugin.type ) {
      return false;
    }
    if ( versions != null ? !versions.equals( plugin.versions ) : plugin.versions != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + ( name != null ? name.hashCode() : 0 );
    result = 31 * result + ( img != null ? img.hashCode() : 0 );
    result = 31 * result + ( smallImg != null ? smallImg.hashCode() : 0 );
    result = 31 * result + ( documentationUrl != null ? documentationUrl.hashCode() : 0 );
    result = 31 * result + ( description != null ? description.hashCode() : 0 );
    result = 31 * result + ( authorName != null ? authorName.hashCode() : 0 );
    result = 31 * result + ( authorUrl != null ? authorUrl.hashCode() : 0 );
    result = 31 * result + ( authorLogo != null ? authorLogo.hashCode() : 0 );
    result = 31 * result + ( installedBranch != null ? installedBranch.hashCode() : 0 );
    result = 31 * result + ( installedVersion != null ? installedVersion.hashCode() : 0 );
    result = 31 * result + ( installedBuildId != null ? installedBuildId.hashCode() : 0 );
    result = 31 * result + ( installationNotes != null ? installationNotes.hashCode() : 0 );
    result = 31 * result + ( installed ? 1 : 0 );
    result = 31 * result + ( versions != null ? versions.hashCode() : 0 );
    result = 31 * result + ( screenshots != null ? Arrays.hashCode( screenshots ) : 0 );
    result = 31 * result + ( dependencies != null ? dependencies.hashCode() : 0 );
    result = 31 * result + ( license != null ? license.hashCode() : 0 );
    result = 31 * result + ( licenseName != null ? licenseName.hashCode() : 0 );
    result = 31 * result + ( licenseText != null ? licenseText.hashCode() : 0 );
    result = 31 * result + ( category != null ? category.hashCode() : 0 );
    result = 31 * result + ( type != null ? type.hashCode() : 0 );
    return result;
  }
}
