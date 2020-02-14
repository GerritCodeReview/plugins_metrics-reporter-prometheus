// Copyright (C) 2020 The Android Open Source Project
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

import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class GerritPrometheusExporterConfig {
  private static final String PLUGIN_NAME = "metrics-reporter-prometheus";
  private static final String PROMETHEUS_BEARER_TOKEN = "prometheusBearerToken";
  private static final String EXCLUDE_KEY = "excludeMetrics";

  private final PluginConfigFactory cfgFactory;

  @Inject
  GerritPrometheusExporterConfig(PluginConfigFactory cfgFactory) {
    this.cfgFactory = cfgFactory;
  }

  public Set<String> getExcludedMetrics() {
    Set<String> excludedMetrics = new HashSet<>();
    excludedMetrics.addAll(
        Arrays.asList(cfgFactory.getFromGerritConfig(PLUGIN_NAME).getStringList(EXCLUDE_KEY)));
    return excludedMetrics;
  }

  public String getPrometheusBearerToken() {
    return cfgFactory.getFromGerritConfig(PLUGIN_NAME).getString(PROMETHEUS_BEARER_TOKEN);
  }
}
