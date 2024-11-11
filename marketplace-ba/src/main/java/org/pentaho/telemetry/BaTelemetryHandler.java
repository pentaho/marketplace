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


package org.pentaho.telemetry;

import org.pentaho.platform.engine.core.system.PentahoSystem;


/**
 * Creates a telemetry handler for the BA server.
 */
public class BaTelemetryHandler extends TelemetryHandler {

  public BaTelemetryHandler() {
    this( DEFAULT_SEND_PERIOD_IN_MINUTES );
  }

  public BaTelemetryHandler( long sendPeriodInMinutes ) {
    super( getPentahoSystemPath( DEFAULT_TELEMETRY_DIR_NAME ), sendPeriodInMinutes );
  }

  private static String getPentahoSystemPath( String telemetryDirName ) {
    return PentahoSystem.getApplicationContext().getSolutionPath( "system/" + telemetryDirName );
  }
}
