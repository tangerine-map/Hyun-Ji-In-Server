from dataclasses import dataclass
import os


def _positive_int(name: str, default: int, maximum: int) -> int:
    raw_value = os.getenv(name)
    if raw_value is None:
        return default
    try:
        value = int(raw_value)
    except ValueError as exception:
        raise ValueError(f"{name} must be an integer") from exception
    if value < 1 or value > maximum:
        raise ValueError(f"{name} must be between 1 and {maximum}")
    return value


@dataclass(frozen=True)
class CrawlerSettings:
    parallelism: int = 3
    navigation_timeout_ms: int = 30_000
    max_page_characters: int = 12_000
    max_response_bytes: int = 1_000_000
    browser_executable_path: str | None = None
    ollama_base_url: str = "http://localhost:11434"
    ollama_model: str = "qwen3.5:4b"

    @classmethod
    def from_environment(cls) -> "CrawlerSettings":
        return cls(
            parallelism=_positive_int("CRAWLER_PARALLELISM", 3, 10),
            navigation_timeout_ms=_positive_int("CRAWLER_NAVIGATION_TIMEOUT_MS", 30_000, 120_000),
            max_page_characters=_positive_int("CRAWLER_MAX_PAGE_CHARACTERS", 12_000, 100_000),
            max_response_bytes=_positive_int("CRAWLER_MAX_RESPONSE_BYTES", 1_000_000, 10_000_000),
            browser_executable_path=os.getenv("CRAWLER_BROWSER_EXECUTABLE_PATH"),
            ollama_base_url=os.getenv("OLLAMA_BASE_URL", "http://localhost:11434").rstrip("/"),
            ollama_model=os.getenv("OLLAMA_MODEL", "qwen3.5:4b"),
        )
