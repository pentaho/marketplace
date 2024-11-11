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


package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;

public class DomainStatusMessage implements IDomainStatusMessage {

  //region Attributes
  private String code;
  private String message;
  //endregion

  //region IDomainStatusMessage implementation
  @Override
  public String getCode() {
    return this.code;
  }

  @Override
  public void setCode( String value ) {
    this.code = value;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public void setMessage( String value ) {
    this.message = value;
  }
  //endregion
}
