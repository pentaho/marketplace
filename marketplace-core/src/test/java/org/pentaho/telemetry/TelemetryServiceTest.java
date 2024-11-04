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

import org.junit.Assert;
import org.junit.Test;


public class TelemetryServiceTest extends TelemetryBaseTest {

  @Test
  public void testCreateEvent() {
    ITelemetryService telemetryService = createTelemetryService( true, createTelemetryHandler() );
    TelemetryEvent te = telemetryService.createEvent( TelemetryEvent.Type.OTHER );
    Assert.assertEquals( te.getEventType(), TelemetryEvent.Type.OTHER );
    Assert.assertEquals( te.getPluginName(), TEST_PLUGIN_NAME );
    Assert.assertEquals( te.getPluginVersion(), TEST_PLUGIN_VERSION );
    Assert.assertEquals( te.getPlatformVersion(), TEST_PLATFORM_VERSION );
    Assert.assertEquals( te.getUrlToCall(), TEST_TELEMETRY_URL );
  }

  @Test
  public void testPublishEvent() {
    ITelemetryService telemetryService;
    TelemetryEvent te;

    // test if publish event fails when the telemetry service is inactive
    telemetryService = createTelemetryService( false, null );
    te = telemetryService.createEvent( TelemetryEvent.Type.OTHER );
    Assert.assertFalse( telemetryService.publishEvent( te ) );

    // test if publish event fails when the telemetry service has an invalid telemetry handler
    telemetryService = createTelemetryService( true, null );
    te = telemetryService.createEvent( TelemetryEvent.Type.OTHER );
    Assert.assertFalse( telemetryService.publishEvent( te ) );

    // test if publish event succeeds when the telemetry service is properly configured
    telemetryService = createTelemetryService( true, createTelemetryHandler() );
    te = telemetryService.createEvent( TelemetryEvent.Type.OTHER );
    Assert.assertTrue( telemetryService.publishEvent( te ) );
  }
}
