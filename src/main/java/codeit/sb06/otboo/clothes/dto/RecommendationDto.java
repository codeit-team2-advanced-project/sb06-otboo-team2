package codeit.sb06.otboo.clothes.dto;

import codeit.sb06.otboo.clothes.entity.Clothes;

import java.util.List;
import java.util.UUID;

public record RecommendationDto(
        UUID weatherId,
        UUID userId,
        List<ClothesDto> clothes
) {
}
