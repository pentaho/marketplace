package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.StatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IStatusMessageFactory;

public class StatusMessageFactory implements IStatusMessageFactory {

  //region IStatusMessageFactory implementation
  @Override public IStatusMessage create() {
    return new StatusMessage();
  }
  //endregion
}
