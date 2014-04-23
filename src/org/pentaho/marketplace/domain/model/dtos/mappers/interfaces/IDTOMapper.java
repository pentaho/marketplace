package org.pentaho.marketplace.domain.model.dtos.mappers.interfaces;

public interface IDTOMapper<TDTO, TEntity> {

  TEntity toEntity( TDTO dto );
  TDTO toDTO( TEntity entity );

  Iterable<TEntity> toEntities( Iterable<TDTO> dtos );
  Iterable<TDTO> toDTOs( Iterable<TEntity> entities );
}
