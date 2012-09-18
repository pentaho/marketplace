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
 */package org.pentaho.telemetry;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

 
 /**
  * Internal class to be used in conjuntion with the telemetry event request queue.
  * Useful for setting up retry attempts
  * @author pedrovale
  */
public class TelemetryEvent implements Delayed {

  private String urlToCall;
  private long delay;
  private TimeUnit delayTimeUnit;
  private final long origin;
  private final int attempts;

  
  
  public TelemetryEvent(String urlToCall, long delay, TimeUnit delayTimeUnit, int attempts) {
    this.urlToCall = urlToCall;
    this.delay = delay;
    this.delayTimeUnit = delayTimeUnit;
    this.origin = System.currentTimeMillis();
    this.attempts = attempts;
  }
  
  
  public int getNumAttempts() {
    return this.attempts;
  }
  
  @Override
  public long getDelay(TimeUnit tu) {
    long delayInMillisecods = TimeUnit.MILLISECONDS.convert(delay, delayTimeUnit);
    
    return tu.convert(delayInMillisecods - ( System.currentTimeMillis() - origin ), TimeUnit.MILLISECONDS);
  }

  @Override
  public int compareTo(Delayed t) {
    long tDelay = t.getDelay(delayTimeUnit);
    long myDelay = getDelay(delayTimeUnit);
    if (myDelay < tDelay )
      return -1;
    else if (myDelay == tDelay)
      return 0;

    return 1;
  }

  /**
   * @return the urlToCall
   */
  public String getUrlToCall() {
    return urlToCall;
  }

  /**
   * @param urlToCall the urlToCall to set
   */
  public void setUrlToCall(String urlToCall) {
    this.urlToCall = urlToCall;
  }
  
}
