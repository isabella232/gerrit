// Copyright (C) 2012 The Android Open Source Project
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

package com.google.gerrit.server.git;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.eclipse.jgit.lib.Config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Module providing the {@link ReceiveCommitsExecutor}. */
public class ReceiveCommitsExecutorModule extends AbstractModule {
  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  @ReceiveCommitsExecutor
  public WorkQueue.Executor createReceiveCommitsExecutor(
      @GerritServerConfig Config config,
      WorkQueue queues) {
    int poolSize = config.getInt("receive", null, "threadPoolSize",
        Runtime.getRuntime().availableProcessors());
    return queues.createQueue(poolSize, "ReceiveCommits");
  }

  @Provides
  @Singleton
  @SendEmailExecutor
  public ExecutorService createSendEmailExecutor(
      @GerritServerConfig Config config, WorkQueue queues) {
    int poolSize = config.getInt("sendemail", null, "threadPoolSize", 1);
    if (poolSize == 0) {
      return MoreExecutors.newDirectExecutorService();
    }
    return queues.createQueue(poolSize, "SendEmail");
  }

  @Provides
  @Singleton
  @ChangeUpdateExecutor
  public ListeningExecutorService createChangeUpdateExecutor(@GerritServerConfig Config config) {
    int poolSize = config.getInt("receive", null, "changeUpdateThreads", 1);
    if (poolSize <= 1) {
      return MoreExecutors.newDirectExecutorService();
    }
    return MoreExecutors.listeningDecorator(
        MoreExecutors.getExitingExecutorService(
          new ThreadPoolExecutor(1, poolSize,
              10, TimeUnit.MINUTES,
              new ArrayBlockingQueue<Runnable>(poolSize),
              new ThreadFactoryBuilder()
                .setNameFormat("ChangeUpdate-%d")
                .setDaemon(true)
                .build(),
              new ThreadPoolExecutor.CallerRunsPolicy())));
  }
}
