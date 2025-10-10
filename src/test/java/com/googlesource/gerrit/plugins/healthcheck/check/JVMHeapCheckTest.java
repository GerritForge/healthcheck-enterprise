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

import static com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig.HEALTHCHECK_PLUGIN_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.EnterpriseCheck;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.HealthCheckNames;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.JVMHeapCheck;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.testing.TestingExecutors;
import com.google.gerrit.metrics.MetricMaker;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.config.PluginConfigFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;

public class JVMHeapCheckTest {

  @Test
  public void doCheck_returnsPassed() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();

    EnterpriseHealthCheckConfig enterpriseHealthCheckConfig =
        enterpriseHealthCheckConfig(new Config());

    MetricMaker metricMaker = mock(MetricMaker.class);
    MemoryMXBean memoryMXBean = mock(MemoryMXBean.class);
    MemoryUsage memoryUsage = mock(MemoryUsage.class);
    when(memoryMXBean.getHeapMemoryUsage()).thenReturn(memoryUsage);

    JVMHeapCheck check =
        new JVMHeapCheck(executor, enterpriseHealthCheckConfig, metricMaker, memoryMXBean);

    when(memoryUsage.getMax()).thenReturn(100L);
    when(memoryUsage.getUsed()).thenReturn(1L);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.PASSED, result);
  }

  @Test
  public void doCheck_returnsPassWhenMaxUsageUndefined() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();
    EnterpriseHealthCheckConfig enterpriseHealthCheckConfig =
        enterpriseHealthCheckConfig(new Config());
    MetricMaker metricMaker = mock(MetricMaker.class);
    MemoryMXBean memoryMXBean = mock(MemoryMXBean.class);
    MemoryUsage memoryUsage = mock(MemoryUsage.class);
    when(memoryMXBean.getHeapMemoryUsage()).thenReturn(memoryUsage);

    JVMHeapCheck check =
        new JVMHeapCheck(executor, enterpriseHealthCheckConfig, metricMaker, memoryMXBean);

    when(memoryUsage.getMax()).thenReturn(-1L);
    when(memoryUsage.getUsed()).thenReturn(90L);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.PASSED, result);
  }

  @Test
  public void doCheck_returnsFailWithDefaultThreshold() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();
    EnterpriseHealthCheckConfig enterpriseHealthCheckConfig =
        enterpriseHealthCheckConfig(new Config());
    MetricMaker metricMaker = mock(MetricMaker.class);
    MemoryMXBean memoryMXBean = mock(MemoryMXBean.class);
    MemoryUsage memoryUsage = mock(MemoryUsage.class);
    when(memoryMXBean.getHeapMemoryUsage()).thenReturn(memoryUsage);

    JVMHeapCheck check =
        new JVMHeapCheck(executor, enterpriseHealthCheckConfig, metricMaker, memoryMXBean);

    when(memoryUsage.getMax()).thenReturn(100L);
    when(memoryUsage.getUsed()).thenReturn(90L);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.FAILED, result);
  }

  @Test
  public void doCheck_returnsFailWithCustomThreshold() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();

    int customThreshold = 50;
    Config config = new Config();
    String configText =
        "[healthcheck \""
            + HealthCheckNames.JVM_HEAP_USED
            + "\"]\n"
            + "  threshold = "
            + customThreshold;
    try {
      config.fromText(configText);
    } catch (ConfigInvalidException e) {
      throw new IllegalArgumentException("Invalid configuration " + configText, e);
    }

    MetricMaker metricMaker = mock(MetricMaker.class);
    EnterpriseHealthCheckConfig enterpriseHealthCheckConfig = enterpriseHealthCheckConfig(config);

    MemoryMXBean memoryMXBean = mock(MemoryMXBean.class);
    MemoryUsage memoryUsage = mock(MemoryUsage.class);
    when(memoryMXBean.getHeapMemoryUsage()).thenReturn(memoryUsage);

    JVMHeapCheck check =
        new JVMHeapCheck(executor, enterpriseHealthCheckConfig, metricMaker, memoryMXBean);

    when(memoryUsage.getMax()).thenReturn(100L);
    when(memoryUsage.getUsed()).thenReturn((long) (customThreshold + 1));

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.FAILED, result);
  }

  private static EnterpriseHealthCheckConfig enterpriseHealthCheckConfig(Config config) {
    PluginConfigFactory pluginConfigFactoryMock = mock(PluginConfigFactory.class);
    when(pluginConfigFactoryMock.getGlobalPluginConfig(HEALTHCHECK_PLUGIN_NAME)).thenReturn(config);

    return new EnterpriseHealthCheckConfig(
        pluginConfigFactoryMock,
        new AllProjectsName("All-Projects"),
        new AllUsersName("All-Users"),
        false);
  }
}
