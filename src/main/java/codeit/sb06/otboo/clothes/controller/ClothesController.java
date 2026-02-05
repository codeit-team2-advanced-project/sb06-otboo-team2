package codeit.sb06.otboo.clothes.controller;

import codeit.sb06.otboo.clothes.dto.ClothesCreateRequest;
import codeit.sb06.otboo.clothes.dto.ClothesDto;
import codeit.sb06.otboo.clothes.service.ClothesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes")
public class ClothesController {

    private final ClothesService clothesService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClothesDto> create(
            @RequestPart("request") @Valid ClothesCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ClothesDto created = clothesService.create(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
