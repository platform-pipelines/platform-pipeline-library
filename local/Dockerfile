FROM jenkins/jenkins:2.568.2-lts-jdk21

USER root
# docker-cli, not docker.io: on Debian 13 docker.io is only the daemon.
RUN apt-get update \
 && apt-get install -y --no-install-recommends docker-cli python3 curl \
 && rm -rf /var/lib/apt/lists/* \
 # The library is bind-mounted from the host, owned by a different uid.
 && git config --system --add safe.directory '*'
USER jenkins

# Plugins are pinned in one file. Nobody installs through the UI.
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt --latest false

COPY casc/ /var/jenkins_home/casc/
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc/jenkins.yaml
