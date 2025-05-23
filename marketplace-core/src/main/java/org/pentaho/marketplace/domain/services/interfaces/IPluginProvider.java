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


package org.pentaho.marketplace.domain.services.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;

import java.util.Map;

public interface IPluginProvider {

  Map<String, IPlugin> getPlugins();

}
