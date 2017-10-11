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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;


@Ignore
public class TelemetryBaseTest {

  protected static final String TEST_PLUGIN_NAME = "PLUGIN";
  protected static final String TEST_PLUGIN_VERSION = "1.0.0";
  protected static final String TEST_PLATFORM_VERSION = "PLATFORM-5.2.0";
  protected static final String TEST_TELEMETRY_URL = "http://localhost:8080/telemetry-servlet/telemetry";

  @Rule
  public TemporaryFolder telemetryDir = new TemporaryFolder();

  protected TelemetryHandler createTelemetryHandler() {
    return new TelemetryHandler( telemetryDir.getRoot().getAbsolutePath(),
      TelemetryHandler.DEFAULT_SEND_PERIOD_IN_MINUTES );
  }

  protected TelemetryService createTelemetryService( final boolean telemetryEnabled,
                                                     final TelemetryHandler telemetryHandler ) {
    return new TelemetryService( TEST_PLUGIN_NAME, TEST_PLUGIN_VERSION, TEST_PLATFORM_VERSION, TEST_TELEMETRY_URL,
      telemetryEnabled, telemetryHandler );
  }

  protected TelemetryEvent createTelemetryEvent() {
    return new TelemetryEvent( TelemetryEvent.Type.OTHER, TEST_PLUGIN_NAME, TEST_PLUGIN_VERSION, TEST_PLATFORM_VERSION,
      TEST_TELEMETRY_URL );
  }

  protected File[] getFilesInTelemetryDir( final String path ) {
    File file = new File( telemetryDir.getRoot().getAbsolutePath() + "/" + path );

    return file.listFiles( new FilenameFilter() {
      @Override
      public boolean accept( File file, String name ) {
        return name.endsWith( TelemetryEventKeeper.FILE_EXT );
      }
    } );
  }

  protected File[] getFilesInTelemetryDir() {
    return getFilesInTelemetryDir( "." );
  }

  protected void createEventFileInTelemetryDir( final String path, final TelemetryEvent event ) throws IOException {
    File file = new File( telemetryDir.getRoot().getAbsolutePath() + "/" + path );
    if ( !file.exists() ) {
      telemetryDir.newFolder( path );
    }
    String filename = System.currentTimeMillis() + TelemetryEventKeeper.FILE_EXT;
    FileOutputStream fout =
      new FileOutputStream( telemetryDir.getRoot().getAbsolutePath() + "/" + path + "/" + filename );
    ObjectOutputStream oos = new ObjectOutputStream( fout );
    oos.writeObject( event );
    oos.close();
  }

  protected void createEventFileInTelemetryDir( final TelemetryEvent event ) throws IOException {
    createEventFileInTelemetryDir( ".", event );
  }
}
