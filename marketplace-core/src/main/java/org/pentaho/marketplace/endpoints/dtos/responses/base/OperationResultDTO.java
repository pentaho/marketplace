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


package org.pentaho.marketplace.endpoints.dtos.responses.base;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OperationResultDTO {

  //region Attributes
  public StatusMessageDTO statusMessage;
  //endregion

  //region Constructors
  public OperationResultDTO() {
    this.statusMessage = new StatusMessageDTO();
  }
  //endregion
}
