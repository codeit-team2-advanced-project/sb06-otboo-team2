package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClothesRepository extends JpaRepository<Clothes, UUID> {

}
