package org.pentaho.marketplace.endpoints.dtos.responses;

import org.pentaho.marketplace.endpoints.dtos.entities.PluginDTO;
import org.pentaho.marketplace.endpoints.dtos.responses.base.OperationResultDTO;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class IterablePluginOperationResultDTO extends OperationResultDTO {

  //region Attributes
  public List<PluginDTO> plugins;
  //endregion
}
