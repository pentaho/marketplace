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
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *
 * Created Set 20th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */
package org.pentaho.telemetry;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelemetryEventKeeper implements Runnable {

  private static final Log logger = LogFactory.getLog(TelemetryEventKeeper.class);  
  
  private String requestPath;
  private BlockingQueue<TelemetryEvent> queue;
  
  public TelemetryEventKeeper(BlockingQueue<TelemetryEvent> queue, String requestPath) {
    this.requestPath = requestPath;
    this.queue = queue;
  }
  
  @Override
  public void run() {
        try {
          do {
            process();
          } while (true);
        } catch (InterruptedException ie) {
          logger.warn("Got interrupted. Exiting.");
        }
  }
  
  public void process() throws InterruptedException {
            TelemetryEvent event = queue.take();
            
            try {
              FileOutputStream fout = new FileOutputStream(requestPath + "/" + System.currentTimeMillis() + ".tel");
              ObjectOutputStream oos = new ObjectOutputStream(fout);
              oos.writeObject(event);
              oos.close();            
            } catch (FileNotFoundException fnfe) {
              logger.warn("Unable to create file for telemetry event", fnfe);
            }
            catch (IOException ioe) {
              logger.error("Error caught while creating file for telemetry event", ioe);
            }                            
  }
  
}
