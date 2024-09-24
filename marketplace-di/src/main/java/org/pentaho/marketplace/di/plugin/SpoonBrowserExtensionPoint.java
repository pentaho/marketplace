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

package org.pentaho.marketplace.di.plugin;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;

@ExtensionPoint( id = "SpoonBrowserExtensionPoint", extensionPointId = "SpoonBrowserFunction",
    description = "SpoonBrowserFunction" )
public class SpoonBrowserExtensionPoint implements ExtensionPointInterface {

  public void callExtensionPoint( LogChannelInterface log, Object obj ) throws KettleException {
    Object[] args = (Object[]) obj;
    String function = (String) args[0];
    if ( "openMarketplace".equalsIgnoreCase( function ) ) {
      MenuHandler mh = new MenuHandler();
      mh.openMarketplace();
    }
  }

}
