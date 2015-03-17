package org.pentaho.marketplace.di.plugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path( "testService" )
public class HelloRest {

    @GET
    @Path("say/{name}")
    public String sayHello( @PathParam("name") String name ) {
        return "Hello " + name;
    }


}
