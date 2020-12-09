// Copyright (C) 2020 The Android Open Source Project
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

import com.google.gerrit.common.Version;
import io.prometheus.client.Counter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class GerritBuildInformationMetric {
  static final Counter requests =
      Counter.build()
          .name("gerrit_build_info")
          .help("Gerrit build information.")
          .labelNames("javaversion", "version", "revision")
          .register();

  public void compute() {
    GerritVersionInfo versionInfo = extractVersionComponents();
    requests.labels(getJavaVersion(), versionInfo.version, versionInfo.revision);
  }

  private static String getJavaVersion() {
    RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
    return String.format("%s(%s)", mxBean.getSpecVersion(), mxBean.getVmVersion());
  }

  private static GerritVersionInfo extractVersionComponents() {
    GerritVersionInfo versionInfo = new GerritVersionInfo();
    String fullVersion = Version.getVersion();

    if (fullVersion == null) {
      return versionInfo;
    }

    String[] versionComponents = fullVersion.split("-");

    versionInfo.version = versionComponents[0];

    if (versionComponents.length > 2) {
      versionInfo.revision = versionComponents.length > 2 ? versionComponents[2].substring(1) : "";
    } else {
      versionInfo.revision = "";
    }

    return versionInfo;
  }

  private static class GerritVersionInfo {
    String revision = "unknown";
    String version = "unknown";
  }
}
