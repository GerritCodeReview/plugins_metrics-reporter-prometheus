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

package com.googlesource.gerrit.plugins.metricsreporterprometheus;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.util.SortedMap;
import java.util.function.Predicate;

public class FilteredMetricRegistry extends MetricRegistry {
  private final MetricRegistry registry;
  private final MetricFilter nonExcluded;

  FilteredMetricRegistry(MetricRegistry registry, Predicate<String> exclusionFilter) {
    this.registry = registry;
    this.nonExcluded = (n, m) -> !exclusionFilter.test(n);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public SortedMap<String, Gauge> getGauges() {
    return registry.getGauges(nonExcluded);
  }

  @Override
  public SortedMap<String, Counter> getCounters() {
    return registry.getCounters(nonExcluded);
  }

  @Override
  public SortedMap<String, Histogram> getHistograms() {
    return registry.getHistograms(nonExcluded);
  }

  @Override
  public SortedMap<String, Timer> getTimers() {
    return registry.getTimers(nonExcluded);
  }

  @Override
  public SortedMap<String, Meter> getMeters() {
    return registry.getMeters(nonExcluded);
  }
}
