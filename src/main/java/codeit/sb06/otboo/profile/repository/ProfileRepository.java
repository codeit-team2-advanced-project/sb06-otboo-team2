package codeit.sb06.otboo.profile.repository;

import codeit.sb06.otboo.profile.entity.Profile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

}
