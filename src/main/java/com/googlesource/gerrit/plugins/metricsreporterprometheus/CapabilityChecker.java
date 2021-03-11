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
package com.googlesource.gerrit.plugins.metricsreporterprometheus;

import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.api.access.PluginPermission;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.permissions.GlobalPermission;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class CapabilityChecker {
  private final PermissionBackend permissionBackend;
  private final Provider<CurrentUser> userProvider;
  private final String pluginName;

  @Inject
  CapabilityChecker(
      PermissionBackend permissionBackend,
      Provider<CurrentUser> userProvider,
      @PluginName String pluginName) {
    this.permissionBackend = permissionBackend;
    this.userProvider = userProvider;
    this.pluginName = pluginName;
  }

  public boolean canViewMetrics() {
    try {
      permissionBackend
          .user(userProvider.get())
          .checkAny(
              ImmutableSet.of(
                  GlobalPermission.ADMINISTRATE_SERVER,
                  new PluginPermission(pluginName, ViewMetricsCapability.ID)));
      return true;
    } catch (AuthException | PermissionBackendException e) {
      return false;
    }
  }
  
  public boolean canViewConfig() {
    try {
      permissionBackend.user(userProvider.get()).check(GlobalPermission.ADMINISTRATE_SERVER);
      return true;
    } catch (AuthException | PermissionBackendException e) {
      return false;
    }
  }
}
