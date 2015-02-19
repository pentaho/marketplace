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
  @Override public ICategory getParent() { return this.parent; }
  @Override public ICategory setParent( ICategory parent ) {
    this.parent = parent;
    return this;
  }

  @Override public Map<String, ICategory> getChildren() {
    // lazy creation
    if ( this.children == null ) {
      this.children = new Hashtable<String, ICategory>();
    }

    return this.children;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public String toString() {
    return "(Category - Name: " + this.getName() + "Parent: " + parent != null ? parent.getName() : "null" + ")";
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
