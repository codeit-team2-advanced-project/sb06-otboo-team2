package codeit.sb06.otboo.message.repository;

import codeit.sb06.otboo.message.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    Optional<ChatRoom> findByDmKey(String dmKey);
}
