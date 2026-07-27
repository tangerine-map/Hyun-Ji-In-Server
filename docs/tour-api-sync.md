# TourAPI 음식점 초기 적재

한국관광공사 국문 관광정보서비스 `KorService2`에서 제주 음식점 정보를 가져와 `restaurants`와
`restaurant_menus`에 저장한다.

## 저장 항목

- `contentid` -> `restaurants.tour_content_id`
- 식당명, 카테고리, 주소, 전화번호
- 위도와 경도
- 영업시간과 휴무일
- 소개문
- 대표 메뉴명
- TourAPI 최종 수정 시각과 마지막 동기화 시각

TourAPI가 메뉴 가격을 제공하지 않으므로 대표 메뉴 가격은 `null`로 저장한다. 현지인 추천과 가격
적정성도 이 동기화에서 임의로 생성하지 않는다.

## DB 준비

기존 DB를 사용한다면 [`database/tour-api-restaurant-import.sql`](../database/tour-api-restaurant-import.sql)을
한 번 적용한다. 개발 DB를 Hibernate `ddl-auto=update`로 새로 만드는 경우에는 별도로 실행하지 않아도 된다.

## 서버 설정

TourAPI 인증키는 서버 환경변수나 Kubernetes Secret에 저장하지 않는다. 서버는 일반 설정으로 실행하고,
공공데이터포털에서 발급받은 `Decoding` 인증키를 동기화 요청의 `X-Tour-Api-Key` 헤더로 전달한다.

```bash
./gradlew :user-api:bootRun
```

요청으로 받은 인증키는 해당 TourAPI 호출에만 사용하며 데이터베이스나 서버 설정에 저장하지 않는다.

## 수동 동기화 실행

서버가 실행 중일 때 다음 API를 호출한다.

```bash
curl -X POST 'http://localhost:8080/api/internal/tour-api/restaurants/sync' \
  -H 'Content-Type: application/json' \
  -H "X-Tour-Api-Key: ${TOUR_API_SERVICE_KEY}" \
  -d '{"pageNo":1,"maxItems":100}'
```

응답 예시는 다음과 같다.

```json
{
  "pageNo": 1,
  "nextPageNo": 2,
  "fetchedCount": 100,
  "createdCount": 80,
  "updatedCount": 20
}
```

한 번에 1~100개까지 요청할 수 있다. 응답의 `nextPageNo`를 다음 요청의 `pageNo`로 전달하며,
마지막 페이지에서는 `nextPageNo`가 `null`이다. 식당 한 곳마다 공통정보와 음식점 소개정보를 추가
조회하므로 목록 호출을 제외하고 식당당 두 번의 API 호출을 사용한다. 개발계정 일일 호출량을
고려해 페이지 단위로 실행한다.

같은 `tour_content_id`가 이미 있으면 새 행을 만들지 않고 기존 식당을 갱신한다. 초기 적재를 마치면
추가 호출을 하지 않는 한 자동으로 다시 동기화되지 않는다. 동시 요청은 중복 저장 경합을 막기 위해
`409 Conflict`로 거절한다.
