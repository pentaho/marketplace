
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
