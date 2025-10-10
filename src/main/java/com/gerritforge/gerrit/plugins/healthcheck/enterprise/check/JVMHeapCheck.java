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

import com.codahale.metrics.MetricRegistry;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import com.google.common.flogger.FluentLogger;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gerrit.metrics.MetricMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.healthcheck.check.AbstractHealthCheck;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.HealthCheckNames.JVM_HEAP_USED;

@Singleton
public class JVMHeapCheck extends AbstractHealthCheck {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final Integer threshold;

  @Inject
  public JVMHeapCheck(
      ListeningExecutorService executor,
      EnterpriseHealthCheckConfig config,
      MetricMaker metricMaker) {
    super(executor, config, JVM_HEAP_USED, metricMaker);
    this.threshold = config.getJVMHeapPercentageThreshold(JVM_HEAP_USED);
  }

  @Override
  public Result doCheck() throws Exception {
    MemoryMXBean mxBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage heapUsage = mxBean.getHeapMemoryUsage();

    long max = heapUsage.getMax();  // may be -1 if undefined

    if (max <= 0) {
      logger.atWarning().log("Max memory is undefined. Check 'container.heapLimit' in you configuration");
      return Result.PASSED;
    }

    long used = heapUsage.getUsed();
    double pct = (double) used * 100.0 / max;

    logger.atSevere().log("Max heap allocated: %d, Heap used %d, Percentage used: %.2f, threshold: %d", max, used, pct, threshold);
    if (pct > threshold) {
      return Result.FAILED;
    }
    return Result.PASSED;
  }
}
