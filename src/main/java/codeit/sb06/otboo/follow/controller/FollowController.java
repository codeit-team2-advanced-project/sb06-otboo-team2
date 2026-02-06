package codeit.sb06.otboo.follow.controller;

import codeit.sb06.otboo.follow.dto.FollowCreateRequest;
import codeit.sb06.otboo.follow.dto.FollowDto;
import codeit.sb06.otboo.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Follow", description = "팔로우 API")
public class FollowController {

  private final FollowService followService;

  @Operation(summary = "팔로우 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "팔로우 생성 성공"),
      @ApiResponse(responseCode = "400", description = "팔로우 생성 실패")
  })
  @PostMapping("/follows")
  public ResponseEntity<FollowDto> follow(
      @RequestBody FollowCreateRequest followCreateRequest) {

    FollowDto response = followService.createFollow(followCreateRequest);
    return ResponseEntity.status(201)
        .body(response);
  }
}
