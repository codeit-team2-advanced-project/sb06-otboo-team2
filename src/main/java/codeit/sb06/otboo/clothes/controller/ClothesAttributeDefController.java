package codeit.sb06.otboo.clothes.controller;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefDto;
import codeit.sb06.otboo.clothes.dto.ClothesAttributeDefUpdateRequest;
import codeit.sb06.otboo.clothes.service.ClothesAttributeDefService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes/attribute-defs")
public class ClothesAttributeDefController {

    private final ClothesAttributeDefService service;

    // 속성 정의 등록
    @PostMapping
    public ResponseEntity<ClothesAttributeDefDto> create(
            @Valid @RequestBody ClothesAttributeDefCreateRequest request
    ) {
        ClothesAttributeDefDto response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 속성 정의 수정
    @PatchMapping("/{definitionId}")
    public ResponseEntity<ClothesAttributeDefDto> update(
            @PathVariable UUID definitionId,
            @Valid @RequestBody ClothesAttributeDefUpdateRequest request
    ) {
        ClothesAttributeDefDto response = service.update(definitionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 속성 정의 삭제
    @DeleteMapping("/{definitionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID definitionId) {
        service.delete(definitionId);
        return ResponseEntity.noContent().build();
    }

    //속성 정의 목록 조회
    @GetMapping
    public ResponseEntity<List<ClothesAttributeDefDto>> list(
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) String keywordLike
    ) {
        return ResponseEntity.ok(
                service.getList(sortBy, sortDirection, keywordLike)
        );
    }
}
