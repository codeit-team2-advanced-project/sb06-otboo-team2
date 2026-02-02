package codeit.sb06.otboo.comment.controller;

import codeit.sb06.otboo.comment.dto.CommentCreateRequest;
import codeit.sb06.otboo.comment.dto.CommentDto;
import codeit.sb06.otboo.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds/{feedId}/comments")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController{

  private final CommentService commentService;

  @Operation(summary = "피드 댓글 등록")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "피드 댓글 등록 성공"),
      @ApiResponse(responseCode = "400", description = "피드 댓글 등록 실패")
  })

  @PostMapping
  public ResponseEntity<CommentDto> createComment(
      @Parameter(description = "피드 Id", required = true)
      @PathVariable UUID feedId,
      @RequestBody CommentCreateRequest commentCreateRequest
  ){
    CommentDto response = commentService.createComment(feedId, commentCreateRequest);
    return ResponseEntity.ok(response);
  }

}