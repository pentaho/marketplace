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

package org.pentaho.marketplace.domain.model.entities.serialization;

import org.pentaho.marketplace.domain.model.entities.serialization.jaxb.JAXBMarketplaceXmlSerializer;
import org.pentaho.marketplace.domain.model.factories.interfaces.ICategoryFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IPluginVersionFactory;
import org.pentaho.marketplace.domain.model.factories.interfaces.IVersionDataFactory;


public class JAXBMarketplaceXmlSerializerTest extends MarketplaceXmlSerializerTest<JAXBMarketplaceXmlSerializer> {
  @Override
  protected JAXBMarketplaceXmlSerializer create(IPluginFactory pluginFactory, IPluginVersionFactory pluginVersionFactory, IVersionDataFactory versionDataFactory, ICategoryFactory categoryFactory) {
    return new JAXBMarketplaceXmlSerializer( pluginFactory, pluginVersionFactory, categoryFactory );
  }
}
