package codeit.sb06.otboo.clothes.dto;

import java.util.List;

public record ClothesDtoCursorResponse(
        List<ClothesDto> data,
        String nextCursor,
        String nextIdAfter,
        boolean hasNext,
        long totalCount,
        String sortBy,
        String sortDirection
) {
}
