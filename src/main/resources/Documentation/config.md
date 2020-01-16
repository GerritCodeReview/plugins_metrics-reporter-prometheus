Configuration
=============

The configuration of the @PLUGIN@ plugin is done in the `gerrit.config`
file.

To access the monitoring URL, a user must be a member of a group that is granted
the ‘View Metrics’ capability (provided by this plugin) or the ‘Administrate
Server’ capability. Alternatively, authentication using prometheus bearer token
is also supported.

This capability can be configured in the 'Global Capabilities' section of the
['All-Projects'](@URL@#/admin/projects/All-Projects,access) access right.

It is possible to allow anonymous access to the metrics by giving the capability
to the 'Anonymous Users' group.

plugin.@PLUGIN@.prometheusBearerToken
:	Bearer token for allowing Prometheus to query Gerrit metrics through its scraper.
	Defaults to undefine.

When defined, access to the plugins/@PLUGIN@/metrics URL does not require any
authentication and do not check any ACL related to the ‘View Metrics’ global capability.
See [Prometheus documentation](https://prometheus.io/docs/prometheus/latest/configuration/configuration)
for how to configure the integration with Prometheus.

plugin.@PLUGIN@.excludeMetrics
:   String used to exclude metrics from the report. It can be specified multiple times.
    Parsed as regular expression. Note, ^ and $ are automatically added around the string.
    By default no metric is excluded.
    For example, to exclude all cache metrics, use: `excludeMetrics = cache.*`

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
