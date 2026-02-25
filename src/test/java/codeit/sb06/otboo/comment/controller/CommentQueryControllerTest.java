package codeit.sb06.otboo.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.comment.dto.AuthorDto;
import codeit.sb06.otboo.comment.dto.CommentDto;
import codeit.sb06.otboo.comment.dto.CommentDtoCursorResponse;
import codeit.sb06.otboo.comment.service.CommentService;
import codeit.sb06.otboo.exception.comment.CommentListReadFailException;
import codeit.sb06.otboo.security.jwt.JwtAuthenticationFilter;
import codeit.sb06.otboo.security.jwt.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CommentQueryControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  RoleHierarchy roleHierarchy;

  @MockitoBean
  JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  JwtAuthenticationFilter jwtAuthenticationFilter;


  @MockitoBean
  CommentService commentService;

  private final UUID feedId = UUID.randomUUID();
  private final UUID authorId = UUID.randomUUID();

  // 피드 댓글 조회 성공
  @Test
  void getCommentList_success_200response() throws Exception {

    //given

    // limit은 프론트에서 던져주는것 같더라구요 20으로 그래서 테스트상 2로 계속 두고있습니다.
    int limit = 2;

    LocalDateTime createdAt = LocalDateTime.now();

//    CommentDto c1 = new CommentDto(UUID.randomUUID(), createdAt.minusMinutes(3),feedId, new AuthorDto(authorId, "test",null),"테스트 댓글 1");
    CommentDto c2 = new CommentDto(UUID.randomUUID(), createdAt.minusMinutes(2),feedId, new AuthorDto(authorId, "test",null),"테스트 댓글 2");
    CommentDto c3 = new CommentDto(UUID.randomUUID(), createdAt.minusMinutes(1),feedId, new AuthorDto(authorId, "test",null),"테스트 댓글 3");

    CommentDtoCursorResponse response = new CommentDtoCursorResponse(
        List.of(c3,c2),
        c2.createdAt().toString(),
        c2.id(),
        true,
        3L,
        "createdAt",
        "DESCENDING"
    );

    when(commentService.getComments(eq(feedId),any(),any(),eq(limit)))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/api/feeds/{feedId}/comments", feedId)
            .param("limit", String.valueOf(limit)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].content").value("테스트 댓글 3"))
        .andExpect(jsonPath("$.data[1].content").value("테스트 댓글 2"))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").value(c2.createdAt().toString()))
        .andExpect(jsonPath("$.nextIdAfter").value(c2.id().toString()));
  }

  // 피드 댓글 조회 실패
  // 요청은 정상이나, 로직에서 실패
  @Test
  void getCommentList_fail_400response() throws Exception {

    //given
    int limit = 2;

    when(commentService.getComments(eq(feedId), any(), any(), eq(limit)))
        .thenThrow(new CommentListReadFailException());

    mockMvc.perform(get("/api/feeds/{feedId}/comments", feedId)
            .param("limit", String.valueOf(limit)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.exceptionName")
            .value("CommentListReadFailException"))
        .andExpect(jsonPath("$.message")
            .value("댓글 목록 조회 실패"))
        .andExpect(jsonPath("$.details").exists());
  }
}
