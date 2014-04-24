package org.pentaho.marketplace.endpoints;

import org.pentaho.marketplace.endpoints.dtos.PluginDTO;
import org.pentaho.marketplace.endpoints.dtos.StatusMessageDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginDTOMapper;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IStatusMessageDTOMapper;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;
import org.pentaho.marketplace.domain.services.interfaces.IRDO;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.util.Collection;

import static javax.ws.rs.core.MediaType.*;

@Path( "@plugin.java.rest.path.root@" )
public class MarketplaceService {

  private IRDO RDO;
  private IPluginDTOMapper pluginDTOMapper;
  private IStatusMessageDTOMapper statusMessageDTOMapper;

  @Autowired
  public MarketplaceService( IRDO rdo,
                             IPluginDTOMapper pluginDTOMapper,
                             IStatusMessageDTOMapper statusMessageDTOMapper ) {

    //dependency obtained via constructor dependency injection from spring framework
    this.RDO = rdo;
    this.pluginDTOMapper = pluginDTOMapper;
    this.statusMessageDTOMapper = statusMessageDTOMapper;
  }

  @GET
  @Path( "/hello" )
  @Produces( TEXT_PLAIN )
  public String hello() {
    return "Hello World from Marketplace!";
  }

  @GET
  @Path( "/plugins" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public Iterable<PluginDTO> getPlugins() {

    //get plugins from the domain model
    Collection<IPlugin> plugins = this.RDO.getPluginService().getPlugins();

    //transform plugins to DTOs for serialization
    return this.pluginDTOMapper.toDTOs( plugins );
  }

  @GET
  @Path( "/plugin/{pluginId}/{versionBranch}" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public StatusMessageDTO installPlugin( @PathParam( "pluginId" ) String pluginId,
                                         @PathParam( "versionBranch" ) String versionBranch ) {

    //install plugin
    IStatusMessage statusMessage = this.RDO.getPluginService().installPlugin( pluginId, versionBranch );

    //send installation result
    return this.statusMessageDTOMapper.toDTO( statusMessage );
  }

  @GET
  @Path( "/plugins/{pluginId}" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public StatusMessageDTO uninstallPlugin( @PathParam( "pluginId" ) String pluginId ) {

    //uninstall plugin
    IStatusMessage statusMessage = this.RDO.getPluginService().uninstallPlugin( pluginId );

    //send installation result
    return this.statusMessageDTOMapper.toDTO( statusMessage );
  }
}
