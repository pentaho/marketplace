package org.pentaho.telemetry;

import java.io.File;

public class DiTelemetryServiceConfiguration {

  /*
  // BA Server

  protected static String getTelemetryPath() {
    //return PentahoSystem.getApplicationContext().getSolutionPath( "system/.telemetry" );
  }

*/


    public DiTelemetryServiceConfiguration() {

    }

    public String getTelemetryPath() {
        //return "/Users/mvala/Temp/.telemetry";
        File f = new File( "" );
        return f.getAbsolutePath();
    }

}
