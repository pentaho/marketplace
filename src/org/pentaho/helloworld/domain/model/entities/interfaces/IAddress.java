package org.pentaho.helloworld.domain.model.entities.interfaces;

public interface IAddress {

  //region properties
  String getStreetName();
  void setStreetName( String streetName );

  String getPostalCode();
  void setPostalCode( String postalCode );
  //endregion
}
