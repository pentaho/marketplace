package org.pentaho.marketplace.domain.services.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;
import java.util.Collection;

public interface IPluginService {

  Collection<IPlugin> getPlugins();
  IStatusMessage installPlugin( String pluginId, String versionBranch );
  IStatusMessage uninstallPlugin( String pluginId );
}
