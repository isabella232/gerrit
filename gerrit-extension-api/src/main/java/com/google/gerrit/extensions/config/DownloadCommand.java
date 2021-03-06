// Copyright (C) 2013 The Android Open Source Project
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

package com.google.gerrit.extensions.config;

import com.google.gerrit.extensions.annotations.ExtensionPoint;

@ExtensionPoint
public abstract class DownloadCommand {
  /**
   * Returns the download command for the given download scheme, project and
   * ref.
   *
   * @param scheme the download scheme for which the command should be returned
   * @param project the name of the project for which the download command
   *        should be returned
   * @param ref the change ref
   * @return the download command
   */
  public abstract String getCommand(DownloadScheme scheme, String project,
      String ref);
}
