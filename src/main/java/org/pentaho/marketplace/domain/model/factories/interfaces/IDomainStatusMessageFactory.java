package org.pentaho.marketplace.domain.model.factories.interfaces;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;

public interface IDomainStatusMessageFactory extends IParameterlessConstructorFactory<IDomainStatusMessage> {

  IDomainStatusMessage create( String code, String message );
}
