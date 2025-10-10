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

package com.gerritforge.gerrit.plugins.healthcheck.enterprise;

import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.config.GerritIsReplica;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig;
import org.eclipse.jgit.lib.Config;

@Singleton
public class EnterpriseHealthCheckConfig extends HealthCheckConfig {
  private static final int JVM_MEMORY_PERCENTAGE_DEFAULT = 80;

  private final Config config;
  private final AllProjectsName allProjectsName;
  private final AllUsersName allUsersName;
  private final boolean isReplica;

  @Inject
  public EnterpriseHealthCheckConfig(
      PluginConfigFactory configFactory,
      AllProjectsName allProjectsName,
      AllUsersName allUsersName,
      @GerritIsReplica boolean isReplica) {
    super(configFactory, allProjectsName, allUsersName, isReplica);
    this.allProjectsName = allProjectsName;
    this.allUsersName = allUsersName;
    this.isReplica = isReplica;
    config =
        configFactory.getGlobalPluginConfig(
            com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig.HEALTHCHECK_PLUGIN_NAME);
  }

  public int getJVMHeapPercentageThreshold(String healthCheckName) {
    int defaultThreshold =
        healthCheckName == null
            ? JVM_MEMORY_PERCENTAGE_DEFAULT
            : getJVMHeapPercentageThreshold(null);
    return config.getInt(
        com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig.HEALTHCHECK,
        healthCheckName,
        "threshold",
        defaultThreshold);
  }
}
