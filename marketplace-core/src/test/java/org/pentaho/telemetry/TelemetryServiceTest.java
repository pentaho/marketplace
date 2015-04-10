/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

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
