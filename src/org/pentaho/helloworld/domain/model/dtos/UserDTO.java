package org.pentaho.helloworld.domain.model.dtos;

import org.pentaho.helloworld.domain.model.dtos.interfaces.IDTO;
import org.pentaho.helloworld.domain.model.factories.interfaces.IUserFactory;
import org.pentaho.helloworld.domain.model.entities.interfaces.IUser;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class UserDTO implements IDTO<UserDTO, IUser, IUserFactory> {

  //region Attributes
  public String userName;
  public String password;
  public AddressDTO address;
  //endregion

  //region Constructors

  //required constructor for JAX-RS object serialization via JAXB
  public UserDTO() { }
  //endregion

  //region IDTO implementation
  @Override
  public IUser toEntity( IUserFactory userFactory ) {
    return userFactory.create( this.userName, this.password, this.address.streetName, this.address.postalCode, 0 );
  }

  @Override
  public void fillDTO( IUser user ) {
    this.userName = user.getUserName();
    this.password = user.getPassword();
    this.address = new AddressDTO();
    this.address.fillDTO( user.getAddress() );
  }

  @Override
  public Iterable<IUser> toEntities( Iterable<UserDTO> userDTOs, IUserFactory userFactory ) {

    Collection<IUser> users = new ArrayList<IUser>();

    for ( UserDTO userDTO : userDTOs ) {
      users.add(userFactory.create( userDTO.userName, userDTO.password, userDTO.address.streetName,
                                    userDTO.address.postalCode, 0 ) );
    }

    return users;
  }

  @Override
  public Iterable<UserDTO> toDTOs( Iterable<IUser> users ) {

    Collection<UserDTO> userDTOs = new ArrayList<UserDTO>();

    for ( IUser user : users ) {
      UserDTO userDTO = new UserDTO();
      userDTO.fillDTO( user );
      userDTOs.add( userDTO );
    }

    return userDTOs;
  }
  //endregion
}
