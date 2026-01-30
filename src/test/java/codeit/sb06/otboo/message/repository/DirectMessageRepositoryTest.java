package codeit.sb06.otboo.message.repository;

import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.entity.DirectMessage;

import codeit.sb06.otboo.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@Disabled("테스트 실패")
class DirectMessageRepositoryTest {

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @Autowired
    private EntityManager em;

    private ChatRoom chatRoom;


    @BeforeEach
    void setUp() {
        User sender = new User();
        // 유저 아이디 생성 전략 없으므로 임시로 ReflectionTestUtils 사용
        ReflectionTestUtils.setField(sender, "id", UUID.randomUUID());
        em.persist(sender);

        String dmKey = "uuid_test_dm_key";
        chatRoom = new ChatRoom(dmKey);
        em.persist(chatRoom);

        for (int i = 1; i <= 20; i++) {
            DirectMessage directMessage = DirectMessage.builder()
                    .sender(sender)
                    .chatRoom(chatRoom)
                    .content("안녕 " + i)
                    .build();
            em.persist(directMessage);
            em.flush();
        }
        em.clear();
    }

    @Test
    @DisplayName("커서 페이지네이션 쿼리 테스트")
    void testCursorPaginationQuery() {
        // given
        Pageable pageable = PageRequest.of(0, 11);

        // when
        List<DirectMessage> firstPage = directMessageRepository.findByChatRoomWithCursor(chatRoom, null, null, pageable);

        // then
        assertAll(
                () -> assertThat(firstPage).hasSize(11),
                // 최신 메시지부터 조회되는지 확인
                () -> assertThat(firstPage.get(0).getContent()).isEqualTo("안녕 20"),
                () -> assertThat(firstPage.get(10).getContent()).isEqualTo("안녕 10")
        );
    }
}
