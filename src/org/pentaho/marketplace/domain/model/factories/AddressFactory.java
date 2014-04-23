package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.Address;
import org.pentaho.marketplace.domain.model.entities.interfaces.IAddress;
import org.pentaho.marketplace.domain.model.factories.interfaces.IAddressFactory;

public class AddressFactory implements IAddressFactory {

  @Override public IAddress create() {
    return new Address();
  }
}
