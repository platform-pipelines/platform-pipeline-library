# The pipeline's Package stage has already built dist/edge-router (goPackage),
# so the image only copies the tested binary in — it does not recompile.
FROM gcr.io/distroless/static-debian12:nonroot

ARG APP_VERSION=dev
LABEL org.opencontainers.image.version=$APP_VERSION

COPY dist/edge-router /edge-router

EXPOSE 8080
USER nonroot:nonroot
ENTRYPOINT ["/edge-router"]
