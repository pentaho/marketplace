package org.pentaho.marketplace.endpoints.dtos.mappers;

import org.pentaho.marketplace.domain.model.entities.interfaces.IDomainStatusMessage;
import org.pentaho.marketplace.domain.model.factories.interfaces.IDomainStatusMessageFactory;
import org.pentaho.marketplace.endpoints.dtos.responses.base.StatusMessageDTO;
import org.pentaho.marketplace.endpoints.dtos.mappers.interfaces.IStatusMessageDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatusMessageDTOMapper implements IStatusMessageDTOMapper {

  //region Attributes
  IDomainStatusMessageFactory statusMessageFactory;
  //endregion

  //region Constructors
  @Autowired
  public StatusMessageDTOMapper( IDomainStatusMessageFactory domainStatusMessageFactory ) {

    //initialize dependencies
    this.statusMessageFactory = domainStatusMessageFactory;
  }
  //endregion

  //region IStatusMessageDTOMapper
  @Override
  public IDomainStatusMessage toEntity( StatusMessageDTO dto ) {

    //get new statusMessage instance
    IDomainStatusMessage statusMessage = this.statusMessageFactory.create();

    //fill the instance
    statusMessage.setCode( dto.code );
    statusMessage.setMessage( dto.message );

    //return the instance
    return statusMessage;
  }

  @Override
  public StatusMessageDTO toDTO( IDomainStatusMessage statusMessage ) {

    //get new dto instance
    StatusMessageDTO dto = new StatusMessageDTO();

    //fill this dto's attributes
    dto.code = statusMessage.getCode();
    dto.message = statusMessage.getMessage();

    //return the dto
    return dto;
  }

  @Override
  public Collection<IDomainStatusMessage> toEntities( List<StatusMessageDTO> dtos ) {

    Collection<IDomainStatusMessage> statusMessages = new ArrayList<IDomainStatusMessage>();

    for ( StatusMessageDTO dto : dtos ) {
      statusMessages.add( this.toEntity( dto ) );
    }

    return statusMessages;
  }

  @Override
  public List<StatusMessageDTO> toDTOs( Collection<IDomainStatusMessage> statusMessages ) {

    List<StatusMessageDTO> statusMessageDTOs = new ArrayList<StatusMessageDTO>();

    for ( IDomainStatusMessage statusMessage : statusMessages ) {
      statusMessageDTOs.add( this.toDTO( statusMessage ) );
    }

    return statusMessageDTOs;
  }
  //endregion
}
