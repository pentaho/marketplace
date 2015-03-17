
package org.pentaho.marketplace.domain.model.entities.serialization.jaxb.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="market_entry" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="type" type="{}market_entry_type"/>
 *                   &lt;element name="category" type="{}category" minOccurs="0"/>
 *                   &lt;element name="img" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="small_img" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="documentation_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
 *                   &lt;element name="author_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="author_logo" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="installation_notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="dependencies" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="license" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="license_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="license_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="support_level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="support_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="support_organization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="support_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="versions" type="{}versionList" minOccurs="0"/>
 *                   &lt;element name="forum_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="cases_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="screenshots" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="screenshot" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "marketEntry"
})
@XmlRootElement(name = "market")
public class Market {

    @XmlElement(name = "market_entry", required = true)
    protected List<Market.MarketEntry> marketEntry;

    /**
     * Gets the value of the marketEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the marketEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMarketEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Market.MarketEntry }
     * 
     * 
     */
    public List<Market.MarketEntry> getMarketEntry() {
        if (marketEntry == null) {
            marketEntry = new ArrayList<Market.MarketEntry>();
        }
        return this.marketEntry;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="type" type="{}market_entry_type"/>
     *         &lt;element name="category" type="{}category" minOccurs="0"/>
     *         &lt;element name="img" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="small_img" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="documentation_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/>
     *         &lt;element name="author_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="author_logo" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="installation_notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="dependencies" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="license" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="license_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="license_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="support_level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="support_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="support_organization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="support_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="versions" type="{}versionList" minOccurs="0"/>
     *         &lt;element name="forum_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="cases_url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="screenshots" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="screenshot" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class MarketEntry {

        @XmlElement(required = true)
        @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
        @XmlSchemaType(name = "normalizedString")
        protected String id;
        @XmlElement(required = true)
        protected String name;
        @XmlElement(required = true)
        protected MarketEntryType type;
        protected Category category;
        @XmlSchemaType(name = "anyURI")
        protected String img;
        @XmlElement(name = "small_img")
        @XmlSchemaType(name = "anyURI")
        protected String smallImg;
        @XmlElement(name = "documentation_url")
        @XmlSchemaType(name = "anyURI")
        protected String documentationUrl;
        protected String description;
        @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
        @XmlSchemaType(name = "normalizedString")
        protected String author;
        @XmlElement(name = "author_url")
        @XmlSchemaType(name = "anyURI")
        protected String authorUrl;
        @XmlElement(name = "author_logo")
        @XmlSchemaType(name = "anyURI")
        protected String authorLogo;
        @XmlElement(name = "installation_notes")
        protected String installationNotes;
        protected String dependencies;
        protected String license;
        @XmlElement(name = "license_name")
        protected String licenseName;
        @XmlElement(name = "license_text")
        protected String licenseText;
        @XmlElement(name = "support_level")
        protected String supportLevel;
        @XmlElement(name = "support_message")
        protected String supportMessage;
        @XmlElement(name = "support_organization")
        protected String supportOrganization;
        @XmlElement(name = "support_url")
        @XmlSchemaType(name = "anyURI")
        protected String supportUrl;
        protected VersionList versions;
        @XmlElement(name = "forum_url")
        @XmlSchemaType(name = "anyURI")
        protected String forumUrl;
        @XmlElement(name = "cases_url")
        @XmlSchemaType(name = "anyURI")
        protected String casesUrl;
        protected Market.MarketEntry.Screenshots screenshots;

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link MarketEntryType }
         *     
         */
        public MarketEntryType getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link MarketEntryType }
         *     
         */
        public void setType(MarketEntryType value) {
            this.type = value;
        }

        /**
         * Gets the value of the category property.
         * 
         * @return
         *     possible object is
         *     {@link Category }
         *     
         */
        public Category getCategory() {
            return category;
        }

        /**
         * Sets the value of the category property.
         * 
         * @param value
         *     allowed object is
         *     {@link Category }
         *     
         */
        public void setCategory(Category value) {
            this.category = value;
        }

        /**
         * Gets the value of the img property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImg() {
            return img;
        }

        /**
         * Sets the value of the img property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImg(String value) {
            this.img = value;
        }

        /**
         * Gets the value of the smallImg property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSmallImg() {
            return smallImg;
        }

        /**
         * Sets the value of the smallImg property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSmallImg(String value) {
            this.smallImg = value;
        }

        /**
         * Gets the value of the documentationUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDocumentationUrl() {
            return documentationUrl;
        }

        /**
         * Sets the value of the documentationUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDocumentationUrl(String value) {
            this.documentationUrl = value;
        }

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the author property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAuthor() {
            return author;
        }

        /**
         * Sets the value of the author property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAuthor(String value) {
            this.author = value;
        }

        /**
         * Gets the value of the authorUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAuthorUrl() {
            return authorUrl;
        }

        /**
         * Sets the value of the authorUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAuthorUrl(String value) {
            this.authorUrl = value;
        }

        /**
         * Gets the value of the authorLogo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAuthorLogo() {
            return authorLogo;
        }

        /**
         * Sets the value of the authorLogo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAuthorLogo(String value) {
            this.authorLogo = value;
        }

        /**
         * Gets the value of the installationNotes property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstallationNotes() {
            return installationNotes;
        }

        /**
         * Sets the value of the installationNotes property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstallationNotes(String value) {
            this.installationNotes = value;
        }

        /**
         * Gets the value of the dependencies property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDependencies() {
            return dependencies;
        }

        /**
         * Sets the value of the dependencies property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDependencies(String value) {
            this.dependencies = value;
        }

        /**
         * Gets the value of the license property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLicense() {
            return license;
        }

        /**
         * Sets the value of the license property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLicense(String value) {
            this.license = value;
        }

        /**
         * Gets the value of the licenseName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLicenseName() {
            return licenseName;
        }

        /**
         * Sets the value of the licenseName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLicenseName(String value) {
            this.licenseName = value;
        }

        /**
         * Gets the value of the licenseText property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLicenseText() {
            return licenseText;
        }

        /**
         * Sets the value of the licenseText property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLicenseText(String value) {
            this.licenseText = value;
        }

        /**
         * Gets the value of the supportLevel property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupportLevel() {
            return supportLevel;
        }

        /**
         * Sets the value of the supportLevel property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupportLevel(String value) {
            this.supportLevel = value;
        }

        /**
         * Gets the value of the supportMessage property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupportMessage() {
            return supportMessage;
        }

        /**
         * Sets the value of the supportMessage property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupportMessage(String value) {
            this.supportMessage = value;
        }

        /**
         * Gets the value of the supportOrganization property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupportOrganization() {
            return supportOrganization;
        }

        /**
         * Sets the value of the supportOrganization property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupportOrganization(String value) {
            this.supportOrganization = value;
        }

        /**
         * Gets the value of the supportUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupportUrl() {
            return supportUrl;
        }

        /**
         * Sets the value of the supportUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupportUrl(String value) {
            this.supportUrl = value;
        }

        /**
         * Gets the value of the versions property.
         * 
         * @return
         *     possible object is
         *     {@link VersionList }
         *     
         */
        public VersionList getVersions() {
            return versions;
        }

        /**
         * Sets the value of the versions property.
         * 
         * @param value
         *     allowed object is
         *     {@link VersionList }
         *     
         */
        public void setVersions(VersionList value) {
            this.versions = value;
        }

        /**
         * Gets the value of the forumUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getForumUrl() {
            return forumUrl;
        }

        /**
         * Sets the value of the forumUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setForumUrl(String value) {
            this.forumUrl = value;
        }

        /**
         * Gets the value of the casesUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCasesUrl() {
            return casesUrl;
        }

        /**
         * Sets the value of the casesUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCasesUrl(String value) {
            this.casesUrl = value;
        }

        /**
         * Gets the value of the screenshots property.
         * 
         * @return
         *     possible object is
         *     {@link Market.MarketEntry.Screenshots }
         *     
         */
        public Market.MarketEntry.Screenshots getScreenshots() {
            return screenshots;
        }

        /**
         * Sets the value of the screenshots property.
         * 
         * @param value
         *     allowed object is
         *     {@link Market.MarketEntry.Screenshots }
         *     
         */
        public void setScreenshots(Market.MarketEntry.Screenshots value) {
            this.screenshots = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="screenshot" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "screenshot"
        })
        public static class Screenshots {

            @XmlSchemaType(name = "anyURI")
            protected List<String> screenshot;

            /**
             * Gets the value of the screenshot property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the screenshot property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getScreenshot().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getScreenshot() {
                if (screenshot == null) {
                    screenshot = new ArrayList<String>();
                }
                return this.screenshot;
            }

        }

    }

}
