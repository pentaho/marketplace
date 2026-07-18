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

public interface ITelemetryHandler {

  /**
   * Add a telemetry event to the handler queue.
   *
   * @return <i>true</i> if the event was queued correctly, <i>false</i> otherwise
   */
  boolean queueEvent( TelemetryEvent event );
}
