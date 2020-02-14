package com.googlesource.gerrit.plugins.metricsreporters;

import com.codahale.metrics.*;
import com.google.common.flogger.FluentLogger;
import com.google.inject.Inject;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;

public class GerritPrometheusMetricRegistryListener implements MetricRegistryListener {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private GerritPrometheusExporter gerritPrometheusExporter;
    @Inject
    GerritPrometheusMetricRegistryListener(GerritPrometheusExporter gerritPrometheusExporter) {
        this.gerritPrometheusExporter = gerritPrometheusExporter;
    }

    @Override
    public void onGaugeAdded(String name, Gauge <?> gauge) {
        logger.atFine().log("Gauge added: %s", name);
        updateMetric(name, gauge);
    }

    @Override
    public void onGaugeRemoved(String name) {
        logger.atSevere().log("===>>> Caught gauge remove %s", name);

    }

    @Override
    public void onCounterAdded(String name, Counter counter) {
        logger.atFine().log("Counter added: %s", name);
        updateMetric(name, counter);
    }

    @Override
    public void onCounterRemoved(String name) {

    }

    @Override
    public void onHistogramAdded(String name, Histogram histogram) {
        logger.atFine().log("Histogram added: %s", name);
        updateMetric(name, histogram);
    }

    @Override
    public void onHistogramRemoved(String name) {

    }

    @Override
    public void onMeterAdded(String name, Meter meter) {
        logger.atFine().log("Meter added: %s", name);
        updateMetric(name, meter);
    }

    @Override
    public void onMeterRemoved(String name) {

    }

    @Override
    public void onTimerAdded(String name, Timer timer) {
        logger.atFine().log("Timer added: %s", name);
        updateMetric(name, timer);
    }

    @Override
    public void onTimerRemoved(String name) {

    }

    private void updateMetric(String name, Metric metric) {
        for (String exclude : gerritPrometheusExporter.excludedMetrics) {
            if (name.matches(exclude)) {
                return;
            }
        }

        logger.atFine().log("Registering metric: %s", name);
        gerritPrometheusExporter.filteredRegistry.register(name, metric);
        CollectorRegistry.defaultRegistry.clear();
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(gerritPrometheusExporter.filteredRegistry));
    }
}
