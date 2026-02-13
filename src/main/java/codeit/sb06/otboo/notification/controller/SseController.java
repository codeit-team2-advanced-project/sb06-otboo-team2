package codeit.sb06.otboo.notification.controller;

import codeit.sb06.otboo.notification.service.SseService;
import codeit.sb06.otboo.security.resolver.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @CurrentUserId UUID userId,
            @RequestParam(required = false) String lastEventId
    ) {
        return sseService.subscribe(userId, lastEventId);
    }
}
