import httpx

from hyunjiin_crawler.main import app
from hyunjiin_crawler.models import (
    CrawlSource,
    CrawlSourceKind,
    ExtractedCandidate,
    RestaurantCrawlResponse,
    RestaurantEnrichmentResponse,
)


class FakeCrawler:
    async def crawl(self, payload):
        return RestaurantCrawlResponse(
            restaurantName=payload.restaurantName,
            sources=[
                CrawlSource(
                    title=payload.restaurantName,
                    url="https://www.google.com/maps/place/test",
                    content="전화번호 064-782-9006",
                    kind=CrawlSourceKind.GOOGLE_MAPS,
                )
            ],
        )


class FakeExtractor:
    async def is_ready(self):
        return True

    async def extract(self, payload, sources):
        return RestaurantEnrichmentResponse(
            restaurantName=payload.restaurantName,
            sourceCount=len(sources),
            fetchedCount=len(sources),
            candidates=[
                ExtractedCandidate(
                    field="PHONE_NUMBER",
                    valueText="064-782-9006",
                    sourceUrl=sources[0].url,
                    evidence="전화번호 064-782-9006",
                    confidence=0.98,
                )
            ],
        )


async def test_crawl_endpoint_returns_sources() -> None:
    app.state.crawler = FakeCrawler()
    transport = httpx.ASGITransport(app=app)
    async with httpx.AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post(
            "/internal/v1/restaurants/crawl",
            json={
                "restaurantName": "가는곶 세화",
                "address": "제주특별자치도 제주시 구좌읍 세화14길 3",
                "latitude": 33.5205,
                "longitude": 126.8606,
                "missingFields": ["PHONE_NUMBER"],
                "maxSources": 5,
            },
        )

    assert response.status_code == 200
    assert response.json()["restaurantName"] == "가는곶 세화"
    assert response.json()["sources"][0]["kind"] == "GOOGLE_MAPS"


async def test_crawl_endpoint_validates_request() -> None:
    app.state.crawler = FakeCrawler()
    transport = httpx.ASGITransport(app=app)
    async with httpx.AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post(
            "/internal/v1/restaurants/crawl",
            json={
                "restaurantName": "",
                "address": "제주",
                "missingFields": [],
                "maxSources": 0,
            },
        )

    assert response.status_code == 422


async def test_enrich_endpoint_crawls_and_extracts_candidates() -> None:
    app.state.crawler = FakeCrawler()
    app.state.extractor = FakeExtractor()
    transport = httpx.ASGITransport(app=app)
    async with httpx.AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post(
            "/internal/v1/restaurants/enrich",
            json={
                "restaurantName": "가는곶 세화",
                "address": "제주특별자치도 제주시 구좌읍 세화14길 3",
                "missingFields": ["PHONE_NUMBER"],
                "maxSources": 5,
            },
        )

    assert response.status_code == 200
    assert response.json()["sourceCount"] == 1
    assert response.json()["candidates"][0]["valueText"] == "064-782-9006"


async def test_ready_endpoint_checks_local_model() -> None:
    app.state.extractor = FakeExtractor()
    transport = httpx.ASGITransport(app=app)
    async with httpx.AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.get("/ready")

    assert response.status_code == 200
    assert response.json() == {"status": "UP"}
