package org.pentaho.marketplace.endpoints.dtos;

import org.pentaho.marketplace.endpoints.dtos.entities.PluginDTO;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class IterablePluginOperationResultDTO extends OperationResultDTO {

  public List<PluginDTO> resultDTO;
}
