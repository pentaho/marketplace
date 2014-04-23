package org.pentaho.marketplace.domain.model.entities.interfaces;

public interface IUser {

  //region Properties
  String getUserName();
  public String getPassword();
  public IAddress getAddress();
  public int getAge();
  //endregion
}
