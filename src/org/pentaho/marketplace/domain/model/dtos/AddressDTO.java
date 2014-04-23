package org.pentaho.marketplace.domain.model.dtos;

import org.pentaho.marketplace.domain.model.entities.interfaces.IAddress;
import org.pentaho.marketplace.domain.model.dtos.interfaces.IDTO;
import org.pentaho.marketplace.domain.model.factories.interfaces.IAddressFactory;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement
public class AddressDTO implements IDTO<AddressDTO, IAddress, IAddressFactory> {

  public String streetName;
  public String postalCode;

  @Override
  public IAddress toEntity( IAddressFactory addressFactory ) {
    IAddress address = addressFactory.create();
    address.setStreetName( this.streetName );
    address.setPostalCode( this.postalCode );
    return address;
  }

  @Override
  public void fillDTO( IAddress address ) {
    this.streetName = address.getStreetName();
    this.postalCode = address.getPostalCode();
  }

  @Override
  public Iterable<IAddress> toEntities( Iterable<AddressDTO> addressDTOs, IAddressFactory addressFactory ) {

    Collection<IAddress> addresses = new ArrayList<IAddress>();

    for ( AddressDTO addressDTO : addressDTOs ) {
      IAddress address = addressFactory.create();
      address.setStreetName( addressDTO.streetName );
      address.setPostalCode( addressDTO.postalCode );
      addresses.add( address );
    }

    return addresses;
  }

  @Override
  public Iterable<AddressDTO> toDTOs( Iterable<IAddress> addresses ) {

    Collection<AddressDTO> addressDTOs = new ArrayList<AddressDTO>();

    for ( IAddress address : addresses ) {
      AddressDTO addressDTO = new AddressDTO();
      addressDTO.fillDTO( address );
      addressDTOs.add( addressDTO );
    }

    return addressDTOs;
  }
}
