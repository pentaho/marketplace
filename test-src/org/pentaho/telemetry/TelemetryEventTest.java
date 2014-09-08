/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.telemetry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.Test;



public class TelemetryEventTest extends TelemetryBaseTest {

  
  @Test
  public void testEventCreation() {
    TelemetryEvent te = new TelemetryEvent(getDefaultTelemetryDataProvider("CDF", true));

    
    Assert.assertEquals("CDF", te.getPluginName());
    Assert.assertEquals("12.09.05", te.getPluginVersion());
    Assert.assertEquals("4.5", te.getPlatformVersion());
    Assert.assertEquals(TelemetryHelper.TelemetryEventType.OTHER, te.getEventType());
        
  }
  
  
  
  @Test
  public void testEventEncode() {
    TelemetryEvent te = new TelemetryEvent(getDefaultTelemetryDataProvider("CDF", true));
    
    
    String x = te.encodeEvent();
    Assert.assertEquals(x.substring(79), "\"eventType\":\"OTHER\",\"extraInfo\":{\"ep1\":\"ev1\"},\"platformVersion\":\"4.5\",\"pluginName\":\"CDF\",\"pluginVersion\":\"12.09.05\",\"urlToCall\":\"pentahoTelemetry\"}");   
  }
  
  

  
}
