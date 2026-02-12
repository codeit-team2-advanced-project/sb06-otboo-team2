package codeit.sb06.otboo.notification.repository;

import codeit.sb06.otboo.notification.dto.SseEvent;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
@Deprecated(since = "인메모리 캐시에서 Redis로 변경")
public class SseEventCacheRepository {

    public static final int CACHE_SIZE = 50;
    private final Map<UUID, Deque<SseEvent>> eventCache = new ConcurrentHashMap<>();

    public Deque<SseEvent> findById(UUID userId) {
        return eventCache.get(userId);
    }

    public void save(UUID userId, SseEvent event) {
        eventCache.compute(userId, (key, deque) -> {
            if (deque == null) {
                deque = new ConcurrentLinkedDeque<>();
            }
            deque.addLast(event);
            if (deque.size() > CACHE_SIZE) {
                deque.pollFirst();
            }
            return deque;
        });
    }

    public List<SseEvent> findAllAfterEventId(UUID userId, String lastEventId) {

        Deque<SseEvent> sseEvents = eventCache.get(userId);
        if (sseEvents == null) {
            return Collections.emptyList();
        }

        // id > lastEventId를 만족하는 이벤트 가져옴
        // id에 시간 정보가 포함되어 있어 문자열 비교로도 시간 순서 비교 가능
        return sseEvents.stream()
                .filter(event -> event.id().compareTo(lastEventId) > 0)
                .toList();
    }
}
