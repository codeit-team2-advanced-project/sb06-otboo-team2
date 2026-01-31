package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefDto;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefUpdateRequest;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClothesAttributeDefService {
    private final ClothesAttributeDefRepository repository;
    private final ClothesAttributeRepository clothesAttributeRepository;

    //속성 정의 등록
    public ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request) {
        String name = normalizeName(request.name());
        List<String> selectableValues = normalizeSelectableValues(request.selectableValues());

        if (repository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 의상 속성 정의입니다: " + name);
        }

        try {
            ClothesAttributeDef saved = repository.save(new ClothesAttributeDef(name, selectableValues));
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 존재하는 의상 속성 정의입니다: " + name);
        }
    }

    //속성 정의 수정
    public ClothesAttributeDefDto update(UUID id, ClothesAttributeDefUpdateRequest request) {
        String name = normalizeName(request.name());
        List<String> selectableValues = normalizeSelectableValues(request.selectableValues());

        ClothesAttributeDef def = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("의상 속성 정의를 찾을 수 없습니다: " + id));

        if (repository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException("이미 존재하는 의상 속성 정의입니다: " + name);
        }

        def.changeName(name);
        def.replaceSelectableValues(selectableValues);

        return toDto(def);
    }

    //속성 정의 삭제
    public void delete(UUID id) {
        ClothesAttributeDef def = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의상 속성 정의를 찾을 수 없습니다: " + id));

        // 옷의 속성 먼저 제거 후 속성 정의 제거
        clothesAttributeRepository.deleteByDefinitionId(id);

        repository.delete(def);
    }

    private ClothesAttributeDefDto toDto(ClothesAttributeDef def) {
        return new ClothesAttributeDefDto(
                def.getId(),
                def.getName(),
                def.getSelectableValues(),
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
}