package codeit.sb06.otboo.follow.repository;

import static org.assertj.core.api.Assertions.assertThat;

import codeit.sb06.otboo.config.JpaAuditingConfig;
import codeit.sb06.otboo.config.QueryDslConfig;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.entity.FollowDirection;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
public class FollowRepositoryTest {

  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  private User targetUser;
  private User me;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    me = userRepository.save(
        User.from(
            new UserCreateRequest("나", "me@test.com", "pw")
        )
    );

    targetUser = userRepository.save(
        User.from(
            new UserCreateRequest("상대", "target@test.com", "pw")
        )
    );

    for (int i = 0; i < 25; i++) {
      User follower = userRepository.save(
          User.from(
              new UserCreateRequest(
                  i < 5 ? "an_user_" + i : "user_" + i,
                  "user" + i + "@test.com",
                  "pw"
              )
          )
      );
      Follow follow = followRepository.save(
          Follow.of(follower, targetUser)
      );

      Field createdAtField = Follow.class.getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(follow, LocalDateTime.now().withNano(0).minusMinutes(i)
      );
      followRepository.save(follow);
    }

    for (int i = 0; i < 25; i++) {
      User followee = userRepository.save(
          User.from(
              new UserCreateRequest(
                  "followee_" + i,
                  "followee" + i + "@test.com",
                  "pw"
              )
          )
      );

      Follow follow = followRepository.save(
          Follow.of(me, followee)
      );

      Field createdAtField = Follow.class.getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(
          follow,
          LocalDateTime.now().withNano(0).minusMinutes(i)
      );
      followRepository.save(follow);
    }
  }

  @Test
  void findFollowers_firstPage() {

    // when
    List<Follow> result =
        followRepository.findByCursor(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            null,
            null,
            20,
            null
        );

    Long count =
        followRepository.countByCondition(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            null
        );

    // then
    assertThat(result).hasSize(20);
    assertThat(count).isEqualTo(25);

    assertThat(result)
        .allMatch(f -> f.getFollowee().getId().equals(targetUser.getId()));
  }

  @Test
  void findFollowers_nextPage() {
    // given
    List<Follow> firstPage =
        followRepository.findByCursor(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            null,
            null,
            20,
            null
        );

    assertThat(firstPage).hasSize(20);

    Follow lastFollow = firstPage.get(firstPage.size() - 1);

    // when
    List<Follow> secondPage =
        followRepository.findByCursor(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            lastFollow.getCreatedAt(),
            lastFollow.getId(),
            20,
            null
        );

    // then
    assertThat(secondPage).hasSize(5);

    assertThat(secondPage)
        .allMatch(f -> f.getFollowee().getId().equals(targetUser.getId()));

    assertThat(secondPage.get(0).getCreatedAt())
        .isBefore(lastFollow.getCreatedAt());
  }

  @Test
  void findFollowers_searchName() {
    List<Follow> result =
        followRepository.findByCursor(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            null,
            null,
            20,
            "an"
        );
    Long count =
        followRepository.countByCondition(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            "an"
        );

    // then
    assertThat(result).hasSize(5);
    assertThat(count).isEqualTo(5);
    assertThat(result)
        .allMatch(f -> f.getFollower().getName().contains("an"));
  }


  @Test
  void findFollowings_firstPage() {
    // when
    List<Follow> result =
        followRepository.findByCursor(
            FollowDirection.FOLLOWING,
            me.getId(),
            null,
            null,
            20,
            null
        );

    Long count =
        followRepository.countByCondition(
            FollowDirection.FOLLOWING,
            me.getId(),
            null
        );

    // then
    assertThat(result).hasSize(20);
    assertThat(count).isEqualTo(25);

    assertThat(result)
        .allMatch(f -> f.getFollower().getId().equals(me.getId()));
  }

  @Test
  void findFollowings_nextPage() {
    //given
    List<Follow> firstPage =
        followRepository.findByCursor(
            FollowDirection.FOLLOWING,
            me.getId(),
            null,
            null,
            20,
            null
        );

    assertThat(firstPage).hasSize(20);

    Follow lastFollow = firstPage.get(firstPage.size() - 1);

    //when
    List<Follow> secondPage =
        followRepository.findByCursor(
            FollowDirection.FOLLOWING,
            me.getId(),
            lastFollow.getCreatedAt(),
            lastFollow.getId(),
            20,
            null
        );

    // then
    assertThat(secondPage).hasSize(5);

    assertThat(secondPage)
        .allMatch(f -> f.getFollower().getId().equals(me.getId()));

    assertThat(secondPage.get(0).getCreatedAt())
        .isBeforeOrEqualTo(lastFollow.getCreatedAt());
  }


  @Test
  void findFollowings_searchName() {
    // given
    followRepository.save(
        Follow.of(me, targetUser)
    );

    // when
    List<Follow> result =
        followRepository.findByCursor(
            FollowDirection.FOLLOWING,
            me.getId(),
            null,
            null,
            20,
            "상"
        );

    Long count =
        followRepository.countByCondition(
            FollowDirection.FOLLOWING,
            me.getId(),
            "상"
        );

    // then
    assertThat(result).hasSize(1);
    assertThat(count).isEqualTo(1);

    assertThat(result.get(0).getFollowee().getName())
        .contains("상");
  }


  @Test
  void findFollowers_searchName_notFound() {
    // when
    List<Follow> result =
        followRepository.findByCursor(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            null,
            null,
            20,
            "없는이름"
        );

    Long count =
        followRepository.countByCondition(
            FollowDirection.FOLLOWER,
            targetUser.getId(),
            "없는이름"
        );

    // then
    assertThat(result).isEmpty();
    assertThat(count).isZero();
  }

}
