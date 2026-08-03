import pytest

from hyunjiin_crawler.config import CrawlerSettings


def test_reads_parallelism_from_environment(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setenv("CRAWLER_PARALLELISM", "4")
    monkeypatch.setenv("OLLAMA_BASE_URL", "http://ollama:11434/")
    monkeypatch.setenv("OLLAMA_MODEL", "qwen3.5:9b")

    settings = CrawlerSettings.from_environment()

    assert settings.parallelism == 4
    assert settings.ollama_base_url == "http://ollama:11434"
    assert settings.ollama_model == "qwen3.5:9b"


def test_rejects_invalid_parallelism(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setenv("CRAWLER_PARALLELISM", "0")

    with pytest.raises(ValueError):
        CrawlerSettings.from_environment()
