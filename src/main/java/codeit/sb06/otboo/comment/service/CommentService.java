package codeit.sb06.otboo.comment.service;

import codeit.sb06.otboo.comment.dto.CommentCreateRequest;
import codeit.sb06.otboo.comment.dto.CommentDto;
import java.util.UUID;

public interface CommentService {

  CommentDto createComment(UUID feedId, CommentCreateRequest commentCreateRequest);
}