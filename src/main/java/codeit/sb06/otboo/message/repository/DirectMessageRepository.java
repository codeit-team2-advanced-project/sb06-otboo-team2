package codeit.sb06.otboo.message.repository;

import codeit.sb06.otboo.message.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, UUID> {
}
