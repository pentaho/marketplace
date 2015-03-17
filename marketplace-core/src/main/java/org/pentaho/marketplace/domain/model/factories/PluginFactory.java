package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.Plugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;

public class PluginFactory implements IPluginFactory {

  //region IPluginFactory implementation
  @Override
  public IPlugin create() {
    return new Plugin();
  }
  //endregion
}
