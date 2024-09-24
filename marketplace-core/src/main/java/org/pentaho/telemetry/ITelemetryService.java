/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.telemetry;

public interface ITelemetryService {

  /**
   * Creates a telemetry event.
   *
   * @param eventType the type of event
   * @return the telemetry event
   */
  TelemetryEvent createEvent( TelemetryEvent.Type eventType );

  /**
   * Publishes a telemetry event.
   *
   * @return <i>true</i> if the event was published correctly, <i>false</i> otherwise
   */
  boolean publishEvent( TelemetryEvent event );
}
