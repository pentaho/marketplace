package org.pentaho.helloworld.domain.model.dtos.interfaces;

public interface IDTO<TDTO, TEntity, TEntityFactory> {

  TEntity toEntity( TEntityFactory entityFactory );
  void fillDTO( TEntity entity );

  Iterable<TEntity> toEntities( Iterable<TDTO> dtos, TEntityFactory entityFactory );
  Iterable<TDTO> toDTOs( Iterable<TEntity> entities );
}
