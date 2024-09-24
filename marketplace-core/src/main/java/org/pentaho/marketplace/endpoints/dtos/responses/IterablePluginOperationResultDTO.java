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

package org.pentaho.marketplace.endpoints.dtos.responses;

import org.pentaho.marketplace.endpoints.dtos.entities.PluginDTO;
import org.pentaho.marketplace.endpoints.dtos.responses.base.OperationResultDTO;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class IterablePluginOperationResultDTO extends OperationResultDTO {

  //region Attributes
  public List<PluginDTO> plugins;
  //endregion
}
