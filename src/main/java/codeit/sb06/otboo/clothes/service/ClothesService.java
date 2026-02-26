package codeit.sb06.otboo.clothes.service;


import codeit.sb06.otboo.clothes.dto.*;
import codeit.sb06.otboo.clothes.entity.*;
import codeit.sb06.otboo.clothes.repository.ClothesAttributeDefRepository;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.clothes.*;
import codeit.sb06.otboo.exception.storage.StorageUploadFailedException;
import codeit.sb06.otboo.profile.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private final S3StorageService s3StorageService;


    public ClothesDto create(UUID currentUserId, ClothesCreateRequest request, MultipartFile image) {
        UUID ownerIdFromRequest;
        try {
            ownerIdFromRequest = UUID.fromString(request.ownerId().trim());
        } catch (Exception e) {
            throw new ClothesBadRequestException("ownerId 형식이 올바르지 않습니다.", e);
        }

        if (!currentUserId.equals(ownerIdFromRequest)) {
            throw new AccessDeniedException("ownerId mismatch");
        }

        UUID ownerId = currentUserId;

        String name = request.name().trim();

        ClothesType type;
        try {
            type = ClothesType.valueOf(request.type().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidClothesTypeException(request.type(), e);
        }

        String imageKey = null;
        if (image != null && !image.isEmpty()) {
            imageKey = String.valueOf(UUID.randomUUID());
            try {
                s3StorageService.putObject(imageKey, image.getBytes());
            } catch (IOException e) {
                throw new StorageUploadFailedException(imageKey, "IO_ERROR", e);
            }
        }

        Clothes clothes = new Clothes(ownerId, name, imageKey, type);

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

        return toDtoWithPresignedUrl(saved);
    }

    public ClothesDto update(UUID clothesId,UUID currentUserId, ClothesUpdateRequest request, MultipartFile image) {

        Clothes clothes = clothesRepository.findWithAttributesById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(clothesId));

        if (!clothes.getOwnerId().equals(currentUserId)) {
            throw new ClothesNotFoundException(clothesId);
        }

        String name = request.name().trim();
        clothes.changeName(name);

        ClothesType type;
        try {
            type = ClothesType.valueOf(request.type().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidClothesTypeException(request.type(), e);
        }
        clothes.changeType(type);

        String oldKey = clothes.getImageUrl();
        String newKey = null;

        if (image != null && !image.isEmpty()) {
            newKey = String.valueOf(UUID.randomUUID());
            try {
                s3StorageService.putObject(newKey, image.getBytes());
            } catch (IOException e) {
                throw new StorageUploadFailedException(newKey, "IO_ERROR", e);
            }
            clothes.changeImageUrl(newKey);
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

        try {
            Clothes saved = clothesRepository.save(clothes);

            if (newKey != null && oldKey != null && !oldKey.isBlank() && !oldKey.equals(newKey)) {
                s3StorageService.deleteObject(oldKey);
            }

            return toDtoWithPresignedUrl(saved);

        } catch (RuntimeException e) {
            if (newKey != null) {
                try {
                    s3StorageService.deleteObject(newKey);
                } catch (RuntimeException ignore) {

                }
            }
            throw e;
        }
    }

    public void delete(UUID clothesId, UUID ownerId) {
        Clothes clothes = clothesRepository.findByIdAndOwnerId(clothesId, ownerId)
                .orElseThrow(() -> new ClothesNotFoundException(clothesId));

        String key = clothes.getImageUrl();

        clothesRepository.delete(clothes);

        if (key != null && !key.isBlank()) {
            s3StorageService.deleteObject(key);
        }
    }

    @Transactional(readOnly = true)
    public ClothesDtoCursorResponse getList(String cursor,
                                            UUID idAfter,
                                            int limit,
                                            String typeEqual,
                                            UUID ownerId) {

        if (limit <= 0) throw new ClothesBadRequestException("limit은 1 이상이어야 합니다.");

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
            try {
                cursorCreatedAt = LocalDateTime.parse(cursor.trim());
            } catch (Exception e) {
                throw new ClothesBadRequestException("cursor 형식이 올바르지 않습니다.", e);
            }
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
                .map(this::toDtoWithPresignedUrl)
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

    private ClothesDto toDtoWithPresignedUrl(Clothes clothes) {
        String key = clothes.getImageUrl();
        String presignedUrl = null;

        if (key != null && !key.isBlank()) {
            presignedUrl = s3StorageService.getPresignedUrl(key);
        }

        return ClothesDto.from(clothes, presignedUrl);
    }
}