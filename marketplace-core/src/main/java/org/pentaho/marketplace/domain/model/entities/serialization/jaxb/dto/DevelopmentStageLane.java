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

package org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for development_stage_lane.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="development_stage_lane">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Customer"/>
 *     &lt;enumeration value="Community"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType( name = "development_stage_lane" )
@XmlEnum
public enum DevelopmentStageLane {

  @XmlEnumValue( "Customer" )
  CUSTOMER( "Customer" ),
  @XmlEnumValue( "Community" )
  COMMUNITY( "Community" );
  private final String value;

  DevelopmentStageLane( String v ) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static DevelopmentStageLane fromValue( String v ) {
    for ( DevelopmentStageLane c : DevelopmentStageLane.values() ) {
      if ( c.value.equals( v ) ) {
        return c;
      }
    }
    throw new IllegalArgumentException( v );
  }

}
