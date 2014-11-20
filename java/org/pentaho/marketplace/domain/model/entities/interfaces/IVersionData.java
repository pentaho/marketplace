package org.pentaho.marketplace.domain.model.entities.interfaces;

public interface IVersionData extends Comparable<IVersionData> {

  //region Properties
  int getMajor();

  int getMinor();

  int getPatch();

  String getInfo();
  //endregion

  //region Methods
  boolean within( IVersionData min, IVersionData max );
  //endregion
}
