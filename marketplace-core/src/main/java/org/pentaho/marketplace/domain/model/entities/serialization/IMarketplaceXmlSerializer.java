/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
