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
