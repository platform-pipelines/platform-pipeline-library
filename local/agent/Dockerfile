# Build agent for the local stack: the `linux docker` node from casc/jenkins.yaml.
# It carries only a docker CLI; build steps run inside CI_TOOLBOX_IMAGE through
# the host's docker socket (see vars/inContainer.groovy).
FROM jenkins/inbound-agent:3391.va_37fa_a_305d6d-2-jdk21

USER root
# docker-cli, not docker.io: on Debian 13 docker.io is only the daemon.
RUN apt-get update \
 && apt-get install -y --no-install-recommends docker-cli python3 curl \
 && rm -rf /var/lib/apt/lists/*

COPY start-agent.sh /usr/local/bin/start-agent.sh
RUN chmod +x /usr/local/bin/start-agent.sh

# Root on purpose: Docker Desktop's socket is root:root 0660 inside containers.
# Local stack only — never run a shared agent like this.
ENTRYPOINT ["/usr/local/bin/start-agent.sh"]
