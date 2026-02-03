package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeDto;
import codeit.sb06.otboo.clothes.dto.ClothesCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesDto;
import codeit.sb06.otboo.clothes.entity.*;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.clothes.ClothesAttributeDefNotFoundException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesAttributeValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClothesServiceTest {

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private ClothesAttributeDefRepository clothesAttributeDefRepository;

    @InjectMocks
    private ClothesService clothesService;


    @Test
    @DisplayName("create: 이미지 없이 정상 요청이면 ClothesDto를 반환한다")
    void create_success_withoutImage_returnsDto() {

        // given
        UUID ownerId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();

        ClothesCreateRequest req = new ClothesCreateRequest(
                ownerId.toString(),
                "  검정 티셔츠  ",
                "TOP",
                List.of(new ClothesAttributeDto(defId.toString(), "Black"))
        );

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        when(def.getId()).thenReturn(defId);
        when(def.getName()).thenReturn("색상");

        ClothesAttributeDefValue v1 = mock(ClothesAttributeDefValue.class);
        when(v1.getValue()).thenReturn("Black");
        ClothesAttributeDefValue v2 = mock(ClothesAttributeDefValue.class);
        when(v2.getValue()).thenReturn("White");

        when(def.getValues()).thenReturn(List.of(v1, v2));

        when(clothesAttributeDefRepository.findAllById(anyList())).thenReturn(List.of(def));

        UUID savedId = UUID.randomUUID();
        Clothes saved = mock(Clothes.class);
        when(saved.getId()).thenReturn(savedId);
        when(saved.getOwnerId()).thenReturn(ownerId);
        when(saved.getName()).thenReturn("검정 티셔츠");
        when(saved.getImageUrl()).thenReturn(null);
        when(saved.getType()).thenReturn(ClothesType.TOP);

        ClothesAttribute attr = mock(ClothesAttribute.class);
        when(attr.getDefinition()).thenReturn(def);
        when(attr.getValue()).thenReturn("Black");
        when(saved.getAttributes()).thenReturn(List.of(attr));

        when(clothesRepository.save(any(Clothes.class))).thenReturn(saved);

        // when
        ClothesDto result = clothesService.create(req, null);

        // then
        assertThat(result.id()).isEqualTo(savedId);
        assertThat(result.ownerId()).isEqualTo(ownerId);
        assertThat(result.name()).isEqualTo("검정 티셔츠");
        assertThat(result.type()).isEqualTo("TOP");
        assertThat(result.imageUrl()).isNull();
        assertThat(result.attributes()).hasSize(1);

        verify(clothesAttributeDefRepository).findAllById(anyList());
        verify(clothesRepository).save(any(Clothes.class));

    }

    @Test
    @DisplayName("create: definitionId가 존재하지 않으면 ClothesAttributeDefNotFoundException을 던진다")
    void create_fail_whenDefinitionNotFound() {
        // given
        UUID ownerId = UUID.randomUUID();
        UUID missingDefId = UUID.randomUUID();

        ClothesCreateRequest req = new ClothesCreateRequest(
                ownerId.toString(),
                "티셔츠",
                "TOP",
                List.of(new ClothesAttributeDto(missingDefId.toString(), "Black"))
        );

        when(clothesAttributeDefRepository.findAllById(anyList()))
                .thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> clothesService.create(req, null))
                .isInstanceOf(ClothesAttributeDefNotFoundException.class);

        verify(clothesRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: selectableValues에 없는 값을 보내면 InvalidClothesAttributeValueException을 던진다")
    void create_fail_whenSelectableValuesMismatch() {
        // given
        UUID ownerId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();

        ClothesCreateRequest req = new ClothesCreateRequest(
                ownerId.toString(),
                "티셔츠",
                "TOP",
                List.of(new ClothesAttributeDto(defId.toString(), "Red"))
        );

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        when(def.getId()).thenReturn(defId);
        ClothesAttributeDefValue v1 = mock(ClothesAttributeDefValue.class);
        when(v1.getValue()).thenReturn("Black");

        ClothesAttributeDefValue v2 = mock(ClothesAttributeDefValue.class);
        when(v2.getValue()).thenReturn("White");

        when(def.getValues()).thenReturn(List.of(v1, v2));

        when(clothesAttributeDefRepository.findAllById(anyList()))
                .thenReturn(List.of(def));

        // when & then
        assertThatThrownBy(() -> clothesService.create(req, null))
                .isInstanceOf(InvalidClothesAttributeValueException.class);

        verify(clothesRepository, never()).save(any());
    }
}
