package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface ClothesAttributeDefRepository extends JpaRepository<ClothesAttributeDef, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Optional<ClothesAttributeDef> findByName(String name);
}
