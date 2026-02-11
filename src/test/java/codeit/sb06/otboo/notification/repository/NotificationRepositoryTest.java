package codeit.sb06.otboo.notification.repository;

import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.config.QueryDslConfig;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.NotificationLevel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import({JpaAuditingConfig.class, QueryDslConfig.class})
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager em;

    private UUID myUserId;

    @BeforeEach
    void setUp() {

        myUserId = UUID.randomUUID();

        for (int i = 1; i <= 20; i++) {
            Notification notification = Notification.builder()
                    .receiverId(myUserId)
                    .title("알림 제목 " + i)
                    .content("알림 내용 " + i)
                    .level(NotificationLevel.INFO)
                    .build();
            em.persist(notification);
            em.flush();
        }
        em.clear();
    }

    @Test
    @DisplayName("커서 페이지네이션 쿼리 테스트")
    void testCursorPaginationQuery() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Notification> firstPage = notificationRepository.findFirstPageByReceiverId(myUserId, pageable);
        List<Notification> firstPageContents = firstPage.getContent();

        // then
        assertAll(
                () -> assertThat(firstPage).hasSize(10),
                () -> assertThat(firstPageContents.get(0).getContent()).isEqualTo("알림 내용 20"),
                () -> assertThat(firstPageContents.get(9).getContent()).isEqualTo("알림 내용 11")
        );
    }
}
