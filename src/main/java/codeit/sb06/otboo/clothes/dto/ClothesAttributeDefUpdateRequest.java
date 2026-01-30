package codeit.sb06.otboo.clothes.dto;

import org.springframework.validation.annotation.Validated;

import java.util.List;


public record ClothesAttributeDefUpdateRequest(
        String name,
        List<String> selectableValues
) {
}
