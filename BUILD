load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "metrics-reporter-prometheus",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-prometheus",
        "Gerrit-Module: com.googlesource.gerrit.plugins.metricsreporterprometheus.GerritPrometheusModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.metricsreporterprometheus.GerritPrometheusHttpModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@prometheus_simpleclient//jar",
        "@prometheus_simpleclient_common//jar",
        "@prometheus_simpleclient_servlet//jar",
        "@metrics_prometheus//jar",
    ],
)
