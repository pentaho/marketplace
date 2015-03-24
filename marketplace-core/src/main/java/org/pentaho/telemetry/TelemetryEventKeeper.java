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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * Used by TelemetryService to manage storing telemetry events on file system
 */
public class TelemetryEventKeeper implements Runnable {

  private static final Log logger = LogFactory.getLog( TelemetryEventKeeper.class );

    // region Constants

    private static final String FILE_EXT = ".tel";

    private static final String UNABLE_TO_CREATE_FILE_MESSAGE =
            "Unable to create file for telemetry event";

    private static final String ERROR_CREATING_FILE_MESSAGE =
            "Error while creating file for telemetry event";

    // endregion


    private BlockingQueue<TelemetryEvent> eventQueue;
  private String telemetryDirPath;

  public TelemetryEventKeeper( BlockingQueue<TelemetryEvent> eventQueue, File telemetryDir ) {
      this.eventQueue = eventQueue;
    this.telemetryDirPath = telemetryDir.getAbsolutePath();
  }

  @Override
  public void run() {
    try {
      do {
        process();
      } while ( true );
    } catch ( InterruptedException ie ) {
        // run until interrupted
    }
  }

  /**
   * Takes an event from the event eventQueue and stores it in the filesystem.
   *
   * @throws InterruptedException
   */
  public void process() throws InterruptedException {
    TelemetryEvent event = eventQueue.take();

    try {
        String filename = System.currentTimeMillis() + FILE_EXT;
      FileOutputStream fout = new FileOutputStream( telemetryDirPath + "/" + filename );
      ObjectOutputStream oos = new ObjectOutputStream( fout );
      oos.writeObject( event );
      oos.close();
    } catch ( FileNotFoundException fnfe ) {
      logger.warn( UNABLE_TO_CREATE_FILE_MESSAGE, fnfe );
    } catch ( IOException ioe ) {
      logger.error( ERROR_CREATING_FILE_MESSAGE, ioe );
    }
  }

}
