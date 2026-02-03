package codeit.sb06.otboo.clothes.service;


import codeit.sb06.otboo.clothes.dto.ClothesAttributeDto;
import codeit.sb06.otboo.clothes.dto.ClothesCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesDto;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesAttribute;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import codeit.sb06.otboo.clothes.entity.ClothesType;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.clothes.ClothesAttributeDefNotFoundException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesAttributeValueException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClothesService {

    private final ClothesRepository clothesRepository;
    private final ClothesAttributeDefRepository clothesAttributeDefRepository;

    // todo : s3 적용후 코드 가독성과 책임 분리를 위해 리팩터링 예정
    public ClothesDto create(ClothesCreateRequest request, MultipartFile image) {

        UUID ownerId = UUID.fromString(request.ownerId().trim());
        String name = request.name().trim();

        ClothesType type;
        try {
            type = ClothesType.valueOf(request.type().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidClothesTypeException(request.type(), e);
        }

        // todo : s3 적용후 imageUrl 로직 추가 예정
        String imageUrl = null;

        Clothes clothes = new Clothes(ownerId, name, imageUrl, type);

        List<UUID> defIds = request.attributes().stream()
                .map(a -> UUID.fromString(a.definitionId().trim()))
                .distinct()
                .toList();

        Map<UUID, ClothesAttributeDef> defMap = clothesAttributeDefRepository.findAllById(defIds).stream()
                .collect(Collectors.toMap(ClothesAttributeDef::getId, Function.identity()));

        for (ClothesAttributeDto a : request.attributes()) {
            UUID defId = UUID.fromString(a.definitionId().trim());
            ClothesAttributeDef def = defMap.get(defId);
            if (def == null) {
                throw new ClothesAttributeDefNotFoundException(defId);
            }

            String value = a.value().trim();

            List<String> selectable = def.getSelectableValues();
            if (selectable != null && !selectable.isEmpty() && !selectable.contains(value)) {
                throw new InvalidClothesAttributeValueException(defId, value);
            }

            new ClothesAttribute(clothes, def, value);
        }

        Clothes saved = clothesRepository.save(clothes);

        return ClothesDto.from(saved);
    }
}
