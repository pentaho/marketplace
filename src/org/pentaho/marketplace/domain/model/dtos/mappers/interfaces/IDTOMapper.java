package org.pentaho.marketplace.domain.model.dtos.mappers.interfaces;

import java.util.Collection;

public interface IDTOMapper<TDTO, TEntity> {

  TEntity toEntity( TDTO dto );
  TDTO toDTO( TEntity entity );

  Collection<TEntity> toEntities( Collection<TDTO> dtos );
  Collection<TDTO> toDTOs( Collection<TEntity> entities );
}
