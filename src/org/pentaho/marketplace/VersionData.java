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
 * Copyright 2012 Pentaho Corporation.  All rights reserved.
 *
 * Created Oct 12th, 2012
 * @author Will Gorman (wgorman@pentaho.com)
 */
package org.pentaho.marketplace;

public class VersionData implements Comparable<VersionData> {
  int major;
  int minor;
  int patch;
  String info;
  
  public VersionData(String info) {

    // general parsing algorithm
    // first see if this starts with a N.N.N
    // second see if it starts with a N.N
    // third see with "TRUNK"
    // also handle -SNAPSHOT, etc

    this.info = info;
    if (this.info == null) {
      this.info = "";
    }
    int majorStop = this.info.indexOf(".");
    if (majorStop >= 0) {
      // read in first number
      // Examples: 1.0-SNAPSHOT, 1.0.0-GA, etc
      try {
        this.major = Integer.parseInt(this.info.substring(0, majorStop));
      } catch (Exception e) {
        // couldn't parse "N.xxxx", setting to MAX-INT
        this.major = Integer.MAX_VALUE;
        this.minor = Integer.MAX_VALUE;
        this.patch = Integer.MAX_VALUE;
        return;
      }

      int minorStop = this.info.indexOf(".", majorStop + 1);
      if (minorStop >= 0) {
        try {
          this.minor = Integer.parseInt(this.info.substring(majorStop + 1, minorStop));
        } catch (Exception e) {
          // couldn't parse "MAJOR.N.xxxx", setting to MAX-INT
          this.minor = Integer.MAX_VALUE;
          this.patch = Integer.MAX_VALUE;
          return;
        }
        
        // Examples: 1.2.0, 1.2.0-stable, 1.2.0-GA, 1.2.0.GA
        
        int patchStop = this.info.indexOf(".", minorStop + 1);
        if (patchStop < 0) {
          patchStop = this.info.indexOf("-", minorStop + 1);
        }
        if (patchStop < 0) {
          patchStop = this.info.length();
        }
        
        try {
          this.patch = Integer.parseInt(this.info.substring(minorStop + 1, patchStop));
        } catch (Exception e) {
          // couldn't parse "MAJOR.MINOR.N[.-]xxxx", setting to MAX-INT
          this.patch = Integer.MAX_VALUE;
          return;
        }      
      } else {
        minorStop = this.info.indexOf("-", majorStop + 1);
        if (minorStop >= 0) {
          // Examples: 4.5-SNAPSHOT, 1.2-SNAPSHOT
          try {
            this.minor = Integer.parseInt(this.info.substring(majorStop + 1, minorStop));
          } catch (Exception e) {
            // couldn't parse "MAJOR.N-xxxx", setting to MAX-INT
            this.minor = Integer.MAX_VALUE;
            this.patch = Integer.MAX_VALUE;
            return;
          }
          
          // whatever is after the -, set the patch to MAX_VALUE
          this.patch = Integer.MAX_VALUE;
          return;
          
        } else {
          // Examples: 4.5, 3.1, etc
          minorStop = this.info.length();
          try {
            this.minor = Integer.parseInt(this.info.substring(majorStop + 1, minorStop));
          } catch (Exception e) {
            // couldn't parse "MAJOR.N.xxxx", setting to MAX-INT
            this.minor = Integer.MAX_VALUE;
            this.patch = Integer.MAX_VALUE;
            return;
          }
          
          // set the patch to 0
          this.patch = 0;
          return;              
        }
      }
    
    } else {
      majorStop = this.info.indexOf("-");
      if (majorStop >= 0) {
        // 4-SNAPSHOT
        // TRUNK-SNAPSHOT

        try {
          this.major = Integer.parseInt(this.info.substring(0, majorStop));
        } catch (Exception e) {
          // couldn't parse "N.xxxx", setting to MAX-INT
          this.major = Integer.MAX_VALUE;
          this.minor = Integer.MAX_VALUE;
          this.patch = Integer.MAX_VALUE;
          return;
        }
        
        this.minor = Integer.MAX_VALUE;
        this.patch = Integer.MAX_VALUE;
        return;

      } else {
        // 4
        try {
          this.major = Integer.parseInt(this.info);
        } catch (Exception e) {
          // couldn't parse "N.xxxx", setting to MAX-INT
          this.major = Integer.MAX_VALUE;
          this.minor = Integer.MAX_VALUE;
          this.patch = Integer.MAX_VALUE;
          return;
        }
        
        this.minor = 0;
        this.patch = 0;
        return;
      }
    }
  }

  public boolean within(VersionData min, VersionData max) {
    // if min and max aren't specified, skip check
    if (min.info.equals("") && max.info.equals("")) {
      return true;
    }

    // see if min and max are equal to one another.  If so, make sure platform version is equal
    // this allows for folks to specify specific -GA and -stable releases 
    if (min.info.equals(max.info)) {
      return this.info.equals(min.info);
    }
       
    // do a major, minor, patch comparison
    return (this.compareTo(min) <= 0 && this.compareTo(max) >= 0);
  }

  @Override
  public int compareTo(VersionData arg0) {
    if (arg0.major > this.major) return 1;
    if (arg0.major < this.major) return -1;
    if (arg0.minor > this.minor) return 1;
    if (arg0.minor < this.minor) return -1;
    if (arg0.patch > this.patch) return 1;
    if (arg0.patch < this.patch) return -1;
    return 0;
  }
  
}