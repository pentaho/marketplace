package org.pentaho.marketplace.endpoints.dtos;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StringOperationResultDTO extends OperationResultDTO {

  public String result;
}
