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


    @Query("""
           select distinct d
           from ClothesAttributeDef d
           left join fetch d.values v
           where d.id = :id
           """)
    Optional<ClothesAttributeDef> findByIdWithValues(@Param("id") UUID id);

    // keywordLike 있을 때
    @Query("""
           select distinct d
           from ClothesAttributeDef d
           left join fetch d.values v
           where d.name like concat('%', :keywordLike, '%')
           """)
    List<ClothesAttributeDef> searchByNameLikeWithValues(
            @Param("keywordLike") String keywordLike,
            Sort sort
    );

    // keywordLike 없을때
    @Query("""
           select distinct d
           from ClothesAttributeDef d
           left join fetch d.values v
           """)
    List<ClothesAttributeDef> findAllWithValues(Sort sort);
}
