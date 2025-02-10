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
package org.pentaho.marketplace.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class MarketplaceJaxbJsonProvider extends JacksonJaxbJsonProvider {
  public MarketplaceJaxbJsonProvider() {
    super( JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS );

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
    setMapper( objectMapper );
  }

}