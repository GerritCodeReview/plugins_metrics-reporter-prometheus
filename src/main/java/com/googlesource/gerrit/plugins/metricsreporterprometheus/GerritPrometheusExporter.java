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
package com.googlesource.gerrit.plugins.metricsreporterprometheus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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
  private static final String PROMETHEUS_BEARER_TOKEN = "prometheusBearerToken";
  private static final String EXCLUDE_KEY = "excludeMetrics";

  private final CapabilityChecker capabilityChecker;
  private final String prometheusBearerToken;

  @Inject
  public GerritPrometheusExporter(
      MetricRegistry registry,
      CapabilityChecker capabilityChecker,
      PluginConfigFactory cfgFactory,
      @PluginName String pluginName) {
    this.capabilityChecker = capabilityChecker;
    this.prometheusBearerToken =
        cfgFactory.getFromGerritConfig(pluginName).getString(PROMETHEUS_BEARER_TOKEN);

    Set<String> excludedMetrics = new HashSet<>();
    excludedMetrics.addAll(
        Arrays.asList(cfgFactory.getFromGerritConfig(pluginName).getStringList(EXCLUDE_KEY)));

    FilteredMetricRegistry filteredRegistry =
        new FilteredMetricRegistry(
            registry, s -> excludedMetrics.stream().anyMatch(e -> s.matches(e)));

    // Hook the Dropwizard registry into the Prometheus registry
    // via the DropwizardExports collector.
    CollectorRegistry.defaultRegistry.register(new DropwizardExports(filteredRegistry));
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
