package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClothesAttributeDefRepository extends JpaRepository<ClothesAttributeDef, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Optional<ClothesAttributeDef> findByName(String name);

    // 목록 조회: 대소문자 구분 보장을 위해 JPQL사용
    @Query("""
           select d
           from ClothesAttributeDef d
           where d.name like concat('%', :keywordLike, '%')
           """)
    List<ClothesAttributeDef> searchByNameLike(@Param("keywordLike") String keywordLike, Sort sort);
}
