package codeit.sb06.otboo.clothes.service;


import codeit.sb06.otboo.clothes.dto.*;
import codeit.sb06.otboo.clothes.entity.*;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.clothes.ClothesAttributeDefNotFoundException;
import codeit.sb06.otboo.exception.clothes.ClothesNotFoundException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesAttributeValueException;
import codeit.sb06.otboo.exception.clothes.InvalidClothesTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        List<ClothesAttributeDto> attrs =
                (request.attributes() == null) ? List.of() : request.attributes();

        if (!attrs.isEmpty()) {

            List<UUID> defIds = attrs.stream()
                    .map(a -> UUID.fromString(a.definitionId().trim()))
                    .distinct()
                    .toList();

            Map<UUID, ClothesAttributeDef> defMap = clothesAttributeDefRepository.findAllById(defIds).stream()
                    .collect(Collectors.toMap(ClothesAttributeDef::getId, Function.identity()));

            for (ClothesAttributeDto a : attrs) {
                UUID defId = UUID.fromString(a.definitionId().trim());
                ClothesAttributeDef def = defMap.get(defId);
                if (def == null) {
                    throw new ClothesAttributeDefNotFoundException(defId);
                }

                String value = a.value().trim();

                List<String> selectable = def.getValues().stream()
                        .map(ClothesAttributeDefValue::getValue)
                        .toList();

                if (!selectable.isEmpty() && !selectable.contains(value)) {
                    throw new InvalidClothesAttributeValueException(defId, value);
                }

                new ClothesAttribute(clothes, def, value);
            }
        }

        Clothes saved = clothesRepository.save(clothes);

        return ClothesDto.from(saved);
    }

    public ClothesDto update(UUID clothesId, ClothesUpdateRequest request, MultipartFile image) {

        Clothes clothes = clothesRepository.findWithAttributesById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(clothesId));

        String name = request.name().trim();
        clothes.changeName(name);

        ClothesType type;
        try {
            type = ClothesType.valueOf(request.type().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidClothesTypeException(request.type(), e);
        }
        clothes.changeType(type);

        if (image != null && !image.isEmpty()) {
            String imageUrl = null;
            clothes.changeImageUrl(imageUrl);
        }

        List<ClothesAttributeDto> attrs = request.attributes();
        if (attrs != null) {

            clothes.replaceAttributes(List.of());

            if (!attrs.isEmpty()) {

                List<UUID> defIds = attrs.stream()
                        .map(a -> UUID.fromString(a.definitionId().trim()))
                        .distinct()
                        .toList();

                Map<UUID, ClothesAttributeDef> defMap =
                        clothesAttributeDefRepository.findAllByIdInWithValues(defIds).stream()
                                .collect(Collectors.toMap(
                                        ClothesAttributeDef::getId,
                                        Function.identity()
                                ));

                for (ClothesAttributeDto a : attrs) {
                    UUID defId = UUID.fromString(a.definitionId().trim());
                    ClothesAttributeDef def = defMap.get(defId);
                    if (def == null) {
                        throw new ClothesAttributeDefNotFoundException(defId);
                    }

                    String value = a.value().trim();

                    List<String> selectable = def.getValues().stream()
                            .map(ClothesAttributeDefValue::getValue)
                            .toList();

                    if (!selectable.isEmpty() && !selectable.contains(value)) {
                        throw new InvalidClothesAttributeValueException(defId, value);
                    }

                    new ClothesAttribute(clothes, def, value);
                }
            }
        }

        Clothes saved = clothesRepository.save(clothes);
        return ClothesDto.from(saved);
    }

    public void delete(UUID clothesId, UUID ownerId) {
        Clothes clothes = clothesRepository.findByIdAndOwnerId(clothesId, ownerId)
                .orElseThrow(() -> new ClothesNotFoundException(clothesId));

        clothesRepository.delete(clothes);
    }

    @Transactional(readOnly = true)
    public ClothesDtoCursorResponse getList(String cursor,
                                            UUID idAfter,
                                            int limit,
                                            String typeEqual,
                                            UUID ownerId) {

        if (limit <= 0) throw new IllegalArgumentException("limit은 1 이상이어야 합니다.");

        ClothesType type = null;
        if (typeEqual != null && !typeEqual.isBlank()) {
            try {
                type = ClothesType.valueOf(typeEqual.trim());
            } catch (IllegalArgumentException e) {
                throw new InvalidClothesTypeException(typeEqual, e);
            }
        }

        LocalDateTime cursorCreatedAt = null;
        if (cursor != null && !cursor.isBlank()) {
            cursorCreatedAt = LocalDateTime.parse(cursor.trim()); // ISO_LOCAL_DATE_TIME 기준
        }

        List<UUID> ids = clothesRepository.findIdsByCursor(
                ownerId, type, cursorCreatedAt, idAfter, limit + 1
        );

        boolean hasNext = ids.size() > limit;
        if (hasNext) {
            ids = ids.subList(0, limit);
        }

        List<Clothes> clothesList = clothesRepository.findWithAllByIds(ids);

        List<ClothesDto> data = clothesList.stream()
                .map(ClothesDto::from)
                .toList();

        String nextCursor = null;
        UUID nextIdAfter = null;
        if (!clothesList.isEmpty()) {
            Clothes last = clothesList.get(clothesList.size() - 1);
            nextCursor = last.getCreatedAt().toString();
            nextIdAfter = last.getId();
        }

        long totalCount = clothesRepository.countByFilter(ownerId, type);

        return new ClothesDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                "createdAt",
                "DESCENDING"
        );
    }
}