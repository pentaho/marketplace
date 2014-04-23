package org.pentaho.helloworld.domain.model.factories;

import org.pentaho.helloworld.domain.model.entities.Address;
import org.pentaho.helloworld.domain.model.entities.interfaces.IAddress;
import org.pentaho.helloworld.domain.model.factories.interfaces.IAddressFactory;

public class AddressFactory implements IAddressFactory {

  @Override public IAddress create() {
    return new Address();
  }
}
