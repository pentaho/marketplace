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

/**
 * Describes the various types of market entries (plugins)
 */
public enum MarketEntryType {
  Step, JobEntry, Partitioner, SpoonPlugin, Database, Mixed, Platform, HadoopShim;

  public static MarketEntryType getMarketEntryType( String code ) {
    for ( MarketEntryType type : values() ) {
      if ( type.name().equalsIgnoreCase( code ) ) {
        return type;
      }
    }
    return null;
  }
}
