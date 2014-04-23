package org.pentaho.marketplace.domain.services.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IUser;

public interface IUserService {
  Iterable<IUser> getUsers();
  IUser getUser( String userName );
}
