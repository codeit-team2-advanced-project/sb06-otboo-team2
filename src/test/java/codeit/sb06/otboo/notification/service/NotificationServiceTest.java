package codeit.sb06.otboo.notification.service;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.NotificationLevel;
import codeit.sb06.otboo.notification.mapper.NotificationMapper;
import codeit.sb06.otboo.notification.repository.NotificationRepository;
import codeit.sb06.otboo.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("알림 생성 성공 테스트")
    void createNotificationTest() {
        // given
        UUID receiverId = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        NotificationLevel level = NotificationLevel.INFO;

        given(notificationRepository.save(any(Notification.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        // when
        NotificationDto notificationDto = notificationService.create(receiverId, title, content, level);

        // then
        assertThat(notificationDto).isNotNull();
    }

    @Test
    @DisplayName("알림 삭제 성공 테스트")
    void deleteNotificationTest() {
        // given
        UUID notificationId = UUID.randomUUID();

        // when
        notificationService.deleteById(notificationId);

        // then
        verify(notificationRepository).deleteById(notificationId);
    }
}
