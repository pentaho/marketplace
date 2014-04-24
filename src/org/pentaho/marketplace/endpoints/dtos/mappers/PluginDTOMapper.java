package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.endpoints.dtos.PluginDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginDTOMapper;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginVersionDTOMapper;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Collection;

public class PluginDTOMapper implements IPluginDTOMapper {

  //region Attributes
  IPluginFactory pluginFactory;
  IPluginVersionDTOMapper pluginVersionDTOMapper;
  //endregion

  //region Constructors
  @Autowired
  public PluginDTOMapper( IPluginFactory pluginFactory, IPluginVersionDTOMapper pluginVersionDTOMapper ) {

    //initialize dependencies
    this.pluginFactory = pluginFactory;
    this.pluginVersionDTOMapper = pluginVersionDTOMapper;
  }
  //endregion

  //region IPluginDTOMapper implementation
  @Override
  public IPlugin toEntity( PluginDTO dto ) {

    //get new plugin instance
    IPlugin plugin = this.pluginFactory.create();

    //fill the instance
    plugin.setId( dto.id );
    plugin.setName( dto.name );
    plugin.setImg( dto.img );
    plugin.setSmallImg( dto.smallImg );
    plugin.setLearnMoreUrl( dto.learnMoreUrl );
    plugin.setDescription( dto.description );
    plugin.setCompany( dto.company );
    plugin.setCompanyUrl( dto.companyUrl );
    plugin.setCompanyLogo( dto.companyLogo );
    plugin.setInstalledBranch( dto.installedBranch );
    plugin.setInstalledVersion( dto.installedVersion );
    plugin.setInstalledBuildId( dto.installedBuildId );
    plugin.setInstallationNotes( dto.installationNotes );
    plugin.setInstalled( dto.installed );
    plugin.setVersions( this.pluginVersionDTOMapper.toEntities( dto.versions ) );
    plugin.setScreenshots( dto.screenshots );
    plugin.setDependencies( dto.dependencies );
    plugin.setLicense( dto.license );

    //return the instance
    return plugin;
  }

  @Override
  public PluginDTO toDTO( IPlugin plugin ) {

    //get new dto instance
    PluginDTO dto = new PluginDTO();

    //fill this dto's attributes
    dto.id = plugin.getId();
    dto.name = plugin.getName();
    dto.img = plugin.getImg();
    dto.smallImg = plugin.getSmallImg();
    dto.learnMoreUrl = plugin.getLearnMoreUrl();
    dto.description = plugin.getDescription();
    dto.company = plugin.getCompany();
    dto.companyUrl = plugin.getCompanyUrl();
    dto.companyLogo = plugin.getCompanyLogo();
    dto.installedBranch = plugin.getInstalledBranch();
    dto.installedVersion = plugin.getInstalledVersion();
    dto.installedBuildId = plugin.getInstalledBuildId();
    dto.installationNotes = plugin.getInstallationNotes();
    dto.installed = plugin.isInstalled();
    dto.versions = this.pluginVersionDTOMapper.toDTOs( plugin.getVersions() );
    dto.screenshots = plugin.getScreenshots();
    dto.dependencies = plugin.getDependencies();
    dto.license = plugin.getLicense();

    //return the dto
    return dto;
  }

  @Override
  public Collection<IPlugin> toEntities( Collection<PluginDTO> dtos ) {

    Collection<IPlugin> plugins = new ArrayList<IPlugin>();

    for ( PluginDTO dto : dtos ) {
      plugins.add( this.toEntity( dto ) );
    }

    return plugins;
  }

  @Override
  public Collection<PluginDTO> toDTOs( Collection<IPlugin> plugins ) {

    Collection<PluginDTO> pluginDTOs = new ArrayList<PluginDTO>();

    for ( IPlugin plugin : plugins ) {
      pluginDTOs.add( this.toDTO( plugin ) );
    }

    return pluginDTOs;
  }
  //endregion
}
