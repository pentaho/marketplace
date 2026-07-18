/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.marketplace.domain.model.factories.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;

public interface IDomainStatusMessageFactory extends IParameterlessConstructorFactory<IDomainStatusMessage> {

  IDomainStatusMessage create( String code, String message );
}
