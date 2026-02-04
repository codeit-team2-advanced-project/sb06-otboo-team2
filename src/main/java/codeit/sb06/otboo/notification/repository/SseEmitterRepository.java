package codeit.sb06.otboo.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    // thread-safe
    private final Map<UUID, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(UUID userId, SseEmitter emitter) {
        emitterMap.put(userId, emitter);
        return emitter;
    }

    public SseEmitter findById(UUID userId) {
        return emitterMap.get(userId);
    }

    public void deleteById(UUID userId) {
        emitterMap.remove(userId);
    }
}
