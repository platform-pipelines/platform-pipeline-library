# Installs the wheel the Package stage built (pythonPackage), so the image runs
# exactly the artifact that was tested and published.
FROM python:3.12-slim

ARG APP_VERSION=dev
ENV APP_VERSION=$APP_VERSION \
    PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app
COPY dist/*.whl /tmp/
RUN pip install --no-cache-dir /tmp/*.whl && rm -f /tmp/*.whl

RUN useradd --system --uid 10001 app
USER 10001

EXPOSE 8080
CMD ["gunicorn", "--bind", "0.0.0.0:8080", "--workers", "2", "orders_api.app:create_app()"]
