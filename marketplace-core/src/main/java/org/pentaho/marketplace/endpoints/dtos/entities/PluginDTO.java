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
  public String type;

  public CategoryDTO category;

  //endregion
}
