package codeit.sb06.otboo.notification.service;

import codeit.sb06.otboo.notification.repository.SseEmitterRepository;
import codeit.sb06.otboo.notification.repository.SseEventCacheRepository;
import codeit.sb06.otboo.notification.service.impl.SseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    private final UUID userId = UUID.randomUUID();
    @Mock
    private SseEmitterRepository sseEmitterRepository;
    @Mock
    private SseEventCacheRepository sseEventCacheRepository;
    @InjectMocks
    private SseServiceImpl sseService;

    @Test
    @DisplayName("sseEmitter를 저장하고 반환한다.")
    void subscribeTest() {
        // given
        given(sseEmitterRepository.save(eq(userId), any(SseEmitter.class)))
                .willAnswer(invocation -> invocation.getArgument(1));

        // when
        SseEmitter emitter = sseService.subscribe(userId, "");

        // then
        assertThat(emitter).isNotNull();
    }

    @Test
    @DisplayName("sse 이벤트 전송을 테스트한다.")
    void sendTest() {
        // given
        SseEmitter mockEmitter = mock(SseEmitter.class);
        given(sseEmitterRepository.findById(userId)).willReturn(mockEmitter);

        // when
        sseService.send(userId, "testEvent", "testData");

        // then
        try {
            verify(mockEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
