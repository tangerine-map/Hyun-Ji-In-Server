from contextlib import asynccontextmanager
import logging

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from .config import CrawlerSettings
from .crawler import PlaywrightRestaurantCrawler, RestaurantCrawlError
from .extractor import LocalModelError, OllamaRestaurantExtractor
from .models import (
    CrawlerErrorResponse,
    RestaurantCrawlRequest,
    RestaurantCrawlResponse,
    RestaurantEnrichmentResponse,
)


logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s - %(message)s",
)


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = CrawlerSettings.from_environment()
    crawler = PlaywrightRestaurantCrawler(settings)
    await crawler.start()
    app.state.crawler = crawler
    app.state.extractor = OllamaRestaurantExtractor(settings)
    try:
        yield
    finally:
        await crawler.close()


app = FastAPI(
    title="Hyunjiin Restaurant Crawler",
    version="0.1.0",
    lifespan=lifespan,
)


@app.exception_handler(RestaurantCrawlError)
async def crawl_error_handler(_: Request, exception: RestaurantCrawlError) -> JSONResponse:
    response = CrawlerErrorResponse(code="CRAWL_FAILED", message=str(exception))
    return JSONResponse(status_code=502, content=response.model_dump())


@app.exception_handler(LocalModelError)
async def local_model_error_handler(_: Request, exception: LocalModelError) -> JSONResponse:
    response = CrawlerErrorResponse(code="LOCAL_MODEL_FAILED", message=str(exception))
    return JSONResponse(status_code=500, content=response.model_dump())


@app.get("/health")
async def health(request: Request) -> dict[str, str]:
    crawler = getattr(request.app.state, "crawler", None)
    return {"status": "UP" if crawler is not None else "DOWN"}


@app.get("/ready")
async def ready(request: Request) -> JSONResponse:
    extractor: OllamaRestaurantExtractor | None = getattr(request.app.state, "extractor", None)
    if extractor is not None and await extractor.is_ready():
        return JSONResponse(status_code=200, content={"status": "UP"})
    return JSONResponse(status_code=503, content={"status": "DOWN"})


@app.post(
    "/internal/v1/restaurants/crawl",
    response_model=RestaurantCrawlResponse,
    responses={502: {"model": CrawlerErrorResponse}},
)
async def crawl_restaurant(
    payload: RestaurantCrawlRequest,
    request: Request,
) -> RestaurantCrawlResponse:
    crawler: PlaywrightRestaurantCrawler = request.app.state.crawler
    return await crawler.crawl(payload)


@app.post(
    "/internal/v1/restaurants/enrich",
    response_model=RestaurantEnrichmentResponse,
    responses={
        500: {"model": CrawlerErrorResponse},
        502: {"model": CrawlerErrorResponse},
    },
)
async def enrich_restaurant(
    payload: RestaurantCrawlRequest,
    request: Request,
) -> RestaurantEnrichmentResponse:
    crawler: PlaywrightRestaurantCrawler = request.app.state.crawler
    extractor: OllamaRestaurantExtractor = request.app.state.extractor
    crawled = await crawler.crawl(payload)
    return await extractor.extract(payload, crawled.sources)
