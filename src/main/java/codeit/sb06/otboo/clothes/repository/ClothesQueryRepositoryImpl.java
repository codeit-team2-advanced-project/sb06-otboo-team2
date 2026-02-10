package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ClothesQueryRepositoryImpl implements ClothesQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UUID> findIdsByCursor(UUID ownerId,
                                      ClothesType typeEqual,
                                      LocalDateTime cursor,
                                      UUID idAfter,
                                      int limitPlusOne) {

        QClothes c = QClothes.clothes;

        BooleanExpression predicate = c.ownerId.eq(ownerId);
        if (typeEqual != null) {
            predicate = predicate.and(c.type.eq(typeEqual));
        }

        if (cursor != null) {
            BooleanExpression cursorExpr = c.createdAt.lt(cursor);

            if (idAfter != null) {
                cursorExpr = cursorExpr.or(c.createdAt.eq(cursor).and(c.id.lt(idAfter)));
            }
            predicate = predicate.and(cursorExpr);
        }

        return queryFactory
                .select(c.id)
                .from(c)
                .where(predicate)
                .orderBy(c.createdAt.desc(), c.id.desc())
                .limit(limitPlusOne)
                .fetch();
    }

    @Override
    public List<Clothes> findWithAllByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        QClothes c = QClothes.clothes;
        QClothesAttribute a = QClothesAttribute.clothesAttribute;
        QClothesAttributeDef d = QClothesAttributeDef.clothesAttributeDef;
        QClothesAttributeDefValue v = QClothesAttributeDefValue.clothesAttributeDefValue;

        List<Clothes> fetched = queryFactory
                .selectFrom(c)
                .distinct()
                .leftJoin(c.attributes, a).fetchJoin()
                .leftJoin(a.definition, d).fetchJoin()
                .where(c.id.in(ids))
                .fetch();

        List<UUID> defIds = fetched.stream()
                .flatMap(cl -> cl.getAttributes().stream())
                .map(attr -> attr.getDefinition().getId())
                .distinct()
                .toList();

        if (!defIds.isEmpty()) {
            queryFactory
                    .selectFrom(d)
                    .distinct()
                    .leftJoin(d.values, v).fetchJoin()
                    .where(d.id.in(defIds))
                    .fetch();
        }

        Map<UUID, Clothes> map = fetched.stream()
                .collect(Collectors.toMap(Clothes::getId, Function.identity(), (x, y) -> x));

        List<Clothes> ordered = new ArrayList<>();
        for (UUID id : ids) {
            Clothes clothes = map.get(id);
            if (clothes != null) ordered.add(clothes);
        }
        return ordered;
    }

    @Override
    public long countByFilter(UUID ownerId, ClothesType typeEqual) {
        QClothes c = QClothes.clothes;

        BooleanExpression predicate = c.ownerId.eq(ownerId);
        if (typeEqual != null) {
            predicate = predicate.and(c.type.eq(typeEqual));
        }

        Long cnt = queryFactory
                .select(c.count())
                .from(c)
                .where(predicate)
                .fetchOne();

        return cnt == null ? 0L : cnt;
    }


}
