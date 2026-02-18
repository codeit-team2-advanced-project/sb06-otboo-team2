package codeit.sb06.otboo.clothes.controller;

import codeit.sb06.otboo.clothes.dto.ClothesCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesDto;
import codeit.sb06.otboo.clothes.dto.ClothesDtoCursorResponse;
import codeit.sb06.otboo.clothes.dto.ClothesUpdateRequest;
import codeit.sb06.otboo.clothes.service.ClothesService;
import codeit.sb06.otboo.security.resolver.CurrentUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes")
public class ClothesController {

    private final ClothesService clothesService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClothesDto> create(
            @CurrentUserId UUID currentUserId,
            @RequestPart("request") @Valid ClothesCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ClothesDto created = clothesService.create(currentUserId, request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping(value = "/{clothesId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClothesDto> update(
            @PathVariable UUID clothesId,
            @CurrentUserId UUID currentUserId,
            @RequestPart("request") @Valid ClothesUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ClothesDto updated = clothesService.update(clothesId, currentUserId, request, image);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{clothesId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID clothesId,
            @CurrentUserId UUID userId
    ) {
        clothesService.delete(clothesId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ClothesDtoCursorResponse> getList(
            @CurrentUserId UUID currentUserId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam int limit,
            @RequestParam(required = false) String typeEqual,
            @RequestParam UUID ownerId
    ) {
        if (!currentUserId.equals(ownerId)) {
            throw new AccessDeniedException("ownerId mismatch");
        }

        ClothesDtoCursorResponse res = clothesService.getList(cursor, idAfter, limit, typeEqual, ownerId);
        return ResponseEntity.ok(res);
    }
}
