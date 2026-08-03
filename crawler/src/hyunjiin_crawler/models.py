from enum import StrEnum

from pydantic import BaseModel, Field, field_validator


class CrawlSourceKind(StrEnum):
    GOOGLE_MAPS = "GOOGLE_MAPS"
    WEBSITE = "WEBSITE"


class RestaurantCrawlRequest(BaseModel):
    restaurantName: str = Field(min_length=1, max_length=200)
    address: str = Field(min_length=1, max_length=500)
    latitude: float | None = Field(default=None, ge=-90, le=90)
    longitude: float | None = Field(default=None, ge=-180, le=180)
    missingFields: list[str] = Field(min_length=1, max_length=20)
    existingMenuNames: list[str] = Field(default_factory=list, max_length=500)
    maxSources: int = Field(default=5, ge=1, le=20)

    @field_validator("restaurantName", "address")
    @classmethod
    def strip_text(cls, value: str) -> str:
        stripped = value.strip()
        if not stripped:
            raise ValueError("must not be blank")
        return stripped


class CrawlSource(BaseModel):
    title: str
    url: str
    content: str
    kind: CrawlSourceKind
    fetched: bool = True


class RestaurantCrawlResponse(BaseModel):
    restaurantName: str
    sources: list[CrawlSource]


class ExtractedCandidate(BaseModel):
    field: str
    valueText: str
    valueNumber: int | None = None
    representative: bool = False
    sourceUrl: str
    evidence: str
    confidence: float = Field(ge=0, le=1)


class RestaurantEnrichmentResponse(BaseModel):
    restaurantName: str
    sourceCount: int = Field(ge=0)
    fetchedCount: int = Field(ge=0)
    candidates: list[ExtractedCandidate]


class CrawlerErrorResponse(BaseModel):
    code: str
    message: str
