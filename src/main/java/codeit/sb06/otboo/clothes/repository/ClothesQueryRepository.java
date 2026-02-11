package codeit.sb06.otboo.clothes.repository;

import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClothesQueryRepository {
    List<UUID> findIdsByCursor(UUID ownerId,
                               ClothesType typeEqual,
                               LocalDateTime cursor,
                               UUID idAfter,
                               int limitPlusOne);

    List<Clothes> findWithAllByIds(List<UUID> ids);

    long countByFilter(UUID ownerId, ClothesType typeEqual);
}
