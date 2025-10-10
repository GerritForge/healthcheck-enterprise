// Copyright (C) 2025 GerritForge, Inc.
//
// Licensed under the BSL 1.1 (the "License");
// you may not use this file except in compliance with the License.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.plugins.healthcheck.enterprise.check;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gerrit.metrics.MetricMaker;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.healthcheck.check.AbstractHealthCheck;
import java.util.Optional;

@Singleton
public class JVMHeapCheck extends AbstractHealthCheck {

  public static final String JVM_HEAP_USED_PCT_METRIC_NAME = "javamelody_memory_used_pct";

  private final Integer threshold;
  private final MetricRegistry metricRegistry;

  public JVMHeapCheck(
      ListeningExecutorService executor,
      EnterpriseHealthCheckConfig config,
      MetricRegistry metricRegistry,
      String name,
      MetricMaker metricMaker) {
    super(executor, config, name, metricMaker);
    this.metricRegistry = metricRegistry;
    this.threshold = config.getJVMHeapPercentageThreshold(name);
  }

  @Override
  public Result doCheck() throws Exception {
    return Optional.ofNullable(metricRegistry.getGauges().get(JVM_HEAP_USED_PCT_METRIC_NAME))
        .filter(metric -> (getMetricValue(metric) > threshold))
        .map(metric -> Result.FAILED)
        .orElse(Result.PASSED);
  }

  private Long getMetricValue(Gauge<?> metric) {
    Object value = metric.getValue();
    if (value instanceof Long) {
      return (Long) value;
    }

    throw new IllegalArgumentException(
        String.format(
            "JVM heap memory used percentage metric value must be of type java.lang.Long but was %s"
                + " ",
            value.getClass().getName()));
  }
}
