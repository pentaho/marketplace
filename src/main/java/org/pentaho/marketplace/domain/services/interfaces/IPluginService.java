package org.pentaho.marketplace.domain.services.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;

import java.util.Collection;

public interface IPluginService {

  Collection<IPlugin> getPlugins();

  IDomainStatusMessage installPlugin( String pluginId, String versionBranch );

  IDomainStatusMessage uninstallPlugin( String pluginId );
}
