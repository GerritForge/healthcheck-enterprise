<<<<<<< PATCH SET (400bcee55a326f93b6de226d23efab532304a88b Add health check for JVM heap usage percentage)
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

import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.EnterpriseCheck;
import com.gerritforge.gerrit.plugins.healthcheck.enterprise.check.JVMHeapCheck;
import com.google.gerrit.extensions.config.FactoryModule;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.googlesource.gerrit.plugins.healthcheck.check.HealthCheck;

public class HealthCheckSubsystemsModule extends FactoryModule {

  @Override
  protected void configure() {
    DynamicSet.bind(binder(), HealthCheck.class).to(EnterpriseCheck.class);
    DynamicSet.bind(binder(), HealthCheck.class).to(JVMHeapCheck.class);
  }
}
=======
>>>>>>> BASE      (512f866695401a66051c7696002a4818c35ec7e6 Add EnterpriseModule and HealthCheckEnterpriseSubsystemsModu)
