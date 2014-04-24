package org.pentaho.marketplace.endpoints.dtos;

import org.pentaho.marketplace.endpoints.dtos.entities.StatusMessageDTO;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericOperationResultDTO<TResultDTO> {

  public StatusMessageDTO statusMessageDTO;
  public TResultDTO resultDTO;
}
