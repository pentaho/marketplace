/*
 * Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.Category;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;

import java.util.Hashtable;
import java.util.Map;

public class CategoryFactory implements ICategoryFactory {

  // region Attributes
  private static Map<String, ICategory> categories = new Hashtable<String, ICategory>();
  // endregion

  // region Methods
  @Override public ICategory get( String name ) {
    return categories.get( name );
  }

  /**
   * Creates a new category. If a category with the same name already exists returns the previously created category.
   * @param name
   * @param parent
   * @return
   */
  @Override public ICategory create( String name, ICategory parent ) {
    // if category with this name already exists
    ICategory category = categories.get( name );
    if ( category != null ) {
      // category exists with null parent. Allow overwriting parent.
      if ( category.getParent() == null ) {
        category.setParent( parent );
      }
      return category;
    }

    // a new category is being created
    category = new Category( name, parent );
    categories.put( name, category );
    return category;
  }

  /**
   * Creates a new category. If a category with the same name already exists returns the previously created category.
   * @param name
   * @return
   */
  @Override public ICategory create( String name ) {
    return create( name, null );
  }
  // endregion

  // region Constructors
  // endregion
}
