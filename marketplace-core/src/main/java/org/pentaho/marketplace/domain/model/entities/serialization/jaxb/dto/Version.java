
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A descriptor of a particular version of a plugin.
 * <p/>
 * <p/>
 * <p>Java class for version complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="version">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="branch" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *         &lt;element name="build_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="development_stage" type="{}development_stage" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *         &lt;element name="package_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="samples_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="source_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="min_parent_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="max_parent_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="changelog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "version", propOrder = { } )
public class Version {

  @XmlElement( required = true )
  @XmlJavaTypeAdapter( NormalizedStringAdapter.class )
  @XmlSchemaType( name = "normalizedString" )
  protected String branch;
  @XmlElement( required = true )
  @XmlJavaTypeAdapter( NormalizedStringAdapter.class )
  @XmlSchemaType( name = "normalizedString" )
  protected String version;
  @XmlElement( name = "build_id" )
  protected String buildId;
  @XmlElement( name = "development_stage" )
  protected DevelopmentStage developmentStage;
  @XmlJavaTypeAdapter( NormalizedStringAdapter.class )
  @XmlSchemaType( name = "normalizedString" )
  protected String name;
  @XmlElement( name = "package_url" )
  @XmlSchemaType( name = "anyURI" )
  protected String packageUrl;
  @XmlElement( name = "samples_url" )
  @XmlSchemaType( name = "anyURI" )
  protected String samplesUrl;
  @XmlElement( name = "source_url" )
  @XmlSchemaType( name = "anyURI" )
  protected String sourceUrl;
  protected String description;
  @XmlElement( name = "min_parent_version" )
  protected String minParentVersion;
  @XmlElement( name = "max_parent_version" )
  protected String maxParentVersion;
  protected String changelog;

  /**
   * Gets the value of the branch property.
   *
   * @return possible object is {@link String }
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Sets the value of the branch property.
   *
   * @param value allowed object is {@link String }
   */
  public void setBranch( String value ) {
    this.branch = value;
  }

  /**
   * Gets the value of the version property.
   *
   * @return possible object is {@link String }
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the value of the version property.
   *
   * @param value allowed object is {@link String }
   */
  public void setVersion( String value ) {
    this.version = value;
  }

  /**
   * Gets the value of the buildId property.
   *
   * @return possible object is {@link String }
   */
  public String getBuildId() {
    return buildId;
  }

  /**
   * Sets the value of the buildId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setBuildId( String value ) {
    this.buildId = value;
  }

  /**
   * Gets the value of the developmentStage property.
   *
   * @return possible object is {@link DevelopmentStage }
   */
  public DevelopmentStage getDevelopmentStage() {
    return developmentStage;
  }

  /**
   * Sets the value of the developmentStage property.
   *
   * @param value allowed object is {@link DevelopmentStage }
   */
  public void setDevelopmentStage( DevelopmentStage value ) {
    this.developmentStage = value;
  }

  /**
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value allowed object is {@link String }
   */
  public void setName( String value ) {
    this.name = value;
  }

  /**
   * Gets the value of the packageUrl property.
   *
   * @return possible object is {@link String }
   */
  public String getPackageUrl() {
    return packageUrl;
  }

  /**
   * Sets the value of the packageUrl property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPackageUrl( String value ) {
    this.packageUrl = value;
  }

  /**
   * Gets the value of the samplesUrl property.
   *
   * @return possible object is {@link String }
   */
  public String getSamplesUrl() {
    return samplesUrl;
  }

  /**
   * Sets the value of the samplesUrl property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSamplesUrl( String value ) {
    this.samplesUrl = value;
  }

  /**
   * Gets the value of the sourceUrl property.
   *
   * @return possible object is {@link String }
   */
  public String getSourceUrl() {
    return sourceUrl;
  }

  /**
   * Sets the value of the sourceUrl property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSourceUrl( String value ) {
    this.sourceUrl = value;
  }

  /**
   * Gets the value of the description property.
   *
   * @return possible object is {@link String }
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of the description property.
   *
   * @param value allowed object is {@link String }
   */
  public void setDescription( String value ) {
    this.description = value;
  }

  /**
   * Gets the value of the minParentVersion property.
   *
   * @return possible object is {@link String }
   */
  public String getMinParentVersion() {
    return minParentVersion;
  }

  /**
   * Sets the value of the minParentVersion property.
   *
   * @param value allowed object is {@link String }
   */
  public void setMinParentVersion( String value ) {
    this.minParentVersion = value;
  }

  /**
   * Gets the value of the maxParentVersion property.
   *
   * @return possible object is {@link String }
   */
  public String getMaxParentVersion() {
    return maxParentVersion;
  }

  /**
   * Sets the value of the maxParentVersion property.
   *
   * @param value allowed object is {@link String }
   */
  public void setMaxParentVersion( String value ) {
    this.maxParentVersion = value;
  }

  /**
   * Gets the value of the changelog property.
   *
   * @return possible object is {@link String }
   */
  public String getChangelog() {
    return changelog;
  }

  /**
   * Sets the value of the changelog property.
   *
   * @param value allowed object is {@link String }
   */
  public void setChangelog( String value ) {
    this.changelog = value;
  }

}
