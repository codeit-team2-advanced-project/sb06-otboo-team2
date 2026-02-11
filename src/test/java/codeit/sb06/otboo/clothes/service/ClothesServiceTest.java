package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.*;
import codeit.sb06.otboo.clothes.entity.*;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.clothes.ClothesAttributeDefNotFoundException;
import codeit.sb06.otboo.exception.clothes.ClothesNotFoundException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesAttributeValueException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Test
    @DisplayName("update: 의상이 존재하지 않으면 ClothesNotFoundException을 던진다")
    void update_fail_whenClothesNotFound() {
        // given
        UUID clothesId = UUID.randomUUID();

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "  수정 티셔츠  ",
                "TOP",
                null
        );

        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> clothesService.update(clothesId, req, null))
                .isInstanceOf(ClothesNotFoundException.class);

        verify(clothesRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: type이 올바르지 않으면 InvalidClothesTypeException을 던진다")
    void update_fail_whenInvalidType() {
        // given
        UUID clothesId = UUID.randomUUID();

        Clothes clothes = mock(Clothes.class);
        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.of(clothes));

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "수정 티셔츠",
                "NOT_A_TYPE",
                null
        );

        // when & then
        assertThatThrownBy(() -> clothesService.update(clothesId, req, null))
                .isInstanceOf(InvalidClothesTypeException.class);

        verify(clothesRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: attributes가 null이면 기존 속성을 유지하고 ClothesDto를 반환한다")
    void update_success_whenAttributesNull_keepsExistingAttributes() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "  수정 티셔츠  ",
                "TOP",
                null
        );

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        when(def.getId()).thenReturn(defId);
        when(def.getName()).thenReturn("색상");

        ClothesAttributeDefValue v1 = mock(ClothesAttributeDefValue.class);
        when(v1.getValue()).thenReturn("Black");
        when(def.getValues()).thenReturn(List.of(v1));

        Clothes found = new Clothes(ownerId, "OLD", "old-url", ClothesType.TOP);
        new ClothesAttribute(found, def, "Black");

        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.of(found));

        when(clothesRepository.save(any(Clothes.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        ClothesDto result = clothesService.update(clothesId, req, null);

        // then
        assertThat(result.name()).isEqualTo("수정 티셔츠");
        assertThat(result.type()).isEqualTo("TOP");
        assertThat(result.attributes()).hasSize(1);
        assertThat(result.attributes().get(0).definitionId()).isEqualTo(defId);
        assertThat(result.attributes().get(0).value()).isEqualTo("Black");

        verify(clothesAttributeDefRepository, never()).findAllById(anyList());
        verify(clothesRepository).save(any(Clothes.class));
    }

    @Test
    @DisplayName("update: attributes가 빈 리스트면 기존 속성을 모두 삭제하고 ClothesDto를 반환한다")
    void update_success_whenAttributesEmpty_clearsAll() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "수정 티셔츠",
                "TOP",
                List.of()
        );


        Clothes found = new Clothes(ownerId, "OLD", "old-url", ClothesType.TOP);

        ClothesAttributeDef anyDef = mock(ClothesAttributeDef.class);
        new ClothesAttribute(found, anyDef, "Black");

        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.of(found));

        when(clothesRepository.save(any(Clothes.class)))
                .thenAnswer(inv -> inv.getArgument(0));


        // when
        ClothesDto result = clothesService.update(clothesId, req, null);

        // then
        assertThat(result.name()).isEqualTo("수정 티셔츠");
        assertThat(result.attributes()).isEmpty();

        verify(clothesAttributeDefRepository, never()).findAllById(anyList());
        verify(clothesRepository).save(any(Clothes.class));
    }

    @Test
    @DisplayName("update: attributes가 있으면 전체 교체하여 ClothesDto를 반환한다")
    void update_success_whenAttributesProvided_replacesAll() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "수정 티셔츠",
                "TOP",
                List.of(new ClothesAttributeDto(defId.toString(), "White"))
        );

        Clothes found = new Clothes(ownerId, "OLD", "old-url", ClothesType.TOP);

        ClothesAttributeDef oldDef = mock(ClothesAttributeDef.class);
        new ClothesAttribute(found, oldDef, "Black");

        ClothesAttributeDef newDef = mock(ClothesAttributeDef.class);
        when(newDef.getId()).thenReturn(defId);
        when(newDef.getName()).thenReturn("색상");

        ClothesAttributeDefValue v1 = mock(ClothesAttributeDefValue.class);
        when(v1.getValue()).thenReturn("Black");
        ClothesAttributeDefValue v2 = mock(ClothesAttributeDefValue.class);
        when(v2.getValue()).thenReturn("White");
        when(newDef.getValues()).thenReturn(List.of(v1, v2));

        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.of(found));

        when(clothesAttributeDefRepository.findAllByIdInWithValues(anyList()))
                .thenReturn(List.of(newDef));

        when(clothesRepository.save(any(Clothes.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        ClothesDto result = clothesService.update(clothesId, req, null);

        // then
        assertThat(result.name()).isEqualTo("수정 티셔츠");
        assertThat(result.type()).isEqualTo("TOP");
        assertThat(result.attributes()).hasSize(1);
        assertThat(result.attributes().get(0).definitionId()).isEqualTo(defId);
        assertThat(result.attributes().get(0).value()).isEqualTo("White");

        verify(clothesAttributeDefRepository).findAllByIdInWithValues(anyList());
        verify(clothesRepository).save(any(Clothes.class));
    }

    @Test
    @DisplayName("update: attributes에 존재하지 않는 definitionId가 있으면 ClothesAttributeDefNotFoundException을 던진다")
    void update_fail_whenDefinitionNotFound() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "수정 티셔츠",
                "TOP",
                List.of(new ClothesAttributeDto(defId.toString(), "Black"))
        );

        Clothes found = new Clothes(ownerId, "OLD", "old-url", ClothesType.TOP);

        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.of(found));

        when(clothesAttributeDefRepository.findAllByIdInWithValues(anyList()))
                .thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> clothesService.update(clothesId, req, null))
                .isInstanceOf(ClothesAttributeDefNotFoundException.class);

        verify(clothesRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: selectableValues에 없는 값을 보내면 InvalidClothesAttributeValueException을 던진다")
    void update_fail_whenSelectableValuesMismatch() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();

        ClothesUpdateRequest req = new ClothesUpdateRequest(
                "수정 티셔츠",
                "TOP",
                List.of(new ClothesAttributeDto(defId.toString(), "Red"))
        );

        Clothes found = new Clothes(ownerId, "OLD", "old-url", ClothesType.TOP);

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        when(def.getId()).thenReturn(defId);

        ClothesAttributeDefValue v1 = mock(ClothesAttributeDefValue.class);
        when(v1.getValue()).thenReturn("Black");
        ClothesAttributeDefValue v2 = mock(ClothesAttributeDefValue.class);
        when(v2.getValue()).thenReturn("White");
        when(def.getValues()).thenReturn(List.of(v1, v2));

        when(clothesRepository.findWithAttributesById(clothesId))
                .thenReturn(Optional.of(found));

        when(clothesAttributeDefRepository.findAllByIdInWithValues(anyList()))
                .thenReturn(List.of(def));

        // when & then
        assertThatThrownBy(() -> clothesService.update(clothesId, req, null))
                .isInstanceOf(InvalidClothesAttributeValueException.class);

        verify(clothesRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete: 정상 요청이면 옷을 삭제한다")
    void delete_success() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Clothes found = mock(Clothes.class);

        when(clothesRepository.findByIdAndOwnerId(clothesId, ownerId))
                .thenReturn(Optional.of(found));

        // when
        clothesService.delete(clothesId, ownerId);

        // then
        verify(clothesRepository).findByIdAndOwnerId(clothesId, ownerId);
        verify(clothesRepository).delete(found);
    }

    @Test
    @DisplayName("delete: 옷이 없거나(또는 소유자가 아니면) ClothesNotFoundException을 던진다")
    void delete_fail_whenNotFoundOrNotOwner() {
        // given
        UUID clothesId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        when(clothesRepository.findByIdAndOwnerId(clothesId, ownerId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> clothesService.delete(clothesId, ownerId))
                .isInstanceOf(ClothesNotFoundException.class);

        verify(clothesRepository).findByIdAndOwnerId(clothesId, ownerId);
        verify(clothesRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getList: limit이 0 이하이면 IllegalArgumentException")
    void getList_fail_whenLimitZeroOrNegative() {
        // given
        UUID ownerId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> clothesService.getList(null, null, 0, null, ownerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit은 1 이상");

        verifyNoInteractions(clothesRepository);
    }

    @Test
    @DisplayName("getList: typeEqual이 잘못되면 InvalidClothesTypeException")
    void getList_fail_whenInvalidTypeEqual() {
        // given
        UUID ownerId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> clothesService.getList(null, null, 10, "NOT_A_TYPE", ownerId))
                .isInstanceOf(InvalidClothesTypeException.class);

        verifyNoInteractions(clothesRepository);
    }

    @Test
    @DisplayName("getList: 첫 페이지 + type 없음이면 limit만큼 반환하고 hasNext를 계산한다")
    void getList_success_firstPage_noCursor_noType() {
        // given
        UUID ownerId = UUID.randomUUID();
        int limit = 2;

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        when(clothesRepository.findIdsByCursor(
                eq(ownerId),
                isNull(),     // type
                isNull(),     // cursorCreatedAt
                isNull(),     // idAfter
                eq(limit + 1)
        )).thenReturn(List.of(id1, id2, id3));

        LocalDateTime t1 = LocalDateTime.of(2026, 2, 10, 10, 0, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 2, 10, 9, 0, 0);

        Clothes c1 = mockClothesForList(id1, t1);
        Clothes c2 = mockClothesForList(id2, t2);

        when(c1.getOwnerId()).thenReturn(ownerId);
        when(c1.getName()).thenReturn("A");
        when(c1.getType()).thenReturn(ClothesType.TOP);

        when(c2.getOwnerId()).thenReturn(ownerId);
        when(c2.getName()).thenReturn("B");
        when(c2.getType()).thenReturn(ClothesType.TOP);

        when(clothesRepository.findWithAllByIds(List.of(id1, id2)))
                .thenReturn(List.of(c1, c2));

        when(clothesRepository.countByFilter(ownerId, null)).thenReturn(10L);

        // when
        ClothesDtoCursorResponse res = clothesService.getList(
                null, null, limit, null, ownerId
        );

        // then
        assertThat(res.data()).hasSize(2);
        assertThat(res.hasNext()).isTrue();
        assertThat(res.totalCount()).isEqualTo(10L);

        assertThat(res.nextCursor()).isEqualTo(t2.toString());
        assertThat(res.nextIdAfter()).isEqualTo(id2);

        assertThat(res.sortBy()).isEqualTo("createdAt");
        assertThat(res.sortDirection()).isEqualTo("DESCENDING");

        verify(clothesRepository).findIdsByCursor(ownerId, null, null, null, limit + 1);
        verify(clothesRepository).findWithAllByIds(List.of(id1, id2));
        verify(clothesRepository).countByFilter(ownerId, null);
    }

    @Test
    @DisplayName("getList: ids가 limit 이하로 나오면 hasNext=false이고 ids 전체로 상세조회한다")
    void getList_success_lastPage_hasNextFalse() {
        // given
        UUID ownerId = UUID.randomUUID();
        int limit = 3;

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(clothesRepository.findIdsByCursor(
                eq(ownerId),
                eq(ClothesType.TOP),
                any(LocalDateTime.class),
                any(UUID.class),
                eq(limit + 1)
        )).thenReturn(List.of(id1, id2));

        LocalDateTime t1 = LocalDateTime.of(2026, 2, 10, 10, 0, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 2, 10, 9, 0, 0);

        Clothes c1 = mockClothesForList(id1, t1);
        Clothes c2 = mockClothesForList(id2, t2);

        when(c1.getOwnerId()).thenReturn(ownerId);
        when(c1.getName()).thenReturn("A");
        when(c1.getType()).thenReturn(ClothesType.TOP);

        when(c2.getOwnerId()).thenReturn(ownerId);
        when(c2.getName()).thenReturn("B");
        when(c2.getType()).thenReturn(ClothesType.TOP);

        when(clothesRepository.findWithAllByIds(List.of(id1, id2)))
                .thenReturn(List.of(c1, c2));

        when(clothesRepository.countByFilter(ownerId, ClothesType.TOP)).thenReturn(2L);

        // when
        ClothesDtoCursorResponse res = clothesService.getList(
                "2026-02-10T12:00:00",
                UUID.randomUUID(),
                limit,
                "TOP",
                ownerId
        );

        // then
        assertThat(res.hasNext()).isFalse();
        assertThat(res.data()).hasSize(2);
        assertThat(res.totalCount()).isEqualTo(2L);
        assertThat(res.nextCursor()).isEqualTo(t2.toString());
        assertThat(res.nextIdAfter()).isEqualTo(id2);

        verify(clothesRepository).findWithAllByIds(List.of(id1, id2));
    }

    @Test
    @DisplayName("getList: cursor 문자열이 있으면 LocalDateTime으로 파싱되어 findIdsByCursor에 전달된다")
    void getList_success_parsesCursorString() {
        // given
        UUID ownerId = UUID.randomUUID();
        int limit = 2;

        String cursor = "2026-02-10T12:00:00";
        UUID idAfter = UUID.randomUUID();
        UUID id1 = UUID.randomUUID();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);

        when(clothesRepository.findIdsByCursor(
                eq(ownerId),
                isNull(),
                captor.capture(),
                eq(idAfter),
                eq(limit + 1)
        )).thenReturn(List.of(id1));

        LocalDateTime t1 = LocalDateTime.of(2026, 2, 10, 10, 0);

        Clothes c1 = mockClothesForList(id1, t1);

        when(c1.getOwnerId()).thenReturn(ownerId);
        when(c1.getName()).thenReturn("A");
        when(c1.getType()).thenReturn(ClothesType.TOP);

        when(clothesRepository.findWithAllByIds(List.of(id1))).thenReturn(List.of(c1));
        when(clothesRepository.countByFilter(ownerId, null)).thenReturn(1L);

        // when
        clothesService.getList(cursor, idAfter, limit, null, ownerId);

        // then
        assertThat(captor.getValue()).isEqualTo(LocalDateTime.parse(cursor));
    }

    private Clothes mockClothesForList(UUID id, LocalDateTime createdAt) {
        Clothes c = mock(Clothes.class);
        lenient().when(c.getId()).thenReturn(id);
        lenient().when(c.getCreatedAt()).thenReturn(createdAt);
        lenient().when(c.getAttributes()).thenReturn(List.of());
        return c;
    }

}
