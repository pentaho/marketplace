/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.IVersionData;

public class VersionData implements IVersionData {

  //region Attributes
  private int major;
  private int minor;
  private int patch;
  private String info;
  //endregion

  //region Constructors
  public VersionData( String info ) {
    // general parsing algorithm
    // first see if this starts with a N.N.N
    // second see if it starts with a N.N
    // third see with "TRUNK"
    // also handle -SNAPSHOT, etc

    this.info = info;
    if ( this.info == null ) {
      this.info = "";
    }
    int majorStop = this.info.indexOf( "." );
    if ( majorStop >= 0 ) {
      // read in first number
      // Examples: 1.0-SNAPSHOT, 1.0.0-GA, etc
      try {
        this.major = Integer.parseInt( this.info.substring( 0, majorStop ) );
      } catch ( Exception e ) {
        // couldn't parse "N.xxxx", setting to MAX-INT
        this.major = Integer.MAX_VALUE;
        this.minor = Integer.MAX_VALUE;
        this.patch = Integer.MAX_VALUE;
        return;
      }

      int minorStop = this.info.indexOf( ".", majorStop + 1 );
      if ( minorStop >= 0 ) {
        try {
          this.minor = Integer.parseInt( this.info.substring( majorStop + 1, minorStop ) );
        } catch ( Exception e ) {
          // couldn't parse "MAJOR.N.xxxx", setting to MAX-INT
          this.minor = Integer.MAX_VALUE;
          this.patch = Integer.MAX_VALUE;
          return;
        }

        // Examples: 1.2.0, 1.2.0-stable, 1.2.0-GA, 1.2.0.GA

        int patchStop = this.info.indexOf( ".", minorStop + 1 );
        if ( patchStop < 0 ) {
          patchStop = this.info.indexOf( "-", minorStop + 1 );
        }
        if ( patchStop < 0 ) {
          patchStop = this.info.length();
        }

        try {
          this.patch = Integer.parseInt( this.info.substring( minorStop + 1, patchStop ) );
        } catch ( Exception e ) {
          // couldn't parse "MAJOR.MINOR.N[.-]xxxx", setting to MAX-INT
          this.patch = Integer.MAX_VALUE;
          return;
        }
      } else {
        minorStop = this.info.indexOf( "-", majorStop + 1 );
        if ( minorStop >= 0 ) {
          // Examples: 4.5-SNAPSHOT, 1.2-SNAPSHOT
          try {
            this.minor = Integer.parseInt( this.info.substring( majorStop + 1, minorStop ) );
          } catch ( Exception e ) {
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
            this.minor = Integer.parseInt( this.info.substring( majorStop + 1, minorStop ) );
          } catch ( Exception e ) {
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
      majorStop = this.info.indexOf( "-" );
      if ( majorStop >= 0 ) {
        // 4-SNAPSHOT
        // TRUNK-SNAPSHOT

        try {
          this.major = Integer.parseInt( this.info.substring( 0, majorStop ) );
        } catch ( Exception e ) {
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
          this.major = Integer.parseInt( this.info );
        } catch ( Exception e ) {
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
  //endregion

  //region IVersionData implementation
  @Override
  public int getMajor() {
    return this.major;
  }

  @Override
  public int getMinor() {
    return this.minor;
  }

  @Override
  public int getPatch() {
    return this.patch;
  }

  @Override
  public String getInfo() {
    return this.info;
  }

  @Override
  public boolean within( IVersionData min, IVersionData max ) {
    // if min and max aren't specified, skip check
    if ( min.getInfo().equals( "" ) && max.getInfo().equals( "" ) ) {
      return true;
    }

    // see if min and max are equal to one another.  If so, make sure platform version is equal
    // this allows for folks to specify specific -GA and -stable releases
    if ( min.getInfo().equals( max.getInfo() ) ) {
      return this.info.equals( min.getInfo() );
    }

    // do a major, minor, patch comparison
    return ( this.compareTo( min ) <= 0 && this.compareTo( max ) >= 0 );
  }

  @Override
  public int compareTo( IVersionData versionData ) {
    if ( versionData.getMajor() > this.major ) {
      return 1;
    }
    if ( versionData.getMajor() < this.major ) {
      return -1;
    }
    if ( versionData.getMinor() > this.minor ) {
      return 1;
    }
    if ( versionData.getMinor() < this.minor ) {
      return -1;
    }
    if ( versionData.getPatch() > this.patch ) {
      return 1;
    }
    if ( versionData.getPatch() < this.patch ) {
      return -1;
    }
    return 0;
  }
  //endregion
}
