# CI toolbox: one image with every toolchain and scanner the pipeline uses.
#
# Why one image instead of per-language containers:
#   - The agent needs no docker socket for builds, so agents stay unprivileged.
#   - One `docker pull` per agent instead of six, which dominates cold-start time.
#   - Tool versions are pinned in one file and upgraded in one PR.
#
# Cost: the image is large (~2.5GB) and a Go-only repo still pulls the JDK.
# If that becomes the bottleneck, split into toolbox-jvm / toolbox-scripting
# and set `toolboxImage:` per repo in .ci/config.yaml.
#
# Every version below was resolved from the project's release feed and every
# download URL verified to resolve before being pinned here.
#
#   docker build -t ghcr.io/acme/ci-toolbox:1.0.0 .

# ---------------------------------------------------------------------------
# Stage 1: fetch static binaries. Keeping downloads out of the final stage
# means curl, the tarballs, and the checksums never reach the shipped layers.
# ---------------------------------------------------------------------------
FROM debian:12-slim AS fetch

ARG TRIVY_VERSION=0.74.0
ARG GITLEAKS_VERSION=8.30.1
ARG GOLANGCI_VERSION=2.13.1
ARG HADOLINT_VERSION=2.15.1
ARG ARGOCD_VERSION=3.5.1
ARG YQ_VERSION=4.53.6
ARG SHELLCHECK_VERSION=0.11.0
ARG SONAR_SCANNER_VERSION=6.2.1.4610
# v3 exists; pinning latest v2 because v3 changed attestation flags and the
# ecosystem (policy controllers, verifiers) is still catching up.
ARG COSIGN_VERSION=2.6.5
ARG TFLINT_VERSION=0.64.0
ARG CONFTEST_VERSION=0.69.0

RUN apt-get update && apt-get install -y --no-install-recommends \
        curl ca-certificates tar xz-utils unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /out

RUN set -eux; \
    # Trivy
    curl -fsSL -o trivy.tgz \
      "https://github.com/aquasecurity/trivy/releases/download/v${TRIVY_VERSION}/trivy_${TRIVY_VERSION}_Linux-64bit.tar.gz"; \
    tar -xzf trivy.tgz trivy; \
    \
    # Gitleaks
    curl -fsSL -o gitleaks.tgz \
      "https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz"; \
    tar -xzf gitleaks.tgz gitleaks; \
    \
    # golangci-lint (v2 — note the flag syntax differs from v1)
    curl -fsSL -o gcl.tgz \
      "https://github.com/golangci/golangci-lint/releases/download/v${GOLANGCI_VERSION}/golangci-lint-${GOLANGCI_VERSION}-linux-amd64.tar.gz"; \
    tar -xzf gcl.tgz --strip-components=1 "golangci-lint-${GOLANGCI_VERSION}-linux-amd64/golangci-lint"; \
    \
    # hadolint
    curl -fsSL -o hadolint \
      "https://github.com/hadolint/hadolint/releases/download/v${HADOLINT_VERSION}/hadolint-Linux-x86_64"; \
    \
    # Argo CD CLI
    curl -fsSL -o argocd \
      "https://github.com/argoproj/argo-cd/releases/download/v${ARGOCD_VERSION}/argocd-linux-amd64"; \
    \
    # yq
    curl -fsSL -o yq \
      "https://github.com/mikefarah/yq/releases/download/v${YQ_VERSION}/yq_linux_amd64"; \
    \
    # cosign (image signing and SBOM attestation)
    curl -fsSL -o cosign \
      "https://github.com/sigstore/cosign/releases/download/v${COSIGN_VERSION}/cosign-linux-amd64"; \
    \
    # tflint
    curl -fsSL -o tflint.zip \
      "https://github.com/terraform-linters/tflint/releases/download/v${TFLINT_VERSION}/tflint_linux_amd64.zip"; \
    unzip -q tflint.zip tflint; rm tflint.zip; \
    \
    # conftest (OPA policy checks over terraform plans)
    curl -fsSL -o conftest.tgz \
      "https://github.com/open-policy-agent/conftest/releases/download/v${CONFTEST_VERSION}/conftest_${CONFTEST_VERSION}_Linux_x86_64.tar.gz"; \
    tar -xzf conftest.tgz conftest; rm conftest.tgz; \
    \
    # shellcheck
    curl -fsSL -o sc.tar.xz \
      "https://github.com/koalaman/shellcheck/releases/download/v${SHELLCHECK_VERSION}/shellcheck-v${SHELLCHECK_VERSION}.linux.x86_64.tar.xz"; \
    tar -xJf sc.tar.xz --strip-components=1 "shellcheck-v${SHELLCHECK_VERSION}/shellcheck"; \
    \
    chmod +x trivy gitleaks golangci-lint hadolint argocd yq shellcheck cosign tflint conftest; \
    rm -f ./*.tgz ./*.tar.xz

# SonarScanner CLI ships as a zip with a bundled JRE we discard, since the
# image already has a JDK.
RUN set -eux; \
    curl -fsSL -o sonar.zip \
      "https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-${SONAR_SCANNER_VERSION}-linux-x64.zip"; \
    unzip -q sonar.zip; \
    mv "sonar-scanner-${SONAR_SCANNER_VERSION}-linux-x64" /out/sonar-scanner; \
    rm -rf /out/sonar-scanner/jre sonar.zip

# ---------------------------------------------------------------------------
# Stage 2: the runtime image.
# Base is Temurin JDK because the JVM is the fiddliest thing to install; the
# other runtimes are simpler to layer on than the JDK is.
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-noble

# Maven stays on 3.x: 4.0.0 is released but is a large behavioural change and
# not every plugin in the ecosystem has caught up.
ARG MAVEN_VERSION=3.9.16
ARG GRADLE_VERSION=9.8.0
# Node 24 is Active LTS. 26 is current but not yet LTS.
ARG NODE_MAJOR=24
# golangci-lint 2.13 added go1.27 support, so these two move together.
ARG GO_VERSION=1.27.0
ARG RUFF_VERSION=0.16.4
ARG TERRAFORM_VERSION=1.17.0
ARG CHECKOV_VERSION=3.3.15
ARG CFN_LINT_VERSION=1.55.1

ENV DEBIAN_FRONTEND=noninteractive \
    LANG=C.UTF-8 \
    JAVA_HOME=/opt/java/openjdk

# --- base OS packages -------------------------------------------------------
# git and curl are needed by the pipeline itself; python3 backs the library's
# resource scripts; jq is for ad-hoc debugging in a shell on the agent.
RUN apt-get update && apt-get install -y --no-install-recommends \
        git curl ca-certificates gnupg jq bash tar gzip unzip xz-utils \
        python3 python3-pip python3-venv \
        openssh-client rsync make \
    && rm -rf /var/lib/apt/lists/*

# --- Node ------------------------------------------------------------------
RUN set -eux; \
    curl -fsSL "https://deb.nodesource.com/setup_${NODE_MAJOR}.x" | bash -; \
    apt-get install -y --no-install-recommends nodejs; \
    rm -rf /var/lib/apt/lists/*; \
    npm install -g npm@latest; \
    npm cache clean --force

# --- Go --------------------------------------------------------------------
ENV GOROOT=/usr/local/go \
    GOPATH=/home/jenkins/go \
    GOTOOLCHAIN=local
RUN set -eux; \
    curl -fsSL -o go.tgz "https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz"; \
    tar -C /usr/local -xzf go.tgz; \
    rm go.tgz

# --- Maven -----------------------------------------------------------------
ENV MAVEN_HOME=/opt/maven
RUN set -eux; \
    curl -fsSL -o mvn.tgz \
      "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"; \
    mkdir -p "$MAVEN_HOME"; \
    tar -xzf mvn.tgz -C "$MAVEN_HOME" --strip-components=1; \
    rm mvn.tgz

# --- Gradle ----------------------------------------------------------------
ENV GRADLE_HOME=/opt/gradle
RUN set -eux; \
    curl -fsSL -o gradle.zip \
      "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"; \
    unzip -q gradle.zip -d /opt; \
    mv "/opt/gradle-${GRADLE_VERSION}" "$GRADLE_HOME"; \
    rm gradle.zip

# --- Terraform -------------------------------------------------------------
# HashiCorp publishes binaries only from releases.hashicorp.com; there are no
# binaries attached to the GitHub releases.
RUN set -eux; \
    curl -fsSL -o tf.zip \
      "https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip"; \
    unzip -q tf.zip -d /usr/local/bin; \
    rm tf.zip; \
    terraform version

# --- AWS CLI v2 ------------------------------------------------------------
# v2 is not on PyPI; the official installer is the only supported route.
RUN set -eux; \
    curl -fsSL -o awscli.zip "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip"; \
    unzip -q awscli.zip; \
    ./aws/install; \
    rm -rf awscli.zip aws; \
    aws --version

# --- Python tooling --------------------------------------------------------
# --break-system-packages because Ubuntu marks the system Python as externally
# managed (PEP 668). In a disposable build container that protection buys
# nothing and a venv would just complicate every step's PATH.
RUN pip3 install --no-cache-dir --break-system-packages \
        "ruff==${RUFF_VERSION}" \
        mypy \
        pytest pytest-cov \
        build \
        twine \
        "checkov==${CHECKOV_VERSION}" \
        "cfn-lint==${CFN_LINT_VERSION}"

# --- static binaries from stage 1 ------------------------------------------
COPY --from=fetch /out/trivy          /usr/local/bin/trivy
COPY --from=fetch /out/gitleaks       /usr/local/bin/gitleaks
COPY --from=fetch /out/golangci-lint  /usr/local/bin/golangci-lint
COPY --from=fetch /out/hadolint       /usr/local/bin/hadolint
COPY --from=fetch /out/argocd         /usr/local/bin/argocd
COPY --from=fetch /out/yq             /usr/local/bin/yq
COPY --from=fetch /out/shellcheck     /usr/local/bin/shellcheck
COPY --from=fetch /out/cosign         /usr/local/bin/cosign
COPY --from=fetch /out/tflint         /usr/local/bin/tflint
COPY --from=fetch /out/conftest       /usr/local/bin/conftest
COPY --from=fetch /out/sonar-scanner  /opt/sonar-scanner

ENV PATH="/usr/local/go/bin:${MAVEN_HOME}/bin:${GRADLE_HOME}/bin:/opt/sonar-scanner/bin:${GOPATH}/bin:${PATH}"

# --- unprivileged user ------------------------------------------------------
# uid 1000 matches the default Jenkins agent uid, so a mounted workspace does
# not end up root-owned.
RUN groupadd -g 1000 jenkins \
 && useradd -u 1000 -g jenkins -m -s /bin/bash jenkins \
 && mkdir -p /home/jenkins/agent "$GOPATH" \
 && chown -R jenkins:jenkins /home/jenkins

USER jenkins
WORKDIR /home/jenkins/agent

# Warm the Go build cache directory so the first build does not pay to create it.
RUN go env -w GOCACHE=/home/jenkins/.cache/go-build && go version

LABEL org.opencontainers.image.title="ci-toolbox" \
      org.opencontainers.image.description="All toolchains and scanners for company-pipeline" \
      org.opencontainers.image.source="https://github.com/acme/jenkins-shared-library"
