package org.pentaho.marketplace.domain.services;

import org.pentaho.marketplace.domain.services.interfaces.IPluginService;
import org.pentaho.marketplace.domain.services.interfaces.IRDO;
import org.springframework.beans.factory.annotation.Autowired;

public class RDO implements IRDO {

  private IPluginService pluginService;

  //region Constructors
  @Autowired
  public RDO( IPluginService pluginService ) {

    //dependency obtained via constructor dependency injection from spring framework
    this.pluginService = pluginService;
  }
  //endregion

  //region IRDO implementation
  @Override
  public IPluginService getPluginService() {
    return this.pluginService;
  }
  //endregion
}
