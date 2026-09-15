# Copies the binary goPackage built and tested; the ECS task definition runs
# it with a read-only root filesystem, which distroless static is happy with.
FROM gcr.io/distroless/static-debian12:nonroot

ARG APP_VERSION=dev
LABEL org.opencontainers.image.version=$APP_VERSION

COPY dist/payments-api /payments-api

EXPOSE 8080
USER nonroot:nonroot
ENTRYPOINT ["/payments-api"]
