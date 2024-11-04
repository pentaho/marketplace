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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class TelemetryEventKeeperTest extends TelemetryBaseTest {

  @Test
  @Ignore("Depends on processEvent() thread finishing before assert. Needs to be changed to accommodate this.")
  public void testWriteEventToFile() throws InterruptedException {
    TelemetryEvent te;
    BlockingQueue<TelemetryEvent> eventQueue = new ArrayBlockingQueue<TelemetryEvent>( 2 );
    TelemetryEventKeeper eventKeeper = new TelemetryEventKeeper( eventQueue, telemetryDir.getRoot() );
    File[] files;

    te = createTelemetryEvent();
    eventQueue.offer( te );
    eventKeeper.processEvent();
    files = getFilesInTelemetryDir();
    Assert.assertEquals( files.length, 1 );

    te = createTelemetryEvent();
    eventQueue.offer( te );
    eventKeeper.processEvent();
    files = getFilesInTelemetryDir();
    Assert.assertEquals( files.length, 2 );
  }

  @Test
  @Ignore("Depends on processEvent() thread finishing before assert. Needs to be changed to accommodate this.")
  public void testEventReadFromFile() throws InterruptedException, IOException, ClassNotFoundException {
    TelemetryEvent te;
    BlockingQueue<TelemetryEvent> eventQueue = new ArrayBlockingQueue<TelemetryEvent>( 2 );
    TelemetryEventKeeper eventKeeper = new TelemetryEventKeeper( eventQueue, telemetryDir.getRoot() );
    File[] files;

    te = createTelemetryEvent();
    eventQueue.offer( te );
    eventKeeper.processEvent();
    files = getFilesInTelemetryDir();
    Assert.assertEquals( files.length, 1 );

    FileInputStream fin = new FileInputStream( files[ 0 ].getAbsoluteFile() );
    ObjectInputStream ois = new ObjectInputStream( fin );
    TelemetryEvent teFromFile = (TelemetryEvent) ois.readObject();
    ois.close();
    Assert.assertEquals( te.getEventType(), teFromFile.getEventType() );
    Assert.assertEquals( te.getPluginName(), teFromFile.getPluginName() );
    Assert.assertEquals( te.getPluginVersion(), teFromFile.getPluginVersion() );
    Assert.assertEquals( te.getPlatformVersion(), teFromFile.getPlatformVersion() );
    Assert.assertEquals( te.getUrlToCall(), teFromFile.getUrlToCall() );
  }
}
