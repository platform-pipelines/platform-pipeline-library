.DEFAULT_GOAL := help

# Prefer a real JDK 17 over whatever `java` happens to resolve to — Groovy
# 3.0.19 (this project's compiler) cannot read class files newer than that.
JAVA17 := $(shell /usr/libexec/java_home -v 17 2>/dev/null)
ifneq ($(JAVA17),)
export JAVA_HOME := $(JAVA17)
endif

GRADLE      := ./gradlew
COMPOSE     := docker compose -f local/docker-compose.yml
TOOLBOX_TAG := ci-toolbox:local

.PHONY: help test lint check local-up local-down local-logs local-clean local-restart toolbox-build toolbox-verify docs-install docs-serve docs-build

help:
	@echo "Library:"
	@echo "  make test              run the Groovy/jenkins-pipeline-unit test suite"
	@echo "  make lint              run CodeNarc over vars/ and test/"
	@echo "  make check             test + lint (what CI runs)"
	@echo ""
	@echo "Local stack (Jenkins + SonarQube + Nexus, see local/docker-compose.yml):"
	@echo "  make local-up          build and start the stack in the background (NEXUS=1 adds Nexus)"
	@echo "  make local-logs        follow logs for all services"
	@echo "  make local-down        stop the stack, keep data volumes"
	@echo "  make local-clean       stop the stack and delete data volumes"
	@echo "  make local-restart     local-down + local-up"
	@echo ""
	@echo "Toolbox image (see toolbox/):"
	@echo "  make toolbox-build     build the ci-toolbox image locally"
	@echo "  make toolbox-verify    build, then run toolbox/verify.sh inside it"
	@echo ""
	@echo "Docs site (see docs/, mkdocs.yml):"
	@echo "  make docs-install      install mkdocs + mkdocs-material into .venv-docs"
	@echo "  make docs-serve        serve the docs site locally with live reload"
	@echo "  make docs-build        build the static docs site into site/"

test:
	$(GRADLE) test --console=plain

lint:
	$(GRADLE) codenarcMain codenarcTest --console=plain

check: test lint

# docker compose reads local/.env on its own; this only seeds it from the
# template and warns when the GitHub token is still blank.
local/.env:
	cp local/.env.example local/.env
	@echo "Created local/.env — add GITHUB_USER / GITHUB_TOKEN, then run make local-up again to load them."

local-up: local/.env
	@grep -q '^GITHUB_TOKEN=..*' local/.env || [ -n "$$GITHUB_TOKEN" ] \
		|| echo "[WARN]  GITHUB_TOKEN is empty in local/.env — github-token / github-scm credentials will be blank"
	$(COMPOSE) $(if $(NEXUS),--profile nexus) up -d --build
	@echo "Jenkins   http://localhost:8080  (admin / \$${JENKINS_ADMIN_PASSWORD:-admin})"
	@echo "SonarQube http://localhost:9000  (admin/admin)"
	@$(if $(NEXUS),echo "Nexus     http://localhost:8081",echo "Nexus     not started (make local-up NEXUS=1)")

local-logs:
	$(COMPOSE) --profile nexus logs -f

# --profile nexus so an opt-in Nexus is stopped too.
local-down:
	$(COMPOSE) --profile nexus down

local-clean:
	$(COMPOSE) --profile nexus down -v

local-restart: local-down local-up

# The toolbox is amd64-only (see toolbox/Dockerfile); pinning the platform
# keeps the build working on Apple Silicon instead of crashing under emulation.
TOOLBOX_PLATFORM := linux/amd64

toolbox-build:
	docker build --platform $(TOOLBOX_PLATFORM) -t $(TOOLBOX_TAG) toolbox/

toolbox-verify: toolbox-build
	docker run --rm --platform $(TOOLBOX_PLATFORM) -i $(TOOLBOX_TAG) bash < toolbox/verify.sh

DOCS_VENV := .venv-docs

docs-install:
	uv venv $(DOCS_VENV)
	uv pip install --python $(DOCS_VENV)/bin/python -r docs/requirements.txt

docs-serve: docs-install
	uv run --python $(DOCS_VENV)/bin/python mkdocs serve

docs-build: docs-install
	uv run --python $(DOCS_VENV)/bin/python mkdocs build
