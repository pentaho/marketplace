/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.Plugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;

public class PluginFactory implements IPluginFactory {

  //region IPluginFactory implementation
  @Override
  public IPlugin create() {
    return new Plugin();
  }
  //endregion
}
