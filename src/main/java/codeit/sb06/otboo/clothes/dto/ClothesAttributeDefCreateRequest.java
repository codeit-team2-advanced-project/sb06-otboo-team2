package codeit.sb06.otboo.clothes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ClothesAttributeDefCreateRequest(
        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 100, message = "name은 100자 이하여야 합니다.")
        String name,

        @NotNull(message = "selectableValues는 필수입니다.")
        @NotEmpty(message = "selectableValues는 최소 1개 이상이어야 합니다.")
        List<@NotBlank(message = "selectableValues에는 공백이 올 수 없습니다.") String> selectableValues
) {
}
