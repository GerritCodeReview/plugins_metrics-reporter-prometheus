load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_dependency_tests",
)

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
        "@metrics-reporter-prometheus_plugin_deps//:io_prometheus_simpleclient",
        "@metrics-reporter-prometheus_plugin_deps//:io_prometheus_simpleclient_servlet",
        "@metrics-reporter-prometheus_plugin_deps//:io_prometheus_simpleclient_dropwizard",
    ],
)

gerrit_plugin_dependency_tests(plugin = "metrics-reporter-prometheus")
