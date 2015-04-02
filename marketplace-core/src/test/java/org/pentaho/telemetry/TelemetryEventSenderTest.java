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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class TelemetryEventSenderTest extends TelemetryBaseTest {

  public class TelemetryEventSenderForTests extends TelemetryEventSender {

    public int blockSize;
    public String blockDataZero;
    public String blockDataOne;

    public TelemetryEventSenderForTests( File telemetryDir,
                                         final int resultCode, final boolean exception ) {

      super( telemetryDir );

      defaultHttpMethod = new PostMethod() {

        private int callNumber;

        @Override
        public void addParameter( String paramName, String paramValue ) {
        }

        @Override
        public void setRequestEntity( RequestEntity requestEntity ) {
          StringRequestEntity r = (StringRequestEntity) requestEntity;
          if ( callNumber == 0 ) {
            blockDataZero = r.getContent();
          } else {
            blockDataOne = r.getContent();
          }
          callNumber++;

        }

        @Override
        public void setURI( URI uri ) throws URIException {
        }

        @Override
        public void setStrictMode( boolean bln ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean isStrictMode() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setRequestHeader( String string, String string1 ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setRequestHeader( Header header ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void addRequestHeader( String string, String string1 ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void addRequestHeader( Header header ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header getRequestHeader( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void removeRequestHeader( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void removeRequestHeader( Header header ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean getFollowRedirects() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setFollowRedirects( boolean bln ) {
        }

        @Override
        public void setQueryString( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setQueryString( NameValuePair[] nvps ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public String getQueryString() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header[] getRequestHeaders() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header[] getRequestHeaders( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean validate() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public int getStatusCode() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public String getStatusText() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header[] getResponseHeaders() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header getResponseHeader( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header[] getResponseHeaders( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header[] getResponseFooters() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Header getResponseFooter( String string ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public byte[] getResponseBody() throws IOException {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public String getResponseBodyAsString() throws IOException {
          return "<result>OK</result>";
        }

        @Override
        public InputStream getResponseBodyAsStream() throws IOException {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean hasBeenUsed() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public int execute( HttpState hs, HttpConnection hc ) throws HttpException, IOException {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void abort() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void recycle() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void releaseConnection() {

        }

        @Override
        public void addResponseFooter( Header header ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public StatusLine getStatusLine() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean getDoAuthentication() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setDoAuthentication( boolean bln ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public HttpMethodParams getParams() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setParams( HttpMethodParams hmp ) {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public AuthState getHostAuthState() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public AuthState getProxyAuthState() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean isRequestSent() {
          throw new UnsupportedOperationException( "Not supported yet." );
        }
      };

      defaultHttpClient = new HttpClient() {
        @Override
        public int executeMethod( HttpMethod method ) throws IOException {
          if ( exception ) {
            throw new IOException();
          }
          return resultCode;
        }
      };
    }

    @Override
    protected void sendRequest( File[] blockToSend ) {
      this.blockSize = 0;
      for ( File f : blockToSend ) {
        if ( f != null ) {
          blockSize++;
        }
      }
      super.sendRequest( blockToSend );
    }
  }

  @Test
  public void TestSend() throws InterruptedException, IOException {
    File[] files;
    String lastSubmissionDir = TelemetryEventSender.LAST_SUBMISSION_FOLDER;

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
    TelemetryEventSenderForTests eventSender = new TelemetryEventSenderForTests( telemetryDir.getRoot(), 200, false );
    eventSender.run();

    // block data should have the test plugin name
    String blockDataTest = "\"pluginName\":\"" + TEST_PLUGIN_NAME + "\"";
    Assert.assertTrue( eventSender.blockDataZero.indexOf( blockDataTest ) > 0 );
    Assert.assertEquals( eventSender.blockSize, 2 );

    // telemetry dir should be empty
    files = getFilesInTelemetryDir();
    Assert.assertEquals( files.length, 0 );

    // last submission dir should have the 2 event files that were sent
    files = getFilesInTelemetryDir( lastSubmissionDir );
    Assert.assertEquals( files.length, 2 );
  }
}
