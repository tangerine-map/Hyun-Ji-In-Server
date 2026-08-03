import asyncio
from dataclasses import dataclass
import ipaddress
import logging
import socket
from typing import Iterable
from urllib.parse import quote, urljoin, urlparse

from bs4 import BeautifulSoup
import httpx
from playwright.async_api import Browser, BrowserContext, Page, Playwright, async_playwright

from .config import CrawlerSettings
from .models import CrawlSource, CrawlSourceKind, RestaurantCrawlRequest, RestaurantCrawlResponse


logger = logging.getLogger(__name__)

GOOGLE_HOSTS = (
    "google.com",
    "google.co.kr",
    "googleusercontent.com",
    "gstatic.com",
    "ggpht.com",
)

BLOCKED_PAGE_MARKERS = (
    "unusual traffic",
    "비정상적인 트래픽",
    "our systems have detected",
    "로봇이 아닙니다",
)


class RestaurantCrawlError(RuntimeError):
    pass


@dataclass(frozen=True)
class CrawledDocument:
    title: str
    url: str
    content: str


class PlaywrightRestaurantCrawler:
    def __init__(self, settings: CrawlerSettings) -> None:
        self._settings = settings
        self._semaphore = asyncio.Semaphore(settings.parallelism)
        self._playwright: Playwright | None = None
        self._browser: Browser | None = None

    async def start(self) -> None:
        if self._browser is not None:
            return
        self._playwright = await async_playwright().start()
        launch_options = {
            "headless": True,
            "args": ["--disable-dev-shm-usage"],
        }
        if self._settings.browser_executable_path:
            launch_options["executable_path"] = self._settings.browser_executable_path
        self._browser = await self._playwright.chromium.launch(
            **launch_options,
        )
        logger.info("Playwright Chromium started")

    async def close(self) -> None:
        if self._browser is not None:
            try:
                await self._browser.close()
            except Exception:
                logger.warning("Browser connection was already closed during shutdown")
            finally:
                self._browser = None
        if self._playwright is not None:
            try:
                await self._playwright.stop()
            except Exception:
                logger.warning("Playwright driver was already stopped during shutdown")
            finally:
                self._playwright = None
        logger.info("Playwright Chromium stopped")

    async def crawl(self, request: RestaurantCrawlRequest) -> RestaurantCrawlResponse:
        async with self._semaphore:
            if self._browser is None:
                raise RestaurantCrawlError("crawler browser is not ready")
            context = await self._new_context()
            try:
                maps_source, external_urls = await self._crawl_google_maps(context, request)
                sources = [maps_source]
                for url in external_urls:
                    if len(sources) >= request.maxSources:
                        break
                    document = await self._crawl_external_page(url)
                    if document is None:
                        continue
                    sources.append(CrawlSource(
                        title=document.title,
                        url=document.url,
                        content=document.content,
                        kind=CrawlSourceKind.WEBSITE,
                    ))
                logger.info(
                    "Restaurant crawl completed name=%s source_count=%d",
                    request.restaurantName,
                    len(sources),
                )
                return RestaurantCrawlResponse(
                    restaurantName=request.restaurantName,
                    sources=sources,
                )
            finally:
                await context.close()

    async def _new_context(self) -> BrowserContext:
        assert self._browser is not None
        context = await self._browser.new_context(
            locale="ko-KR",
            timezone_id="Asia/Seoul",
            viewport={"width": 1440, "height": 1000},
            user_agent=(
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
                "(KHTML, like Gecko) Chrome/149.0 Safari/537.36"
            ),
        )
        context.set_default_timeout(self._settings.navigation_timeout_ms)
        context.set_default_navigation_timeout(self._settings.navigation_timeout_ms)
        return context

    async def _crawl_google_maps(
        self,
        context: BrowserContext,
        request: RestaurantCrawlRequest,
    ) -> tuple[CrawlSource, list[str]]:
        page = await context.new_page()
        query = f"{request.restaurantName} {request.address}"
        maps_url = f"https://www.google.com/maps/search/{quote(query, safe='')}?hl=ko"
        try:
            await page.goto(maps_url, wait_until="domcontentloaded")
            await self._accept_consent(page)
            await self._open_best_place_result(page, request.restaurantName)
            await self._settle_page(page)
            body_text = (await page.locator("body").inner_text()).strip()
            self._ensure_not_blocked(body_text)
            if len(body_text) < 20:
                raise RestaurantCrawlError("Google Maps page did not contain restaurant information")
            external_urls = await self._extract_external_urls(page)
            title = await self._page_title(page, request.restaurantName)
            return CrawlSource(
                title=title,
                url=page.url,
                content=body_text[: self._settings.max_page_characters],
                kind=CrawlSourceKind.GOOGLE_MAPS,
            ), external_urls
        except RestaurantCrawlError:
            raise
        except Exception as exception:
            raise RestaurantCrawlError("failed to crawl Google Maps restaurant page") from exception
        finally:
            await page.close()

    async def _accept_consent(self, page: Page) -> None:
        for label in ("모두 수락", "Accept all", "동의"):
            button = page.get_by_role("button", name=label, exact=True)
            try:
                if await button.is_visible(timeout=1_000):
                    await button.click()
                    await page.wait_for_load_state("domcontentloaded")
                    return
            except Exception:
                continue

    async def _open_best_place_result(self, page: Page, restaurant_name: str) -> None:
        heading = page.locator("h1").first
        try:
            if await heading.is_visible(timeout=2_000):
                heading_text = (await heading.inner_text()).strip()
                if self._names_match(heading_text, restaurant_name):
                    return
        except Exception:
            pass

        candidates = page.locator('a[href*="/maps/place/"]')
        count = min(await candidates.count(), 20)
        selected_index: int | None = None
        for index in range(count):
            candidate = candidates.nth(index)
            text = " ".join(filter(None, [
                (await candidate.get_attribute("aria-label") or "").strip(),
                (await candidate.inner_text()).strip(),
            ]))
            if self._names_match(text, restaurant_name):
                selected_index = index
                break
        if selected_index is None and count > 0:
            selected_index = 0
        if selected_index is not None:
            await candidates.nth(selected_index).click()
            await page.wait_for_load_state("domcontentloaded")

    async def _settle_page(self, page: Page) -> None:
        try:
            await page.locator("h1").first.wait_for(state="visible", timeout=8_000)
        except Exception:
            pass
        await page.wait_for_timeout(1_000)

    async def _page_title(self, page: Page, fallback: str) -> str:
        heading = page.locator("h1").first
        try:
            if await heading.is_visible(timeout=1_000):
                value = (await heading.inner_text()).strip()
                if value:
                    return value
        except Exception:
            pass
        return fallback

    async def _extract_external_urls(self, page: Page) -> list[str]:
        urls: list[str] = []
        seen: set[str] = set()
        for href in await page.locator('a[href^="http"]').evaluate_all(
            "elements => elements.map(element => element.href)",
        ):
            if href in seen or not self._is_external_url(href):
                continue
            seen.add(href)
            urls.append(href)
        return urls

    async def _crawl_external_page(self, url: str) -> CrawledDocument | None:
        current_url = url
        headers = {
            "User-Agent": (
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
                "(KHTML, like Gecko) Chrome/149.0 Safari/537.36"
            ),
            "Accept": "text/html,application/xhtml+xml,text/plain;q=0.9",
            "Accept-Language": "ko-KR,ko;q=0.9,en;q=0.7",
        }
        try:
            async with httpx.AsyncClient(headers=headers, follow_redirects=False) as client:
                for _ in range(4):
                    await self._validate_public_url(current_url)
                    response = await client.get(
                        current_url,
                        timeout=self._settings.navigation_timeout_ms / 1_000,
                    )
                    if response.is_redirect:
                        location = response.headers.get("location")
                        if not location:
                            return None
                        current_url = urljoin(current_url, location)
                        continue
                    if response.status_code < 200 or response.status_code >= 300:
                        return None
                    if len(response.content) > self._settings.max_response_bytes:
                        return None
                    content_type = response.headers.get("content-type", "").lower()
                    if content_type and not any(
                        supported in content_type
                        for supported in ("text/html", "application/xhtml+xml", "text/plain")
                    ):
                        return None
                    return self._extract_document(current_url, response.text)
        except (httpx.HTTPError, OSError, ValueError):
            logger.warning("External page crawl failed url=%s", url)
        return None

    def _extract_document(self, url: str, html: str) -> CrawledDocument:
        soup = BeautifulSoup(html, "html.parser")
        json_ld = "\n".join(
            element.get_text(" ", strip=True)
            for element in soup.select('script[type="application/ld+json"]')
        )
        for element in soup.select("script,style,noscript,svg,nav,footer"):
            element.decompose()
        title = soup.title.get_text(" ", strip=True) if soup.title else url
        body_text = soup.body.get_text(" ", strip=True) if soup.body else soup.get_text(" ", strip=True)
        content = f"구조화 데이터: {json_ld}\n본문: {body_text}"
        return CrawledDocument(
            title=title,
            url=url,
            content=content[: self._settings.max_page_characters],
        )

    async def _validate_public_url(self, url: str) -> None:
        parsed = urlparse(url)
        if parsed.scheme not in {"http", "https"} or not parsed.hostname:
            raise ValueError("only public HTTP URLs can be crawled")
        addresses = await asyncio.to_thread(socket.getaddrinfo, parsed.hostname, None)
        for address in addresses:
            ip = ipaddress.ip_address(address[4][0])
            if not ip.is_global:
                raise ValueError("private network URL cannot be crawled")

    def _is_external_url(self, url: str) -> bool:
        parsed = urlparse(url)
        if parsed.scheme not in {"http", "https"} or not parsed.hostname:
            return False
        hostname = parsed.hostname.lower()
        return not any(hostname == domain or hostname.endswith(f".{domain}") for domain in GOOGLE_HOSTS)

    def _ensure_not_blocked(self, body_text: str) -> None:
        normalized = body_text.lower()
        if any(marker in normalized for marker in BLOCKED_PAGE_MARKERS):
            raise RestaurantCrawlError("Google Maps returned an automated traffic verification page")

    def _names_match(self, candidate: str, expected: str) -> bool:
        normalized_candidate = self._normalize_name(candidate)
        normalized_expected = self._normalize_name(expected)
        if not normalized_candidate or not normalized_expected:
            return False
        return normalized_expected in normalized_candidate or normalized_candidate in normalized_expected

    def _normalize_name(self, value: str) -> str:
        return "".join(character.lower() for character in value if character.isalnum())
