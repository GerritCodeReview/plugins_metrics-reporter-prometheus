// Copyright (C) 2018 The Android Open Source Project
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
package com.googlesource.gerrit.plugins.metricsreporters;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.prometheus.client.exporter.MetricsServlet;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@Singleton
public class GerritPrometheusExporter extends MetricsServlet {

  private final CapabilityChecker capabilityChecker;
  private final String prometheusBearerToken;
  public Set<String> excludedMetrics;
  public MetricRegistry filteredRegistry;

  private PluginConfigFactory cfgFactory;
  private GerritPrometheusExporterConfig gerritPrometheusExporterConfig =
      new GerritPrometheusExporterConfig(cfgFactory);

  @Inject
  public GerritPrometheusExporter(
      MetricRegistry registry,
      CapabilityChecker capabilityChecker,
      GerritPrometheusExporterConfig gerritPrometheusExporterConfig) {
    this.gerritPrometheusExporterConfig = gerritPrometheusExporterConfig;
    excludedMetrics = gerritPrometheusExporterConfig.getExcludedMetrics();
    filteredRegistry = new MetricRegistry();
    this.capabilityChecker = capabilityChecker;
    this.prometheusBearerToken = gerritPrometheusExporterConfig.getPrometheusBearerToken();
    registry.addListener(new GerritPrometheusMetricRegistryListener(this));
  }

  @Override
  public void service(ServletRequest req, ServletResponse res)
      throws ServletException, IOException {
    if (capabilityChecker.canViewMetrics() || canExportUsingPrometheusBearerToken(req)) {
      super.service(req, res);
    } else {
      HttpServletResponse httpResponse = (HttpServletResponse) res;
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden access");
    }
  }

  private boolean canExportUsingPrometheusBearerToken(ServletRequest req) {
    if (Strings.isNullOrEmpty(prometheusBearerToken)) {
      return false;
    }

    HttpServletRequest httpRequest = (HttpServletRequest) req;
    return Optional.ofNullable(httpRequest.getHeader(HttpHeaders.AUTHORIZATION))
        .map(h -> h.equals("Bearer " + prometheusBearerToken))
        .orElse(false);
  }
}
