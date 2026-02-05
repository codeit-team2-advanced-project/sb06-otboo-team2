package codeit.sb06.otboo.message.repository;

import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.entity.DirectMessage;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.UUID;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, UUID> {

    @Query("""
            SELECT dm FROM DirectMessage dm
            WHERE dm.chatRoom = :chatRoom
            AND (
                    :cursor IS NULL
                    OR dm.createdAt < :cursor
                    OR (dm.createdAt = :cursor AND dm.id < :idAfter)
                )
            ORDER BY dm.createdAt DESC, dm.id DESC
            """)
    Slice<DirectMessage> findByChatRoomWithCursor(ChatRoom chatRoom, LocalDateTime cursor, UUID idAfter, Pageable pageable);
}
