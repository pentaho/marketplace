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

package org.pentaho.marketplace.domain.model.entities;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;

import java.util.Hashtable;
import java.util.Map;

public class Category implements ICategory {

  // region Attributes
  private final String name;
  private ICategory parent;
  private Map<String, ICategory> children;
  // endregion

  // region Constructors
  public Category( String name, ICategory parent ) {
    if ( name == null ) {
      throw new IllegalArgumentException( "Argument name can not be null." );
    }

    this.name = name;
    this.parent = parent;
  }

  public Category( String name ) {
    this( name, null );
  }
  // endregion

  // region Methods
  @Override public ICategory getParent() {
    return this.parent;
  }
  @Override public ICategory setParent( ICategory parent ) {
    this.parent = parent;
    return this;
  }

  @Override public Map<String, ICategory> getChildren() {
    // lazy creation
    if ( this.children == null ) {
      this.children = new Hashtable<>();
    }

    return this.children;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public String toString() {
    String parentString = parent != null ? parent.getName() : "null";
    return "(Category - Name: " + this.getName() + "Parent: " + parentString + ")";
  }
  // endregion

  @Override public boolean equals( Object otherObj ) {
    if ( otherObj == null ) {
      return false;
    }

    if ( otherObj == this ) {
      return true;
    }

    if ( !( otherObj instanceof ICategory ) ) {
      return false;
    }

    ICategory otherCategory = (ICategory) otherObj;

    return this.getName().equals( otherCategory.getName() )
      && this.getParent() == null ? otherCategory.getParent() == null : this.getParent().equals( otherCategory.getParent() );
  }

  @Override public int hashCode() {
    return new HashCodeBuilder( 23, 41 )
      .append( this.getName() )
      .append( this.getParent() )
      .toHashCode();
  }

}
