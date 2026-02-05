package codeit.sb06.otboo.notification.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SseEmitterRepositoryTest {

    private SseEmitterRepository sseEmitterRepository;

    private UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sseEmitterRepository = new SseEmitterRepository();
    }

    @Test
    @DisplayName("SseEmitter를 저장하고 조회할 수 있다.")
    void saveSseEmitterTest() {
        // given
        SseEmitter emitter = new SseEmitter();

        // when
        sseEmitterRepository.save(userId, emitter);

        // then
        assertThat(sseEmitterRepository.findById(userId)).isEqualTo(emitter);
    }

    @Test
    @DisplayName("SseEmitter를 삭제할 수 있다.")
    void deleteSseEmitterTest() {
        // given
        SseEmitter emitter = new SseEmitter();
        sseEmitterRepository.save(userId, emitter);

        // when
        sseEmitterRepository.deleteById(userId);

        // then
        assertThat(sseEmitterRepository.findById(userId)).isNull();
    }
}
