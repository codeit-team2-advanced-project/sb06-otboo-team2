package codeit.sb06.otboo.user.repository;

import codeit.sb06.otboo.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
}
