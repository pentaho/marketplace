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
 * The telemetry handler publishes telemetry events to a known location, so that developers can track usage/updates
 * of their work.
 * <p/>
 * Collected data for each telemetry event (described in {@link TelemetryEvent}) is stored in a handler queue.
 * A dedicated thread ({@link TelemetryEventKeeper}) reads this queue and stores the telemetry events in the filesystem.
 * At periodic intervals (default = once a day) another thread ({@link TelemetryEventSender}) reads these events from
 * the filesystem and publishes them to a remote endpoint. In case it succeeds, events are removed from the filesystem.
 * In case it fails, events are kept for a maximum of 5 days. After this time, events will be purged from the
 * filesystem.
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


  public File getTelemetryDir() {
    return this.telemetryDir;
  }

  protected void setTelemetryDir( File telemetryDir ) {
    // ensure that the telemetry folder exists
    if ( !telemetryDir.exists() ) {
      telemetryDir.mkdir();
    }
    this.telemetryDir = telemetryDir;
  }

  private File telemetryDir;


  public long getSendPeriodInMinutes() {
    return this.sendPeriodInMinutes;
  }

  public void setSendPeriodInMinutes( long sendPeriodInMinutes ) {
    this.sendPeriodInMinutes = sendPeriodInMinutes;
  }

  private long sendPeriodInMinutes;


  protected Thread getEventKeeperThread() {
    return this.eventKeeperThread;
  }

  protected void setEventKeeperThread( Thread eventKeeperThread ) {
    this.eventKeeperThread = eventKeeperThread;
  }

  private Thread eventKeeperThread;


  protected ScheduledThreadPoolExecutor getEventSenderThreadPoolExecutor() {
    return eventSenderThreadPoolExecutor;
  }

  protected void setEventSenderThreadPoolExecutor(
    ScheduledThreadPoolExecutor eventSenderThreadPoolExecutor ) {
    this.eventSenderThreadPoolExecutor = eventSenderThreadPoolExecutor;
  }

  private ScheduledThreadPoolExecutor eventSenderThreadPoolExecutor;

  // endregion

  // region Constructors

  public TelemetryHandler( String telemetryDirPath, long sendPeriodInMinutes ) {
    // initialize the event queue
    this.setEventQueue( new ArrayBlockingQueue<TelemetryEvent>( EVENT_QUEUE_CAPACITY ) );

    this.setTelemetryDir( new File( telemetryDirPath ) );
    this.setSendPeriodInMinutes( sendPeriodInMinutes );
  }

  public TelemetryHandler( String telemetryDirPath ) {
    this( telemetryDirPath, DEFAULT_SEND_PERIOD_IN_MINUTES );
  }

  /**
   * Called after class is instantiated by dependency injection
   */
  public void init() {
    this.startEventKeeper();
    this.startEventSender();
  }

  /**
   * Called on object destruction by dependency injection
   */
  public void destroy() {
    this.stopEventKeeper();
    this.stopEventSender();
  }

  // endregion

  // region Methods

  /**
   * Add a telemetry event to the handler queue
   */
  public boolean queueEvent( TelemetryEvent event ) {
    BlockingQueue<TelemetryEvent> eventQueue = this.getEventQueue();
    return eventQueue.offer( event );
  }

  /**
   * Starts a dedicated thread ({@link TelemetryEventKeeper}) that reads the handler queue and stores the telemetry
   * events in the filesystem.
   */
  private void startEventKeeper() {
    // create an event keeper that will store the events in the telemetry dir
    TelemetryEventKeeper eventKeeper = new TelemetryEventKeeper( this.getEventQueue(), this.getTelemetryDir() );

    // create a thread to run the event keeper
    Thread thread = new Thread( eventKeeper );
    thread.setName( EVENT_KEEPER_THREAD_NAME );
    thread.setDaemon( true );
    this.setEventKeeperThread( thread );

    // start the thread
    thread.start();
  }

  /**
   * Stops the event keeper thread.
   */
  private void stopEventKeeper() {
    Thread thread = this.getEventKeeperThread();
    thread.interrupt();
  }

  /**
   * Starts a dedicated thread ({@link TelemetryEventSender}) that runs at periodic intervals, reads the events from
   * the filesystem and publishes them to a remote endpoint.
   */
  private void startEventSender() {
    // create an event sender that will send the events from the telemetry dir to the remote endpoint
    TelemetryEventSender eventSender = new TelemetryEventSender( this.getTelemetryDir() );

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
    this.setEventSenderThreadPoolExecutor( threadPoolExecutor );

    // schedule the event sender to run at periodic intervals
    threadPoolExecutor.scheduleAtFixedRate( eventSender, 0, this.getSendPeriodInMinutes(), TimeUnit.MINUTES );
 }

  /**
   * Stops the event sender thread.
   */
  private void stopEventSender() {
    // shutdown the event sender thread pool
    ScheduledThreadPoolExecutor threadPoolExecutor = this.getEventSenderThreadPoolExecutor();
    threadPoolExecutor.shutdown();
  }

  // endregion
}
