#!/bin/sh
# Waits for the controller, fetches this node's inbound secret with the local
# admin login, then hands over to the stock jenkins-agent launcher.
set -eu

: "${JENKINS_URL:?}" "${JENKINS_AGENT_NAME:?}"
user="${JENKINS_ADMIN_USER:-admin}"
pass="${JENKINS_ADMIN_PASSWORD:-admin}"

until JENKINS_SECRET=$(curl -fsS -u "${user}:${pass}" \
        "${JENKINS_URL}/computer/${JENKINS_AGENT_NAME}/jenkins-agent.jnlp" 2>/dev/null \
      | sed -n 's/.*<argument>\([0-9a-f]\{64\}\)<\/argument>.*/\1/p') \
      && [ -n "${JENKINS_SECRET}" ]; do
    echo "waiting for ${JENKINS_URL} to expose node ${JENKINS_AGENT_NAME}..."
    sleep 5
done

export JENKINS_SECRET
exec /usr/local/bin/jenkins-agent
