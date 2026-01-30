package codeit.sb06.otboo.message.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomTest {

    @Test
    @DisplayName("dmKey 생성 규칙을 검증한다.")
    void testGenerateDmKey() {
        // given
        UUID smallerId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID largerId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

        // when
        String dmKey = ChatRoom.generateDmKey(largerId, smallerId);

        // then
        assertThat(dmKey).isEqualTo(smallerId + "_" + largerId);
    }
}
