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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.testing.TestingExecutors;
import com.google.gerrit.metrics.MetricMaker;
import com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig;
import org.junit.Test;

public class EnterpriseCheckTest {

  @Test
  public void doCheck_returnsPassed() throws Exception {
    ListeningExecutorService executor = TestingExecutors.sameThreadScheduledExecutor();
    HealthCheckConfig config = mock(HealthCheckConfig.class);
    MetricMaker metricMaker = mock(MetricMaker.class);

    EnterpriseCheck check = new EnterpriseCheck(executor, config, metricMaker);

    EnterpriseCheck.Result result = check.doCheck();

    assertEquals(EnterpriseCheck.Result.PASSED, result);
  }
}
