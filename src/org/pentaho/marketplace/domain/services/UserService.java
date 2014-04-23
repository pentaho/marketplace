package org.pentaho.marketplace.domain.services;

import org.pentaho.marketplace.domain.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Collection;

public class UserService implements IUserService {

  private Collection<IUser> users;

  //region Constructors
  @Autowired
  public UserService( IUserFactory userFactory, IAddressFactory addressFactory ) {

    this.users = new ArrayList<IUser>();

    //initialize user array to simulate persistency
    for ( int i = 0; i < 5; i++ ) {

      IAddress userAddress = addressFactory.create();
      userAddress.setStreetName( "street" + i );
      userAddress.setPostalCode( "postalCode" + i );
      IUser user = userFactory.create( "user" + i, "pass" + i, userAddress, i );
      this.users.add( user );
    }
  }
  //endregion

  //region IUserInterface implementation
  @Override
  public Iterable<IUser> getUsers() {
    return this.users;
  }

  @Override
  public IUser getUser( String userName ) {

    for ( IUser user : this.users ) {
      if ( user.getUserName().equals( userName ) ) {
        return user;
      }
    }

    return null;
  }
  //endregion
}
