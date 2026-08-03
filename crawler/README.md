# Hyunjiin Restaurant Crawler + Local AI

Google Maps와 식당 홈페이지를 브라우저로 수집하고 로컬 Ollama 모델로 필요한 정보를 추출하는
독립 Python 서비스다. 검색 API 키와 OpenAI API 키를 사용하지 않는다. Spring 서버의 비동기 보강
작업이 내부 HTTP로 이 서비스를 호출한다.

## 로컬 실행

먼저 Ollama를 실행하고 모델을 한 번 내려받는다.

```bash
ollama serve
ollama pull qwen3.5:4b
```

크롤러를 컨테이너로 실행한다. macOS와 Windows의 Docker에서는 Ollama가 호스트에서 실행되므로
`host.docker.internal`을 사용한다.

```bash
docker build -t hyunjiin-crawler ./crawler
docker run --rm --init --ipc=host -p 8001:8000 \
  -e OLLAMA_BASE_URL=http://host.docker.internal:11434 \
  -e OLLAMA_MODEL=qwen3.5:4b \
  hyunjiin-crawler
```

상태 확인:

```bash
curl http://localhost:8001/health
```

수집과 로컬 AI 추출 확인:

```bash
curl -X POST http://localhost:8001/internal/v1/restaurants/enrich \
  -H 'Content-Type: application/json' \
  -d '{
    "restaurantName":"가는곶 세화",
    "address":"제주특별자치도 제주시 구좌읍 세화14길 3",
    "latitude":33.5205279098,
    "longitude":126.860696168,
    "missingFields":["PHONE_NUMBER","OPENING_HOURS","MENU","MENU_PRICE"],
    "maxSources":5
  }'
```

## 환경 변수

- `CRAWLER_PARALLELISM`: 동시에 실행할 브라우저 컨텍스트 수, 기본값 `3`
- `CRAWLER_NAVIGATION_TIMEOUT_MS`: 페이지 이동 제한 시간, 기본값 `30000`
- `CRAWLER_MAX_PAGE_CHARACTERS`: 출처별 최대 글자 수, 기본값 `12000`
- `CRAWLER_MAX_RESPONSE_BYTES`: 외부 홈페이지 최대 응답 크기, 기본값 `1000000`
- `CRAWLER_BROWSER_EXECUTABLE_PATH`: 로컬에 설치된 Chrome을 사용할 때만 지정하는 선택값
- `OLLAMA_BASE_URL`: Ollama 내부 주소, 기본값 `http://localhost:11434`
- `OLLAMA_MODEL`: 사용할 로컬 모델, 기본값 `qwen3.5:4b`

운영 환경에서는 컨테이너를 한 개의 Uvicorn worker로 실행한다. 프로세스 단위 확장이 필요하면 Kubernetes
Deployment replica를 늘린다.
