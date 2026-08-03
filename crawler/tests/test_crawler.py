from hyunjiin_crawler.config import CrawlerSettings
from hyunjiin_crawler.crawler import PlaywrightRestaurantCrawler


def test_google_domains_are_not_external_sources() -> None:
    crawler = PlaywrightRestaurantCrawler(CrawlerSettings())

    assert not crawler._is_external_url("https://www.google.com/maps/place/test")
    assert not crawler._is_external_url("https://lh3.googleusercontent.com/image")
    assert crawler._is_external_url("https://restaurant.example/menu")


def test_restaurant_names_are_compared_without_spacing() -> None:
    crawler = PlaywrightRestaurantCrawler(CrawlerSettings())

    assert crawler._names_match("가는곶 세화 - 카페", "가는곶 세화")
    assert crawler._names_match("가는곶세화", "가는곶 세화")
