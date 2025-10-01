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

import static com.googlesource.gerrit.plugins.healthcheck.check.HealthCheckNames.GLOBAL;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gerrit.metrics.MetricMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig;
import com.googlesource.gerrit.plugins.healthcheck.check.AbstractHealthCheck;

@Singleton
public class EnterpriseCheck extends AbstractHealthCheck {

  @Inject
  public EnterpriseCheck(
      ListeningExecutorService executor,
      HealthCheckConfig config,
      MetricMaker metricMaker) {
    super(executor, config, GLOBAL, metricMaker);
  }

  @Override
  public Result doCheck() throws Exception {
    return Result.PASSED;
  }
}
