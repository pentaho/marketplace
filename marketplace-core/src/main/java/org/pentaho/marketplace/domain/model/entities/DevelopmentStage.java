/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDevelopmentStage;

public final class DevelopmentStage implements IDevelopmentStage {

  // region Attributes
  private final String lane;
  private final String phase;
  // endregion

  // region Constructors
  public DevelopmentStage( String lane, String phase ) {
    this.lane = lane;
    this.phase = phase;
  }
  // endregion

  // region Methods
  @Override public String getLane() {
    return this.lane;
  }

  @Override public String getPhase() {
    return this.phase;
  }
  // endregion


  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    DevelopmentStage that = (DevelopmentStage) o;

    if ( lane != null ? !lane.equals( that.lane ) : that.lane != null ) {
      return false;
    }
    if ( phase != null ? !phase.equals( that.phase ) : that.phase != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = lane != null ? lane.hashCode() : 0;
    result = 31 * result + ( phase != null ? phase.hashCode() : 0 );
    return result;
  }
}
