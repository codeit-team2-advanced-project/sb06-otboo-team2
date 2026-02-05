package codeit.sb06.otboo.profile.repository;

import codeit.sb06.otboo.profile.entity.Location;
import codeit.sb06.otboo.profile.entity.Profile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByProfile(Profile profile);
}
