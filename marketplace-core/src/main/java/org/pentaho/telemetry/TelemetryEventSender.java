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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.pentaho.marketplace.util.web.HttpClientManager;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Used by {@link TelemetryHandler} to send telemetry events to a remote endpoint
 */
public class TelemetryEventSender implements Runnable {

  // region Constants

  protected static final String FILE_EXT = ".tel";
  protected static final String LAST_SUBMISSION_DIR_NAME = "lastsubmission";
  private static final int DAYS_TO_KEEP_FILES = 5;
  private static final int BLOCK_SIZE = 50;
  private static final int HTTP_CALL_TIMEOUT = 30000;
  private static final HttpClientManager HTTP_CLIENT_MANAGER = HttpClientManager.getInstance();

  // endregion

  // region Properties

  public Log getLogger() {
    return logger;
  }

  private static final Log logger = LogFactory.getLog( TelemetryEventSender.class );

  protected static HttpClient getHttpClient() {
    return defaultHttpClient != null ? defaultHttpClient
      : HTTP_CLIENT_MANAGER.createBuilder().setConnectionTimeout( HTTP_CALL_TIMEOUT ).build();
  }

  protected static HttpPost defaultHttpMethod;


  protected static HttpPost getHttpMethod() {
    return defaultHttpMethod != null ? defaultHttpMethod : new HttpPost();
  }

  protected static HttpClient defaultHttpClient;


  public File getTelemetryDir() {
    return this.telemetryDir;
  }

  public void setTelemetryDir( File telemetryDir ) {
    this.telemetryDir = telemetryDir;
  }

  private File telemetryDir;


  public File getLastSubmissionDir() {
    return this.lastSubmissionDir;
  }

  public void setLastSubmissionDir( File lastSubmissionDir ) {
    if ( !lastSubmissionDir.exists() ) {
      lastSubmissionDir.mkdir();
    }
    this.lastSubmissionDir = lastSubmissionDir;
  }

  private File lastSubmissionDir;

  // endregion

  // region Constructors

  public TelemetryEventSender( File telemetryDir ) {
    this.setTelemetryDir( telemetryDir );
    this.setLastSubmissionDir( new File( telemetryDir.getAbsolutePath() + "/" + LAST_SUBMISSION_DIR_NAME ) );
  }

  // endregion

  // region Methods

  @Override
  public void run() {
    //Delete everything in lastSubmission folder
    File[] submittedFiles = this.getLastSubmissionDir().listFiles();
    for ( File f : submittedFiles ) {
      f.delete();
    }

    //Get all requests in telemetryPath
    File[] unsubmittedRequests = this.getTelemetryDir().listFiles( new FilenameFilter() {

      @Override
      public boolean accept( File file, String name ) {
        return name.endsWith( FILE_EXT );
      }
    } );

    File[] block = new File[ BLOCK_SIZE ];
    int blockIndex = 0;
    Calendar cld = Calendar.getInstance();
    cld.add( Calendar.DAY_OF_YEAR, -DAYS_TO_KEEP_FILES );
    for ( File f : unsubmittedRequests ) {

      //Check if file was created more than 5 days ago. If so, dismiss it
      if ( f.lastModified() < cld.getTime().getTime() ) {
        f.delete();
        continue;
      }

      //Create blocks of BLOCK_SIZE
      if ( blockIndex > 0 && blockIndex % BLOCK_SIZE == 0 ) {
        sendRequest( block );
        block = new File[ BLOCK_SIZE ];
        blockIndex = 0;
      } else {
        block[ blockIndex ] = f;
        blockIndex++;
      }
    }

    if ( blockIndex > 0 ) {
      sendRequest( block );
    }
  }

  /**
   * Given an array of telemetry event files, parses them, builds a JSON array with all the events and dispatches them
   * to one or more urls. Deletes files if request was successful.
   *
   * @param blockToSend Array of files with telemetry events to send to the server
   */
  protected void sendRequest( File[] blockToSend ) {
    HashMap<String, StringBuffer> urlsAndPostData = new HashMap<String, StringBuffer>();
    HashMap<String, List<File>> urlsAndFiles = new HashMap<String, List<File>>();
    for ( File f : blockToSend ) {
      if ( f == null ) {
        break;
      }

      try {
        FileInputStream fin = new FileInputStream( f );
        ObjectInputStream ois = new ObjectInputStream( fin );
        TelemetryEvent event = (TelemetryEvent) ois.readObject();
        ois.close();

        StringBuffer postData = urlsAndPostData.get( event.getUrlToCall() );

        if ( postData == null ) {
          postData = new StringBuffer().append( "[" );
        } else {
          postData.append( ", " );
        }

        postData.append( event.encodeToJSON() );

        urlsAndPostData.put( event.getUrlToCall(), postData );
        List<File> filesForThisUrl = urlsAndFiles.get( event.getUrlToCall() );
        if ( filesForThisUrl == null ) {
          filesForThisUrl = new ArrayList<File>();
        }
        filesForThisUrl.add( f );
        urlsAndFiles.put( event.getUrlToCall(), filesForThisUrl );
      } catch ( EOFException eofe ) {
        this.getLogger().warn(
          "EOF caught while deserializing telemetry event. Probably a corrupt save. Deleting event.", eofe );
        f.delete();
      } catch ( IOException ioe ) {
        this.getLogger().error( "Error caught while deserializing telemetry event.", ioe );
        f.delete();
      } catch ( ClassNotFoundException cnfe ) {
        this.getLogger().error( "Class not found while deserializing telemetry event.", cnfe );
      }
    }

    Iterator<String> urlIterator = urlsAndPostData.keySet().iterator();
    while ( urlIterator.hasNext() ) {
      String url = urlIterator.next();
      StringBuffer postData = urlsAndPostData.get( url );
      postData.append( "]" );
      postData.append( System.lineSeparator() );
      boolean success = true;

      try {
        final HttpClient httpClient = getHttpClient();
        final HttpPost httpMethod = getHttpMethod();
        httpMethod.setURI( URI.create( url ) );

        httpMethod.setEntity( new StringEntity( postData.toString(), ContentType.APPLICATION_JSON ) );
        this.getLogger().info( "Calling " + url );
        this.getLogger().info( "Data: " + postData.toString() );

        // Execute the request
        HttpResponse httpResponse = httpClient.execute( httpMethod );
        final int resultCode = httpResponse.getStatusLine().getStatusCode();
        if ( resultCode != HttpURLConnection.HTTP_OK ) {
          this.getLogger().error( "Invalid Result Code Returned: " + resultCode );
          success = false;
        } else {
          HttpEntity entity = httpResponse.getEntity();
          String resultXml = EntityUtils.toString( entity, "UTF-8" );
          //TO DO: Improve error detection
          if ( !resultXml.contains( "<result>OK</result>" ) ) {
            this.getLogger().warn( "Telemetry request had unexpected result: " + resultXml + "." );
            success = false;
          }
        }

        // Clean up
        httpMethod.releaseConnection();

      } catch ( Exception e ) {
        this.getLogger().warn( "Exception caught while making telemetry request.", e );
        success = false;
      }

      //Clear files
      if ( success ) {
        for ( File f : blockToSend ) {
          if ( f != null ) {
            File newFile = new File( this.getLastSubmissionDir(), f.getName() );
            f.renameTo( newFile );
            f.delete();
          }
        }
      }
    }
  }

  // endregion
}
