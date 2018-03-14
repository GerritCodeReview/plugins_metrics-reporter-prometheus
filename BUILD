load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "metrics-reporter-prometheus",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-prometheus",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@prometheus_simpleclient//jar",
        "@prometheus_simpleclient_common//jar",
        "@prometheus_simpleclient_servlet//jar",
        "@metrics_prometheus//jar",
    ],
)
