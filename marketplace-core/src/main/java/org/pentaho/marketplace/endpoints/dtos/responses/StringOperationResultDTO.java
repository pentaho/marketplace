package org.pentaho.marketplace.endpoints.dtos.responses;

import org.pentaho.marketplace.endpoints.dtos.responses.base.OperationResultDTO;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StringOperationResultDTO extends OperationResultDTO {

  //region Attributes
  public String string;
  //endregion
}
