Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `gerrit.config`
file.

To access the monitoring URL, a user must be a member of a group that is granted
the ‘View Metrics’ capability (provided by this plugin) or the ‘Administrate
Server’ capability.This plugin requires no configuration.

This capability can be configured in the 'Global Capabilities' section of the
['All-Projects'](@URL@#/admin/projects/All-Projects,access) access right.

It is possible to allow anonymous access to the metrics by giving the capability
to the 'Anonymous Users' group.

<a id="prometheusBearerToken"> `plugin.@PLUGIN@.prometheusBearerToken`

  Bearer token for allowing Prometheus to query Gerrit metrics
  through its scraper.
  When defined, access to the plugins/@PLUGIN@/metrics URL
  does not require any authentication and do not check any ACL related
  to the ‘View Metrics’ global capability.
  See
  <a href="https://prometheus.io/docs/prometheus/latest/configuration/configuration/">Prometheus documentation</a>
  for how to configure the integration with Prometheus.
  By default undefined.

[Back to @PLUGIN@ documentation index][index]

[index]: index.html