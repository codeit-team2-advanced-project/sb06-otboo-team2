package codeit.sb06.otboo.message.publisher;

import codeit.sb06.otboo.message.dto.DirectMessageCreatedRedisEvent;
import codeit.sb06.otboo.message.dto.DirectMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectMessageEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishDirectMessageCreatedEvent(DirectMessageDto dto, String destination) {

        eventPublisher.publishEvent(new DirectMessageCreatedRedisEvent(dto, destination));
    }
}
