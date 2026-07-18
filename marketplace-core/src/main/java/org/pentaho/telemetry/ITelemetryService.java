/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
