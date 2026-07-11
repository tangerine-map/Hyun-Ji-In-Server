# Spring Boot 멀티모듈 아키텍처 추천

## 1. 현재 결정

초기 구현은 `core`와 `user-api` 두 모듈로 시작한다.

```text
jeju-matzip
├── core
└── user-api
```

`admin-api`는 지금 만들지 않는다. 다만 나중에 모노레포 안에서 별도 모듈로 추가할 수 있도록 `core`를 공통 도메인/유스케이스 모듈로 설계한다.

초기 의존성은 다음 하나만 허용한다.

```text
user-api -> core
```

금지할 의존성은 다음과 같다.

```text
core -> user-api  금지
```

## 2. 설계 방향

이 프로젝트는 사용자 앱 API를 먼저 구현하지만, 추후 어드민 페이지에서 식당, 메뉴, 가격 기준, 현지인 추천, 신고 데이터를 관리할 가능성이 높다.

따라서 `core`에는 사용자 앱과 어드민이 함께 사용할 수 있는 핵심 비즈니스 영역을 둔다.

`user-api`에는 모바일 앱 사용자에게 노출되는 HTTP API만 둔다.

핵심 원칙은 다음과 같다.

- `core`는 실행 애플리케이션이 아니다.
- `core`는 Controller, Request DTO, Response DTO를 모른다.
- `core`는 식당, 검색, 추천, 저장, 신고의 도메인 규칙과 유스케이스를 가진다.
- `user-api`는 Spring Boot 실행 모듈이다.
- `user-api`는 Controller, Request DTO, Response DTO, 사용자 인증/인가를 가진다.
- 나중에 `admin-api`가 생기면 `admin-api -> core` 의존성만 추가한다.

## 3. 추천 모노레포 구조

```text
jeju-matzip
├── settings.gradle
├── build.gradle
├── gradle
├── docs
│   ├── requirements.md
│   └── spring-boot-architecture.md
├── core
│   ├── build.gradle
│   └── src
│       ├── main
│       │   ├── java/com/example/jejumatzip/core
│       │   │   ├── restaurant
│       │   │   ├── search
│       │   │   ├── recommendation
│       │   │   ├── useractivity
│       │   │   ├── report
│       │   │   └── global
│       │   └── resources
│       └── test
└── user-api
    ├── build.gradle
    └── src
        ├── main
        │   ├── java/com/example/jejumatzip/user
        │   │   ├── UserApiApplication.java
        │   │   ├── restaurant
        │   │   ├── search
        │   │   ├── recommendation
        │   │   ├── useractivity
        │   │   ├── report
        │   │   └── global
        │   └── resources
        └── test
```

## 4. settings.gradle

초기에는 `core`, `user-api`만 include한다.

```groovy
rootProject.name = 'jeju-matzip'

include 'core'
include 'user-api'
```

나중에 어드민을 만들 때만 다음을 추가한다.

```groovy
include 'admin-api'
```

## 5. 모듈 책임

## 5.1 core

`core`는 공통 비즈니스 모듈이다.

### 포함 대상

- JPA Entity
- Value Object
- Enum
- Repository interface
- Repository implementation
- Application Service
- Command / Query
- Result DTO
- Domain Service
- 가격 적정성 계산
- 현지인 추천 판정
- 추천 점수 계산
- 신고 상태 변경 규칙
- QueryDSL 기반 조회
- 외부 API client 구현

### 제외 대상

- Controller
- 사용자 API Request DTO
- 사용자 API Response DTO
- 어드민 API Request DTO
- 어드민 API Response DTO
- 사용자 인증 필터
- 어드민 인증 필터
- 화면 전용 응답 모델

## 5.2 user-api

`user-api`는 모바일 앱 사용자를 위한 Spring Boot 실행 모듈이다.

### 포함 대상

- `UserApiApplication`
- 사용자 앱 Controller
- 사용자 앱 Request DTO
- 사용자 앱 Response DTO
- 사용자 인증/인가 설정
- API 예외 응답 포맷
- API 문서 설정
- 사용자 앱 전용 Web 설정

### 주요 API 책임

- 지도 영역 기반 식당 목록 조회
- 식당 상세 조회
- 검색
- 빠른 필터
- AI맛잘알 추천 요청
- 추천 피드백 제출
- 저장 토글
- 저장 목록 조회
- 최근 본 식당 기록
- 정보 오류 신고 제출

## 6. core 내부 패키지 구조

```text
core
└── src/main/java/com/example/jejumatzip/core
    ├── restaurant
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    ├── search
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    ├── recommendation
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    ├── useractivity
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    ├── report
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    └── global
        ├── common
        ├── error
        └── util
```

## 7. core 계층별 책임

## 7.1 application

유스케이스를 실행한다.

### 포함 대상

- Application Service
- Command
- Query
- Result
- 트랜잭션 경계
- 여러 도메인을 조합하는 흐름

### 예시

```text
core/restaurant/application
├── RestaurantQueryService.java
├── RestaurantCommandService.java
├── FindRestaurantsQuery.java
├── GetRestaurantDetailQuery.java
├── RestaurantSummaryResult.java
└── RestaurantDetailResult.java
```

### 규칙

- 하나의 public method는 하나의 유스케이스를 표현한다.
- 트랜잭션은 Application Service에서 시작한다.
- Controller Request DTO를 직접 받지 않는다.
- Controller Response DTO를 직접 반환하지 않는다.
- `user-api`에 의존하지 않는다.
- 현재는 admin 전용 Service를 만들지 않는다.

## 7.2 domain

핵심 비즈니스 규칙을 담는다.

### 포함 대상

- Entity
- Value Object
- Enum
- Domain Service
- Repository interface
- 도메인 정책

### 예시

```text
core/restaurant/domain
├── Restaurant.java
├── RestaurantMenu.java
├── RestaurantStatus.java
├── PriceAdequacy.java
├── PriceAdequacyCalculator.java
├── LocalRecommendationPolicy.java
└── RestaurantRepository.java
```

### 규칙

- Web 타입을 참조하지 않는다.
- 사용자 API DTO를 참조하지 않는다.
- 외부 AI API client를 직접 호출하지 않는다.
- 가능하면 도메인 규칙은 Entity나 Domain Service로 표현한다.

## 7.3 infrastructure

DB, 외부 API, 기술 세부 구현을 담당한다.

### 포함 대상

- Spring Data JPA Repository
- QueryDSL Repository
- Repository interface 구현체
- 외부 AI API client
- 외부 지도 API client
- 파일/스토리지 client

### 예시

```text
core/restaurant/infrastructure
├── RestaurantJpaRepository.java
├── RestaurantQueryRepository.java
├── RestaurantRepositoryImpl.java
└── RestaurantMapper.java
```

### 규칙

- domain의 Repository interface를 구현한다.
- 복잡한 지도 검색, 필터 조합, 거리순 정렬은 QueryDSL 전용 클래스로 분리한다.
- 외부 API 응답 모델은 infrastructure 내부에 가둔다.

## 8. user-api 내부 패키지 구조

```text
user-api
└── src/main/java/com/example/jejumatzip/user
    ├── UserApiApplication.java
    ├── global
    │   ├── config
    │   ├── error
    │   ├── security
    │   └── web
    ├── restaurant
    │   ├── RestaurantController.java
    │   ├── RestaurantMapSearchRequest.java
    │   ├── RestaurantListResponse.java
    │   └── RestaurantDetailResponse.java
    ├── search
    │   └── SearchController.java
    ├── recommendation
    │   └── RecommendationController.java
    ├── useractivity
    │   └── UserActivityController.java
    └── report
        └── RestaurantReportController.java
```

## 9. 요청 처리 흐름

## 9.1 식당 목록 조회

```text
user-api/restaurant/RestaurantController
    -> core/restaurant/application/RestaurantQueryService
    -> core/restaurant/domain/RestaurantRepository
    -> core/restaurant/infrastructure/RestaurantRepositoryImpl
```

## 9.2 식당 상세 조회

```text
user-api/restaurant/RestaurantController
    -> core/restaurant/application/RestaurantQueryService
    -> core/restaurant/domain/RestaurantRepository
    -> core/useractivity/application/SavedRestaurantService
```

상세 조회 응답에 저장 여부가 필요하면 `RestaurantQueryService`에서 사용자 ID를 받아 저장 여부까지 조합하거나, 별도 Facade를 둘 수 있다.

MVP에서는 `RestaurantQueryService`에서 조합하고, 복잡해지면 `RestaurantFacade`를 추가하는 방식을 추천한다.

## 9.3 정보 오류 신고 제출

```text
user-api/report/RestaurantReportController
    -> core/report/application/RestaurantReportCommandService
    -> core/report/domain/RestaurantReport
    -> core/report/domain/RestaurantReportRepository
```

## 10. DTO 분리 기준

## 10.1 user-api Request/Response

모바일 앱 화면에 맞춘 입출력 모델이다.

```text
user-api/restaurant/RestaurantDetailResponse
```

사용자 상세 응답에는 다음이 포함될 수 있다.

- 식당명
- 대표 메뉴
- 가격 적정성 라벨
- 현지인 추천 뱃지
- 저장 여부
- 길찾기 좌표

## 10.2 core Command/Query/Result

core application service는 API 전용 Response DTO를 반환하지 않고 Result를 반환한다.

```text
core/restaurant/application/RestaurantDetailResult
```

변환 흐름은 다음과 같다.

```text
user-api Request DTO
    -> core Query/Command
    -> core Domain
    -> core Result
    -> user-api Response DTO
```

## 11. Repository 위치

## 11.1 Repository interface

`core/domain`에 둔다.

```java
package com.example.jejumatzip.core.restaurant.domain;

public interface RestaurantRepository {
    Optional<Restaurant> findById(Long id);
    List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition);
}
```

## 11.2 JPA Repository

`core/infrastructure`에 둔다.

```java
package com.example.jejumatzip.core.restaurant.infrastructure;

interface RestaurantJpaRepository extends JpaRepository<Restaurant, Long> {
}
```

## 11.3 구현체

`core/infrastructure`에서 domain repository interface를 구현한다.

```java
@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantQueryRepository restaurantQueryRepository;

    @Override
    public Optional<Restaurant> findById(Long id) {
        return restaurantJpaRepository.findById(id);
    }

    @Override
    public List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition) {
        return restaurantQueryRepository.findByMapBounds(condition);
    }
}
```

## 12. build.gradle 추천

## 12.1 root build.gradle

공통 플러그인과 버전을 루트에서 관리한다.

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.0' apply false
    id 'io.spring.dependency-management' version '1.1.5' apply false
}

subprojects {
    group = 'com.example'
    version = '0.0.1-SNAPSHOT'

    repositories {
        mavenCentral()
    }
}
```

## 12.2 core/build.gradle

`core`는 실행 가능한 Spring Boot 애플리케이션이 아니므로 `bootJar`를 끄고 `jar`를 켠다.

```groovy
plugins {
    id 'java-library'
    id 'io.spring.dependency-management'
}

jar {
    enabled = true
}

dependencies {
    api 'org.springframework.boot:spring-boot-starter-data-jpa'
    api 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'com.querydsl:querydsl-jpa'
    runtimeOnly 'org.postgresql:postgresql'
}
```

## 12.3 user-api/build.gradle

```groovy
plugins {
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}

dependencies {
    implementation project(':core')
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
}
```

## 13. 설정 파일

초기 실행 설정은 `user-api`에 둔다.

```text
user-api/src/main/resources/application.yml
```

`core`는 실행 모듈이 아니므로 기본적으로 운영용 `application.yml`을 갖지 않는다. 단, `core` 단위 테스트나 repository 테스트를 위한 `src/test/resources/application-test.yml`은 둘 수 있다.

## 14. 인증/인가

초기에는 사용자 API 기준으로만 설계한다.

### user-api

- 일반 사용자 로그인
- 익명 세션 일부 허용
- 저장/신고/마이페이지는 인증 필요
- `@CurrentUser` 같은 resolver는 `user-api/global/security`에 둔다

### core

- 인증 필터를 두지 않는다.
- 필요한 경우 application service에 `userId`를 파라미터로 전달한다.
- core는 인증 방식, 토큰 방식, 세션 방식을 몰라야 한다.

## 15. 테스트 전략

## 15.1 core 테스트

가장 중요하다. 비즈니스 규칙은 core에서 테스트한다.

### 대상

- 가격 적정성 계산
- 현지인 추천 판정
- 추천 점수 계산
- 신고 상태 변경
- 저장 중복 처리
- 지도 영역 조회 쿼리

## 15.2 user-api 테스트

사용자 API의 요청/응답 계약을 테스트한다.

### 대상

- 요청 validation
- 응답 schema
- 인증 필요 여부
- 모바일 화면에 필요한 필드 포함 여부

## 16. 추후 확장: admin-api

어드민 기능이 필요해지면 모노레포에 `admin-api` 모듈을 추가한다.

```text
jeju-matzip
├── core
├── user-api
└── admin-api
```

의존성은 다음만 허용한다.

```text
admin-api -> core
```

금지할 의존성은 다음과 같다.

```text
admin-api -> user-api  금지
user-api  -> admin-api 금지
core      -> admin-api 금지
```

## 16.1 admin-api 내부 구조 예시

```text
admin-api
└── src/main/java/com/example/jejumatzip/admin
    ├── AdminApiApplication.java
    ├── global
    │   ├── config
    │   ├── error
    │   ├── security
    │   └── web
    ├── restaurant
    │   ├── AdminRestaurantController.java
    │   ├── AdminRestaurantSearchRequest.java
    │   ├── AdminRestaurantUpdateRequest.java
    │   └── AdminRestaurantResponse.java
    ├── price
    │   ├── AdminPriceBenchmarkController.java
    │   └── AdminPriceBenchmarkUpdateRequest.java
    ├── recommendation
    │   └── AdminLocalRecommendationController.java
    └── report
        ├── AdminRestaurantReportController.java
        ├── AdminReportStatusUpdateRequest.java
        └── AdminRestaurantReportResponse.java
```

## 16.2 admin-api 추가 시 core 변경 기준

어드민 전용 유스케이스가 명확해질 때만 core application에 별도 서비스를 추가한다.

예:

```text
core/restaurant/application/AdminRestaurantCommandService.java
core/report/application/AdminRestaurantReportCommandService.java
```

지금은 만들지 않는다. 현재는 사용자 앱 MVP에 필요한 유스케이스만 core에 둔다.

## 17. 추후 확장: core 분리

`core`가 너무 커지면 아래처럼 더 나눌 수 있다.

```text
core-domain
core-application
core-infrastructure
user-api
admin-api
```

다만 처음부터 이렇게 쪼개면 개발 속도가 떨어질 수 있으므로, MVP 단계에서는 `core` 단일 모듈을 추천한다.

## 18. 추후 확장: batch

데이터 수집, 가격 기준 재계산, 인기 검색어 집계 같은 작업이 생기면 별도 실행 모듈을 추가한다.

```text
batch
```

`batch`도 `core`를 의존한다.

```text
batch -> core
```

## 19. 최종 추천

초기 구현은 다음 두 모듈로 간다.

```text
jeju-matzip
├── core
└── user-api
```

`core`에는 식당, 가격, 추천, 검색, 저장, 신고의 핵심 도메인과 유스케이스를 둔다.

`user-api`에는 모바일 앱 사용자용 Controller, Request DTO, Response DTO, 인증/인가 설정을 둔다.

`admin-api`는 지금 만들지 않는다. 나중에 필요해질 때 `admin-api -> core` 의존성으로 추가한다.
