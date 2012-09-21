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
 * Created Set 20th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */
package org.pentaho.telemetry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelemetryEventSender implements Runnable {

  private static final Log logger = LogFactory.getLog(TelemetryEventSender.class);
  protected static PostMethod defaultHttpMethod;
  protected static HttpClient defaultHttpClient;
  protected File lastSubmissionDir;
  private File telemetryDir;

  
  public TelemetryEventSender(File lastSubmissionDir, File telemetryDir) {
    this.lastSubmissionDir = lastSubmissionDir;
    this.telemetryDir = telemetryDir;
  }
  
  
  protected static HttpClient getHttpClient() {
    return defaultHttpClient != null ? defaultHttpClient : new HttpClient();
  }

  protected static PostMethod getHttpMethod() {
    return defaultHttpMethod != null ? defaultHttpMethod : new PostMethod();
  }

  protected void sendRequest(File[] blockToSend) {
    final HttpClient httpClient = getHttpClient();
    final PostMethod httpMethod = getHttpMethod();

    String baseUrl = null;
    HashMap<String, StringBuffer> urlsAndPostData = new HashMap<String, StringBuffer>();
    for (File f : blockToSend) {
      if (f == null)
        break;
      
      try {
        FileInputStream fin = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fin);
        TelemetryEvent event = (TelemetryEvent) ois.readObject();
        ois.close();

        StringBuffer postData = urlsAndPostData.get(event.getUrlToCall());
        
        if (postData == null) {
          postData = new StringBuffer().append("[");
        } else
          postData.append(", ");
        
        postData.append(event.encodeEvent());
        
        urlsAndPostData.put(event.getUrlToCall(), postData);

      } catch (IOException ioe) {
        logger.error("Error caught while deserializing telemetry event.", ioe);
      } catch (ClassNotFoundException cnfe) {
        logger.error("Class not found while deserializing telemetry event.", cnfe);
      }
    }

    
    Iterator<String> urlIterator = urlsAndPostData.keySet().iterator();
    while (urlIterator.hasNext()) {
      String url = urlIterator.next();
      StringBuffer postData = urlsAndPostData.get(url);
      postData.append("]");

      try {                  
        int timeout = 30000;

        httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);        
        httpMethod.setURI(new URI(url, true));
      
      
        httpMethod.setParameter("body", postData.toString());

        logger.info("Calling " + url);
        logger.info("Data: " + postData.toString());

        // Execute the request
        final int resultCode = httpClient.executeMethod(httpMethod);
        if (resultCode != HttpURLConnection.HTTP_OK) {
          logger.error("Invalid Result Code Returned: " + resultCode);
        } else {
          String resultXml = httpMethod.getResponseBodyAsString();
          //TO DO: Improve error detection
          if (resultXml.indexOf("OK") < 0) {
            logger.warn("Telemetry request had unexpected result: " + resultXml + ".");
          }
        }

        // Clean up
        httpMethod.releaseConnection();

      } catch (Exception e) {
        logger.warn("Exception caught while making telemetry reuest.", e);
      }                        
    }
    

    for (File f : blockToSend) {
      if (f != null) {
        File newFile = new File(lastSubmissionDir, f.getName());
        f.renameTo(newFile);
        f.delete();
      }
    }
  }

  @Override
  public void run() {
    //Delete everything in lastSubmission folder
    File[] submittedFiles = lastSubmissionDir.listFiles();
    for (File f : submittedFiles) {
      f.delete();
    }

    //Get all requests in telemetryPath
    File[] unsubmittedRequests = telemetryDir.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File file, String name) {
        return name.endsWith(".tel");
      }
    });


    //TO DO: Set a maximum number of requests that can be sent per thread 
    //run
    File[] block = new File[50];
    int blockIndex = 0;
    for (File f : unsubmittedRequests) {
      //Create blocks of 50
      if (blockIndex > 0 && blockIndex % 50 == 0) {
        sendRequest(block);
        block = new File[50];
        blockIndex = 0;
      } else {
        block[blockIndex] = f;
        blockIndex++;
      }
    }

    if (blockIndex > 0) {
      sendRequest(block);
    }

  }

}
