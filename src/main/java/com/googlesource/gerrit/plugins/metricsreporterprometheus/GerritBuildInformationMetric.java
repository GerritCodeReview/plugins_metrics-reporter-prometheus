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

public class GerritBuildInformationMetric {
  static final Counter requests =
      Counter.build()
          .name("gerrit_build_info")
          .help("Gerrit build information.")
          .labelNames("javaversion", "version", "revision", "commits_ahead", "dirty")
          .register();

  public void compute() {
    GerritVersionInfo versionInfo = extractVersionComponents();
    requests.labels(
        ManagementFactory.getRuntimeMXBean().getSpecVersion(),
        versionInfo.version,
        versionInfo.revision,
        String.valueOf(versionInfo.commitsAhead),
        String.valueOf(versionInfo.dirty));
  }

  private static GerritVersionInfo extractVersionComponents() {
    GerritVersionInfo versionInfo = new GerritVersionInfo();
    String fullVersion = Version.getVersion();

    if (fullVersion == null) {
      return versionInfo;
    }

    String[] versionComponents = fullVersion.split("-");

    if (versionComponents[versionComponents.length - 1].equals("dirty")) {
      versionInfo.dirty = true;
    }

    versionInfo.version = versionComponents[0];

    if (versionComponents.length > 2) {
      versionInfo.commitsAhead = Integer.parseInt(versionComponents[1]);
      versionInfo.revision = versionComponents[2].substring(1);
    } else {
      versionInfo.commitsAhead = 0;
      versionInfo.revision = "";
    }

    return versionInfo;
  }

  private static class GerritVersionInfo {
    int commitsAhead = -1;
    boolean dirty = false;
    String revision = "unknown";
    String version = "unknown";
  }
}
