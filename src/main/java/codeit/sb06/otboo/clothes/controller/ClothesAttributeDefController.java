package codeit.sb06.otboo.clothes.controller;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefDto;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefUpdateRequest;
import codeit.sb06.otboo.clothes.service.ClothesAttributeDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes/attribute-defs")
public class ClothesAttributeDefController {

    private final ClothesAttributeDefService service;

    // 속성 정의 등록
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClothesAttributeDefDto> create(
            @RequestBody ClothesAttributeDefCreateRequest request
    ) {
        ClothesAttributeDefDto response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 속성 정의 수정
    @PatchMapping("/{definitionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClothesAttributeDefDto> update(
            @PathVariable UUID definitionId,
            @RequestBody ClothesAttributeDefUpdateRequest request
    ) {
        ClothesAttributeDefDto response = service.update(definitionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 속성 정의 삭제
    @DeleteMapping("/{definitionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID definitionId) {
        service.delete(definitionId);
        return ResponseEntity.noContent().build();
    }
}
