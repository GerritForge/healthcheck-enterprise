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

package com.googlesource.gerrit.plugins.healthcheck.check;

import static com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.JVMHeapCheck.JVM_HEAP_USED_PCT_METRIC_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.EnterpriseCheck;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.HealthCheckName;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.JVMHeapCheck;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.testing.TestingExecutors;
import com.google.gerrit.metrics.MetricMaker;
import org.junit.Test;

public class JVMHeapCheckTest {

  @Test
  public void doCheck_returnsPassed() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();
    EnterpriseHealthCheckConfig config = mock(EnterpriseHealthCheckConfig.class);
    MetricMaker metricMaker = mock(MetricMaker.class);

    JVMHeapCheck check =
        new JVMHeapCheck(
            executor,
            config,
            createMetricRegistry(0L),
            HealthCheckName.JVM_HEAP,
            JVM_HEAP_USED_PCT_METRIC_NAME,
            metricMaker);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.PASSED, result);
  }

  @Test
  public void doCheck_returnsFailWithDefaultThreshold() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();
    EnterpriseHealthCheckConfig config = mock(EnterpriseHealthCheckConfig.class);
    MetricMaker metricMaker = mock(MetricMaker.class);

    JVMHeapCheck check =
        new JVMHeapCheck(
            executor,
            config,
            createMetricRegistry(90L),
            HealthCheckName.JVM_HEAP,
            JVM_HEAP_USED_PCT_METRIC_NAME,
            metricMaker);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.FAILED, result);
  }

  private MetricRegistry createMetricRegistry(Long value) {
    MetricRegistry metricRegistry = new MetricRegistry();
    metricRegistry.register(
        JVM_HEAP_USED_PCT_METRIC_NAME,
        new Gauge<Long>() {
          @Override
          public Long getValue() {
            return value;
          }
        });
    return metricRegistry;
  }
}
