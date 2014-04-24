package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.endpoints.dtos.entities.StatusMessageDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IStatusMessageDTOMapper;
import org.pentaho.marketplace.domain.model.entities.interfaces.IStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IStatusMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Collection;

public class StatusMessageDTOMapper implements IStatusMessageDTOMapper {

  //region Attributes
  IStatusMessageFactory statusMessageFactory;
  //endregion

  //region Constructors
  @Autowired
  public StatusMessageDTOMapper( IStatusMessageFactory statusMessageFactory ) {

    //initialize dependencies
    this.statusMessageFactory = statusMessageFactory;
  }
  //endregion

  //region IStatusMessageDTOMapper
  @Override
  public IStatusMessage toEntity( StatusMessageDTO dto ) {

    //get new statusMessage instance
    IStatusMessage statusMessage = this.statusMessageFactory.create();

    //fill the instance
    statusMessage.setCode( dto.code );
    statusMessage.setMessage( dto.message );

    //return the instance
    return statusMessage;
  }

  @Override
  public StatusMessageDTO toDTO( IStatusMessage statusMessage ) {

    //get new dto instance
    StatusMessageDTO dto = new StatusMessageDTO();

    //fill this dto's attributes
    dto.code = statusMessage.getCode();
    dto.message = statusMessage.getMessage();

    //return the dto
    return dto;
  }

  @Override
  public Collection<IStatusMessage> toEntities( Collection<StatusMessageDTO> dtos ) {

    Collection<IStatusMessage> statusMessages = new ArrayList<IStatusMessage>();

    for ( StatusMessageDTO dto : dtos ) {
      statusMessages.add( this.toEntity( dto ) );
    }

    return statusMessages;
  }

  @Override
  public Collection<StatusMessageDTO> toDTOs( Collection<IStatusMessage> statusMessages ) {

    Collection<StatusMessageDTO> statusMessageDTOs = new ArrayList<StatusMessageDTO>();

    for ( IStatusMessage statusMessage : statusMessages ) {
      statusMessageDTOs.add( this.toDTO( statusMessage ) );
    }

    return statusMessageDTOs;
  }
  //endregion
}
