package codeit.sb06.otboo.message.publisher;

import codeit.sb06.otboo.message.dto.DirectMessageCreatedRedisEvent;
import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.util.EasyRandomUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DirectMessagePublisherTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private DirectMessageEventPublisher directMessageEventPublisher;

    @Test
    @DisplayName("DirectMessageCreatedRedisEvent가 발행된다.")
    void publishDirectMessageEventTest() {
        // given
        DirectMessageDto dto = easyRandom.nextObject(DirectMessageDto.class);

        // when
        directMessageEventPublisher.publishDirectMessageCreatedEvent(
                dto, "destination");

        // then
        verify(publisher, org.mockito.Mockito.times(1)).publishEvent(org.mockito.ArgumentMatchers.any(DirectMessageCreatedRedisEvent.class));
    }
}
