package org.pentaho.marketplace.endpoints.dtos;

import org.pentaho.marketplace.endpoints.dtos.entities.PluginDTO;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IterablePluginOperationResultDTO extends OperationResultDTO {

  public Iterable<PluginDTO> resultDTO;
}
