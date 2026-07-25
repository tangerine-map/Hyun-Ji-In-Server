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

공공데이터포털에서 발급받은 일반 인증키 중 `Decoding` 키와 수동 동기화 API를 보호할 별도 키를
환경변수로 설정한다. 두 키는 서로 다른 값을 사용한다.

```bash
export TOUR_API_SERVICE_KEY='발급받은 Decoding 인증키'
export TOUR_API_SYNC_API_KEY='직접 생성한 충분히 긴 임의의 값'
./gradlew :user-api:bootRun
```

동기화 API 키는 다음과 같이 생성할 수 있다.

```bash
openssl rand -base64 32
```

## 수동 동기화 실행

서버가 실행 중일 때 다음 API를 호출한다.

```bash
curl -X POST 'http://localhost:8080/api/internal/tour-api/restaurants/sync' \
  -H 'Content-Type: application/json' \
  -H "X-Sync-Api-Key: ${TOUR_API_SYNC_API_KEY}" \
  -d '{"maxItems":100}'
```

응답 예시는 다음과 같다.

```json
{
  "fetchedCount": 100,
  "createdCount": 80,
  "updatedCount": 20
}
```

한 번에 1~300개까지 요청할 수 있다. 식당 한 곳마다 공통정보와 음식점 소개정보를 추가 조회하므로
목록 호출을 제외하고 식당당 두 번의 API 호출을 사용한다. 개발계정 일일 호출량을 고려해 나눠서
실행한다.

같은 `tour_content_id`가 이미 있으면 새 행을 만들지 않고 기존 식당을 갱신한다. 초기 적재를 마치면
추가 호출을 하지 않는 한 자동으로 다시 동기화되지 않는다.

## Kubernetes Secret

`hyunjiin-user-api-secret`에 다음 두 키를 넣고 Deployment에서 각각 환경변수로 주입한다.

```yaml
stringData:
  tour-api-service-key: 공공데이터포털-Decoding-인증키
  tour-api-sync-api-key: 직접-생성한-수동-동기화-인증키
```
