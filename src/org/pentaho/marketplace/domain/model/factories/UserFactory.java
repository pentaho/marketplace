package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.interfaces.IAddress;
import org.pentaho.marketplace.domain.model.entities.interfaces.IUser;
import org.pentaho.marketplace.domain.model.entities.User;
import org.pentaho.marketplace.domain.model.factories.interfaces.IAddressFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IUserFactory;

public class UserFactory implements IUserFactory {

  private IAddressFactory addressFactory;

  public UserFactory( IAddressFactory addressFactory ) {
    this.addressFactory = addressFactory;
  }

  @Override
  public IUser create( String userName, String password, IAddress address, int age ) {
    return new User( userName, password, address, age );
  }

  @Override
  public IUser create( String userName, String password, String addressStreetName, String addressPostalCode,
                              int age ) {
    IAddress address = this.addressFactory.create();
    address.setStreetName( addressStreetName );
    address.setPostalCode( addressPostalCode );
    return new User( userName, password, address, age );
  }
}
