package org.pentaho.marketplace.endpoints;

import org.pentaho.marketplace.endpoints.dtos.GenericOperationResultDTO;
import org.pentaho.marketplace.endpoints.dtos.IterablePluginOperationResultDTO;
import org.pentaho.marketplace.endpoints.dtos.OperationResultDTO;
import org.pentaho.marketplace.endpoints.dtos.StringOperationResultDTO;
import org.pentaho.marketplace.endpoints.dtos.entities.PluginDTO;
import org.pentaho.marketplace.endpoints.dtos.entities.StatusMessageDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IPluginDTOMapper;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;
import org.pentaho.marketplace.domain.services.interfaces.IRDO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IStatusMessageDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
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
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public StringOperationResultDTO hello() {

    //create response object
    StringOperationResultDTO result = new StringOperationResultDTO();
    result.resultDTO = "Hello World from Marketplace!";

    //status message
    result.statusMessageDTO = new StatusMessageDTO();
    result.statusMessageDTO.code = "OK_CODE";
    result.statusMessageDTO.message = "OK_MESSAGE";

    //return result
    return result;
  }

  @GET
  @Path( "/plugins" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public IterablePluginOperationResultDTO getPlugins() {

    //get plugins from the domain model
    Collection<IPlugin> plugins = this.RDO.getPluginService().getPlugins();

    //transform plugins to DTOs for serialization
    IterablePluginOperationResultDTO result = new IterablePluginOperationResultDTO();
    result.resultDTO = this.pluginDTOMapper.toDTOs( plugins );

    //status message
    result.statusMessageDTO = new StatusMessageDTO();
    result.statusMessageDTO.code = "OK_CODE";
    result.statusMessageDTO.message = "OK_MESSAGE";

    //return result
    return result;
  }

  @GET
  @Path( "/plugin/{pluginId}/{versionBranch}" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public OperationResultDTO installPlugin( @PathParam( "pluginId" ) String pluginId,
                                           @PathParam( "versionBranch" ) String versionBranch ) {

    //install plugin
    IStatusMessage statusMessage = this.RDO.getPluginService().installPlugin( pluginId, versionBranch );

    //send installation result
    OperationResultDTO result = new OperationResultDTO();
    result.statusMessageDTO = this.statusMessageDTOMapper.toDTO( statusMessage );
    return result;
  }

  @GET
  @Path( "/plugins/{pluginId}" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public OperationResultDTO uninstallPlugin( @PathParam( "pluginId" ) String pluginId ) {

    //uninstall plugin
    IStatusMessage statusMessage = this.RDO.getPluginService().uninstallPlugin( pluginId );

    //send installation result
    OperationResultDTO result = new OperationResultDTO();
    result.statusMessageDTO = this.statusMessageDTOMapper.toDTO( statusMessage );
    return result;
  }
}
