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

plugin.@PLUGIN@.isolateFromHttpdPool
:	When `true`, starts a dedicated metrics HTTP server with its own thread pool,
	isolated from Gerrit's main `httpd.threads` pool. This ensures the metrics endpoint
	remains reachable even when Gerrit's HTTP thread pool is saturated under high load.

	When enabled, the metrics are served on a separate URL:

	```
	http://<host>:<prometheusPort>/metrics
	```

	rather than the default Gerrit endpoint:

	```
	http://<host>:<httpd.listenUrl>/plugins/@PLUGIN@/metrics
	```

	Note: only bearer token authentication is supported on the isolated server.
	Capability-based access (`View Metrics`) requires the main Gerrit HTTP server.
	Default: `false`.

plugin.@PLUGIN@.prometheusPort
:	Port for the dedicated metrics server. Only takes effect when
	`isolateFromHttpdPool` is `true`.
	Default: `9090`.

plugin.@PLUGIN@.metricsThreads
:	Number of threads in the dedicated metrics server pool. Only takes effect when
	`isolateFromHttpdPool` is `true`. Cannot be set below 4, as Jetty requires a
	minimum of 4 threads (1 acceptor, 1 selector, 1 reserved, 1 handler).
	Default: `4`.

plugin.@PLUGIN@.excludeMetrics
:   Regex pattern used to exclude metrics from the report.

	The matching is done against the Gerrit metrics names as documented on
	['Metrics'](@URL@Documentation/metrics.html). The pattern matching is done
	internally using `regex.Matcher.matches()` and can match anywhere (not
    necessarly starting at the beginning of the metric name)

    By default no metric is excluded.

	For examples:

	To exclude all metrics matching `cache` at some place in their key, use:
    `excludeMetrics = cache.*`

    To exclude multiple metrics:
	```
    [plugin "metrics-reporter-prometheus"]
        excludeMetrics = ^http/server/rest_api/.*
        excludeMetrics = ^license/cla_check_count
        excludeMetrics = ^plugin/latency/.*
        excludeMetrics = ^reviewer_suggestion/.*
        excludeMetrics = ^sequence/next_id_latency.*
    ```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
