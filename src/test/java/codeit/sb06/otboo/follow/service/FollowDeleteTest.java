package codeit.sb06.otboo.follow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.follow.FollowCancelFailException;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FollowDeleteTest {

  @InjectMocks
  private BasicFollowService basicFollowService;

  @Mock
  private FollowRepository followRepository;

  @Test
  void deleteFollow_success() {

    //given
    UUID followId = UUID.randomUUID();

    when(followRepository.existsById(followId)).thenReturn(true);

    doNothing().when(followRepository).deleteById(followId);

    //when
    basicFollowService.deleteFollow(followId);

    //then
    verify(followRepository).deleteById(followId);
  }

  @Test
  void deleteFollow_repositoryThrows_fail() {

    // given
    UUID followId = UUID.randomUUID();

    when(followRepository.existsById(followId)).thenReturn(false);

    // when
    FollowCancelFailException exception  = assertThrows(
        FollowCancelFailException.class,
        () -> basicFollowService.deleteFollow(followId)
    );

    //then
    assertEquals("팔로우 취소 실패", exception.getMessage());

    verify(followRepository,never()).deleteById(any());

  }
}
