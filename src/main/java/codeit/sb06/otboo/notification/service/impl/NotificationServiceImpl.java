package codeit.sb06.otboo.notification.service.impl;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.NotificationLevel;
import codeit.sb06.otboo.notification.mapper.NotificationMapper;
import codeit.sb06.otboo.notification.repository.NotificationRepository;
import codeit.sb06.otboo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationDto create(UUID receiverId, String title, String content, NotificationLevel level) {

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .level(level)
                .build();

        Notification saved = notificationRepository.save(notification);

        log.debug("알림 생성: {}", notification);

        return notificationMapper.toDto(saved);
    }

    @Override
    public void deleteById(UUID notificationId) {
        log.debug("알림 삭제: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }
}
