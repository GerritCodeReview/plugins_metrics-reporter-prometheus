// Copyright (C) 2021 The Android Open Source Project
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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import io.prometheus.client.Counter;

public class GerritConfigInfoMetric {
  static final Counter info =
      Counter.build()
          .name("config_current")
          .help("Current settings")
          .labelNames("gerrit_config")
          .register();

  private final MetricRegistry registry;

  GerritConfigInfoMetric(MetricRegistry registry) {
    this.registry = registry;
  }

  public void compute() {
    @SuppressWarnings("unchecked")
    Gauge<String> gerritConfigMetric = registry.gauge("config/current", null);
    if (gerritConfigMetric != null) {
      info.labels(gerritConfigMetric.getValue());
    }
  }
}
