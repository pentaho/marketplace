package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.DomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;

public class DomainStatusMessageFactory implements IDomainStatusMessageFactory {

  //region IStatusMessageFactory implementation
  @Override public IDomainStatusMessage create() {
    return new DomainStatusMessage();
  }
  //endregion
}
