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



package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.VersionData;
import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;

public class VersionDataFactory implements IVersionDataFactory {

  //region IVersionDataFactory implementation
  @Override
  public IVersionData create( String info ) {
    return new VersionData( info );
  }
  //endregion
}
