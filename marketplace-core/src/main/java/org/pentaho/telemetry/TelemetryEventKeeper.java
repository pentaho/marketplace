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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Used by TelemetryService to manage storing telemetry events in the file system
 */
public class TelemetryEventKeeper implements Runnable {

  // region Constants

  protected static final String FILE_EXT = ".tel";
  private static final String UNABLE_TO_CREATE_FILE_MESSAGE = "Unable to create file for telemetry event";
  private static final String ERROR_CREATING_FILE_MESSAGE = "Error while creating file for telemetry event";

  // endregion

  // region Properties

  protected Log getLogger() {
    return logger;
  }

  private static final Log logger = LogFactory.getLog( TelemetryEventKeeper.class );


  protected BlockingQueue<TelemetryEvent> getEventQueue() {
    return this.eventQueue;
  }

  protected void setEventQueue( BlockingQueue<TelemetryEvent> eventQueue ) {
    this.eventQueue = eventQueue;
  }

  private BlockingQueue<TelemetryEvent> eventQueue;


  protected String getTelemetryDirPath() {
    return this.telemetryDirPath;
  }

  protected void setTelemetryDirPath( String telemetryDirPath ) {
    this.telemetryDirPath = telemetryDirPath;
  }

  private String telemetryDirPath;

  // endregion

  // region Constructors

  public TelemetryEventKeeper( BlockingQueue<TelemetryEvent> eventQueue, File telemetryDir ) {
    this.setEventQueue( eventQueue );
    this.setTelemetryDirPath( telemetryDir.getAbsolutePath() );
  }

  // endregion

  // region Methods

  @Override
  public void run() {
    try {
      do {
        processEvent();
      } while ( true );
    } catch ( InterruptedException ie ) {
      // run until interrupted
    }
  }

  /**
   * Takes an event from the event queue and stores it in the file system.
   *
   * @throws InterruptedException
   */
  protected void processEvent() throws InterruptedException {
    BlockingQueue<TelemetryEvent> eventQueue = this.getEventQueue();
    TelemetryEvent event = eventQueue.take();
    try {
      String filename = System.currentTimeMillis() + FILE_EXT;
      FileOutputStream fout = new FileOutputStream( this.getTelemetryDirPath() + "/" + filename );
      ObjectOutputStream oos = new ObjectOutputStream( fout );
      oos.writeObject( event );
      oos.close();
    } catch ( FileNotFoundException fnfe ) {
      this.getLogger().warn( UNABLE_TO_CREATE_FILE_MESSAGE, fnfe );
    } catch ( IOException ioe ) {
      this.getLogger().error( ERROR_CREATING_FILE_MESSAGE, ioe );
    }
  }

  // endregion
}
