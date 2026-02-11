package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.Clothes;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClothesRepository extends JpaRepository<Clothes, UUID>, ClothesQueryRepository {

    @EntityGraph(attributePaths = {
            "attributes",
            "attributes.definition"
    })
    Optional<Clothes> findWithAttributesById(UUID id);

    Optional<Clothes> findByIdAndOwnerId(UUID id, UUID ownerId);
}
