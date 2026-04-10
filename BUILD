load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
)
load(
    "@com_googlesource_gerrit_bazlets//tools:in_gerrit_tree.bzl",
    "in_gerrit_tree_enabled",
)
load(
    "@com_googlesource_gerrit_bazlets//tools:runtime_jars_allowlist.bzl",
    "runtime_jars_allowlist_test",
)
load(
    "@com_googlesource_gerrit_bazlets//tools:runtime_jars_overlap.bzl",
    "runtime_jars_overlap_test",
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

runtime_jars_allowlist_test(
    name = "check_metrics-reporter-prometheus_third_party_runtime_jars",
    allowlist = ":metrics-reporter-prometheus_third_party_runtime_jars.allowlist.txt",
    hint = "plugins/metrics-reporter-prometheus:check_metrics-reporter-prometheus_third_party_runtime_jars_manifest",
    target = ":metrics-reporter-prometheus__plugin",
)

runtime_jars_overlap_test(
    name = "metrics-reporter-prometheus_no_overlap_with_gerrit",
    against = "//:headless.war.jars.txt",
    hint = "Exclude overlaps via maven.install(excluded_artifacts=[...]) and re-run this test.",
    target = ":metrics-reporter-prometheus__plugin",
    target_compatible_with = in_gerrit_tree_enabled(),
)
