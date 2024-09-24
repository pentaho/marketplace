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

import java.util.Map;

public interface ICategory {

  /**
   *
   * @return Gets the parent category of this category. Returns null if it is a root category.
   */
  ICategory getParent();

  /**
   * Sets the parent category of this category.
   * @param parent the parent to set.
   * @return Returns this
   */
  ICategory setParent( ICategory parent );

  /**
   *
   * @return Gets the name of this category.
   */
  String getName();

  /**
   *
   * @return Returns the children categories.
   */
  Map<String, ICategory> getChildren();

}
