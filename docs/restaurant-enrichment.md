# AI 식당 정보 보강

TourAPI로 저장한 식당의 누락 필드를 웹 검색, 원본 페이지 수집, 로컬 AI 구조화 출력으로 보강한다.
스케줄러는 사용하지 않으며 내부 API를 호출했을 때만 실행한다.

## 준비

1. Ollama를 설치하고 `qwen3.5:4b` 모델을 내려받는다.
2. `database/restaurant-enrichment.sql`을 DB에 한 번 적용한다.

OpenAI API와 API 키는 사용하지 않는다. 파드가 재시작되면 실행 중인 작업은 복구되지 않는다.

## 설정

```yaml
restaurant-enrichment:
  max-sources-per-restaurant: 5
  parallelism: 3
  crawler-base-url: http://localhost:8001
```

- `max-sources-per-restaurant`: 한 식당에서 확인할 검색 결과의 최대 개수
- `parallelism`: 동시에 처리할 식당 개수
- `crawler-base-url`: 별도로 실행한 Python 크롤러·로컬 AI 서버의 내부 주소

## Python 크롤러 실행

크롤러는 같은 저장소의 `crawler` 디렉터리에 있지만 Spring과 별도 서버로 실행한다. Ollama도 먼저
실행하고 모델을 한 번 내려받아야 한다.

```bash
ollama serve
ollama pull qwen3.5:4b

docker build -t hyunjiin-crawler ./crawler
docker run --rm --init --ipc=host -p 8001:8000 \
  -e OLLAMA_BASE_URL=http://host.docker.internal:11434 \
  -e OLLAMA_MODEL=qwen3.5:4b \
  hyunjiin-crawler
```

Spring 서버를 Kubernetes에서 실행할 때는 Python 크롤러 Service 주소를 설정한다.

```yaml
env:
  - name: RESTAURANT_ENRICHMENT_CRAWLER_BASE_URL
    value: http://hyunjiin-crawler:8000
```

Python 크롤러 Deployment에는 Ollama Service 주소와 모델을 설정한다.

```yaml
env:
  - name: OLLAMA_BASE_URL
    value: http://ollama:11434
  - name: OLLAMA_MODEL
    value: qwen3.5:4b
```

## 실행

```bash
curl -X POST 'https://hyunjiin.site/api/internal/restaurant-enrichment-jobs' \
  -H 'Content-Type: application/json' \
  -d '{"restaurantIds":[2,3,4,5]}'
```

검색 API는 사용하지 않는다. Spring은 식당별 요청을 Python 서버로 보내고, Python 서버는 Playwright의
Chromium으로 Google Maps를 렌더링한다. 검색 결과에 외부 식당 홈페이지가 있으면 최대
`max-sources-per-restaurant`개까지 추가로 수집한 뒤 로컬 Ollama 모델로 구조화한다. Google Maps가
CAPTCHA나 429 응답을 반환하거나 로컬 모델을 사용할 수 없으면 해당 식당 작업은 실패 처리된다.

서버는 `202 Accepted`와 `jobId`를 즉시 반환한다. 식당별 누락 필드만 검색하고 이미 모든 대상
필드가 채워진 식당은 `SKIPPED` 처리한다.

```bash
curl 'https://hyunjiin.site/api/internal/restaurant-enrichment-jobs/JOB_ID'
curl 'https://hyunjiin.site/api/internal/restaurant-enrichment-jobs/JOB_ID/candidates'
```

후보에는 원본 URL, 근거 문장, AI 신뢰도가 포함된다. 기존 식당 값은 자동으로 덮어쓰지 않는다.

```bash
curl -X POST 'https://hyunjiin.site/api/internal/restaurant-enrichment-jobs/JOB_ID/apply' \
  -H 'Content-Type: application/json' \
  -d '{"candidateIds":[12,15,18]}'
```

각 후보는 독립 트랜잭션으로 반영한다. 반영 시점에도 값이 비어 있을 때만 저장한다.

## 현재 보강 대상

- 전화번호가 없음
- 영업시간이 없거나 `-`
- 소개가 없음
- 영업 상태가 `UNKNOWN`
- 메뉴가 없음
- 기존 메뉴의 가격이 없음

현지인 추천 여부와 추천 사유는 웹 문서나 AI 판단으로 변경하지 않는다. 가격 적정성은 충분한
메뉴 가격 표본이 확보된 뒤 별도의 계산 정책으로 처리한다.
