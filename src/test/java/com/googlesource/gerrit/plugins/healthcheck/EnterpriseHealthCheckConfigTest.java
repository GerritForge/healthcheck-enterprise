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

package com.googlesource.gerrit.plugins.healthcheck;

import static com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig.JVM_MEMORY_PERCENTAGE_DEFAULT;
import static com.google.gerrit.testing.GerritJUnit.assertThrows;
import static com.googlesource.gerrit.plugins.healthcheck.TestUtils.enterpriseHealthCheckConfig;
import static com.googlesource.gerrit.plugins.healthcheck.TestUtils.getConfigWithThreshold;
import static org.junit.Assert.assertEquals;

import com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseHealthCheckConfig;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;

public class EnterpriseHealthCheckConfigTest {

  @Test
  public void shouldReturnDefaultThresholdWhenHealthCheckNameIsNull() {
    EnterpriseHealthCheckConfig checkConfig = enterpriseHealthCheckConfig(new Config());

    int threshold = checkConfig.getJVMHeapPercentageThreshold(null);

    assertEquals(JVM_MEMORY_PERCENTAGE_DEFAULT, threshold);
  }

  @Test
  public void shouldReturnConfiguredThresholdWhenSet() {
    int customThreshold = 75;
    EnterpriseHealthCheckConfig checkConfig =
        enterpriseHealthCheckConfig(getConfigWithThreshold(customThreshold));

    int threshold = checkConfig.getJVMHeapPercentageThreshold("jvm_heap_used");

    assertEquals(customThreshold, threshold);
  }

  @Test
  public void shouldThrowExceptionWhenThresholdIsLowerThanValidValue() {
    EnterpriseHealthCheckConfig checkConfig =
        enterpriseHealthCheckConfig(getConfigWithThreshold(0));

    assertThrows(
        IllegalArgumentException.class,
        () -> checkConfig.getJVMHeapPercentageThreshold("jvm_heap_used"));
  }

  @Test
  public void shouldThrowExceptionWhenThresholdIsGreaterThanValidValue() {
    EnterpriseHealthCheckConfig checkConfig =
        enterpriseHealthCheckConfig(getConfigWithThreshold(100));

    assertThrows(
        IllegalArgumentException.class,
        () -> checkConfig.getJVMHeapPercentageThreshold("jvm_heap_used"));
  }
}
