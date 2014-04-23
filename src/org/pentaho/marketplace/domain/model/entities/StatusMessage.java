package org.pentaho.marketplace.domain.model.entities;

import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;

public class StatusMessage implements IStatusMessage {

  //region Attributes
  private String code;
  private String message;
  //endregion

  //region IStatusMessage implementation
  @Override
  public String getCode() {
    return this.code;
  }

  @Override
  public void setCode( String value ) {
    this.code = value;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public void setMessage( String value ) {
    this.message = value;
  }
  //endregion
}
