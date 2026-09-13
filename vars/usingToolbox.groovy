// True when the agent already carries every toolchain.
//
// Set CI_TOOLBOX=true on toolbox-image agents (via the node's environment in
// JCasC). When true, build steps run directly on the agent instead of being
// wrapped in a per-language container — which is the whole point of the
// toolbox: no docker socket on the agent, one pull instead of six.
//
// Usage:
//   if (usingToolbox()) { ... } else { docker.image(...).inside { ... } }
// Returns: true if env.CI_TOOLBOX is set to 'true' on this agent
def call() {
    return env.CI_TOOLBOX == 'true'
}
