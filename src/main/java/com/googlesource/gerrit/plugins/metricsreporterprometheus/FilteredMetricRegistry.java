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
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Maps;
import java.util.SortedMap;
import java.util.function.Predicate;

public class FilteredMetricRegistry extends MetricRegistry {
  private final MetricRegistry registry;
  private final Predicate<String> exclusionFilter;

  FilteredMetricRegistry(MetricRegistry registry, Predicate<String> exclusionFilter) {
    this.registry = registry;
    this.exclusionFilter = exclusionFilter;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public SortedMap<String, Gauge> getGauges() {
    return filter(registry.getGauges());
  }

  @Override
  public SortedMap<String, Counter> getCounters() {
    return filter(registry.getCounters());
  }

  @Override
  public SortedMap<String, Histogram> getHistograms() {
    return filter(registry.getHistograms());
  }

  @Override
  public SortedMap<String, Timer> getTimers() {
    return filter(registry.getTimers());
  }

  @Override
  public SortedMap<String, Meter> getMeters() {
    return filter(registry.getMeters());
  }

  private <T> SortedMap<String, T> filter(SortedMap<String, T> unfiltered) {
    return Maps.filterKeys(unfiltered, k -> !exclusionFilter.test(k));
  }
}
