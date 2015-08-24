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

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;

public class CategoryFactoryTest {

  private ICategoryFactory createFactory() {
    return new CategoryFactory();
  }

  @Test
  public void testGettingANonExistingRootCategoryReturnsNull() {
    // arrange
    ICategoryFactory factory = this.createFactory();

    // act
    ICategory category = factory.get( "IDoNotExist" );

    // assert
    Assert.assertNull( category );
  }

  @Test
  public void testGettingAnExistingRootCategoryReturnsTheCategory() {
    // arrange
    ICategoryFactory factory = this.createFactory();
    String name = "RootCategory";
    ICategory expectedRootCategory = factory.create( name );

    // act
    ICategory actualRootCategory = factory.get( name );

    // assert
    Assert.assertSame( expectedRootCategory, actualRootCategory );
  }

  @Test
  public void testCreateRootCategory() {
    // arrange
    ICategoryFactory factory = this.createFactory();
    String name = "Category";

    // act
    ICategory root = factory.create( name );

    // assert
    Assert.assertNotNull( root );
    Assert.assertEquals( root.getName(), name );
    Assert.assertNull( root.getParent() );
  }

  @Test
  public void testCreateChildCategoryIsChildOfSpecifiedParent() {
    // arrange
    ICategoryFactory factory = this.createFactory();
    String parentName = "ParentCategory";
    String childName = "ChildCategory";
    ICategory expectedParent = factory.create( parentName );
    ICategory expectedChild = factory.create( childName, expectedParent );

    // act
    ICategory actualChild = expectedParent.getChildren().get( childName );
    ICategory actualParent = actualChild.getParent();

    // assert
    Assert.assertSame( expectedChild, actualChild );
    Assert.assertSame( expectedParent, actualParent );
  }

  @Test
  public void testCreateExistingChildCategory() {
    // arrange
    ICategoryFactory factory = this.createFactory();
    String parentName = "ParentCategory";
    String childName = "ChildCategory";
    ICategory parent = factory.create( parentName );
    ICategory expectedChild = factory.create( childName, parent );

    // act
    ICategory actualChild = factory.create( childName, parent );

    // assert
    Assert.assertSame( expectedChild, actualChild );
  }

}
