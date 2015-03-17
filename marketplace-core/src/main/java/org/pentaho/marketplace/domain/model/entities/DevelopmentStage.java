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
