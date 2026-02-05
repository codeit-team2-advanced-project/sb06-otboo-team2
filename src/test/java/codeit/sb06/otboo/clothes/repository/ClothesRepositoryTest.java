package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesAttribute;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import codeit.sb06.otboo.clothes.entity.ClothesType;
import codeit.sb06.otboo.config.JpaAuditingConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
public class ClothesRepositoryTest {

    @Autowired
    ClothesRepository clothesRepository;

    @Autowired
    ClothesAttributeDefRepository clothesAttributeDefRepository;

    @Autowired
    ClothesAttributeRepository clothesAttributeRepository;

    @TestConfiguration
    static class QuerydslTestConfig {
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Test
    void save_clothes_shouldCascadeSave_attributes() {
        // given
        ClothesAttributeDef def = new ClothesAttributeDef("색상");
        def.replaceValues(List.of("Black", "White"));
        def = clothesAttributeDefRepository.saveAndFlush(def);

        UUID ownerId = UUID.randomUUID();
        Clothes clothes = new Clothes(ownerId, "티셔츠", null, ClothesType.TOP);

        new ClothesAttribute(clothes, def, "Black");

        // when
        Clothes saved = clothesRepository.saveAndFlush(clothes);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(clothesAttributeRepository.findAll()).hasSize(1);
        ClothesAttribute savedAttr = clothesAttributeRepository.findAll().get(0);
        assertThat(savedAttr.getClothes().getId()).isEqualTo(saved.getId());
        assertThat(savedAttr.getDefinition().getId()).isEqualTo(def.getId());
        assertThat(savedAttr.getValue()).isEqualTo("Black");
    }
}
