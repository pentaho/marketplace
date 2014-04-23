package org.pentaho.marketplace.tests.domain.dtos;

import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.marketplace.domain.model.dtos.UserDTO;
import org.pentaho.marketplace.domain.model.factories.AddressFactory;
import org.pentaho.marketplace.domain.model.factories.UserFactory;
import org.pentaho.marketplace.domain.model.entities.interfaces.IAddress;
import org.pentaho.marketplace.domain.model.entities.interfaces.IUser;

public class UserDTOTest {

  @Test
  public void convertUserToUserDTO() {

    AddressFactory addressFactory = new AddressFactory();
    UserFactory userFactory = new UserFactory(addressFactory);
    IAddress address = addressFactory.create();
    address.setStreetName( "test street name" );
    address.setPostalCode( "test postal code" );
    IUser user = userFactory.create( "test user", "test password", address, 30 );
    UserDTO userDTO = new UserDTO();
    userDTO.fillDTO( user );

    Assert.assertEquals( userDTO.userName, user.getUserName() );
    Assert.assertEquals( userDTO.password, user.getPassword() );
    Assert.assertEquals( userDTO.address.streetName, user.getAddress().getStreetName() );
    Assert.assertEquals( userDTO.address.postalCode, user.getAddress().getPostalCode() );
  }
}
