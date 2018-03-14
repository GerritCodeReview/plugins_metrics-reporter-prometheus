load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'prometheus_simpleclient',
    artifact = 'io.prometheus:simpleclient:0.2.0',
    sha1 = 'be8de6a5a01f25074be3b27a8db4448c9cce0168',
  )
  maven_jar(
    name = 'prometheus_simpleclient_common',
    artifact = 'io.prometheus:simpleclient_common:0.2.0',
    sha1 = '42d513358b26ae44137c620fa517d37b5e707ae1',
  )
  maven_jar(
    name = 'prometheus_simpleclient_servlet',
    artifact = 'io.prometheus:simpleclient_servlet:0.2.0',
    sha1 = '98b6235fe4277ce0eb08ba8d171e57da9298a6f7',
  )
  maven_jar(
    name = 'metrics_prometheus',
    artifact = 'io.prometheus:simpleclient_dropwizard:0.2.0',
    sha1 = '7a73c8db679de5193728fdc205d9734dbdd3d9f9',
  )
