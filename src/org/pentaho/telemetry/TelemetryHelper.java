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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import java.net.HttpURLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelemetryHelper {

  public enum TelemetryEventType {

    INSTALLATION, REMOVAL, USAGE, OTHER
  };

  private static final int MAX_NUM_ATTEMPTS = 10;
  private static final int WAIT_BETWEEN_TRIES_MINUTES = 5;
  
  private static Log logger = LogFactory.getLog(TelemetryHelper.class);
  protected static BlockingQueue<TelemetryEvent> requestQueue = new DelayQueue<TelemetryEvent>();
  private ITelemetryDataProvider dataProvider;
  protected static HttpClient defaultHttpClient;

  protected static HttpClient getHttpClient() {
    return defaultHttpClient != null ? defaultHttpClient : new HttpClient();
  }
  protected static HttpMethod defaultHttpMethod;

  protected static HttpMethod getHttpMethod() {
    return defaultHttpMethod != null ? defaultHttpMethod : new GetMethod();
  }

  static {
    //Launch the thread that will send the request
    Thread requestThread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          do {
            TelemetryEvent event = requestQueue.take();

            
            final HttpClient httpClient = getHttpClient();
            final HttpMethod httpMethod = getHttpMethod();
            try {
              int timeout = 30000;

              httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);

              String url = event.getUrlToCall();
              httpMethod.setURI(new URI(url, true));

              
              logger.info("Calling " + url);
              
              // Execute the request
              final int resultCode = httpClient.executeMethod(httpMethod);
              if (resultCode != HttpURLConnection.HTTP_OK) {
                logger.error("Invalid Result Code Returned: " + resultCode);
                setupEventRetry(event);
              } else {
                String resultXml = httpMethod.getResponseBodyAsString();
                //TO DO: Improve error detection
                if (resultXml.indexOf("OK") < 0) {
                  logger.warn("Telemetry request had unexpected result: " + resultXml + ".");
                  setupEventRetry(event);
                  
                }
              }
              
              // Clean up
              httpMethod.releaseConnection();
              
            } catch (Exception e) {
              logger.warn("Exception caught while making telemetry reuest.", e);
              setupEventRetry(event);
            }
          } while (true);
        } catch (InterruptedException ie) {
          logger.warn("Got interrupted");
        }

      }
    });

    requestThread.start();
  }

  
  private static void setupEventRetry(TelemetryEvent event) {
    int pastAttempts = event.getNumAttempts();
    pastAttempts++;
    if (pastAttempts < MAX_NUM_ATTEMPTS) {
      logger.info("Rescheduling telemetry event publish to " + WAIT_BETWEEN_TRIES_MINUTES*pastAttempts + " minutes from now.");
      requestQueue.add(new TelemetryEvent(event.getUrlToCall(), WAIT_BETWEEN_TRIES_MINUTES*pastAttempts, TimeUnit.MINUTES, pastAttempts));        
    } else
      logger.info("Unable to publish telemetry event for url " + event.getUrlToCall() + ". Tried " + MAX_NUM_ATTEMPTS + " times and failed. Removing event from queue");
  }
  
  
  public TelemetryHelper() {
  }

  /**
   * Sets the data provider that will be used to retrieve the data
   * about this instance of the running application. If this method 
   * is called multiple times, the last data provider specified 
   * will be used.
   */
  public void setDataProvider(ITelemetryDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * Publishes a telemetry event using the provided data provider as a 
   * source for what to publish and to where.
   * @return <i>true</i> if the event was published correctly, <i>false</i> otherwise
   */
  public boolean publishTelemetryEvent() {

    if (!isTelemetryEnabled()) {
      logger.info("Telemetry was not enabled for this server installation");
      return false;
    }

    try {
      return sendRequest(getUrl());
    } catch (UnsupportedEncodingException uee) {
      logger.error("UTF-8 is apparently not supported by your system. Unable to create telemetry event", uee);
      return false;
    }

  }


  protected String getUrl() throws UnsupportedEncodingException {
    if (dataProvider != null) {
      //Build parameters map
      Map<String, String> parameters = new HashMap<String, String>();
      if (dataProvider.getExtraInformation() != null) {
        parameters.putAll(dataProvider.getExtraInformation());
      }
      parameters.put("type", dataProvider.getEventType() == TelemetryHelper.TelemetryEventType.OTHER ? "other"
              : (dataProvider.getEventType() == TelemetryHelper.TelemetryEventType.INSTALLATION ? "install"
              : (dataProvider.getEventType() == TelemetryHelper.TelemetryEventType.REMOVAL ? "removal"
              : "usage")));
      parameters.put("plugin", dataProvider.getPluginName());
      parameters.put("platVersion", dataProvider.getPlatformVersion());
      parameters.put("pluginVersion", dataProvider.getPluginVersion());
      parameters.put("timestamp","" + System.currentTimeMillis() );

      String baseUrl = dataProvider.getBaseUrl();

      final StringBuffer queryString = new StringBuffer();
      queryString.append(baseUrl);
      if (parameters != null) {
        String connector = ""; //$NON-NLS-1$
        if (baseUrl.indexOf('?') == -1) {
          connector = "?"; //$NON-NLS-1$
        } else if (!baseUrl.endsWith("&")) { //$NON-NLS-1$
          connector = "&"; //$NON-NLS-1$
        }

        for (final Iterator it = parameters.keySet().iterator(); it.hasNext();) {
          final String key = (String)it.next();
          if (key != null) {
            final String value = parameters.get(key);
            queryString.append(connector).append(URLEncoder.encode(key, "UTF-8")).
                    append('=').
                    append(URLEncoder.encode(value, "UTF-8"));
            connector = "&"; //$NON-NLS-1$
          }
        }
      }
      return queryString.toString();
    }

    return null;

  }

  protected boolean sendRequest(String url) {
    return requestQueue.offer(new TelemetryEvent(url, 0, TimeUnit.NANOSECONDS, 0));
  }

  private boolean isTelemetryEnabled() {
    if (dataProvider != null) {
      return dataProvider.isTelemetryEnabled();
    }

    return false;
  }
}
