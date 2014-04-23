package org.pentaho.helloworld.endpoints;

import org.pentaho.helloworld.domain.model.dtos.UserDTO;
import org.pentaho.helloworld.domain.model.entities.interfaces.IUser;
import org.pentaho.helloworld.domain.services.interfaces.IRDO;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.*;

@Path( "@plugin.java.rest.path.root@" )
public class PentahoHelloWorldService {

  private IRDO RDO;

  @Autowired
  public PentahoHelloWorldService( IRDO rdo ) {

    //dependency obtained via constructor dependency injection from spring framework
    this.RDO = rdo;
  }

  @GET
  @Path( "/hello" )
  @Produces( TEXT_PLAIN )
  public String hello() {
    return "Hello World from Pentaho Service!";
  }

  @GET
  @Path( "/users" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public Iterable<UserDTO> getUsers() {

    //get users from the domain model
    Iterable<IUser> users = this.RDO.getUserService().getUsers();

    //transform users to DTOs for serialization
    return new UserDTO().toDTOs( users );
  }

  @GET
  @Path( "/user/{userName}" )
  @Produces( { APPLICATION_JSON, APPLICATION_XML } )
  public UserDTO getUser( @PathParam( "userName" ) String userName ) {

    IUser user = this.RDO.getUserService().getUser( userName );

    UserDTO userDTO = new UserDTO();
    userDTO.fillDTO( user );
    return userDTO;
  }
}
