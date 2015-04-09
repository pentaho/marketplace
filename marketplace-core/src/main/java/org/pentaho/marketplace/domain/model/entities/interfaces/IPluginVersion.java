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

  //endregion
}
