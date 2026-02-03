package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefDto;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefUpdateRequest;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClothesAttributeDefServiceTest {

    @Mock
    ClothesAttributeDefRepository repository;

    @Mock
    private ClothesAttributeRepository clothesAttributeRepository;

    @InjectMocks
    ClothesAttributeDefService service;

    @Test
    @DisplayName("create: 정상 생성하면 DTO를 반환한다")
    void create_success() {
        // given
        ClothesAttributeDefCreateRequest req =
                new ClothesAttributeDefCreateRequest("  색상  ", List.of("Black", "White"));

        UUID id = UUID.randomUUID();
        ClothesAttributeDef saved = mock(ClothesAttributeDef.class);
        when(saved.getId()).thenReturn(id);
        when(saved.getName()).thenReturn("색상");
        when(saved.getSelectableValues()).thenReturn(List.of("Black", "White"));
        when(saved.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(repository.existsByName("색상")).thenReturn(false);
        when(repository.save(any(ClothesAttributeDef.class))).thenReturn(saved);

        // when
        ClothesAttributeDefDto dto = service.create(req);

        // then
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.name()).isEqualTo("색상");
        assertThat(dto.selectableValues()).containsExactly("Black", "White");
        verify(repository).existsByName("색상");
        verify(repository).save(any(ClothesAttributeDef.class));
    }

    @Test
    @DisplayName("create: name 중복이면 IllegalArgumentException")
    void create_duplicateName() {
        // given
        ClothesAttributeDefCreateRequest req =
                new ClothesAttributeDefCreateRequest("색상", List.of("Black"));

        when(repository.existsByName("색상")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: 동시성 등으로 DB 유니크 충돌 발생 시 IllegalArgumentException으로 변환")
    void create_uniqueConstraintViolation() {
        // given
        ClothesAttributeDefCreateRequest req =
                new ClothesAttributeDefCreateRequest("색상", List.of("Black"));

        when(repository.existsByName("색상")).thenReturn(false);
        when(repository.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        // when & then
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재");
    }

    @Test
    @DisplayName("update: 정상 수정하면 DTO를 반환한다")
    void update_success() {
        // given
        UUID id = UUID.randomUUID();
        ClothesAttributeDefUpdateRequest req =
                new ClothesAttributeDefUpdateRequest("소재", List.of("Cotton", "Wool"));

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        when(def.getId()).thenReturn(id);
        when(def.getName()).thenReturn("소재");
        when(def.getSelectableValues()).thenReturn(List.of("Cotton", "Wool"));
        when(def.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(repository.findById(id)).thenReturn(Optional.of(def));
        when(repository.existsByNameAndIdNot("소재", id)).thenReturn(false);

        // when
        ClothesAttributeDefDto dto = service.update(id, req);

        // then
        verify(def).changeName("소재");
        verify(def).replaceSelectableValues(List.of("Cotton", "Wool"));
        assertThat(dto.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("update: 존재하지 않는 id면 EntityNotFoundException")
    void update_notFound() {
        // given
        UUID id = UUID.randomUUID();
        ClothesAttributeDefUpdateRequest req =
                new ClothesAttributeDefUpdateRequest("소재", List.of("Cotton"));

        when(repository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.update(id, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("update: 다른 row가 같은 name을 쓰면 IllegalArgumentException")
    void update_duplicateName() {
        // given
        UUID id = UUID.randomUUID();
        ClothesAttributeDefUpdateRequest req =
                new ClothesAttributeDefUpdateRequest("색상", List.of("Black"));

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        when(repository.findById(id)).thenReturn(Optional.of(def));
        when(repository.existsByNameAndIdNot("색상", id)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> service.update(id, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재");
    }

    @Test
    @DisplayName("delete: 존재하지 않는 속성 정의면 IllegalArgumentException")
    void delete_whenDefNotFound_thenThrow() {
        // given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> service.delete(id));

        verify(clothesAttributeRepository, never()).deleteByDefinitionId(any());
        verify(repository, never()).delete(any(ClothesAttributeDef.class));
    }

    @Test
    @DisplayName("delete: 속성 정의가 존재하면 해당 속성값(ClothesAttribute)을 먼저 삭제하고 정의를 삭제한다")
    void delete_whenDefExists_thenDeleteAttributesThenDef() {
        // given
        UUID id = UUID.randomUUID();
        ClothesAttributeDef def = new ClothesAttributeDef("색상", List.of("블랙", "화이트"));

        when(repository.findById(id)).thenReturn(Optional.of(def));
        when(clothesAttributeRepository.deleteByDefinitionId(id)).thenReturn(3L);

        // when
        service.delete(id);

        // then
        InOrder inOrder = inOrder(clothesAttributeRepository, repository);
        inOrder.verify(clothesAttributeRepository).deleteByDefinitionId(id);
        inOrder.verify(repository).delete(def);

        verify(repository, times(1)).findById(id);
        verifyNoMoreInteractions(clothesAttributeRepository, repository);
    }

    @Test
    @DisplayName("getList: keywordLike가 없으면 findAll로 전체 조회하고 DTO 리스트를 반환한다")
    void getList_whenKeywordNull_thenFindAll() {
        // given
        ClothesAttributeDef def1 = mock(ClothesAttributeDef.class);
        when(def1.getId()).thenReturn(UUID.randomUUID());
        when(def1.getName()).thenReturn("색상");
        when(def1.getSelectableValues()).thenReturn(List.of("Black", "White"));
        when(def1.getCreatedAt()).thenReturn(LocalDateTime.now());

        ClothesAttributeDef def2 = mock(ClothesAttributeDef.class);
        when(def2.getId()).thenReturn(UUID.randomUUID());
        when(def2.getName()).thenReturn("소재");
        when(def2.getSelectableValues()).thenReturn(List.of("Cotton"));
        when(def2.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(repository.findAll(any(Sort.class))).thenReturn(List.of(def1, def2));

        // when
        List<ClothesAttributeDefDto> result = service.getList("createdAt", "ASCENDING", null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("색상");
        assertThat(result.get(1).name()).isEqualTo("소재");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAll(sortCaptor.capture());
        verify(repository, never()).searchByNameLike(anyString(), any(Sort.class));

        Sort sort = sortCaptor.getValue();
        Sort.Order order = sort.getOrderFor("createdAt");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);

        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("getList: keywordLike가 있으면 searchByNameLike로 조회하고 DTO 리스트를 반환한다")
    void getList_whenKeywordExists_thenSearchByNameLike() {
        // given
        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        UUID id = UUID.randomUUID();
        when(def.getId()).thenReturn(id);
        when(def.getName()).thenReturn("Color");
        when(def.getSelectableValues()).thenReturn(List.of("Red"));
        when(def.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(repository.searchByNameLike(anyString(), any(Sort.class))).thenReturn(List.of(def));

        // when
        List<ClothesAttributeDefDto> result = service.getList("name", "DESCENDING", "Co");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(id);
        assertThat(result.get(0).name()).isEqualTo("Color");
        assertThat(result.get(0).selectableValues()).containsExactly("Red");

        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        verify(repository).searchByNameLike(keywordCaptor.capture(), sortCaptor.capture());
        verify(repository, never()).findAll(any(Sort.class));

        assertThat(keywordCaptor.getValue()).isEqualTo("Co");

        Sort sort = sortCaptor.getValue();
        Sort.Order order = sort.getOrderFor("name");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);

        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("getList: keywordLike가 공백이면 전체 조회(findAll)로 처리한다")
    void getList_whenKeywordBlank_thenFindAll() {
        // given
        when(repository.findAll(any(Sort.class))).thenReturn(List.of());

        // when
        List<ClothesAttributeDefDto> result = service.getList("name", "ASCENDING", "   ");

        // then
        assertThat(result).isEmpty();

        verify(repository).findAll(any(Sort.class));
        verify(repository, never()).searchByNameLike(anyString(), any(Sort.class));

        verifyNoMoreInteractions(repository);
    }


    @Test
    @DisplayName("getList: keywordLike는 trim 처리되어 검색에 사용된다")
    void getList_keywordTrimmed() {
        // given
        when(repository.searchByNameLike(anyString(), any(Sort.class))).thenReturn(List.of());

        // when
        service.getList("name", "ASCENDING", "  Co  ");

        // then
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        verify(repository).searchByNameLike(keywordCaptor.capture(), any(Sort.class));
        assertThat(keywordCaptor.getValue()).isEqualTo("Co");

        verifyNoMoreInteractions(repository);
    }
}
