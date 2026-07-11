# AI 개발 규칙

이 문서는 이 프로젝트에서 AI와 개발자가 반드시 따라야 하는 구조 규칙이다.

모든 코드 생성, 수정, 설계 제안은 아래 규칙을 우선한다.

## 1. 현재 프로젝트 구조

초기 구현은 Spring Boot Gradle 멀티모듈 구조로 간다.

```text
jeju-matzip
├── core
└── user-api
```

현재는 `admin-api`를 만들지 않는다.

나중에 어드민 기능이 필요해지면 다음처럼 추가한다.

```text
jeju-matzip
├── core
├── user-api
└── admin-api
```

## 2. 모듈 의존성 규칙

초기 허용 의존성은 다음 하나뿐이다.

```text
user-api -> core
```

금지 의존성:

```text
core -> user-api  금지
```

추후 `admin-api`가 생기면 다음만 허용한다.

```text
admin-api -> core
```

추후에도 금지:

```text
core      -> admin-api  금지
user-api  -> admin-api  금지
admin-api -> user-api   금지
```

## 3. core 모듈 규칙

`core`는 실행 애플리케이션이 아니다.

`core`에는 공통 도메인, 유스케이스, DB 구현, 외부 연동 구현을 둔다.

포함:

- JPA Entity
- Value Object
- Enum
- Domain Service
- Repository interface
- Repository implementation
- Application Service
- Command / Query
- Result DTO
- QueryDSL 조회 구현
- 가격 적정성 계산
- 현지인 추천 판정
- 추천 점수 계산
- 신고 상태 변경 규칙
- 외부 API client 구현

제외:

- Controller
- 사용자 API Request DTO
- 사용자 API Response DTO
- 어드민 API Request DTO
- 어드민 API Response DTO
- 인증 필터
- 화면 전용 응답 모델

## 4. user-api 모듈 규칙

`user-api`는 모바일 앱 사용자를 위한 Spring Boot 실행 모듈이다.

포함:

- `UserApiApplication`
- Controller
- Request DTO
- Response DTO
- 사용자 인증/인가 설정
- API 예외 응답 포맷
- API 문서 설정
- 사용자 앱 전용 Web 설정

`user-api`는 `core`의 Application Service를 호출한다.

`user-api`에서 Repository를 직접 호출하지 않는다.

## 5. core 내부 패키지 규칙

`core`는 도메인 기준으로 나눈다.

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
```

## 6. core/application 규칙

`application`은 유스케이스를 실행한다.

포함:

- Application Service
- Command
- Query
- Result
- 트랜잭션 경계
- 여러 도메인을 조합하는 흐름

규칙:

- 하나의 public method는 하나의 유스케이스를 표현한다.
- 트랜잭션은 Application Service에서 시작한다.
- Controller Request DTO를 직접 받지 않는다.
- Controller Response DTO를 직접 반환하지 않는다.
- `user-api` 패키지를 참조하지 않는다.
- 현재는 admin 전용 Service를 만들지 않는다.

## 7. core/domain 규칙

`domain`은 핵심 비즈니스 규칙을 담는다.

포함:

- Entity
- Value Object
- Enum
- Domain Service
- Repository interface
- 도메인 정책

규칙:

- Web 타입을 참조하지 않는다.
- Controller DTO를 참조하지 않는다.
- 외부 API client를 직접 호출하지 않는다.
- 가능하면 도메인 규칙은 Entity나 Domain Service로 표현한다.

## 8. core/infrastructure 규칙

`infrastructure`는 기술 구현을 담당한다.

포함:

- Spring Data JPA Repository
- QueryDSL Repository
- Repository interface 구현체
- 외부 AI API client
- 외부 지도 API client
- 파일/스토리지 client

규칙:

- `domain`의 Repository interface를 구현한다.
- 복잡한 지도 검색, 필터 조합, 거리순 정렬은 QueryDSL 전용 클래스로 분리한다.
- 외부 API 응답 모델은 infrastructure 내부에 가둔다.

## 9. user-api 내부 패키지 규칙

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
    ├── search
    ├── recommendation
    ├── useractivity
    └── report
```

각 기능 패키지에는 Controller, Request DTO, Response DTO를 둔다.

## 10. DTO 변환 규칙

API 요청/응답 DTO와 core 모델을 섞지 않는다.

흐름:

```text
user-api Request DTO
    -> core Query/Command
    -> core Domain
    -> core Result
    -> user-api Response DTO
```

`core`의 Application Service는 API Response DTO를 반환하지 않는다.

## 11. Repository 규칙

Repository interface는 `core/{domain}/domain`에 둔다.

Spring Data JPA Repository와 구현체는 `core/{domain}/infrastructure`에 둔다.

예:

```text
core/restaurant/domain/RestaurantRepository.java
core/restaurant/infrastructure/RestaurantJpaRepository.java
core/restaurant/infrastructure/RestaurantQueryRepository.java
core/restaurant/infrastructure/RestaurantRepositoryImpl.java
```

Controller나 user-api Service에서 Repository를 직접 호출하지 않는다.

## 12. admin-api 규칙

현재는 `admin-api`를 만들지 않는다.

어드민 기능이 실제로 필요해지는 시점에만 추가한다.

추가 시 원칙:

- `admin-api -> core`만 허용한다.
- `admin-api`는 어드민 Controller, Request DTO, Response DTO, 관리자 인증/인가만 가진다.
- `admin-api`는 `user-api`를 참조하지 않는다.
- 어드민 전용 유스케이스가 명확할 때만 core application에 admin 전용 Service를 추가한다.

## 13. 설계 우선순위

새 코드를 만들 때 우선순위는 다음과 같다.

1. 현재 확정 구조인 `core + user-api`를 지킨다.
2. 도메인 규칙은 `core/domain`에 둔다.
3. 유스케이스는 `core/application`에 둔다.
4. DB/외부 연동 구현은 `core/infrastructure`에 둔다.
5. HTTP 입출력은 `user-api`에 둔다.
6. admin 관련 코드는 지금 만들지 않는다.

## 14. 날짜/시간 규칙

이 프로젝트의 서비스 기준 시간대는 한국 시간이다.

```text
timezone: Asia/Seoul
offset: +09:00
```

API에서 날짜/시간을 주고받을 때는 ISO-8601 형식을 사용하되, 한국 시간 offset을 포함한다.

예:

```json
{
  "createdAt": "2026-07-11T18:30:00+09:00",
  "updatedAt": "2026-07-11T19:05:00+09:00"
}
```

규칙:

- API 응답 날짜/시간은 `+09:00` offset을 포함한다.
- 사용자에게 노출되는 영업시간, 신고일, 저장일, 최근 본 시간은 한국 시간 기준으로 처리한다.
- 서버 내부 시간 타입은 `OffsetDateTime` 사용을 우선 고려한다.
- 단순 날짜만 필요한 경우 `LocalDate`를 사용한다.
- 영업시간처럼 시각만 필요한 경우 `LocalTime`을 사용한다.
- `LocalDateTime`을 사용할 경우 시간대 정보가 사라지므로 API DTO에는 그대로 노출하지 않는다.

## 15. 예외 처리 규칙

예외 처리는 `BusinessException + 도메인별 ErrorCode` 방식으로 한다.

전역 `ErrorCode` 하나에 모든 에러를 몰아넣지 않는다.

## 15.1 공통 예외 구조

공통 예외 타입과 ErrorCode 인터페이스는 `core/global/error`에 둔다.

```text
core/global/error
├── BusinessException.java
└── ErrorCode.java
```

`ErrorCode`는 인터페이스다.

예:

```java
public interface ErrorCode {
    String code();
    String message();
    HttpStatus status();
}
```

`BusinessException`은 이 인터페이스를 받는다.

예:

```java
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
}
```

## 15.2 도메인별 ErrorCode 위치

각 도메인의 ErrorCode는 해당 도메인 패키지 안에 둔다.

예:

```text
core/restaurant/domain/RestaurantErrorCode.java
core/search/domain/SearchErrorCode.java
core/recommendation/domain/RecommendationErrorCode.java
core/useractivity/domain/UserActivityErrorCode.java
core/report/domain/ReportErrorCode.java
```

도메인별 ErrorCode enum은 공통 `ErrorCode` 인터페이스를 구현한다.

예:

```java
public enum RestaurantErrorCode implements ErrorCode {
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "식당을 찾을 수 없습니다."),
    INVALID_MAP_BOUNDS(HttpStatus.BAD_REQUEST, "지도 영역 값이 올바르지 않습니다.");
}
```

## 15.3 에러 응답 위치

HTTP 에러 응답 처리는 `user-api`에서 담당한다.

```text
user-api/global/error
├── GlobalExceptionHandler.java
├── ErrorResponse.java
└── ValidationErrorResponse.java
```

`core`는 HTTP 응답 형식을 모른다.

## 15.4 에러 응답 형식

일반 비즈니스 예외:

```json
{
  "code": "RESTAURANT_NOT_FOUND",
  "message": "식당을 찾을 수 없습니다."
}
```

validation 예외:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "요청 값이 올바르지 않습니다.",
  "fields": [
    {
      "field": "latitude",
      "message": "위도는 필수입니다."
    }
  ]
}
```

## 15.5 예외 처리 원칙

- 정상 응답은 공통 wrapper 없이 data를 직접 반환한다.
- 에러 응답만 공통 포맷을 사용한다.
- `ResponseStatusException`을 직접 던지지 않는다.
- Controller에서 예외 응답을 직접 만들지 않는다.
- core에서는 `BusinessException`을 던진다.
- user-api의 `GlobalExceptionHandler`가 `BusinessException`을 HTTP 응답으로 변환한다.
