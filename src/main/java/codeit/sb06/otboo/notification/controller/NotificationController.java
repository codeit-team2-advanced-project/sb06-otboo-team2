package codeit.sb06.otboo.notification.controller;

import codeit.sb06.otboo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(
            @PathVariable UUID notificationId
    ) {
        notificationService.deleteById(notificationId);
    }
}
