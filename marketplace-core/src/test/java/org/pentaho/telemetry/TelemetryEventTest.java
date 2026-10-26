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

import com.fasterxml.jackson.core.JsonProcessingException;
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
  public void testEventEncode() throws JsonProcessingException {
    TelemetryEvent te = createTelemetryEvent();
    te.getExtraInfo().put( "info1", "value1" );
    te.setEventTimestamp( 2222 );

    String expectedJson = "{\"class\":\"org.pentaho.telemetry.TelemetryEvent\",\"eventTimestamp\":2222,"
      + "\"eventType\":\"OTHER\",\"extraInfo\":{\"info1\":\"value1\"},"
      + "\"platformVersion\":\"PLATFORM-5.2.0\",\"pluginName\":\"PLUGIN\","
      + "\"pluginVersion\":\"1.0.0\",\"urlToCall\":\"http://localhost:8080/telemetry-servlet/telemetry\"}";
    Assert.assertEquals( expectedJson, te.encodeToJSON() );
  }

  @Test
  public void testEventEncodePreservesNullProperties() throws JsonProcessingException {
    TelemetryEvent te = createTelemetryEvent();
    te.setEventTimestamp( 2222 );
    te.setExtraInfo( null );
    te.setPluginName( null );

    String expectedJson = "{\"class\":\"org.pentaho.telemetry.TelemetryEvent\",\"eventTimestamp\":2222,"
      + "\"eventType\":\"OTHER\",\"extraInfo\":null,\"platformVersion\":\"PLATFORM-5.2.0\","
      + "\"pluginName\":null,\"pluginVersion\":\"1.0.0\","
      + "\"urlToCall\":\"http://localhost:8080/telemetry-servlet/telemetry\"}";
    Assert.assertEquals( expectedJson, te.encodeToJSON() );
  }
}
