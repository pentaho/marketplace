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

package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.domain.model.entities.interfaces.ICategory;
import org.pentaho.marketplace.endpoints.dtos.entities.CategoryDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.ICategoryDTOMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CategoryDTOMapper implements ICategoryDTOMapper {

  @Override public ICategory toEntity( CategoryDTO dto ) {
    return null;
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
    return null;
  }

  @Override public List<CategoryDTO> toDTOs( Collection<ICategory> categories ) {
    List<CategoryDTO> dtos = new ArrayList<CategoryDTO>();

    for ( ICategory category : categories ) {
      dtos.add( this.toDTO( category ) );
    }

    return dtos;
  }
}
