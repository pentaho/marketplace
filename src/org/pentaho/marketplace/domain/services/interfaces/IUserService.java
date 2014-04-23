package org.pentaho.marketplace.domain.services.interfaces;

public interface IUserService {
  Iterable<IUser> getUsers();
  IUser getUser( String userName );
}
