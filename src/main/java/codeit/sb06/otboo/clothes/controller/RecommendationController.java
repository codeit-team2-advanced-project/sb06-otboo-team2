package codeit.sb06.otboo.clothes.controller;

import codeit.sb06.otboo.clothes.dto.RecommendationDto;
import codeit.sb06.otboo.clothes.service.RecommendationService;
import codeit.sb06.otboo.security.resolver.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendationDto> getRecommendation(
            @RequestParam UUID weatherId,
            @CurrentUserId UUID userId
    ) {
        RecommendationDto dto =
                recommendationService.getRecommendation(weatherId, userId);

        return ResponseEntity.ok(dto);
    }

}
