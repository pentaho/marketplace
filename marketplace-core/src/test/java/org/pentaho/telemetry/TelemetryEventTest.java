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
 * Copyright (c) 2015 - 2017 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.telemetry;

import org.junit.Assert;
import org.junit.Test;


public class TelemetryEventTest extends TelemetryBaseTest {

  @Test
  public void testConstructor() {
    TelemetryEvent te = createTelemetryEvent();
    Assert.assertEquals( te.getEventType(), TelemetryEvent.Type.OTHER );
    Assert.assertEquals( te.getPluginName(), TEST_PLUGIN_NAME );
    Assert.assertEquals( te.getPluginVersion(), TEST_PLUGIN_VERSION );
    Assert.assertEquals( te.getPlatformVersion(), TEST_PLATFORM_VERSION );
    Assert.assertEquals( te.getUrlToCall(), TEST_TELEMETRY_URL );
    Assert.assertTrue( te.getExtraInfo().size() == 0 );

    te.getExtraInfo().put( "info1", "value1" );
    te.getExtraInfo().put( "info2", "value2" );
    Assert.assertTrue( te.getExtraInfo().size() == 2 );
    Assert.assertTrue( te.getExtraInfo().containsKey( "info1" ) );
    Assert.assertEquals( te.getExtraInfo().get( "info1" ), "value1" );
    Assert.assertTrue( te.getExtraInfo().containsKey( "info2" ) );
    Assert.assertEquals( te.getExtraInfo().get( "info2" ), "value2" );

    te.setEventTimestamp( 2222 );
    Assert.assertEquals( te.getEventTimestamp(), 2222 );
  }

  @Test
  public void testEventEncode() {
    TelemetryEvent te = createTelemetryEvent();
    te.getExtraInfo().put( "info1", "value1" );
    String json = te.encodeToJSON();
    String expectedJson = "\"eventType\":\"" + TelemetryEvent.Type.OTHER + "\","
      + "\"extraInfo\":{\"" + "info1" + "\":\"" + "value1" + "\"},"
      + "\"platformVersion\":\"" + TEST_PLATFORM_VERSION + "\","
      + "\"pluginName\":\"" + TEST_PLUGIN_NAME + "\","
      + "\"pluginVersion\":\"" + TEST_PLUGIN_VERSION + "\","
      + "\"urlToCall\":\"" + TEST_TELEMETRY_URL + "\"}";
    Assert.assertEquals( json.substring( 79 ), expectedJson );
  }
}
