package org.pentaho.marketplace.endpoints.dtos.mappers.interfaces;

import java.util.Collection;
import java.util.List;

public interface IDTOMapper<TDTO, TEntity> {

  TEntity toEntity( TDTO dto );

  TDTO toDTO( TEntity entity );

  Collection<TEntity> toEntities( List<TDTO> dtos );

  List<TDTO> toDTOs( Collection<TEntity> entities );
}
