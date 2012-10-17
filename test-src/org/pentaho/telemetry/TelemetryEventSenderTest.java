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
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *
 * Created Set 17th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */

package org.pentaho.telemetry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

 




public class TelemetryEventSenderTest extends TelemetryBaseTest {

  public class TelemetryEventSenderForTests extends TelemetryEventSender {
                
      public int blockSize;  
      public String blockDataZero;
      public String blockDataOne;
      
      public TelemetryEventSenderForTests(File submissionDir, File telemetryDir, 
              final int resultCode, final boolean exception) {
        
        super(submissionDir, telemetryDir);

        
        defaultHttpMethod  = new PostMethod() {

          private int callNumber;
            @Override 
        public void addParameter(String paramName, String paramValue) {
              if (callNumber == 0)
                blockDataZero = paramValue;
              else
                blockDataOne = paramValue;
              callNumber++;
            }
          
        @Override
        public void setURI(URI uri) throws URIException {
          
        }

        @Override
        public void setStrictMode(boolean bln) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isStrictMode() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setRequestHeader(String string, String string1) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setRequestHeader(Header header) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addRequestHeader(String string, String string1) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addRequestHeader(Header header) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header getRequestHeader(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeRequestHeader(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeRequestHeader(Header header) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getFollowRedirects() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setFollowRedirects(boolean bln) {

        }

        @Override
        public void setQueryString(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setQueryString(NameValuePair[] nvps) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getQueryString() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header[] getRequestHeaders() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header[] getRequestHeaders(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean validate() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getStatusCode() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getStatusText() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header[] getResponseHeaders() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header getResponseHeader(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header[] getResponseHeaders(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header[] getResponseFooters() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Header getResponseFooter(String string) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public byte[] getResponseBody() throws IOException {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getResponseBodyAsString() throws IOException {
          return "<result>OK</result>";
        }

        @Override
        public InputStream getResponseBodyAsStream() throws IOException {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasBeenUsed() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int execute(HttpState hs, HttpConnection hc) throws HttpException, IOException {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void abort() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void recycle() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void releaseConnection() {
          
        }

        @Override
        public void addResponseFooter(Header header) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public StatusLine getStatusLine() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getDoAuthentication() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setDoAuthentication(boolean bln) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public HttpMethodParams getParams() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setParams(HttpMethodParams hmp) {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AuthState getHostAuthState() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AuthState getProxyAuthState() {
          throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isRequestSent() {
          throw new UnsupportedOperationException("Not supported yet.");
        }
      };
        
        
        defaultHttpClient = new HttpClient() {
          
          @Override
           public int executeMethod(HttpMethod method) throws IOException, HttpException {
             if (exception)
               throw new IOException();
             return resultCode;
          }
          
          public HttpConnectionManager getConnectionManager() {
            return new HttpConnectionManager() {
              @Override
              public HttpConnectionManagerParams getParams() {
                return new HttpConnectionManagerParams();
              }

            @Override
            public HttpConnection getConnection(HostConfiguration hc) {
              throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public HttpConnection getConnection(HostConfiguration hc, long l) throws HttpException {
              throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public HttpConnection getConnectionWithTimeout(HostConfiguration hc, long l) throws ConnectionPoolTimeoutException {
              throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void releaseConnection(HttpConnection hc) {
              throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void closeIdleConnections(long l) {
              throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setParams(HttpConnectionManagerParams hcmp) {
              throw new UnsupportedOperationException("Not supported yet.");
            }
            };
          }
        };
      }
      
      
      @Override
      protected void sendRequest(File[] blockToSend) {     
        
        this.blockSize = 0;
        
        for (File f: blockToSend)
          if (f != null) blockSize++;
        
        super.sendRequest(blockToSend);
        
        
      }
 
       
        
  }  
  
  
 
  
  
  @Before
  public void setup() {
    File f1 = new File("lastSubmission");
    if (!f1.exists())
      f1.mkdir();
    
    File f2 = new File("telemetryFiles");
    if (!f2.exists())
      f2.mkdir();
    
 
    cleanFolder(f1);
    cleanFolder(f2);       
  }
  
  
  @After 
  public void teardown() {
    File f1 = new File("lastSubmission");
    File f2 = new File("telemetryFiles");
    
    cleanFolder(f1);
    cleanFolder(f2);       
    
  }
  
  @Test
  public void TestSend()  throws InterruptedException {

    TelemetryEvent te = new TelemetryEvent(getDefaultTelemetryDataProvider("CDE", true));
    TelemetryEvent te2 = new TelemetryEvent(getDefaultTelemetryDataProvider("CDF", true));
    
    File lastSubmissionDir = new File("lastSubmission");
    File telemetryFiles = new File("telemetryFiles");
    createTelEvent(te, lastSubmissionDir);
    
    createTelEvent(te, telemetryFiles);
    Thread.sleep(100);
    createTelEvent(te2, telemetryFiles);
    
    TelemetryEventSenderForTests th = new TelemetryEventSenderForTests(
            lastSubmissionDir, telemetryFiles
            , 200, false);
    
    th.run();
    
    
    Assert.assertTrue(th.blockDataZero.indexOf("\"pluginName\":\"CDE\"") > 0);
    Assert.assertTrue(th.blockDataZero.indexOf("\"pluginName\":\"CDF\"") > 0);
    Assert.assertEquals(2, th.blockSize);
    
    File[] fileList = getTelFilesInFolder(lastSubmissionDir);
    Assert.assertEquals(2, fileList.length);
    
    fileList = getTelFilesInFolder(telemetryFiles);
    Assert.assertEquals(0, fileList.length);    
    
    
  }
   

  @Test
  public void testSendDifferentUrls()  throws InterruptedException {

    TelemetryEvent te = new TelemetryEvent(getDefaultTelemetryDataProvider("CDE", true, "url1"));
    TelemetryEvent te2 = new TelemetryEvent(getDefaultTelemetryDataProvider("CDF", true, "url2"));
    
    File lastSubmissionDir = new File("lastSubmission");
    File telemetryFiles = new File("telemetryFiles");
    createTelEvent(te, lastSubmissionDir);
    
    createTelEvent(te, telemetryFiles);
    Thread.sleep(100);
    createTelEvent(te2, telemetryFiles);
    
    TelemetryEventSenderForTests th = new TelemetryEventSenderForTests(
            lastSubmissionDir, telemetryFiles
            , 200, false);
    
    th.run();
    
    
    Assert.assertEquals(2, th.blockSize);

    Assert.assertTrue(th.blockDataZero.indexOf("\"pluginName\":\"CDE\"") > 0);
    Assert.assertTrue(th.blockDataOne.indexOf("\"pluginName\":\"CDF\"") > 0);

    File[] fileList = getTelFilesInFolder(lastSubmissionDir);
    Assert.assertEquals(2, fileList.length);
    
    fileList = getTelFilesInFolder(telemetryFiles);
    Assert.assertEquals(0, fileList.length);    
    
    
  }




}
