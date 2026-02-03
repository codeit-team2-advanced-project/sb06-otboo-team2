package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefDto;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefUpdateRequest;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDefValue;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClothesAttributeDefService {
    private final ClothesAttributeDefRepository repository;
    private final ClothesAttributeRepository clothesAttributeRepository;

    // 속성 정의 등록
    public ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request) {
        String name = normalizeName(request.name());
        List<String> selectableValues = normalizeSelectableValues(request.selectableValues());

        if (repository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 의상 속성 정의입니다: " + name);
        }

        try {
            ClothesAttributeDef def = new ClothesAttributeDef(name);
            def.replaceValues(selectableValues);

            ClothesAttributeDef saved = repository.save(def);
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 존재하는 의상 속성 정의입니다: " + name);
        }
    }

    // 속성 정의 수정
    public ClothesAttributeDefDto update(UUID id, ClothesAttributeDefUpdateRequest request) {
        String name = normalizeName(request.name());
        List<String> selectableValues = normalizeSelectableValues(request.selectableValues());

        ClothesAttributeDef def = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("의상 속성 정의를 찾을 수 없습니다: " + id));

        if (repository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException("이미 존재하는 의상 속성 정의입니다: " + name);
        }

        def.changeName(name);
        def.replaceValues(selectableValues);

        return toDto(def);
    }

    // 속성 정의 삭제
    public void delete(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("id는 필수입니다.");
        }

        ClothesAttributeDef def = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의상 속성 정의를 찾을 수 없습니다: " + id));

        // 옷의 속성 먼저 제거 후 속성 정의 제거
        clothesAttributeRepository.deleteByDefinitionId(id);

        repository.delete(def);
    }

    // 속성 정의 목록 조회
    @Transactional(readOnly = true)
    public List<ClothesAttributeDefDto> getList(String sortBy, String sortDirection, String keywordLike) {

        Sort sort = toSort(sortBy, sortDirection);
        String keyword = normalizeKeywordLike(keywordLike);

        List<ClothesAttributeDef> defs = (keyword == null)
                ? repository.findAllWithValues(sort)
                : repository.searchByNameLikeWithValues(keyword, sort);

        return defs.stream()
                .map(this::toDto)
                .toList();
    }

    private ClothesAttributeDefDto toDto(ClothesAttributeDef def) {
        List<String> selectableValues = def.getValues().stream()
                .map(ClothesAttributeDefValue::getValue)
                .toList();

        return new ClothesAttributeDefDto(
                def.getId(),
                def.getName(),
                selectableValues,
                def.getCreatedAt()
        );
    }
    private String normalizeName(String raw) {
        return raw.trim();
    }

    private List<String> normalizeSelectableValues(List<String> raw) {

        List<String> values = raw.stream()
                .map(v -> v == null ? "" : v.trim())
                .filter(v -> !v.isBlank())
                .toList();

        if (values.isEmpty()) {
            throw new IllegalArgumentException("selectableValues는 공백만으로 구성될 수 없습니다.");
        }

        return values;
    }

    private Sort toSort(String sortBy, String sortDirection) {
        String property = resolveSortProperty(sortBy);
        Sort.Direction direction = resolveSortDirection(sortDirection);
        return Sort.by(direction, property);
    }

    private String resolveSortProperty(String sortBy) {
        if (sortBy == null) {
            throw new IllegalArgumentException("sortBy는 필수입니다.");
        }

        return switch (sortBy) {
            case "createdAt", "name" -> sortBy;
            default -> throw new IllegalArgumentException("sortBy는 createdAt 또는 name만 가능합니다.");
        };
    }

    private Sort.Direction resolveSortDirection(String sortDirection) {
        if (sortDirection == null) {
            throw new IllegalArgumentException("sortDirection은 필수입니다.");
        }

        return switch (sortDirection) {
            case "ASCENDING" -> Sort.Direction.ASC;
            case "DESCENDING" -> Sort.Direction.DESC;
            default -> throw new IllegalArgumentException("sortDirection은 ASCENDING 또는 DESCENDING만 가능합니다.");
        };
    }

    private String normalizeKeywordLike(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}