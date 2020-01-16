Configuration
=============

To access the monitoring URL, a user must be a member of a group that is granted
the ‘View Metrics’ capability (provided by this plugin) or the ‘Administrate
Server’ capability. Alternatively, authentication using prometheus bearer token
is also supported.

This capability can be configured in the 'Global Capabilities' section of the
['All-Projects'](@URL@#/admin/projects/All-Projects,access) access right.

It is possible to allow anonymous access to the metrics by giving the capability
to the 'Anonymous Users' group.

File 'gerrit.config'
--------------------

The following fields can be specified in `$site_path/etc/gerrit.config` file:

plugin.@PLUGIN@.prometheusBearerToken
:	Bearer token for allowing Prometheus to query Gerrit metrics through its scraper.
	Defaults to undefine.

When defined, access to the plugins/@PLUGIN@/metrics URL does not require any
authentication and do not check any ACL related to the ‘View Metrics’ global capability.
See [Prometheus documentation](https://prometheus.io/docs/prometheus/latest/configuration/configuration)
for how to configure the integration with Prometheus.

File '@PLUGIN@.config'
--------------------

The following fields can be specified in `$site_path/etc/@PLUGIN@.config` file:

```
[metrics]
  exclude = caches.*
  exclude = events.*
```

metrics.exclude
:   String used to exclude metrics from the report. It can be specified multiple times.
    Parsed as regular expression. Note, ^ and $ are automatically added around the string.

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
