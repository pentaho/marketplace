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

package org.pentaho.marketplace.endpoints.dtos.mappers.interfaces;

import java.util.Collection;
import java.util.List;

public interface IDTOMapper<TDTO, TEntity> {

  TEntity toEntity( TDTO dto );

  TDTO toDTO( TEntity entity );

  Collection<TEntity> toEntities( List<TDTO> dtos );

  List<TDTO> toDTOs( Collection<TEntity> entities );
}
