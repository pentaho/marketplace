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

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * The telemetry package allows Pentaho plugins to publish telemetry events to a known location, so that developers can
 * track usage/updates of their work.
 * <p/>
 * Collected data for each telemetry event (described in {@link TelemetryEvent}) is: <ul> <li>Plugin Name</li>
 * <li>Plugin Version</li> <li>Platform Version</li> <li>Time stamp </li> <li>Event type: One of Installation, removal,
 * usage or other</li> </ul>
 * <p/>
 * Telemetry can be enabled/disabled using the element telemetry in the pentaho.xml file.
 * <p/>
 * For a BA Server plugin, the workflow for telemetry publishing is:
 * <p/>
 * 1. Create an instance of {@link org.pentaho.telemetry.BaPluginTelemetry} providing the plugin name 2. Execute the {@
 * BaPlugin.Telemetry.sendTelemetryRequest} method. 3. The request is handled by TelemetryHelper, that stores it in a
 * queue. 4. A dedicated thread ({@link TelemetryEventKeeper}) reads this queue and stores the telemetry events on the
 * filesystem (under system/.telemetry). 5. Once a day, or whenever the BA server starts up, another thread ({@link
 * TelemetryEventSender}) reads these events from the filesystem and publishes them (in blocks of 50) to a remote
 * endpoint that will store the data. In case it succeeds, events are removed from the filesystem In case it fails,
 * events are kept for a maximum of 5 days. After this time, events will be purged from the filesystem.
 *
 * @author pedrovale
 */
public class TelemetryHandler implements ITelemetryHandler {

  // region Constants

  private static final int EVENT_QUEUE_CAPACITY = 100;
  private static final String EVENT_KEEPER_THREAD_NAME = "Telemetry Event Keeper Thread";
  private static final String EVENT_SENDER_THREAD_NAME = "Telemetry Event Sender Thread";
  private static final long DEFAULT_SEND_PERIOD_IN_MINUTES = 1440; // once a day

  // endregion

  // region Properties

  protected BlockingQueue<TelemetryEvent> getEventQueue() {
    return this.eventQueue;
  }

  protected void setEventQueue( BlockingQueue<TelemetryEvent> eventQueue ) {
    this.eventQueue = eventQueue;
  }

  private BlockingQueue<TelemetryEvent> eventQueue;

  // endregion

  // region Constructors

  public TelemetryHandler( String telemetryDirPath, long sendPeriodInMinutes ) {
    // ensure that the telemetry folder exists
    File telemetryDir = new File( telemetryDirPath );
    if ( !telemetryDir.exists() ) {
      telemetryDir.mkdir();
    }

    // initialize the event queue
    this.setEventQueue( new ArrayBlockingQueue<TelemetryEvent>( EVENT_QUEUE_CAPACITY ) );

    // launch the thread that will store the events in the telemetry folder
    this.launchEventKeeper( new TelemetryEventKeeper( this.eventQueue, telemetryDir ) );

    // launch the thread that will send the events to the remote endpoint
    this.launchEventSender( new TelemetryEventSender( telemetryDir ), sendPeriodInMinutes );
  }

  public TelemetryHandler( String telemetryDirPath ) {
    this( telemetryDirPath, DEFAULT_SEND_PERIOD_IN_MINUTES );
  }

  // endregion

  // region Methods

  public boolean queueEvent( TelemetryEvent event ) {
    BlockingQueue<TelemetryEvent> eventQueue = this.getEventQueue();
    return eventQueue.offer( event );
  }

  private void launchEventKeeper( TelemetryEventKeeper eventKeeper ) {
    // create a thread to run the event keeper
    Thread thread = new Thread( eventKeeper );
    thread.setName( EVENT_KEEPER_THREAD_NAME );
    thread.setDaemon( true );
    thread.start();
  }

  private void launchEventSender( TelemetryEventSender eventSender, long periodInMinutes ) {
    // create a thread pool for the event sender
    ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(
      1,
      new ThreadFactory() {
        @Override
        public Thread newThread( Runnable r ) {
          Thread thread = new Thread( r );
          thread.setName( EVENT_SENDER_THREAD_NAME );
          thread.setDaemon( true );
          return thread;
        }
      },
      new ThreadPoolExecutor.DiscardPolicy()
    );

    // schedule the event sender to run in a new thread at periodic intervals
    threadPoolExecutor.scheduleAtFixedRate( eventSender, 0, periodInMinutes, TimeUnit.MINUTES );
  }

  // endregion
}
