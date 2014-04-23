package org.pentaho.marketplace.domain.services;

import org.pentaho.marketplace.domain.services.interfaces.IRDO;
import org.pentaho.marketplace.domain.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

public class RDO implements IRDO {

  private IUserService userService;

  //region Constructors
  @Autowired
  public RDO( IUserService userService ) {

    //dependency obtained via constructor dependency injection from spring framework
    this.userService = userService;
  }
  //endregion

  //region IRDO implementation
  @Override
  public IUserService getUserService() {
    return this.userService;
  }
  //endregion
}
