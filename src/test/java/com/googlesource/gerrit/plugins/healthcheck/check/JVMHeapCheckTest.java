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
import static com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig.HEALTHCHECK_PLUGIN_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.EnterpriseCheck;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.HealthCheckName;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.JVMHeapCheck;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.testing.TestingExecutors;
import com.google.gerrit.metrics.MetricMaker;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.config.PluginConfigFactory;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
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
            metricMaker);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.FAILED, result);
  }

  @Test
  public void doCheck_returnsFailWithCustomThreshold() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();

    Config config = new Config();
    String configText = "[healthcheck \"" + HealthCheckName.JVM_HEAP + "\"]\n" + "  threshold = 50";
    try {
      config.fromText(configText);
    } catch (ConfigInvalidException e) {
      throw new IllegalArgumentException("Invalid configuration " + configText, e);
    }

    PluginConfigFactory pluginConfigFactoryMock = mock(PluginConfigFactory.class);
    when(pluginConfigFactoryMock.getGlobalPluginConfig(HEALTHCHECK_PLUGIN_NAME)).thenReturn(config);
    MetricMaker metricMaker = mock(MetricMaker.class);

    EnterpriseHealthCheckConfig enterpriseHealthCheckConfig =
        new EnterpriseHealthCheckConfig(
            pluginConfigFactoryMock,
            new AllProjectsName("All-Projects"),
            new AllUsersName("All-Users"),
            false);

    JVMHeapCheck check =
        new JVMHeapCheck(
            executor,
            enterpriseHealthCheckConfig,
            createMetricRegistry(60L),
            HealthCheckName.JVM_HEAP,
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
