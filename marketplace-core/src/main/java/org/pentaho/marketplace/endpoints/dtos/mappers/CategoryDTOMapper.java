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


package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;
import org.pentaho.marketplace.endpoints.dtos.entities.CategoryDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.ICategoryDTOMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CategoryDTOMapper implements ICategoryDTOMapper {

  // region Attributes
  private ICategoryFactory categoryFactory;
  // endregion

  // region Constructors
  public CategoryDTOMapper( ICategoryFactory categoryFactory ) {
    this.categoryFactory = categoryFactory;
  }
  // endregion

  // region Methods
  @Override public ICategory toEntity( CategoryDTO dto ) {
    ICategory parent = null;
    if ( dto.parentName != null ) {
      parent = categoryFactory.create( dto.parentName );
    }

    ICategory category = this.categoryFactory.create( dto.name, parent );
    return category;
  }

  @Override public CategoryDTO toDTO( ICategory category ) {
    if ( category == null ) {
      return null;
    }

    CategoryDTO dto = new CategoryDTO();
    dto.name = category.getName();
    if ( category.getParent() != null ) {
      dto.parentName = category.getParent().getName();
    }
    return dto;
  }

  @Override public Collection<ICategory> toEntities( List<CategoryDTO> dtos ) {
    List<ICategory> entities = new ArrayList<ICategory>();

    for ( CategoryDTO dto : dtos ) {
      entities.add( this.toEntity( dto ) );
    }

    return entities;
  }

  @Override public List<CategoryDTO> toDTOs( Collection<ICategory> categories ) {
    List<CategoryDTO> dtos = new ArrayList<CategoryDTO>();

    for ( ICategory category : categories ) {
      dtos.add( this.toDTO( category ) );
    }

    return dtos;
  }
  // endregion
}
