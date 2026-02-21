package codeit.sb06.otboo.message.publisher;

import codeit.sb06.otboo.message.dto.DirectMessageCreatedRedisEvent;
import codeit.sb06.otboo.message.dto.DirectMessageRedisDto;
import codeit.sb06.otboo.message.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DirectMessageEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishDirectMessageCreatedEvent(UUID senderId, UUID receiverId, String content) {

        String dmKey = ChatRoom.generateDmKey(senderId, receiverId);
        String destination = "/sub/direct-messages_" + dmKey;

        DirectMessageRedisDto dto = DirectMessageRedisDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .destination(destination)
                .build();

        eventPublisher.publishEvent(new DirectMessageCreatedRedisEvent(dto));
    }
}
