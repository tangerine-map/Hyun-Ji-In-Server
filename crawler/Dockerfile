FROM mcr.microsoft.com/playwright/python:v1.61.0-noble

WORKDIR /app

COPY pyproject.toml ./
COPY src ./src

RUN pip install --no-cache-dir .

USER pwuser
EXPOSE 8000

CMD ["uvicorn", "hyunjiin_crawler.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "1"]
