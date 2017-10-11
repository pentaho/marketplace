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
 * Copyright (c) 2015 - 2017 Hitachi Vantara. All rights reserved.
 */

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
