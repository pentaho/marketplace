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



package org.pentaho.marketplace.domain.model.entities.interfaces;

public interface IVersionData extends Comparable<IVersionData> {

  //region Properties
  int getMajor();

  int getMinor();

  int getPatch();

  String getInfo();
  //endregion

  //region Methods
  boolean within( IVersionData min, IVersionData max );
  //endregion
}
