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


package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.DomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;

public class DomainStatusMessageFactory implements IDomainStatusMessageFactory {

  //region IDomainStatusMessageFactory implementation
  @Override public IDomainStatusMessage create() {
    return new DomainStatusMessage();
  }

  @Override public IDomainStatusMessage create( String code, String message ) {

    IDomainStatusMessage domainStatusMessage = new DomainStatusMessage();
    domainStatusMessage.setCode( code );
    domainStatusMessage.setMessage( message );
    return domainStatusMessage;
  }
  //endregion
}
