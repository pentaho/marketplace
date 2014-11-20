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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class TelemetryEventKeeperTest extends TelemetryBaseTest {

  
  @Before
  @After
  public void cleanFiles() {
    File telDir = new File(".");
          File[] requests = telDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
              return name.endsWith(".tel");
            }          
          });
        //Clean up
    for (File f : requests)
      f.delete();

  }
  
  
  @Test
  public void testEventCreation() throws InterruptedException {
    
    TelemetryEvent te = new TelemetryEvent(getDefaultTelemetryDataProvider("CDE", true));
    TelemetryEvent te2 = new TelemetryEvent(getDefaultTelemetryDataProvider("CDF", true));
    
    BlockingQueue<TelemetryEvent> queue = new ArrayBlockingQueue<TelemetryEvent>(2);
    
    
    TelemetryEventKeeper tek = new TelemetryEventKeeper(queue, ".");
    

    
    queue.offer(te);
    
    tek.process();    
    
    File telDir = new File(".");
          File[] requests = telDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
              return name.endsWith(".tel");
            }          
          });

    Assert.assertEquals(1, requests.length);
          
    queue.offer(te2);
    
    tek.process();        
    
    requests = telDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
              return name.endsWith(".tel");
            }          
          });    
    
    Assert.assertEquals(2, requests.length);
    
  }
  
    @Test 
  public void testEventReadFromFile () throws InterruptedException, IOException, 
  ClassNotFoundException {
      
    TelemetryEvent te = new TelemetryEvent(getDefaultTelemetryDataProvider("CDE", true));


    BlockingQueue<TelemetryEvent> queue = new ArrayBlockingQueue<TelemetryEvent>(2);
    
    
    TelemetryEventKeeper tek = new TelemetryEventKeeper(queue, ".");
    
    
    queue.offer(te);
    
    tek.process();    
    
    File telDir = new File(".");
          File[] requests = telDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
              return name.endsWith(".tel");
            }          
          });

    Assert.assertEquals(1, requests.length);
    

              FileInputStream fin = new FileInputStream(requests[0].getAbsoluteFile());
              ObjectInputStream ois = new ObjectInputStream(fin);
              TelemetryEvent event = (TelemetryEvent) ois.readObject();              
              ois.close();            
              
              Assert.assertEquals("pentahoTelemetry",event.getUrlToCall());
              Assert.assertEquals("CDE", event.getPluginName());
    
    
  }
    
    
    

      
    
}




