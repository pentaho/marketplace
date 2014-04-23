package org.pentaho.helloworld.tests.domain.dtos;

import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.helloworld.domain.model.dtos.UserDTO;
import org.pentaho.helloworld.domain.model.factories.AddressFactory;
import org.pentaho.helloworld.domain.model.factories.UserFactory;
import org.pentaho.helloworld.domain.model.entities.interfaces.IAddress;
import org.pentaho.helloworld.domain.model.entities.interfaces.IUser;

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
