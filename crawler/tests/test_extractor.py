import json

import httpx

from hyunjiin_crawler.config import CrawlerSettings
from hyunjiin_crawler.extractor import OllamaRestaurantExtractor
from hyunjiin_crawler.models import CrawlSource, CrawlSourceKind, RestaurantCrawlRequest


async def test_extracts_valid_candidates_with_structured_local_model_output() -> None:
    captured_request: dict[str, object] = {}

    async def handler(request: httpx.Request) -> httpx.Response:
        captured_request.update(json.loads(request.content))
        content = {
            "matchedRestaurant": True,
            "matchConfidence": 0.99,
            "facts": [
                {
                    "field": "PHONE_NUMBER",
                    "value": "064-782-9006",
                    "confidence": 0.98,
                    "evidence": "전화 064-782-9006",
                    "sourceUrl": "https://www.google.com/maps/place/test",
                },
                {
                    "field": "SUMMARY",
                    "value": "요청하지 않은 값",
                    "confidence": 0.9,
                    "evidence": "설명",
                    "sourceUrl": "https://www.google.com/maps/place/test",
                },
            ],
            "menus": [
                {
                    "name": "구운제주감자빵",
                    "price": 4000,
                    "representative": True,
                    "confidence": 0.95,
                    "evidence": "구운제주감자빵 4,000원",
                    "sourceUrl": "https://www.google.com/maps/place/test",
                }
            ],
        }
        return httpx.Response(200, json={"message": {"content": json.dumps(content)}})

    extractor = OllamaRestaurantExtractor(
        CrawlerSettings(),
        transport=httpx.MockTransport(handler),
    )
    request = RestaurantCrawlRequest(
        restaurantName="가는곶 세화",
        address="제주특별자치도 제주시 구좌읍 세화14길 3",
        missingFields=["PHONE_NUMBER", "MENU"],
        maxSources=5,
    )
    sources = [
        CrawlSource(
            title="가는곶 세화",
            url="https://www.google.com/maps/place/test",
            content="전화 064-782-9006, 구운제주감자빵 4,000원",
            kind=CrawlSourceKind.GOOGLE_MAPS,
        )
    ]

    result = await extractor.extract(request, sources)

    assert captured_request["model"] == "qwen3.5:4b"
    assert captured_request["stream"] is False
    assert isinstance(captured_request["format"], dict)
    assert result.sourceCount == 1
    assert [candidate.field for candidate in result.candidates] == ["PHONE_NUMBER", "MENU"]
    assert result.candidates[1].valueNumber == 4000


async def test_ignores_candidates_with_untrusted_source_url() -> None:
    content = {
        "matchedRestaurant": True,
        "matchConfidence": 0.9,
        "facts": [{
            "field": "PHONE_NUMBER",
            "value": "010-0000-0000",
            "confidence": 0.9,
            "evidence": "전화번호",
            "sourceUrl": "https://untrusted.example",
        }],
        "menus": [],
    }

    async def handler(_: httpx.Request) -> httpx.Response:
        return httpx.Response(200, json={"message": {"content": json.dumps(content)}})

    extractor = OllamaRestaurantExtractor(
        CrawlerSettings(),
        transport=httpx.MockTransport(handler),
    )
    request = RestaurantCrawlRequest(
        restaurantName="가는곶 세화",
        address="제주",
        missingFields=["PHONE_NUMBER"],
    )
    sources = [CrawlSource(
        title="가는곶 세화",
        url="https://www.google.com/maps/place/test",
        content="전화번호",
        kind=CrawlSourceKind.GOOGLE_MAPS,
    )]

    result = await extractor.extract(request, sources)

    assert result.candidates == []


async def test_readiness_checks_configured_model() -> None:
    captured_body: dict[str, object] = {}

    async def handler(request: httpx.Request) -> httpx.Response:
        captured_body.update(json.loads(request.content))
        return httpx.Response(200, json={"model_info": {}})

    extractor = OllamaRestaurantExtractor(
        CrawlerSettings(),
        transport=httpx.MockTransport(handler),
    )

    assert await extractor.is_ready()
    assert captured_body == {"model": "qwen3.5:4b"}
