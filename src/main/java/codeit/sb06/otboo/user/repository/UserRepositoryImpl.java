package codeit.sb06.otboo.user.repository;

import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.entity.QUser;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;

    @Override
    public Slice<User> findUsersBySlice(UserSliceRequest userSliceRequest) {
        boolean isDesc = !"ASC".equalsIgnoreCase(userSliceRequest.sortDirection());
        String sortBy = userSliceRequest.sortBy() == null ? "createdAt" : userSliceRequest.sortBy();
        int limit = userSliceRequest.limit() > 0 ? userSliceRequest.limit() : 20;

        BooleanBuilder where = new BooleanBuilder();

        String emailLike = userSliceRequest.emailLike();
        if (emailLike != null && !emailLike.isBlank()) {
            where.and(user.email.containsIgnoreCase(emailLike));
        }

        String roleEqual = userSliceRequest.roleEqual();
        if (roleEqual != null && !roleEqual.isBlank()) {
            try {
                Role role = Role.valueOf(roleEqual.trim().toUpperCase(Locale.ROOT));
                where.and(user.role.eq(role));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid role input.
            }
        }

        Boolean locked = userSliceRequest.locked();
        if (locked != null) {
            where.and(user.isLocked.eq(locked));
        }

        String cursor = userSliceRequest.cursor();
        if (cursor != null && !cursor.isBlank()) {
            addCursorPredicate(where, sortBy, cursor, isDesc);
        }

        String idAfter = userSliceRequest.idAfter();
        if (idAfter != null && !idAfter.isBlank()) {
            try {
                UUID id = UUID.fromString(idAfter);
                where.and(isDesc ? user.id.lt(id) : user.id.gt(id));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid UUID cursor.
            }
        }

        OrderSpecifier<?> primaryOrder = orderSpecifier(sortBy, isDesc);
        OrderSpecifier<?> secondaryOrder = new OrderSpecifier<>(
            isDesc ? Order.DESC : Order.ASC,
            user.id
        );

        List<User> results = jpaQueryFactory.selectFrom(user)
            .where(where)
            .orderBy(primaryOrder, secondaryOrder)
            .limit((long) limit + 1)
            .fetch();

        boolean hasNext = results.size() > limit;
        if (hasNext) {
            results = results.subList(0, limit);
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(
            isDesc ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy
        ));
        return new SliceImpl<>(results, pageable, hasNext);
    }

    private OrderSpecifier<?> orderSpecifier(String sortBy, boolean isDesc) {
        Order order = isDesc ? Order.DESC : Order.ASC;
        if ("email".equalsIgnoreCase(sortBy)) {
            return new OrderSpecifier<>(order, user.email);
        }
        if ("role".equalsIgnoreCase(sortBy)) {
            return new OrderSpecifier<>(order, user.role);
        }
        if ("updatedAt".equalsIgnoreCase(sortBy)) {
            return new OrderSpecifier<>(order, user.updatedAt);
        }
        if ("id".equalsIgnoreCase(sortBy)) {
            return new OrderSpecifier<>(order, user.id);
        }
        return new OrderSpecifier<>(order, user.createdAt);
    }

    private void addCursorPredicate(BooleanBuilder where, String sortBy, String cursor,
        boolean isDesc) {
        if ("email".equalsIgnoreCase(sortBy)) {
            where.and(isDesc ? user.email.lt(cursor) : user.email.gt(cursor));
            return;
        }
        if ("role".equalsIgnoreCase(sortBy)) {
            try {
                Role role = Role.valueOf(cursor.trim().toUpperCase(Locale.ROOT));
                where.and(isDesc ? user.role.lt(role) : user.role.gt(role));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid role cursor.
            }
            return;
        }
        if ("id".equalsIgnoreCase(sortBy)) {
            try {
                UUID id = UUID.fromString(cursor);
                where.and(isDesc ? user.id.lt(id) : user.id.gt(id));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid UUID cursor.
            }
            return;
        }
        try {
            LocalDateTime dateCursor = LocalDateTime.parse(cursor);
            if ("createdAt".equalsIgnoreCase(sortBy)) {
                where.and(isDesc ? user.createdAt.lt(dateCursor) : user.createdAt.gt(dateCursor));
            } else if ("updatedAt".equalsIgnoreCase(sortBy)) {
                where.and(isDesc ? user.updatedAt.lt(dateCursor) : user.updatedAt.gt(dateCursor));
            }
        } catch (DateTimeParseException ignored) {
            // Ignore invalid date cursor.
        }
    }
}
