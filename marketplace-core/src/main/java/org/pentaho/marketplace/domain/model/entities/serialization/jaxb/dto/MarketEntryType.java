
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
 * <p>Java class for market_entry_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
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
 * 
 */
@XmlType(name = "market_entry_type")
@XmlEnum
public enum MarketEntryType {


    /**
     * 
     *             This market entry is a bi platform plugin, such as a content generator plugin.
     *           
     * 
     */
    @XmlEnumValue("Platform")
    PLATFORM("Platform"),

    /**
     * 
     *             This market entry is a database connection plugin for pentaho data integration.
     *           
     * 
     */
    @XmlEnumValue("Database")
    DATABASE("Database"),

    /**
     * 
     *             This market entry is a transformation step plugin for pentaho data integration. 
     *           
     * 
     */
    @XmlEnumValue("Step")
    STEP("Step"),

    /**
     * 
     *             The market entry is a job entry plugin for pentaho data integration.
     *           
     * 
     */
    @XmlEnumValue("JobEntry")
    JOB_ENTRY("JobEntry"),

    /**
     * 
     *             Mixed type. If the market entry could comprise a number of plugins. 
     *           
     * 
     */
    @XmlEnumValue("Mixed")
    MIXED("Mixed"),

    /**
     * 
     *             This market entry is a row partitioner plugin for pentaho data integration.
     *           
     * 
     */
    @XmlEnumValue("Partitioner")
    PARTITIONER("Partitioner"),

    /**
     * 
     *             This market entry is a big data shim - an abstraction of a typical hadoop distro - for use in the pentaho big data plugin. 
     *           
     * 
     */
    @XmlEnumValue("HadoopShim")
    HADOOP_SHIM("HadoopShim"),

    /**
     * 
     *             This market entry is a Spoon plugin, possibly a perspective or something that adds to the PDI UI. 
     *           
     * 
     */
    @XmlEnumValue("SpoonPlugin")
    SPOON_PLUGIN("SpoonPlugin");
    private final String value;

    MarketEntryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MarketEntryType fromValue(String v) {
        for (MarketEntryType c: MarketEntryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
