package org.pentaho.helloworld.domain.services.interfaces;

import org.pentaho.helloworld.domain.model.entities.interfaces.IUser;

public interface IUserService {
  Iterable<IUser> getUsers();
  IUser getUser( String userName );
}
