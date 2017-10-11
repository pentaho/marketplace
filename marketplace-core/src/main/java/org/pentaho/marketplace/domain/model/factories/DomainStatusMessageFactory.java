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
 * Copyright (c) 2015 - 2017 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.marketplace.domain.model.factories;

import org.pentaho.marketplace.domain.model.entities.DomainStatusMessage;
import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;

public class DomainStatusMessageFactory implements IDomainStatusMessageFactory {

  //region IDomainStatusMessageFactory implementation
  @Override public IDomainStatusMessage create() {
    return new DomainStatusMessage();
  }

  @Override public IDomainStatusMessage create( String code, String message ) {

    IDomainStatusMessage domainStatusMessage = new DomainStatusMessage();
    domainStatusMessage.setCode( code );
    domainStatusMessage.setMessage( message );
    return domainStatusMessage;
  }
  //endregion
}
