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
 *
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
  protected static final String DEFAULT_TELEMETRY_DIR_NAME = ".telemetry";
  private static final String EVENT_KEEPER_THREAD_NAME = "Telemetry Event Keeper Thread";
  private static final String EVENT_SENDER_THREAD_NAME = "Telemetry Event Sender Thread";
  protected static final long DEFAULT_SEND_PERIOD_IN_MINUTES = 1440; // once a day

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
    // ensure that the telemetry dir exists
    if ( !telemetryDir.exists() ) {
      telemetryDir.mkdir();
    }
    this.telemetryDir = telemetryDir;
  }

  private File telemetryDir;


  public long getSendPeriodInMinutes() {
    return this.sendPeriodInMinutes;
  }

  protected void setSendPeriodInMinutes( long sendPeriodInMinutes ) {
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

  public TelemetryHandler() {
    this( DEFAULT_TELEMETRY_DIR_NAME, DEFAULT_SEND_PERIOD_IN_MINUTES );
  }

  public TelemetryHandler( String telemetryDirPath, long sendPeriodInMinutes ) {
    // initialize the event queue
    this.setEventQueue( new ArrayBlockingQueue<TelemetryEvent>( EVENT_QUEUE_CAPACITY ) );

    this.setTelemetryDir( new File( telemetryDirPath ) );
    this.setSendPeriodInMinutes( sendPeriodInMinutes );
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

  @Override
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
