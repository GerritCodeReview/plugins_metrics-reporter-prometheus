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

package com.googlesource.gerrit.plugins.metricsreporters;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
  private static final Logger log = LoggerFactory.getLogger(Configuration.class);

  // configuration parameters
  private static final String METRICS_SECTION = "metrics";
  private static final String EXCLUDE_KEY = "exclude";

  private final Set<String> excludes = new HashSet<>();

  @Inject
  public Configuration(@PluginName String pluginName, SitePaths site) {
    Path pluginConfigFile = site.etc_dir.resolve(pluginName + ".config");

    FileBasedConfig config = new FileBasedConfig(pluginConfigFile.toFile(), FS.DETECTED);

    try {
      config.load();
      excludes.addAll(Arrays.asList(config.getStringList(METRICS_SECTION, null, EXCLUDE_KEY)));
    } catch (IOException e) {
      log.error("Failed to open configuration file '" + pluginConfigFile.toString() + "'");
    } catch (ConfigInvalidException e) {
      log.error("Failed to parse configuration file '" + pluginConfigFile.toString() + "'");
    }
  }

  public Set<String> getExcludes() {
    return excludes;
  }
}
