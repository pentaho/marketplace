package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.VersionData;
import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;

public class VersionDataFactory implements IVersionDataFactory {

  //region IVersionDataFactory implementation
  @Override
  public IVersionData create( String info ) {
    return new VersionData( info );
  }
  //endregion
}
