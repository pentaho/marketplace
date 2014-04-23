package org.pentaho.marketplace.domain.services.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;

public interface IPluginService {

  Iterable<IPlugin> getPlugins();
  IStatusMessage installPlugin( String pluginId, String versionBranch );
  IStatusMessage uninstallPlugin( String pluginId );
}
