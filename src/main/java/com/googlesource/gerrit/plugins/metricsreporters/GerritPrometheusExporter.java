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

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
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
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@Singleton
public class GerritPrometheusExporter extends MetricsServlet {
  CapabilityChecker capabilityChecker;
  private static final String EXCLUDE_KEY = "excludeMetrics";

  @Inject
  public GerritPrometheusExporter(
      MetricRegistry registry,
      CapabilityChecker capabilityChecker,
      PluginConfigFactory cfgFactory,
      @PluginName String pluginName) {
    this.capabilityChecker = capabilityChecker;

    /* Copy the registry to avoid filtering the global one */
    MetricRegistry filteredRegistry = new MetricRegistry();
    filteredRegistry.registerAll(registry);

    Set<String> excludedMetrics = new HashSet<>();
    excludedMetrics.addAll(
        Arrays.asList(cfgFactory.getFromGerritConfig(pluginName).getStringList(EXCLUDE_KEY)));

    excludedMetrics.forEach(
        exclude -> {
          filteredRegistry.removeMatching(
              new MetricFilter() {
                @Override
                public boolean matches(String name, Metric metric) {
                  return name.matches(exclude);
                }
              });
        });

    // Hook the Dropwizard registry into the Prometheus registry
    // via the DropwizardExports collector.
    CollectorRegistry.defaultRegistry.register(new DropwizardExports(filteredRegistry));
  }

  @Override
  public void service(ServletRequest req, ServletResponse res)
      throws ServletException, IOException {
    if (capabilityChecker.canViewMetrics()) {
      super.service(req, res);
    } else {
      HttpServletResponse httpResponse = (HttpServletResponse) res;
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden access");
    }
  }
};
