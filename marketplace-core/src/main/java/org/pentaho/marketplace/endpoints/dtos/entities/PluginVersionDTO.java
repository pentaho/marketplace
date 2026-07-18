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



package org.pentaho.marketplace.endpoints.dtos.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PluginVersionDTO {

  //region Attributes
  public String branch;
  public String name;
  public String version;
  public String downloadUrl;
  public String samplesDownloadUrl;
  public String description;
  public String changelog;
  public String buildId;
  public String releaseDate;
  public String minParentVersion;
  public String maxParentVersion;
  public String developmentStageLane;
  public String developmentStagePhase;
  //endregion
}
