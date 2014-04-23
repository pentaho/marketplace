package org.pentaho.marketplace.domain.model.factories.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IAddress;
import org.pentaho.marketplace.domain.model.entities.interfaces.IUser;

public interface IUserFactory {
  IUser create( String userName, String password, IAddress address, int age );
  IUser create( String userName, String password, String addressStreetName, String addressPostalCode, int age );
}
