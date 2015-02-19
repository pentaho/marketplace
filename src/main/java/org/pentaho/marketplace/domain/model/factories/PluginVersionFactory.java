package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.PluginVersion;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;

public class PluginVersionFactory implements IPluginVersionFactory {

  //region IPluginVersionFactory implementation
  @Override
  public IPluginVersion create() {
    return new PluginVersion();
  }
  //endregion
}
