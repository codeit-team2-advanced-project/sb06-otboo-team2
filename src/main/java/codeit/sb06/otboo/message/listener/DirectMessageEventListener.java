package codeit.sb06.otboo.message.listener;

import codeit.sb06.otboo.message.dto.DirectMessageCreatedRedisEvent;
import codeit.sb06.otboo.message.publisher.DirectMessageRedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DirectMessageEventListener {

    private final DirectMessageRedisPublisher directMessageRedisPublisher;

    @TransactionalEventListener
    public void handleDirectMessageEvent(DirectMessageCreatedRedisEvent event) {

        directMessageRedisPublisher.publish(event.message());
    }
}
