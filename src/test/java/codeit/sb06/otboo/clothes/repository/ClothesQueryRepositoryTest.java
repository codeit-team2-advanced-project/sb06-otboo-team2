package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.*;
import codeit.sb06.otboo.config.JpaAuditingConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
public class ClothesQueryRepositoryTest {
    @Autowired
    ClothesRepository clothesRepository;

    @Autowired
    ClothesAttributeDefRepository clothesAttributeDefRepository;

    @Autowired
    EntityManager em;

    @TestConfiguration
    static class QuerydslTestConfig {
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Test
    @DisplayName("countByFilter: ownerId + type 조건으로 전체 개수를 반환한다")
    void countByFilter_success() {
        // given
        UUID ownerId = UUID.randomUUID();
        clothesRepository.saveAndFlush(new Clothes(ownerId, "A", null, ClothesType.TOP));
        clothesRepository.saveAndFlush(new Clothes(ownerId, "B", null, ClothesType.TOP));
        clothesRepository.saveAndFlush(new Clothes(ownerId, "C", null, ClothesType.BOTTOM));

        // when
        long countTop = clothesRepository.countByFilter(ownerId, ClothesType.TOP);
        long countAll = clothesRepository.countByFilter(ownerId, null);

        // then
        assertThat(countTop).isEqualTo(2);
        assertThat(countAll).isEqualTo(3);
    }

    @Test
    @DisplayName("findIdsByCursor: cursor가 null이면 최신순으로 limit+1개까지 조회된다")
    void findIdsByCursor_firstPage_ordersAndLimits() {
        // given
        UUID ownerId = UUID.randomUUID();

        Clothes c1 = clothesRepository.save(new Clothes(ownerId, "c1", null, ClothesType.TOP));
        Clothes c2 = clothesRepository.save(new Clothes(ownerId, "c2", null, ClothesType.TOP));
        Clothes c3 = clothesRepository.save(new Clothes(ownerId, "c3", null, ClothesType.TOP));
        em.flush();

        setCreatedAt(c1.getId(), LocalDateTime.of(2026, 2, 10, 10, 0));
        setCreatedAt(c2.getId(), LocalDateTime.of(2026, 2, 10, 9, 0));
        setCreatedAt(c3.getId(), LocalDateTime.of(2026, 2, 10, 8, 0));
        em.flush();
        em.clear();

        // when
        List<UUID> ids = clothesRepository.findIdsByCursor(
                ownerId,
                ClothesType.TOP,
                null,
                null,
                2 // limitPlusOne
        );

        // then
        assertThat(ids).containsExactly(c1.getId(), c2.getId());
    }

    @Test
    @DisplayName("findWithAllByIds: ids에 해당하는 Clothes를 연관관계까지 로딩하고 ids 순서를 유지한다")
    void findWithAllByIds_loadsAndKeepsOrder() {
        // given
        ClothesAttributeDef def = new ClothesAttributeDef("색상");
        def.replaceValues(List.of("Black", "White"));
        def = clothesAttributeDefRepository.saveAndFlush(def);

        UUID ownerId = UUID.randomUUID();
        Clothes c1 = new Clothes(ownerId, "c1", null, ClothesType.TOP);
        new ClothesAttribute(c1, def, "Black");
        Clothes saved1 = clothesRepository.saveAndFlush(c1);

        Clothes c2 = new Clothes(ownerId, "c2", null, ClothesType.TOP);
        new ClothesAttribute(c2, def, "White");
        Clothes saved2 = clothesRepository.saveAndFlush(c2);

        em.clear();

        List<UUID> ids = List.of(saved2.getId(), saved1.getId());

        // when
        List<Clothes> result = clothesRepository.findWithAllByIds(ids);

        // then
        assertThat(result).extracting(Clothes::getId)
                .containsExactly(saved2.getId(), saved1.getId());


        Clothes first = result.get(0);
        assertThat(first.getAttributes()).hasSize(1);
        ClothesAttribute attr = first.getAttributes().get(0);
        assertThat(attr.getDefinition().getName()).isEqualTo("색상");
        assertThat(attr.getDefinition().getValues())
                .extracting(ClothesAttributeDefValue::getValue)
                .containsExactlyInAnyOrder("Black", "White");
    }

    private void setCreatedAt(UUID id, LocalDateTime t) {
        em.createNativeQuery("update clothes set created_at = ?, updated_at = ? where id = ?")
                .setParameter(1, java.sql.Timestamp.valueOf(t))
                .setParameter(2, java.sql.Timestamp.valueOf(t))
                .setParameter(3, id)
                .executeUpdate();
    }
}
