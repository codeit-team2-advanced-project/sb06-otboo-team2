package codeit.sb06.otboo.notification.service.impl;

import codeit.sb06.otboo.notification.dto.SseEvent;
import codeit.sb06.otboo.notification.repository.SseEmitterRepository;
import codeit.sb06.otboo.notification.repository.SseEventCacheRepository;
import codeit.sb06.otboo.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseServiceImpl implements SseService {

    public static final long TIMEOUT = 60 * 1000L * 60; // 1 hour
    private final SseEmitterRepository sseEmitterRepository;
    private final SseEventCacheRepository sseEventCacheRepository;

    @Override
    public SseEmitter subscribe(UUID userId, String lastEventId) {

        SseEmitter oldEmitter = sseEmitterRepository.findById(userId);
        if (oldEmitter != null) {
            oldEmitter.complete();
            sseEmitterRepository.deleteById(userId);
        }

        SseEmitter emitter = new SseEmitter(TIMEOUT);

        configEmitter(emitter, userId);

        String eventId = makeTimeIncludeId(userId);
        sendToClient(
                emitter,
                userId,
                SseEvent.of(eventId, "subscribe", "EventStream Created. [userId=" + userId + "]")
        );

        sseEmitterRepository.save(userId, emitter);

        if (lastEventId != null && !lastEventId.isEmpty()) {
            sseEventCacheRepository.findAllAfterEventId(userId, lastEventId)
                    .forEach(event ->
                            sendToClient(emitter, userId, SseEvent.of(event.eventId(), event.eventName(), event.data()))
                    );
        }

        return emitter;
    }

    @Override
    public void send(UUID userId, String eventName, Object data) {

        String eventId = makeTimeIncludeId(userId);
        SseEvent event = SseEvent.of(eventId, eventName, data);
        sseEventCacheRepository.save(userId, event);

        SseEmitter emitter = sseEmitterRepository.findById(userId);
        if (emitter != null) {
            sendToClient(emitter, userId, event);
        }
    }

    private void sendToClient(SseEmitter emitter, UUID userId, SseEvent event) {
        try {
            emitter.send(SseEmitter.event()
                    .id(event.eventId())
                    .name(event.eventName())
                    .data(event.data()));
        } catch (IOException e) {
            sseEmitterRepository.deleteById(userId);
        }
    }

    private String makeTimeIncludeId(UUID userId) {
        return System.currentTimeMillis() + "_" + userId.toString();
    }

    private void configEmitter(SseEmitter emitter, UUID userId) {

        emitter.onCompletion(() -> sseEmitterRepository.deleteById(userId));
        emitter.onTimeout(() -> {
            sseEmitterRepository.deleteById(userId);
            log.error("SSE Emitter Timeout. [userId={}]", userId);
        });
        emitter.onError(e -> {
            emitter.complete();
            log.error("SSE Emitter Error. [userId={}]", userId, e);
        });
    }
}
