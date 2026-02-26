package codeit.sb06.otboo.feed.dto;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeWithDefDto;
import codeit.sb06.otboo.clothes.entity.Clothes;
import java.util.List;
import java.util.UUID;

public record OotdDto(
    UUID clothesId,
    String name,
    String imageUrl,
    String type,
    List<ClothesAttributeWithDefDto> attributes
) {
    public static OotdDto from(Clothes clothes) {
        return from(clothes, clothes.getImageUrl());
    }

    public static OotdDto from(Clothes clothes, String imageUrl) {
        return new OotdDto(
                clothes.getId(),
                clothes.getName(),
                imageUrl,
                clothes.getType().name(),
                clothes.getAttributes().stream()
                        .map(ClothesAttributeWithDefDto::from)
                        .toList()
        );
    }
}
