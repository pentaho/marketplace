/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.domain.model.entities.MarketEntryType;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.endpoints.dtos.entities.PluginDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.ICategoryDTOMapper;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginDTOMapper;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginVersionDTOMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PluginDTOMapper implements IPluginDTOMapper {

  //region Attributes
  IPluginFactory pluginFactory;
  IPluginVersionDTOMapper pluginVersionDTOMapper;
  ICategoryDTOMapper categoryDTOMapper;
  //endregion

  //region Constructors
  public PluginDTOMapper( IPluginFactory pluginFactory,
                          IPluginVersionDTOMapper pluginVersionDTOMapper,
                          ICategoryDTOMapper categoryDTOMapper) {

    //initialize dependencies
    this.pluginFactory = pluginFactory;
    this.pluginVersionDTOMapper = pluginVersionDTOMapper;
    this.categoryDTOMapper = categoryDTOMapper;
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
    plugin.setDocumentationUrl( dto.documentationUrl );
    plugin.setDescription( dto.description );
    plugin.setAuthorName( dto.authorName );
    plugin.setAuthorUrl( dto.authorUrl );
    plugin.setAuthorLogo( dto.authorLogo );
    plugin.setInstalledBranch( dto.installedBranch );
    plugin.setInstalledVersion( dto.installedVersion );
    plugin.setInstalledBuildId( dto.installedBuildId );
    plugin.setInstallationNotes( dto.installationNotes );
    plugin.setInstalled( dto.installed );
    plugin.setVersions( this.pluginVersionDTOMapper.toEntities( dto.versions ) );
    plugin.setScreenshots( dto.screenshots );
    plugin.setDependencies( dto.dependencies );
    plugin.setLicense( dto.license );
    plugin.setType( MarketEntryType.valueOf( dto.type ) );

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
    dto.documentationUrl = plugin.getDocumentationUrl();
    dto.description = plugin.getDescription();
    dto.authorName = plugin.getAuthorName();
    dto.authorUrl = plugin.getAuthorUrl();
    dto.authorLogo = plugin.getAuthorLogo();
    dto.installedBranch = plugin.getInstalledBranch();
    dto.installedVersion = plugin.getInstalledVersion();
    dto.installedBuildId = plugin.getInstalledBuildId();
    dto.installationNotes = plugin.getInstallationNotes();
    dto.installed = plugin.isInstalled();
    dto.versions = this.pluginVersionDTOMapper.toDTOs( plugin.getVersions() );
    dto.screenshots = plugin.getScreenshots();
    dto.dependencies = plugin.getDependencies();
    dto.license = plugin.getLicense();
    dto.type = plugin.getType().toString();

    dto.category = this.categoryDTOMapper.toDTO( plugin.getCategory() );

    //return the dto
    return dto;
  }

  @Override
  public Collection<IPlugin> toEntities( List<PluginDTO> dtos ) {

    Collection<IPlugin> plugins = new ArrayList<IPlugin>();

    for ( PluginDTO dto : dtos ) {
      plugins.add( this.toEntity( dto ) );
    }

    return plugins;
  }

  @Override
  public List<PluginDTO> toDTOs( Collection<IPlugin> plugins ) {

    List<PluginDTO> pluginDTOs = new ArrayList<PluginDTO>();

    for ( IPlugin plugin : plugins ) {
      pluginDTOs.add( this.toDTO( plugin ) );
    }

    return pluginDTOs;
  }
  //endregion
}
