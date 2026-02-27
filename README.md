# sb06-otboo-team2 
- [GitHub Issue](https://github.com/codeit-team2-advanced-project/sb06-otboo-team2/issues)
- [Github Project](https://github.com/orgs/codeit-team2-advanced-project/projects/2/views/3)

## 팀원 구성
- 이호건 ([Github 링크](https://github.com/HOGUN00))
- 김태헌 ([Github 링크](https://github.com/Taehun88))
- 서경원 ([Github 링크](https://github.com/SeoGyeongWon))
- 이현욱
- 조동현

---

## 프로젝트 소개

- 옷장을 부탁해: 날씨, 취향을 고려해 사용자가 보유한 의상 조합을 추천해주고, OOTD 피드, 팔로우 등의 소셜 기능을 갖춘 서비스
- 프로젝트 기간: 2026.01.22 ~ 2026.02.27

---

## 기술 스택

- Backend: Java 17, Spring Boot 3.5.10, Spring Security, Spring WebFlux, Spring Batch, WebSocket
- Database: PostgreSQL, Redis (List & Stream)
- Data Access: Spring Data JPA, QueryDSL
- Cloud: AWS S3, Spring Cloud OpenFeign
- Reliability: Resilience4j, ShedLock
- Test: Easy Random
- API Docs: Swagger (Springdoc)
- 공통 Tool: Git & GitHub, Discord

---

<details>
<summary>📌 ERD 상세 보기</summary>
<img width="2730" height="1172" alt="otboo (1)" src="https://github.com/user-attachments/assets/d9c6354a-85d3-459a-a25e-6297ca057a08" />
</details>

---

<details>
<summary>🤝 팀 규칙 및 협업 컨벤션</summary>
   
| 분류 | 내용 |
| :--- | :--- |
| **네이밍 컨벤션** | camelCase (변수, 함수), PascalCase (클래스), kebab-case (파일) |
| **커밋 컨벤션** | feat, fix, refactor, test, chore 등 |
| **커밋 단위** | 작업별로 커밋 |
| **브랜치 전략** | GIT-FLOW (feature branches, develop, main) |
| **PR 규칙** | 2명 이상 Approve 시 Merge (컨벤션, 이슈) |
| **PR 컨벤션** | feat, fix, refactor, merge |
| **PR 단위** | 이슈별로 PR |
| **디벨롭 테스트 커버리지** | 80% (DTO, config 제외) |
| **소통** | 출석하지 못할떄는 전날에 말해준다, PR 올렸을 때 디스코드에 알려주기 |

</details>

---

<details>
<summary>📅 프로젝트 일정 요약</summary>

## 🗓️ 프로젝트 일정 요약
| 항목 | 기간 | 내용 |
| :--- | :--- | :--- |
| 기획 및 요구사항 정리 | 1/22 ~ 1/23 | 프로젝트 방향성 정리 |
| 1차 개발 스프린트 | 1/24 ~ 2/7 | 주요 기능 개발 |
| 2차 개발 스프린트 | 2/9 ~ 2/18 | 심화 개발 |
| 중간 발표 + 회고 | 2/19 | 기능 시연 + 피드백 수렴 |
| 3차 개발 스프린트 | 2/20 ~ 2/25 | 마무리 개발 |
| 최종 발표 + 회고 | 2/27 | 발표 자료 정리 및 시연 |

</details>

---

## R&R

| 팀원 | 주요 담당 |
| :--- | :--- |
| 이호건 | 알림, dm |
| 김태헌 |  |
| 서경원 |  |
| 이현욱 |  |
| 조동현 | 의상 |

---

## 팀원별 구현 기능 상세

<details>
<summary>이호건</summary>

### 1. 실시간 DM 및 알림 시스템 구축
* **통신 목적에 따른 프로토콜 이원화**: 양방향성이 중요한 DM에는 WebSocket을, 단방향성 알림에는 가벼운 SSE를 채택하여 서버 리소스 및 통신 효율 최적화
* **분산 환경 세션 불일치 해결**: 로드밸런싱 환경에서 발생하는 인메모리 세션(SseEmitter, WebSocketSession) 공유 문제를 메시지 브로커로 해결
* **연결 안정성 보장**: 주기적인 Ping 전송으로 연결 끊김을 방지하고, `lastEventId`를 활용해 재연결 시 미수신 메시지 보정

### 2. Redis Stream 기반 메시지 브로커 설계
* **Redis Stream 채택**: 인메모리 기반의 빠른 응답 속도와 Consumer Group, ACK, PEL(Pending Entries List) 기능을 활용해 메시지 유실 없는 아키텍처 설계
* **장애 전파 차단 (Resilience)**: `Resilience4j` 서킷 브레이커를 도입하여 레디스 장애 시 어플리케이션으로의 장애 전파 차단
* **메시지 복구 및 직렬화 최적화**: 미처리 메시지(PEL) 재전송 스케줄러를 구현하고, `ObjectMapper` 설정을 통해 JavaTime 직렬화 및 클래스 타입 매핑 오류 해결
* **메모리 파편화 관리**: 키별 메시지 개수 제한(Maxlen)과 `activedefrag` 설정을 통해 인메모리 데이터베이스 효율성 향상

### 3. Spring Batch 대용량 통계 및 스케줄링 최적화
* **카테시안 곱(Cartesian Product) 해결**: 1:N 연관관계 조인 시 발생하는 데이터 폭발 문제를 스칼라 서브쿼리와 DTO 프로젝션으로 해결하여 I/O 성능 개선
* **배치 안정성 확보**: `LazyInitializationException` 및 N+1 문제를 차단하고, `ShedLock`을 도입하여 다중 서버 환경에서 배치 중복 실행 방지

### 4. 코드 품질 관리
* **SonarQube 정적 분석**
   - 코드 스멜 및 보안 취약점을 자동 검증하여 기술적 부채 관리
   - GitHub Actions를 연동하여 PR 단위로 **테스트 커버리지 80%** 및 빌드 통과를 강제하여 코드 품질 상향 평준화
* **상호 보완적 코드 리뷰**: 기계적 검증은 SonarQube에 맡기고, 팀원 리뷰 시에는 도메인 로직의 정합성과 더 나은 설계 제안에 집중

</details>

---

<details>
<summary>김태헌</summary>

### 1. 로그인 기능 및 SecurityConfig 설정
* **로그인 기능 및 OAuth2기반 로그인 기능 개발**:기초적인 회원가입을 통한 로그인 기능 및 OAuth2 기반 소셜 로그인 기능 개발
* **OAuth2와 OIDC 단일 모듈화**: Kakao와 Google 로그인 방식이 달라 하나의 모듈을 기반으로 OAuth2와 OIDC에 맞춰서 대응할 수 있게 개발
* **Security 설정으로 권한 관리**: USER와 ADMIN일 때 사용 가능한 Method를 Security에서 관리하고, AOP에서 추가적으로 확인해서 사용 가능한 Method를 분리

### 2. Profile 기능 개발
* **S3 모듈 개발**: S3에 대한 모듈을 구현, 이후 S3를 사용하는 기능에 적용 가능하게 모듈화
* **Profile 에 대한 CRUD 구현**

### 3. AWS Infrastructure
* **ECS, ECR에 대한 CD Pipeline 구현**: main branch에 merge 될 때 동작하여 자동으로 배포되는 환경 구현
* **ECS Fargate 환경 구성**: 현재 단계에서는 빠른 배포 및 확인이 필요하다고 생각하여 Fargate 환경 배포를 선택

</details>

---

<details>
<summary>서경원</summary>

### 1. 댓글 생성 및 목록 조회
* **Comment에 대한 CRUD 구현**
* **댓글 목록 조회**를 위한 QueryDsl 기반 커서페이지 네이션 구현
  * 날짜별 최신순으로 댓글이 조회되도록 정렬 조건 추가 + 같은 시각일때 id 조합하여 구분

### 2. 팔로우 생성 취소 및 팔로워 팔로잉 목록 조회
* **Follow에 대한 CRUD 구현**
* **팔로워 팔로잉 리스트 조회**를 위한 QueryDsl 기반 커서페이지 네이션 구현
  * 날짜별 최신순으로 댓글이 조회되도록 정렬 조건 추가 + 같은 시각일때 id 조합하여 구분
* Follow 엔티티에 follower_id, followee_id로 유니크 제약조건을 추가하여 한 사용자가 다른 동일한 사용자에게 한 번만 팔로우 할수 있도록 설정
   
</details>

---

<details>
<summary>이현욱</summary>

</details>

---

<details>
<summary>조동현</summary>

- 의상, 의상 속성, 의상 추천 CRUD

</details>

---

## 파일 구조

<details>
<summary>📌 파일구조 상세 보기</summary>

```text
otboo/
├── gradle/
│   ├── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── codeit/
│   │   │       ├── sb06/
│   │   │           ├── otboo/
│   │   │               ├── clothes/
│   │   │               │   ├── controller/
│   │   │               │   │   ├── ClothesAttributeDefController.java
│   │   │               │   │   ├── ClothesController.java
│   │   │               │   │   └── RecommendationController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── ClothesAttributeDefCreateRequest.java
│   │   │               │   │   ├── ClothesAttributeDefDto.java
│   │   │               │   │   ├── ClothesAttributeDefUpdateRequest.java
│   │   │               │   │   ├── ClothesAttributeDto.java
│   │   │               │   │   ├── ClothesAttributeWithDefDto.java
│   │   │               │   │   ├── ClothesCreateRequest.java
│   │   │               │   │   ├── ClothesDto.java
│   │   │               │   │   ├── ClothesDtoCursorResponse.java
│   │   │               │   │   ├── ClothesUpdateRequest.java
│   │   │               │   │   ├── RecommendationDto.java
│   │   │               │   │   └── RecommendedClothesDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Clothes.java
│   │   │               │   │   ├── ClothesAttribute.java
│   │   │               │   │   ├── ClothesAttributeDef.java
│   │   │               │   │   ├── ClothesAttributeDefValue.java
│   │   │               │   │   └── ClothesType.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── ClothesAttributeDefRepository.java
│   │   │               │   │   ├── ClothesAttributeRepository.java
│   │   │               │   │   ├── ClothesQueryRepository.java
│   │   │               │   │   ├── ClothesQueryRepositoryImpl.java
│   │   │               │   │   └── ClothesRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── ClothesAttributeDefService.java
│   │   │               │       ├── ClothesService.java
│   │   │               │       └── RecommendationService.java
│   │   │               ├── comment/
│   │   │               │   ├── controller/
│   │   │               │   │   └── CommentController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── AuthorDto.java
│   │   │               │   │   ├── CommentCreateRequest.java
│   │   │               │   │   ├── CommentDto.java
│   │   │               │   │   └── CommentDtoCursorResponse.java
│   │   │               │   ├── entity/
│   │   │               │   │   └── Comment.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── CommentQueryRepository.java
│   │   │               │   │   ├── CommentQueryRepositoryImpl.java
│   │   │               │   │   └── CommentRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── BasicCommentService.java
│   │   │               │       └── CommentService.java
│   │   │               ├── common/
│   │   │               │   ├── scheduler/
│   │   │               │       └── AbstractStreamRecoveryScheduler.java
│   │   │               ├── config/
│   │   │               │   ├── AdminInitializer.java
│   │   │               │   ├── CustomFeignRetryer.java
│   │   │               │   ├── FeignClientConfig.java
│   │   │               │   ├── FeignErrorDecoder.java
│   │   │               │   ├── JpaAuditingConfig.java
│   │   │               │   ├── QueryDslConfig.java
│   │   │               │   ├── RedisConfig.java
│   │   │               │   ├── RedisStreamManager.java
│   │   │               │   ├── SchedulingConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── ShedLockConfig.java
│   │   │               │   └── WebMvcConfig.java
│   │   │               ├── exception/
│   │   │               │   ├── auth/
│   │   │               │   │   ├── AuthException.java
│   │   │               │   │   ├── ForbiddenException.java
│   │   │               │   │   ├── InvalidTokenException.java
│   │   │               │   │   └── InvalidUserDetailException.java
│   │   │               │   ├── clothes/
│   │   │               │   │   ├── ClothesAlreadyExistsException.java
│   │   │               │   │   ├── ClothesAttributeDefNotFoundException.java
│   │   │               │   │   ├── ClothesBadRequestException.java
│   │   │               │   │   ├── ClothesException.java
│   │   │               │   │   ├── ClothesForbiddenException.java
│   │   │               │   │   ├── ClothesImageUploadFailedException.java
│   │   │               │   │   ├── ClothesNotFoundException.java
│   │   │               │   │   ├── InvalidClothesAttributeValueException.java
│   │   │               │   │   └── InvalidClothesTypeException.java
│   │   │               │   ├── comment/
│   │   │               │   │   ├── CommentCreateFailException.java
│   │   │               │   │   ├── CommentException.java
│   │   │               │   │   └── CommentListReadFailException.java
│   │   │               │   ├── feed/
│   │   │               │   │   ├── FeedException.java
│   │   │               │   │   └── FeedNotFoundException.java
│   │   │               │   ├── follow/
│   │   │               │   │   ├── FollowCancelFailException.java
│   │   │               │   │   ├── FollowException.java
│   │   │               │   │   ├── FollowNotFoundException.java
│   │   │               │   │   └── SelfFollowDeniedException.java
│   │   │               │   ├── message/
│   │   │               │   │   ├── ChatRoomNotFoundException.java
│   │   │               │   │   ├── DirectMessageMappingException.java
│   │   │               │   │   └── MessageException.java
│   │   │               │   ├── notification/
│   │   │               │   │   ├── NotificationBatchException.java
│   │   │               │   │   ├── NotificationException.java
│   │   │               │   │   └── NotificationMappingException.java
│   │   │               │   ├── profile/
│   │   │               │   │   ├── ProfileException.java
│   │   │               │   │   ├── ProfileNotFoundException.java
│   │   │               │   │   ├── ProfileS3NotFound.java
│   │   │               │   │   └── S3UploadFailedException.java
│   │   │               │   ├── storage/
│   │   │               │   │   ├── StorageDeleteFailedException.java
│   │   │               │   │   ├── StorageException.java
│   │   │               │   │   └── StorageUploadFailedException.java
│   │   │               │   ├── user/
│   │   │               │   │   ├── LockedUserException.java
│   │   │               │   │   ├── MailSendException.java
│   │   │               │   │   ├── UserAlreadyExistException.java
│   │   │               │   │   ├── UserException.java
│   │   │               │   │   └── UserNotFoundException.java
│   │   │               │   ├── weather/
│   │   │               │   │   ├── WeatherException.java
│   │   │               │   │   └── WeatherNotFoundException.java
│   │   │               │   ├── ErrorResponse.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── RootException.java
│   │   │               ├── feed/
│   │   │               │   ├── controller/
│   │   │               │   │   └── FeedController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── FeedCreateRequest.java
│   │   │               │   │   ├── FeedDto.java
│   │   │               │   │   ├── FeedDtoCursorRequest.java
│   │   │               │   │   ├── FeedDtoCursorResponse.java
│   │   │               │   │   ├── FeedSortBy.java
│   │   │               │   │   ├── FeedSortDirection.java
│   │   │               │   │   ├── FeedUpdateRequest.java
│   │   │               │   │   └── OotdDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Feed.java
│   │   │               │   │   ├── FeedClothes.java
│   │   │               │   │   └── FeedLike.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── FeedLikeRepository.java
│   │   │               │   │   ├── FeedRepository.java
│   │   │               │   │   ├── FeedRepositoryCustom.java
│   │   │               │   │   └── FeedRepositoryImpl.java
│   │   │               │   ├── service/
│   │   │               │       └── FeedService.java
│   │   │               ├── follow/
│   │   │               │   ├── controller/
│   │   │               │   │   └── FollowController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── FollowCreateRequest.java
│   │   │               │   │   ├── FollowDto.java
│   │   │               │   │   ├── FollowListResponse.java
│   │   │               │   │   ├── FollowSummaryDto.java
│   │   │               │   │   ├── FolloweeDto.java
│   │   │               │   │   └── FollowerDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Follow.java
│   │   │               │   │   └── FollowDirection.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── FollowQueryRepository.java
│   │   │               │   │   ├── FollowQueryRepositoryImpl.java
│   │   │               │   │   └── FollowRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── BasicFollowService.java
│   │   │               │       └── FollowService.java
│   │   │               ├── message/
│   │   │               │   ├── config/
│   │   │               │   │   ├── WebSocketConfig.java
│   │   │               │   │   └── WebSocketPoolProperties.java
│   │   │               │   ├── controller/
│   │   │               │   │   ├── DirectMessageController.java
│   │   │               │   │   └── DirectMessageWebSocketController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── request/
│   │   │               │   │   │   └── DirectMessageCreateRequest.java
│   │   │               │   │   ├── response/
│   │   │               │   │   │   └── DirectMessageDtoCursorResponse.java
│   │   │               │   │   ├── DirectMessageCreatedRedisEvent.java
│   │   │               │   │   ├── DirectMessageDto.java
│   │   │               │   │   └── DirectMessageRedisDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── ChatMember.java
│   │   │               │   │   ├── ChatRoom.java
│   │   │               │   │   └── DirectMessage.java
│   │   │               │   ├── enums/
│   │   │               │   │   └── SortDirection.java
│   │   │               │   ├── interceptor/
│   │   │               │   │   └── WebSocketChannelInterceptor.java
│   │   │               │   ├── listener/
│   │   │               │   │   ├── DirectMessageEventListener.java
│   │   │               │   │   └── DirectMessageStreamListener.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── DirectMessageMapper.java
│   │   │               │   ├── publisher/
│   │   │               │   │   ├── DirectMessageEventPublisher.java
│   │   │               │   │   └── DirectMessageRedisPublisher.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── ChatMemberRepository.java
│   │   │               │   │   ├── ChatRoomRepository.java
│   │   │               │   │   └── DirectMessageRepository.java
│   │   │               │   ├── scheduler/
│   │   │               │   │   └── DmStreamRecoveryScheduler.java
│   │   │               │   ├── service/
│   │   │               │       ├── impl/
│   │   │               │       │   ├── ChatMemberServiceImpl.java
│   │   │               │       │   ├── ChatRoomServiceImpl.java
│   │   │               │       │   └── DirectMessageServiceImpl.java
│   │   │               │       ├── ChatMemberService.java
│   │   │               │       ├── ChatRoomService.java
│   │   │               │       └── DirectMessageService.java
│   │   │               ├── notification/
│   │   │               │   ├── batch/
│   │   │               │   │   ├── NotificationCleanupBatchConfig.java
│   │   │               │   │   └── UserActivityNotificationBatchConfig.java
│   │   │               │   ├── controller/
│   │   │               │   │   ├── NotificationController.java
│   │   │               │   │   └── SseController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── response/
│   │   │               │   │   │   └── NotificationDtoCursorResponse.java
│   │   │               │   │   ├── NotificationDto.java
│   │   │               │   │   ├── SseEvent.java
│   │   │               │   │   └── StatNotificationDTO.java
│   │   │               │   ├── entity/
│   │   │               │   │   └── Notification.java
│   │   │               │   ├── enums/
│   │   │               │   │   ├── NotificationLevel.java
│   │   │               │   │   └── SortDirection.java
│   │   │               │   ├── event/
│   │   │               │   │   ├── ClothesAttributeAddedEvent.java
│   │   │               │   │   ├── DirectMessageCreatedEvent.java
│   │   │               │   │   ├── FeedCommentedEvent.java
│   │   │               │   │   ├── FeedLikedEvent.java
│   │   │               │   │   ├── FollowedEvent.java
│   │   │               │   │   ├── FolloweeFeedPostedEvent.java
│   │   │               │   │   └── RoleUpdatedEvent.java
│   │   │               │   ├── listener/
│   │   │               │   │   ├── NotificationEventListener.java
│   │   │               │   │   └── NotificationStreamListener.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── NotificationMapper.java
│   │   │               │   ├── publisher/
│   │   │               │   │   ├── impl/
│   │   │               │   │   │   ├── NotificationEventPublisherImpl.java
│   │   │               │   │   │   └── RedisNotificationPublisherImpl.java
│   │   │               │   │   ├── NotificationEventPublisher.java
│   │   │               │   │   └── RedisNotificationPublisher.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── NotificationRepository.java
│   │   │               │   │   ├── SseEmitterRepository.java
│   │   │               │   │   └── SseEventCacheRepository.java
│   │   │               │   ├── scheduler/
│   │   │               │   │   ├── NotificationCleanUpBatchScheduler.java
│   │   │               │   │   ├── NotificationStreamRecoveryScheduler.java
│   │   │               │   │   ├── SseHeartbeatScheduler.java
│   │   │               │   │   └── UserActivityNotificationBatchScheduler.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── impl/
│   │   │               │   │   │   ├── NotificationCacheServiceImpl.java
│   │   │               │   │   │   ├── NotificationServiceImpl.java
│   │   │               │   │   │   └── SseServiceImpl.java
│   │   │               │   │   ├── NotificationCacheService.java
│   │   │               │   │   ├── NotificationService.java
│   │   │               │   │   └── SseService.java
│   │   │               │   ├── util/
│   │   │               │       └── SseEventIdGenerator.java
│   │   │               ├── profile/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── LocationDto.java
│   │   │               │   │   ├── ProfileDto.java
│   │   │               │   │   └── ProfileUpdateRequest.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Gender.java
│   │   │               │   │   ├── Location.java
│   │   │               │   │   └── Profile.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── LocationRepository.java
│   │   │               │   │   └── ProfileRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── ProfileServiceImpl.java
│   │   │               │       └── S3StorageService.java
│   │   │               ├── security/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── JwtDto.java
│   │   │               │   │   └── JwtInformation.java
│   │   │               │   ├── handler/
│   │   │               │   │   ├── Http403ForbiddenAccessDeniedHandler.java
│   │   │               │   │   ├── LoginFailureHandler.java
│   │   │               │   │   ├── OAuth2FailureHandler.java
│   │   │               │   │   ├── OAuth2SuccessHandler.java
│   │   │               │   │   └── SpaCsrfTokenRequestHandler.java
│   │   │               │   ├── jwt/
│   │   │               │   │   ├── AbstractJwtSuccessHandler.java
│   │   │               │   │   ├── InMemoryJwtRegistry.java
│   │   │               │   │   ├── JwtAuthenticationFilter.java
│   │   │               │   │   ├── JwtLoginSuccessHandler.java
│   │   │               │   │   ├── JwtLogoutHandler.java
│   │   │               │   │   ├── JwtRegistry.java
│   │   │               │   │   ├── JwtTokenProvider.java
│   │   │               │   │   └── RedisJwtRegistry.java
│   │   │               │   ├── resolver/
│   │   │               │   │   ├── CurrentUserId.java
│   │   │               │   │   ├── CurrentUserIdArgumentResolver.java
│   │   │               │   │   ├── RequireRole.java
│   │   │               │   │   └── RoleAuthorizationInterceptor.java
│   │   │               │   ├── user/
│   │   │               │       ├── OtbooOidcUserDetails.java
│   │   │               │       ├── OtbooUserDetails.java
│   │   │               │       ├── OtbooUserDetailsService.java
│   │   │               │       └── TemporaryPasswordAuthenticationProvider.java
│   │   │               ├── user/
│   │   │               │   ├── controller/
│   │   │               │   │   ├── AuthController.java
│   │   │               │   │   └── UserController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── request/
│   │   │               │   │   │   ├── ChangePasswordRequest.java
│   │   │               │   │   │   ├── ResetPasswordRequest.java
│   │   │               │   │   │   ├── UserCreateRequest.java
│   │   │               │   │   │   ├── UserLockUpdateRequest.java
│   │   │               │   │   │   ├── UserRoleUpdateRequest.java
│   │   │               │   │   │   └── UserSliceRequest.java
│   │   │               │   │   ├── response/
│   │   │               │   │   │   ├── KakaoTokenResponse.java
│   │   │               │   │   │   ├── KakaoUserResponse.java
│   │   │               │   │   │   └── UserDtoCursorResponse.java
│   │   │               │   │   ├── KakaoAccount.java
│   │   │               │   │   ├── Profile.java
│   │   │               │   │   ├── UserDto.java
│   │   │               │   │   └── UserSummary.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Provider.java
│   │   │               │   │   ├── Role.java
│   │   │               │   │   └── User.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── UserRepository.java
│   │   │               │   │   ├── UserRepositoryCustom.java
│   │   │               │   │   └── UserRepositoryImpl.java
│   │   │               │   ├── service/
│   │   │               │       ├── AuthServiceImpl.java
│   │   │               │       ├── CustomOAuth2UserService.java
│   │   │               │       ├── CustomOidcUserService.java
│   │   │               │       ├── KakaoOAuth2UserService.java
│   │   │               │       ├── KakaoOAuthService.java
│   │   │               │       └── UserServiceImpl.java
│   │   │               ├── weather/
│   │   │               │   ├── client/
│   │   │               │   │   ├── KakaoLocationFeignClient.java
│   │   │               │   │   ├── OpenWeatherFeignClient.java
│   │   │               │   │   └── WeatherApiClient.java
│   │   │               │   ├── controller/
│   │   │               │   │   └── WeatherController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── location/
│   │   │               │   │   │   ├── KakaoRegionDocument.java
│   │   │               │   │   │   ├── KakaoRegionResponse.java
│   │   │               │   │   │   └── LocationDto.java
│   │   │               │   │   ├── weather/
│   │   │               │   │       ├── HumidityDto.java
│   │   │               │   │       ├── OpenWeatherForecastApiResponse.java
│   │   │               │   │       ├── PrecipitationDto.java
│   │   │               │   │       ├── PrecipitationType.java
│   │   │               │   │       ├── SkyStatus.java
│   │   │               │   │       ├── TemperatureDto.java
│   │   │               │   │       ├── WeatherDto.java
│   │   │               │   │       ├── WeatherSummaryDto.java
│   │   │               │   │       ├── WindSpeedDto.java
│   │   │               │   │       └── WindStrength.java
│   │   │               │   ├── entity/
│   │   │               │   │   └── Weather.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── WeatherMapper.java
│   │   │               │   ├── model/
│   │   │               │   │   └── SnapshotCandidate.java
│   │   │               │   ├── repository/
│   │   │               │   │   └── WeatherRepository.java
│   │   │               │   ├── service/
│   │   │               │       └── WeatherService.java
│   │   │               └── OtbooApplication.java
│   │   ├── resources/
│   │       ├── static/
│   │       │   ├── assets/
│   │       │   │   ├── Login BG-CiDhH4iC.svg
│   │       │   │   ├── Logo-wa1Pp3bf.svg
│   │       │   │   ├── index-B6awMYOr.css
│   │       │   │   ├── index-Nj5ghMes.js
│   │       │   │   └── login upper section-DT7EHkpB.svg
│   │       │   ├── index.html
│   │       │   ├── logo_symbol.svg
│   │       │   └── vite.svg
│   │       ├── application-h2.yaml
│   │       └── application.yaml
│   ├── test/
│       ├── java/
│       │   ├── codeit/
│       │       ├── sb06/
│       │           ├── otboo/
│       │               ├── clothes/
│       │               │   ├── repository/
│       │               │   │   ├── ClothesQueryRepositoryTest.java
│       │               │   │   └── ClothesRepositoryTest.java
│       │               │   ├── service/
│       │               │       ├── ClothesAttributeDefServiceTest.java
│       │               │       ├── ClothesServiceTest.java
│       │               │       └── RecommendationServiceTest.java
│       │               ├── comment/
│       │               │   ├── controller/
│       │               │   │   ├── CommentCreateControllerTest.java
│       │               │   │   └── CommentQueryControllerTest.java
│       │               │   ├── repository/
│       │               │   │   └── CommentRepositoryTest.java
│       │               │   ├── service/
│       │               │       ├── CommentCreateServiceTest.java
│       │               │       └── CommentQueryServiceTest.java
│       │               ├── config/
│       │               │   └── AdminInitializerTest.java
│       │               ├── exception/
│       │               │   └── GlobalExceptionHandlerTest.java
│       │               ├── feed/
│       │               │   ├── repository/
│       │               │   │   └── FeedRepositoryTest.java
│       │               │   ├── service/
│       │               │       └── FeedServiceTest.java
│       │               ├── follow/
│       │               │   ├── controller/
│       │               │   │   ├── FollowCreateResponseTest.java
│       │               │   │   ├── FollowDeleteResponseTest.java
│       │               │   │   ├── FollowGetListResponseTest.java
│       │               │   │   └── FollowGetSummaryResponseTest.java
│       │               │   ├── entity/
│       │               │   │   └── FollowTest.java
│       │               │   ├── repository/
│       │               │   │   └── FollowRepositoryTest.java
│       │               │   ├── service/
│       │               │       ├── FollowCreateServiceTest.java
│       │               │       ├── FollowDeleteTest.java
│       │               │       ├── FollowGetListTest.java
│       │               │       └── FollowGetSummaryServiceTest.java
│       │               ├── message/
│       │               │   ├── controller/
│       │               │   │   └── DirectMessageWebSocketControllerTest.java
│       │               │   ├── entity/
│       │               │   │   └── ChatRoomTest.java
│       │               │   ├── interceptor/
│       │               │   │   └── WebSocketChannelInterceptorTest.java
│       │               │   ├── listener/
│       │               │   │   └── DirectMessageRedisStreamListenerTest.java
│       │               │   ├── publisher/
│       │               │   │   ├── DirectMessagePublisherTest.java
│       │               │   │   └── DirectMessageRedisPublisherTest.java
│       │               │   ├── repository/
│       │               │   │   └── DirectMessageRepositoryTest.java
│       │               │   ├── scheduler/
│       │               │   │   ├── DmStreamRecoverySchedulerListenerTest.java
│       │               │   │   └── DmStreamRecoverySchedulerTest.java
│       │               │   ├── service/
│       │               │       ├── ChatMemberServiceImplTest.java
│       │               │       ├── ChatRoomServiceImplTest.java
│       │               │       └── DirectMessageServiceImplTest.java
│       │               ├── notification/
│       │               │   ├── batch/
│       │               │   │   └── UserActivityNotificationBatchTest.java
│       │               │   ├── config/
│       │               │   │   └── EmbeddedRedisConfig.java
│       │               │   ├── listener/
│       │               │   │   └── NotificationListenerTest.java
│       │               │   ├── publisher/
│       │               │   │   ├── NotificationPublisherTest.java
│       │               │   │   └── RedisNotificationPublisherTest.java
│       │               │   ├── repository/
│       │               │   │   ├── NotificationRepositoryTest.java
│       │               │   │   ├── SseEmitterRepositoryTest.java
│       │               │   │   └── SseEventCacheRepositoryTest.java
│       │               │   ├── scheduler/
│       │               │   │   ├── NotificationCleanUpBatchSchedulerTest.java
│       │               │   │   ├── NotificationCleanUpJobIntegrationTest.java
│       │               │   │   ├── NotificationStreamRecoverySchedulerListenerTest.java
│       │               │   │   ├── NotificationStreamRecoverySchedulerTest.java
│       │               │   │   ├── SseHeartbeatSchedulerTest.java
│       │               │   │   ├── UserActivityNotificationBatchSchedulerTest.java
│       │               │   │   └── UserActivityNotificationJobIntegrationTest.java
│       │               │   ├── service/
│       │               │       ├── NotificationCacheServiceRedisTest.java
│       │               │       ├── NotificationCacheServiceUnitTest.java
│       │               │       ├── NotificationServiceTest.java
│       │               │       └── SseServiceTest.java
│       │               ├── profile/
│       │               │   ├── dto/
│       │               │   │   └── ProfileDtoTest.java
│       │               │   ├── entity/
│       │               │   │   └── GenderTest.java
│       │               │   ├── service/
│       │               │       ├── ProfileServiceImplTest.java
│       │               │       ├── S3StorageServiceIntegrationTest.java
│       │               │       └── S3StorageServiceTest.java
│       │               ├── security/
│       │               │   ├── jwt/
│       │               │   │   ├── JwtAuthenticationFilterTest.java
│       │               │   │   ├── JwtLoginSuccessHandlerTest.java
│       │               │   │   ├── JwtLogoutHandlerTest.java
│       │               │   │   └── JwtTokenProviderTest.java
│       │               │   ├── CsrfTokenEndpointTest.java
│       │               │   ├── CurrentUserIdArgumentResolverTest.java
│       │               │   ├── CustomOAuth2UserServiceTest.java
│       │               │   ├── Http403ForbiddenAccessDeniedHandlerTest.java
│       │               │   ├── InMemoryJwtRegistryTest.java
│       │               │   ├── LoginFailureHandlerTest.java
│       │               │   ├── OAuth2AuthorizationEndpointTest.java
│       │               │   ├── OAuth2FailureHandlerTest.java
│       │               │   ├── OAuth2SuccessHandlerTest.java
│       │               │   ├── OtbooUserDetailsServiceTest.java
│       │               │   ├── OtbooUserDetailsTest.java
│       │               │   ├── RedisJwtRegistryTest.java
│       │               │   ├── RoleAuthorizationInterceptorTest.java
│       │               │   ├── SpaCsrfTokenRequestHandlerTest.java
│       │               │   └── TemporaryPasswordAuthenticationProviderTest.java
│       │               ├── user/
│       │               │   ├── controller/
│       │               │   │   ├── AuthControllerTest.java
│       │               │   │   └── UserControllerTest.java
│       │               │   ├── entity/
│       │               │   │   └── UserTest.java
│       │               │   ├── repository/
│       │               │   │   └── UserRepositoryImplTest.java
│       │               │   ├── service/
│       │               │       ├── AuthServiceImplTest.java
│       │               │       ├── CustomOidcUserServiceTest.java
│       │               │       ├── KakaoOAuth2UserServiceTest.java
│       │               │       ├── KakaoOAuthServiceTest.java
│       │               │       └── UserServiceImplTest.java
│       │               ├── util/
│       │               │   └── EasyRandomUtil.java
│       │               ├── weather/
│       │               │   ├── WeatherApiClientTest.java
│       │               │   ├── WeatherControllerTest.java
│       │               │   ├── WeatherRepositoryTest.java
│       │               │   └── WeatherServiceTest.java
│       │               └── OtbooApplicationTests.java
│       ├── resources/
│           └── application-test.yaml
├── Dockerfile
├── README.md
├── build.gradle
├── docker-compose.yml
├── gradlew
├── gradlew.bat
└── settings.gradle

```

</details>




---

## 구현 홈페이지  
http://otboo-alb-1790211223.ap-northeast-2.elb.amazonaws.com/


---

## 프로젝트 회고록  
https://www.notion.so/codeit/2-30c6fd228e8d80fdb16fd96faba318ff
