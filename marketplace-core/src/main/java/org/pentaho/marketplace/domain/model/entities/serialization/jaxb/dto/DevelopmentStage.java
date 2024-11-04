
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The stage of development the version of the market entry is at.
 * <p/>
 * <p/>
 * <p>Java class for development_stage complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="development_stage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="lane" type="{}development_stage_lane"/>
 *         &lt;element name="phase" type="{}development_stage_phase"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "development_stage", propOrder = {} )
public class DevelopmentStage {

  @XmlElement( required = true )
  protected DevelopmentStageLane lane;
  protected long phase;

  /**
   * Gets the value of the lane property.
   *
   * @return possible object is {@link DevelopmentStageLane }
   */
  public DevelopmentStageLane getLane() {
    return lane;
  }

  /**
   * Sets the value of the lane property.
   *
   * @param value allowed object is {@link DevelopmentStageLane }
   */
  public void setLane( DevelopmentStageLane value ) {
    this.lane = value;
  }

  /**
   * Gets the value of the phase property.
   */
  public long getPhase() {
    return phase;
  }

  /**
   * Sets the value of the phase property.
   */
  public void setPhase( long value ) {
    this.phase = value;
  }

}
