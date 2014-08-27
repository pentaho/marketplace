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

import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;

public class Category implements ICategory {

  // region Attributes
  private final String name;
  private ICategory parent;
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

  @Override public String getName() {
    return this.name;
  }

  @Override public String toString() {
    return "(Category - Name: " + this.getName() + "Parent: " + parent != null ? parent.getName() : "null" + ")";
  }
  // endregion
}
