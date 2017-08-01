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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    assertEquals( files.length, 2 );

    // create 1 event file in the last submission dir
    createEventFileInTelemetryDir( lastSubmissionDir, createTelemetryEvent() );
    files = getFilesInTelemetryDir( lastSubmissionDir );
    assertEquals( files.length, 1 );

    // activate the event sender
    TelemetryEventSender eventSender = spy( new TelemetryEventSender( telemetryDir.getRoot() ) );

    HttpClient httpClient = mock( HttpClient.class );
    HttpResponse httpResponse = mock( HttpResponse.class );
    StatusLine statusLine = mock( StatusLine.class );
    when( statusLine.getStatusCode() ).thenReturn( HttpStatus.SC_OK );
    when( httpResponse.getStatusLine() ).thenReturn( statusLine );
    when( httpResponse.getEntity() ).thenReturn( new StringEntity( "<result>OK</result>" ) );
    when( httpClient.execute( any( HttpRequestBase.class ) ) ).thenReturn( httpResponse );
    HttpPost httpMethod = mock( HttpPost.class );

    TelemetryEventSender.defaultHttpClient = httpClient;
    TelemetryEventSender.defaultHttpMethod = httpMethod;

    eventSender.run();

    // block data should have the test plugin name
    String blockDataTest = "\"pluginName\":\"" + TEST_PLUGIN_NAME + "\"";
    ArgumentCaptor<StringEntity> entityArg = ArgumentCaptor.forClass( StringEntity.class );
    verify( httpMethod ).setEntity( entityArg.capture() );
    assertTrue( EntityUtils.toString( entityArg.getValue() ).contains( blockDataTest ) );

    ArgumentCaptor<File[]> filesArg = ArgumentCaptor.forClass( File[].class );
    verify( eventSender ).sendRequest( filesArg.capture() );
    assertEquals( 2, Arrays.asList( filesArg.getValue() ).stream().filter( Objects::nonNull ).count() );

    // telemetry dir should be empty
    files = getFilesInTelemetryDir();
    assertEquals( files.length, 0 );

    // last submission dir should have the 2 event files that were sent
    files = getFilesInTelemetryDir( lastSubmissionDir );
    assertEquals( files.length, 2 );
  }
}
