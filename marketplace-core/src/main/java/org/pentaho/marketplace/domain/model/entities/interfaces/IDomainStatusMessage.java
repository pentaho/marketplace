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


package org.pentaho.marketplace.domain.model.entities.interfaces;

public interface IDomainStatusMessage {

  //region Properties

  //code
  String getCode();

  void setCode( String value );

  //message
  String getMessage();

  void setMessage( String value );

  //endregion
}
