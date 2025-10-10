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

import static com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.HealthCheckNames.JVM_HEAP_USED;

import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.FluentLogger;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gerrit.metrics.MetricMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.healthcheck.check.AbstractHealthCheck;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Singleton
public class JVMHeapCheck extends AbstractHealthCheck {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final Integer threshold;
  private final MemoryMXBean memoryMXBean;

  @Inject
  public JVMHeapCheck(
      ListeningExecutorService executor,
      EnterpriseHealthCheckConfig config,
      MetricMaker metricMaker) {
    super(executor, config, JVM_HEAP_USED, metricMaker);
    this.threshold = config.getJVMHeapPercentageThreshold(JVM_HEAP_USED);
    this.memoryMXBean = ManagementFactory.getMemoryMXBean();
  }

  @VisibleForTesting
  public JVMHeapCheck(
      ListeningExecutorService executor,
      EnterpriseHealthCheckConfig config,
      MetricMaker metricMaker,
      MemoryMXBean memoryMXBean) {
    super(executor, config, JVM_HEAP_USED, metricMaker);
    this.threshold = config.getJVMHeapPercentageThreshold(JVM_HEAP_USED);
    this.memoryMXBean = memoryMXBean;
  }

  @Override
  public Result doCheck() throws Exception {
    MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

    long max = heapUsage.getMax();

    // getMax() may be -1 if undefined, however it should never happen in Gerrit since Xmx is always
    // set
    if (max <= 0) {
      logger.atWarning().log(
          "Max memory is undefined. Check 'container.heapLimit' in you configuration");
      return Result.PASSED;
    }

    long used = heapUsage.getUsed();
    int pct = (int) (used * 100.0 / max);

    logger.atFine().log(
        "Max JVM heap allocated: %d, Heap used %d, Percentage used: %d, threshold: %d",
        max, used, pct, threshold);
    return pct > threshold ? Result.FAILED : Result.PASSED;
  }
}
