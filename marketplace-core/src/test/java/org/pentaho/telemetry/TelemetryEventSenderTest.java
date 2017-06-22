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
 * Copyright (c) 2015-2017 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.telemetry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class TelemetryEventSenderTest extends TelemetryBaseTest {

  @Test
  public void TestSend() throws InterruptedException, IOException {
    File[] files;
    String lastSubmissionDir = TelemetryEventSender.LAST_SUBMISSION_DIR_NAME;

    // create 2 event files in the telemetry dir
    createEventFileInTelemetryDir( createTelemetryEvent() );
    Thread.sleep( 100 );
    createEventFileInTelemetryDir( createTelemetryEvent() );
    files = getFilesInTelemetryDir();
    Assert.assertEquals( files.length, 2 );

    // create 1 event file in the last submission dir
    createEventFileInTelemetryDir( lastSubmissionDir, createTelemetryEvent() );
    files = getFilesInTelemetryDir( lastSubmissionDir );
    Assert.assertEquals( files.length, 1 );

    // activate the event sender
    TelemetryEventSender eventSender = spy( new TelemetryEventSender( telemetryDir.getRoot() ) );
    HttpPost methodMock = spy( new HttpPost( TEST_TELEMETRY_URL ) );

    String blockDataTest = "\"pluginName\":\"" + TEST_PLUGIN_NAME + "\"";
    doAnswer( new Answer<Void>() {
      @Override
      public Void answer( InvocationOnMock invocation ) throws Throwable {
        Object[] arguments = invocation.getArguments();
        if ( arguments != null && arguments.length > 0 && arguments[ 0 ] != null ) {
          HttpEntity entity = (HttpEntity) arguments[ 0 ];
          String result = EntityUtils.toString( entity, "UTF-8" );
          Assert.assertTrue( result.indexOf( blockDataTest ) > 0 );
        }
        return null;
      }
    } ).when( methodMock ).setEntity( any( HttpEntity.class ) );

    doReturn( HttpStatus.SC_OK ).when( eventSender ).getStatusCode( any( HttpResponse.class ) );
    doReturn( "<result>OK</result>" ).when( eventSender ).getResultXml( any( HttpResponse.class ) );
    eventSender.setDefaultHttpMethod( methodMock );
    eventSender.run();

    // block data should have the test plugin name
    verify( eventSender, atLeast( 1 ) ).sendRequest( any( File[].class ) );

    // telemetry dir should be empty
    files = getFilesInTelemetryDir();
    Assert.assertEquals( files.length, 0 );

    // last submission dir should have the 2 event files that were sent
    files = getFilesInTelemetryDir( lastSubmissionDir );
    Assert.assertEquals( files.length, 2 );
  }
}
