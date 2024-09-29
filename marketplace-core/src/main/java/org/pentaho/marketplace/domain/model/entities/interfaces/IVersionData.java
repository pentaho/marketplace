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
