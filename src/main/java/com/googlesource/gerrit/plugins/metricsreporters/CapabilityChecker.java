// Copyright (C) 2018 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.metricsreporters;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.CapabilityControl;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class CapabilityChecker {
  private final Provider<CurrentUser> userProvider;
  private final String capabilityName;

  @Inject
  CapabilityChecker(Provider<CurrentUser> userProvider, @PluginName String pluginName) {
    this.userProvider = userProvider;
    this.capabilityName = String.format("%s-%s", pluginName, ViewMetricsCapability.ID);
  }

  public boolean canViewMetrics() {
    CapabilityControl ctl = userProvider.get().getCapabilities();
    return ctl.canAdministrateServer() || ctl.canPerform(capabilityName);
  }
}
