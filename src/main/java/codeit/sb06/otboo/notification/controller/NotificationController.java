package codeit.sb06.otboo.notification.controller;

import codeit.sb06.otboo.notification.dto.response.NotificationDtoCursorResponse;
import codeit.sb06.otboo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/notifications")
    public ResponseEntity<NotificationDtoCursorResponse> getNotifications(
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam int limit,
            @AuthenticationPrincipal(expression = "userDto.id()") UUID myUserId
            ) {
        NotificationDtoCursorResponse response = notificationService.getNotifications(cursor, idAfter, limit, myUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(
            @PathVariable UUID notificationId
    ) {
        notificationService.deleteById(notificationId);
    }
}
