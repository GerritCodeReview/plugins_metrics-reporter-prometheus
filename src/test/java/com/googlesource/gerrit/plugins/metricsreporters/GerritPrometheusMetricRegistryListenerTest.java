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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.google.gerrit.server.config.PluginConfigFactory;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GerritPrometheusMetricRegistryListenerTest {

  @Mock private MetricRegistry metricregistry;
  @Mock private CapabilityChecker capabilitychecker;

  @Mock private PluginConfigFactory cfgFactory;
  @Mock private HttpServletRequest reqMock;
  @Mock private HttpServletResponse rspMock;
  @Captor private ArgumentCaptor<Map<String, String>> captor;

  @Mock GerritPrometheusExporterConfig gerritPrometheusExporterConfig;

  @Test
  public void shouldAddGauge() {
    when(gerritPrometheusExporterConfig.getExcludedMetrics()).thenReturn(new HashSet<>());

    GerritPrometheusExporter gerritPrometheusExporter =
        new GerritPrometheusExporter(
            metricregistry, capabilitychecker, gerritPrometheusExporterConfig);

    Gauge<Integer> gauge =
        new Gauge<Integer>() {
          public Integer getValue() {
            return 1;
          }
        };
    new GerritPrometheusMetricRegistryListener(gerritPrometheusExporter)
        .onGaugeAdded("testGauge", gauge);

    assertThat(gerritPrometheusExporter.filteredRegistry.getGauges().size()).isEqualTo(1);
    assertThat(gerritPrometheusExporter.filteredRegistry.getGauges().containsKey("testGauge"))
        .isEqualTo(true);
  }

  @Test
  public void shouldAddCounter() {
    when(gerritPrometheusExporterConfig.getExcludedMetrics()).thenReturn(new HashSet<>());

    GerritPrometheusExporter gerritPrometheusExporter =
        new GerritPrometheusExporter(
            metricregistry, capabilitychecker, gerritPrometheusExporterConfig);

    Counter counter = new Counter();
    new GerritPrometheusMetricRegistryListener(gerritPrometheusExporter)
        .onCounterAdded("testCounter", counter);

    assertThat(gerritPrometheusExporter.filteredRegistry.getCounters().size()).isEqualTo(1);
    assertThat(gerritPrometheusExporter.filteredRegistry.getCounters().containsKey("testCounter"))
        .isEqualTo(true);
  }

  @Test
  public void shouldAddTimer() {
    when(gerritPrometheusExporterConfig.getExcludedMetrics()).thenReturn(new HashSet<>());

    GerritPrometheusExporter gerritPrometheusExporter =
        new GerritPrometheusExporter(
            metricregistry, capabilitychecker, gerritPrometheusExporterConfig);

    Timer timer = new Timer();
    new GerritPrometheusMetricRegistryListener(gerritPrometheusExporter)
        .onTimerAdded("testTimer", timer);

    assertThat(gerritPrometheusExporter.filteredRegistry.getTimers().size()).isEqualTo(1);
    assertThat(gerritPrometheusExporter.filteredRegistry.getTimers().containsKey("testTimer"))
        .isEqualTo(true);
  }

  @Test
  public void shouldAddHistogram() {
    when(gerritPrometheusExporterConfig.getExcludedMetrics()).thenReturn(new HashSet<>());

    GerritPrometheusExporter gerritPrometheusExporter =
        new GerritPrometheusExporter(
            metricregistry, capabilitychecker, gerritPrometheusExporterConfig);

    Histogram histogram = new Histogram(new UniformReservoir());
    new GerritPrometheusMetricRegistryListener(gerritPrometheusExporter)
        .onHistogramAdded("testHistogram", histogram);

    assertThat(gerritPrometheusExporter.filteredRegistry.getHistograms().size()).isEqualTo(1);
    assertThat(
            gerritPrometheusExporter.filteredRegistry.getHistograms().containsKey("testHistogram"))
        .isEqualTo(true);
  }

  @Test
  public void shouldAddMeter() {
    when(gerritPrometheusExporterConfig.getExcludedMetrics()).thenReturn(new HashSet<>());

    GerritPrometheusExporter gerritPrometheusExporter =
        new GerritPrometheusExporter(
            metricregistry, capabilitychecker, gerritPrometheusExporterConfig);

    Meter meter = new Meter();
    new GerritPrometheusMetricRegistryListener(gerritPrometheusExporter)
        .onMeterAdded("testMeter", meter);

    assertThat(gerritPrometheusExporter.filteredRegistry.getMetrics().size()).isEqualTo(1);
    assertThat(gerritPrometheusExporter.filteredRegistry.getMetrics().containsKey("testMeter"))
        .isEqualTo(true);
  }

  @Test
  public void shouldNotAddMeter() {
    Set<String> excluded = new HashSet<>();
    excluded.add("excludeMe.*");
    when(gerritPrometheusExporterConfig.getExcludedMetrics()).thenReturn(excluded);

    GerritPrometheusExporter gerritPrometheusExporter =
        new GerritPrometheusExporter(
            metricregistry, capabilitychecker, gerritPrometheusExporterConfig);

    Meter meter = new Meter();
    new GerritPrometheusMetricRegistryListener(gerritPrometheusExporter)
        .onMeterAdded("excludeMeMeter", meter);

    assertThat(gerritPrometheusExporter.filteredRegistry.getMetrics().size()).isEqualTo(0);
  }
}
