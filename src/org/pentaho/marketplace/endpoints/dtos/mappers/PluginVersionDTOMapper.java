package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.domain.model.entities.interfaces.IPluginVersion;
import org.pentaho.marketplace.endpoints.dtos.entities.PluginVersionDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginVersionDTOMapper;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PluginVersionDTOMapper implements IPluginVersionDTOMapper {

  //region Attributes
  IPluginVersionFactory pluginVersionFactory;
  //endregion

  //region Constructors
  @Autowired
  public PluginVersionDTOMapper( IPluginVersionFactory pluginVersionFactory ) {

    //initialize dependencies
    this.pluginVersionFactory = pluginVersionFactory;
  }
  //endregion

  //region IPluginVersionDTOMapper implementation
  @Override
  public IPluginVersion toEntity( PluginVersionDTO dto ) {

    //get new pluginVersion instance
    IPluginVersion pluginVersion = this.pluginVersionFactory.create();

    //fill the instance
    pluginVersion.setBranch( dto.branch );
    pluginVersion.setName( dto.name );
    pluginVersion.setVersion( dto.version );
    pluginVersion.setDownloadUrl( dto.downloadUrl );
    pluginVersion.setSamplesDownloadUrl( dto.samplesDownloadUrl );
    pluginVersion.setDescription( dto.description );
    pluginVersion.setChangelog( dto.changelog );
    pluginVersion.setBuildId( dto.buildId );
    pluginVersion.setReleaseDate( dto.releaseDate );
    pluginVersion.setMinParentVersion( dto.minParentVersion );
    pluginVersion.setMaxParentVersion( dto.maxParentVersion );

    //return the instance
    return pluginVersion;
  }

  @Override
  public PluginVersionDTO toDTO( IPluginVersion pluginVersion ) {

    //get new dto instance
    PluginVersionDTO dto = new PluginVersionDTO();

    //fill this dto's attributes
    dto.branch = pluginVersion.getBranch();
    dto.name = pluginVersion.getName();
    dto.version = pluginVersion.getVersion();
    dto.downloadUrl = pluginVersion.getDownloadUrl();
    dto.samplesDownloadUrl = pluginVersion.getSamplesDownloadUrl();
    dto.description = pluginVersion.getDescription();
    dto.changelog = pluginVersion.getChangelog();
    dto.buildId = pluginVersion.getBuildId();
    dto.releaseDate = pluginVersion.getReleaseDate();
    dto.minParentVersion = pluginVersion.getMinParentVersion();
    dto.maxParentVersion = pluginVersion.getMaxParentVersion();

    //return the dto
    return dto;
  }

  @Override
  public Collection<IPluginVersion> toEntities( List<PluginVersionDTO> dtos ) {

    Collection<IPluginVersion> pluginVersions = new ArrayList<IPluginVersion>();

    for ( PluginVersionDTO dto : dtos ) {
      pluginVersions.add( this.toEntity( dto ) );
    }

    return pluginVersions;
  }

  @Override
  public List<PluginVersionDTO> toDTOs( Collection<IPluginVersion> pluginVersions ) {

    List<PluginVersionDTO> pluginVersionDTOs = new ArrayList<PluginVersionDTO>();

    for ( IPluginVersion pluginVersion : pluginVersions ) {
      pluginVersionDTOs.add( this.toDTO( pluginVersion ) );
    }

    return pluginVersionDTOs;
  }
  //endregion
}
