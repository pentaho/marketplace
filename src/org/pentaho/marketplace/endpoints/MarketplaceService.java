package org.pentaho.marketplace.endpoints;

import org.pentaho.marketplace.domain.model.dtos.PluginDTO;
import org.pentaho.marketplace.domain.model.dtos.mappers.interfaces.IPluginDTOMapper;
import org.pentaho.marketplace.domain.model.entities.interfaces.IPlugin;
import org.pentaho.marketplace.domain.services.interfaces.IRDO;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.Collection;

import static javax.ws.rs.core.MediaType.*;

@Path( "@plugin.java.rest.path.root@" )
public class MarketplaceService {

  private IRDO RDO;
  private IPluginDTOMapper pluginDTOMapper;

  @Autowired
  public MarketplaceService( IRDO rdo, IPluginDTOMapper pluginDTOMapper ) {

    //dependency obtained via constructor dependency injection from spring framework
    this.RDO = rdo;
    this.pluginDTOMapper = pluginDTOMapper;
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
}
