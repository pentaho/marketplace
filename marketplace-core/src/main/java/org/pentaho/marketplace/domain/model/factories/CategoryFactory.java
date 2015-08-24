/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.Category;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;

import java.util.Hashtable;
import java.util.Map;

public class CategoryFactory implements ICategoryFactory {

  // region Attributes
  private static Map<String, ICategory> categories = new Hashtable<>();
  // endregion

  // region Methods
  @Override public ICategory get( String name ) {
    return categories.get( name );
  }

  /**
   * Creates a new category. If category with the same name, in the parent namespace, already exists returns the previously created category.
   * Root categories have no parent with the namespace defined in the Category factory.
   * @param name the name of the category
   * @param parent the parent category of the category to create
   * @return the created category or the existing category that match the given name and parent category.
   */
  @Override public ICategory create( String name, ICategory parent ) {
    ICategory category = parent != null ? parent.getChildren().get( name ) : this.get( name );
    // if category exists
    if ( category != null ) {
      return category;
    }

    // a new category is being created
    category = new Category( name, parent );
    // root category, store in factory namespace
    if ( parent == null ) {
      categories.put( name, category );
    } else {
      parent.getChildren().put( name, category );
    }
    return category;
  }

  /**
   * Creates a new root category. If a root category with the same name already exists returns the previously created category.
   * @param name the name of the category to create or get
   * @return a new root category or an existing category with the given category name
   */
  @Override public ICategory create( String name ) {
    return create( name, null );
  }
  // endregion

  // region Constructors
  // endregion
}
