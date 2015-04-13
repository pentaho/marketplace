/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

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
