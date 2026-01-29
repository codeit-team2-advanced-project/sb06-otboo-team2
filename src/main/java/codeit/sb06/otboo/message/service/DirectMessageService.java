package codeit.sb06.otboo.message.service;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;

public interface DirectMessageService {

    DirectMessageDto create(DirectMessageCreateRequest request);
}
