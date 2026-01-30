package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.ClothesAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClothesAttributeRepository extends JpaRepository<ClothesAttribute, UUID> {

    long deleteByDefinitionId(UUID definitionId);

}
