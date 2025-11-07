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

import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.JVMHeapCheck;
import com.google.gerrit.extensions.config.FactoryModule;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.googlesource.gerrit.plugins.healthcheck.check.HealthCheck;

public class HealthCheckEnterpriseSubsystemsModule extends FactoryModule {

  @Override
  protected void configure() {
    DynamicSet.bind(binder(), HealthCheck.class).to(JVMHeapCheck.class);
  }
}
