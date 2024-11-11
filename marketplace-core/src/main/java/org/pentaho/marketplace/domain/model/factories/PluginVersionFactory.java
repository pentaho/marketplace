/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.PluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;

public class PluginVersionFactory implements IPluginVersionFactory {

  //region IPluginVersionFactory implementation
  @Override
  public IPluginVersion create() {
    return new PluginVersion();
  }
  //endregion
}
