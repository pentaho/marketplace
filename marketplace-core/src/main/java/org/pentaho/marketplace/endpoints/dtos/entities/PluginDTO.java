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
