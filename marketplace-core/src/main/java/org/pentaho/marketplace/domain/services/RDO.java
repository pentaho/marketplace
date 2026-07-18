/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.marketplace.domain.services;

import org.pentaho.marketplace.domain.services.interfaces.IPluginService;
import org.pentaho.marketplace.domain.services.interfaces.IRDO;

public class RDO implements IRDO {

  private IPluginService pluginService;

  //region Constructors
  public RDO( IPluginService pluginService ) {
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
