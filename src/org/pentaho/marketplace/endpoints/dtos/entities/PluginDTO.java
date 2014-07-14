package org.pentaho.marketplace.endpoints.dtos.entities;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class PluginDTO {

  //region Attributes
  public String id;
  public String name;
  public String img;
  public String smallImg;
  public String documentationUrl;
  public String description;
  public String authorName;
  public String authorUrl;
  public String authorLogo;
  public String installedBranch;
  public String installedVersion;
  public String installedBuildId;
  public String installationNotes;
  public boolean installed;
  public List<PluginVersionDTO> versions;
  public String[] screenshots;
  public String dependencies;
  public String license;
  //endregion
}
