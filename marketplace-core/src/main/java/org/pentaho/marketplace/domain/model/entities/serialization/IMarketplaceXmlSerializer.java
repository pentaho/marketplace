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

package org.pentaho.marketplace.domain.model.entities.serialization;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.util.Map;

/**
 * Serialization interface that processes marketplace metadata xml files to domain objects
 */
public interface IMarketplaceXmlSerializer {

  /**
   *
   * @param xmlInputStream the inputstream to get the metadata from
   * @return map which keys are plugin ids and values are plugins
   */
  Map<String, IPlugin> getPlugins( InputStream xmlInputStream );


  /**
   *
   * @param xml the string to get the metadata from
   * @return map which keys are plugin ids and values are plugins
   */
  Map<String, IPlugin> getPlugins( String xml );

  /**
   *
   * @param marketplaceMetadataDocument the document from where to get the metadata
   * @return map which keys are plugin ids and values are plugins
   * @throws XPathExpressionException
   */
  Map<String, IPlugin> getPlugins( Document marketplaceMetadataDocument ) throws
    XPathExpressionException;

  /**
   * Processes a version xml in the format
   * <version branch='branchName'  buildId='buildId'>versionName</version>
   * @param xml the string with the version xml definition
   * @return the parsed version
   */
  IPluginVersion getInstalledVersion( String xml );

  /**
   * Processes a version xml in the format
   * <version branch='branchName'  buildId='buildId'>versionName</version>
   * @param inputDocument the input source from where to get the version definition
   * @return the parsed version
   */
  IPluginVersion getInstalledVersion( InputSource inputDocument );

  /**
   * Processes a version xml in the format
   * <version branch='branchName'  buildId='buildId'>versionName</version>
   * @param installedVersionDocument the document from where to get the version definition
   * @return the parsed version
   */
  IPluginVersion getInstalledVersion( Document installedVersionDocument );
}
