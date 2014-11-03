/*
 * Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

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
