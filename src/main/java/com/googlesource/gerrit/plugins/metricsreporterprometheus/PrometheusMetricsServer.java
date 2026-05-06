// Copyright (C) 2026 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.metricsreporterprometheus;

import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.common.net.HttpHeaders;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.prometheus.client.exporter.MetricsServlet;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * Serves Prometheus metrics on a dedicated port with its own thread pool, isolated from Gerrit's
 * main httpd.threads pool. When that pool is exhausted under high load, this server is unaffected.
 *
 * <p>Enabled by setting {@code plugin.metrics-reporter-prometheus.isolateFromHttpdPool = true} in
 * gerrit.config. When enabled, metrics are served at {@code http://<host>:<prometheusPort>/metrics}
 * instead of the default Gerrit HTTP endpoint.
 */
@Singleton
public class PrometheusMetricsServer implements LifecycleListener {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  static final String ISOLATE_FROM_HTTPD_POOL_KEY = "isolateFromHttpdPool";
  static final String PROMETHEUS_PORT_KEY = "prometheusPort";
  static final String METRICS_THREADS_KEY = "metricsThreads";
  static final int DEFAULT_PROMETHEUS_PORT = 9090;
  static final int DEFAULT_METRICS_THREADS = 4;

  private final Server server;

  @Inject
  public PrometheusMetricsServer(PluginConfigFactory cfgFactory, @PluginName String pluginName) {
    GerritBuildInformationMetric buildInfoMetric = new GerritBuildInformationMetric();
    boolean isolate =
        cfgFactory.getFromGerritConfig(pluginName).getBoolean(ISOLATE_FROM_HTTPD_POOL_KEY, false);
    if (!isolate) {
      server = null;
      return;
    }
    String bearerToken =
        cfgFactory.getFromGerritConfig(pluginName).getString("prometheusBearerToken");
    int numThreads =
        Math.max(
            DEFAULT_METRICS_THREADS,
            cfgFactory
                .getFromGerritConfig(pluginName)
                .getInt(METRICS_THREADS_KEY, DEFAULT_METRICS_THREADS));

    QueuedThreadPool threadPool = new QueuedThreadPool(numThreads, 1);
    threadPool.setName("prometheus-metrics");

    server = new Server(threadPool);
    ServerConnector connector = new ServerConnector(server, 1, 1);
    connector.setPort(
        cfgFactory
            .getFromGerritConfig(pluginName)
            .getInt(PROMETHEUS_PORT_KEY, DEFAULT_PROMETHEUS_PORT));
    server.addConnector(connector);

    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(
        new ServletHolder(new AuthenticatedMetricsServlet(buildInfoMetric, bearerToken)),
        "/metrics");
    server.setHandler(handler);
  }

  @Override
  public void start() {
    if (server == null) {
      return;
    }
    try {
      server.start();
      logger.atInfo().log("Prometheus metrics server started");
    } catch (Exception e) {
      throw new RuntimeException("Failed to start Prometheus metrics server", e);
    }
  }

  @Override
  public void stop() {
    if (server == null) {
      return;
    }
    try {
      server.stop();
      logger.atInfo().log("Prometheus metrics server stopped");
    } catch (Exception e) {
      logger.atWarning().withCause(e).log("Error stopping Prometheus metrics server");
    }
  }

  private static class AuthenticatedMetricsServlet extends HttpServlet {
    private final GerritBuildInformationMetric buildInfoMetric;
    private final MetricsServlet delegate = new MetricsServlet();
    private final String prometheusBearerToken;

    AuthenticatedMetricsServlet(
        GerritBuildInformationMetric buildInfoMetric, String prometheusBearerToken) {
      this.buildInfoMetric = buildInfoMetric;
      this.prometheusBearerToken = prometheusBearerToken;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
      if (!isAuthorized(req)) {
        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden access");
        return;
      }
      buildInfoMetric.compute();
      delegate.service(req, res);
    }

    private boolean isAuthorized(HttpServletRequest req) {
      if (Strings.isNullOrEmpty(prometheusBearerToken)) {
        return true;
      }
      return Optional.ofNullable(req.getHeader(HttpHeaders.AUTHORIZATION))
          .map(h -> h.equals("Bearer " + prometheusBearerToken))
          .orElse(false);
    }
  }
}
