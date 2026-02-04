package codeit.sb06.otboo.notification.repository;

import codeit.sb06.otboo.notification.dto.SseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SseEventCacheRepositoryTest {

    public static final int CACHE_SIZE = 50;
    private final UUID userId = UUID.randomUUID();
    private SseEventCacheRepository sseEventCacheRepository;

    @BeforeEach
    void setUp() {
        sseEventCacheRepository = new SseEventCacheRepository();

        for (int i = 1; i <= 60; i++) {
            String eventId = System.currentTimeMillis() + "_" + userId;
            SseEvent event = SseEvent.of(eventId, "event-" + i, "data-" + i);
            sseEventCacheRepository.save(userId, event);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @DisplayName("이벤트 캐시 저장 및 조회 테스트")
    void testSaveAndFindById() {
        // given
        // setUp()

        // when
        var cachedEvents = sseEventCacheRepository.findById(userId);

        // then
        assertThat(cachedEvents).hasSize(CACHE_SIZE);
    }

    @Test
    @DisplayName("특정 이벤트 ID 이후의 이벤트 조회 테스트")
    void testFindAllAfterEventId() {
        // given
        var cachedEvents = sseEventCacheRepository.findById(userId);
        String lastEventId = cachedEvents.stream()
                .skip(40)
                .findFirst()
                .orElseThrow()
                .id();

        // when
        var eventsAfterLastId = sseEventCacheRepository.findAllAfterEventId(userId, lastEventId);

        // then
        assertAll(
                () -> assertThat(eventsAfterLastId).hasSize(9),
                () -> assertThat(eventsAfterLastId.get(0).id()).isGreaterThan(lastEventId)
        );

    }
}
