
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

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * mypackage package. <p>An ObjectFactory allows you to programatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of schema derived interfaces and
 * classes representing the binding of schema type definitions, element declarations and model groups.  Factory methods
 * for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
   * mypackage
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link Market }
   */
  public Market createMarket() {
    return new Market();
  }

  /**
   * Create an instance of {@link Market.MarketEntry }
   */
  public Market.MarketEntry createMarketMarketEntry() {
    return new Market.MarketEntry();
  }

  /**
   * Create an instance of {@link Category }
   */
  public Category createCategory() {
    return new Category();
  }

  /**
   * Create an instance of {@link DevelopmentStage }
   */
  public DevelopmentStage createDevelopmentStage() {
    return new DevelopmentStage();
  }

  /**
   * Create an instance of {@link VersionList }
   */
  public VersionList createVersionList() {
    return new VersionList();
  }

  /**
   * Create an instance of {@link Version }
   */
  public Version createVersion() {
    return new Version();
  }

  /**
   * Create an instance of {@link Market.MarketEntry.Screenshots }
   */
  public Market.MarketEntry.Screenshots createMarketMarketEntryScreenshots() {
    return new Market.MarketEntry.Screenshots();
  }

}
