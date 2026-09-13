// True when this repo describes infrastructure rather than an application.
def call(Map cfg) {
    return cfg.buildTool in configInfraTools()
}
