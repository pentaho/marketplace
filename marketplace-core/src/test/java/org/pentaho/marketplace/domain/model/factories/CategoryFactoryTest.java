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
