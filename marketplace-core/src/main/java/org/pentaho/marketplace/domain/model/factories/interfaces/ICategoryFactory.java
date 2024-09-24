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

package org.pentaho.marketplace.domain.model.factories.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;

public interface ICategoryFactory {

  /**
   *
   * @param name The name of the category.
   * @return Gets the root category with the given name. Returns null if no root category with the name has been created yet.
   */
  ICategory get( String name );

  ICategory create( String name, ICategory parent );
  ICategory create( String name );

}
