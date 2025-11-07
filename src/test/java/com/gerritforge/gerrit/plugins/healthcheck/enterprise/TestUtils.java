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

import static com.googlesource.gerrit.plugins.healthcheck.HealthCheckConfig.HEALTHCHECK_PLUGIN_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.HealthCheckNames;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllUsersName;
import com.google.gerrit.server.config.PluginConfigFactory;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;

public class TestUtils {

  public static EnterpriseHealthCheckConfig enterpriseHealthCheckConfig(Config config) {
    PluginConfigFactory pluginConfigFactoryMock = mock(PluginConfigFactory.class);
    when(pluginConfigFactoryMock.getGlobalPluginConfig(HEALTHCHECK_PLUGIN_NAME)).thenReturn(config);

    return new EnterpriseHealthCheckConfig(
        pluginConfigFactoryMock,
        new AllProjectsName("All-Projects"),
        new AllUsersName("All-Users"),
        false);
  }

  public static Config getConfigWithThreshold(int customThreshold) {
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
    return config;
  }
}
