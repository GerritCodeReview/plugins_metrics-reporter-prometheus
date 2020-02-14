load("//tools/bzl:junit.bzl", "junit_tests")
load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "metrics-reporter-prometheus",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-prometheus",
        "Gerrit-Module: com.googlesource.gerrit.plugins.metricsreporters.GerritPrometheusModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.metricsreporters.GerritPrometheusHttpModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@prometheus_simpleclient//jar",
        "@prometheus_simpleclient_common//jar",
        "@prometheus_simpleclient_servlet//jar",
        "@metrics_prometheus//jar",
    ],
)

junit_tests(
    name = "metrics_reporter_prometheus_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    tags = [
        "local",
        "metrics-reporter-prometheus",
    ],
    deps = [
            ":metrics-reporter-prometheus__plugin_test_deps",
            "//java/com/google/gerrit/server",
            "//java/com/google/gerrit/reviewdb:server",
            "@mockito//jar",
            "@truth//jar",
            "@dropwizard-core//jar",
            "@servlet-api-3_1//jar",
            "@jgit-lib//jar",
            "@guice-library//jar",
            '@byte-buddy//jar',
            '@objenesis//jar',
    ],
)

java_library(
    name = "metrics-reporter-prometheus__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = [
        ":metrics-reporter-prometheus__plugin",
    ],
)
