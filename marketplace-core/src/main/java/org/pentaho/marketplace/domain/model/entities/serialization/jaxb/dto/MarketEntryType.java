
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


package org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for market_entry_type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="market_entry_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Platform"/>
 *     &lt;enumeration value="Database"/>
 *     &lt;enumeration value="Step"/>
 *     &lt;enumeration value="JobEntry"/>
 *     &lt;enumeration value="Mixed"/>
 *     &lt;enumeration value="Partitioner"/>
 *     &lt;enumeration value="HadoopShim"/>
 *     &lt;enumeration value="SpoonPlugin"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType( name = "market_entry_type" )
@XmlEnum
public enum MarketEntryType {


  /**
   * This market entry is a bi platform plugin, such as a content generator plugin.
   */
  @XmlEnumValue( "Platform" )
  PLATFORM( "Platform" ),

  /**
   * This market entry is a database connection plugin for pentaho data integration.
   */
  @XmlEnumValue( "Database" )
  DATABASE( "Database" ),

  /**
   * This market entry is a transformation step plugin for pentaho data integration.
   */
  @XmlEnumValue( "Step" )
  STEP( "Step" ),

  /**
   * The market entry is a job entry plugin for pentaho data integration.
   */
  @XmlEnumValue( "JobEntry" )
  JOB_ENTRY( "JobEntry" ),

  /**
   * Mixed type. If the market entry could comprise a number of plugins.
   */
  @XmlEnumValue( "Mixed" )
  MIXED( "Mixed" ),

  /**
   * This market entry is a row partitioner plugin for pentaho data integration.
   */
  @XmlEnumValue( "Partitioner" )
  PARTITIONER( "Partitioner" ),

  /**
   * This market entry is a big data shim - an abstraction of a typical hadoop distro - for use in the pentaho big data
   * plugin.
   */
  @XmlEnumValue( "HadoopShim" )
  HADOOP_SHIM( "HadoopShim" ),

  /**
   * This market entry is a Spoon plugin, possibly a perspective or something that adds to the PDI UI.
   */
  @XmlEnumValue( "SpoonPlugin" )
  SPOON_PLUGIN( "SpoonPlugin" );
  private final String value;

  MarketEntryType( String v ) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static MarketEntryType fromValue( String v ) {
    for ( MarketEntryType c : MarketEntryType.values() ) {
      if ( c.value.equals( v ) ) {
        return c;
      }
    }
    throw new IllegalArgumentException( v );
  }

}
