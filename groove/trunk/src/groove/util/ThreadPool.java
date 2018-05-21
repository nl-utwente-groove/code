/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Global thread pool. */
public class ThreadPool {
    private ThreadPool() {
        int threadCount = (Runtime.getRuntime()
            .availableProcessors() + 1) / 2;
        this.exec = Executors.newFixedThreadPool(threadCount);
        this.futures = new ArrayList<>();
    }

    /** Schedules a runnable for execution by this thread pool. */
    public void start(Runnable runnable) {
        this.futures.add(this.exec.submit(runnable));
    }

    /** Synchronises with all currently running tasks. */
    public void sync() {
        for (Future<?> future : this.futures) {
            try {
                future.get();
            } catch (InterruptedException exc) {
                assert false;
                // empty
            } catch (ExecutionException exc) {
                throw new RuntimeException(exc);
            }
        }
        this.futures.clear();
    }

    /** Shuts down the thread pool. */
    public void shutdown() {
        if (!isShutDown()) {
            this.isShutDown = true;
            this.exec.shutdown();
        }
        this.futures.clear();
    }

    private boolean isShutDown() {
        return this.isShutDown;
    }

    private boolean isShutDown;

    /** The internal thread pool */
    private final ExecutorService exec;
    /** Futures of the currently running tasks. */
    private final List<Future<?>> futures;

    /** Returns the singleton instance of this thread pool. */
    public static ThreadPool instance() {
        if (INSTANCE == null || INSTANCE.isShutDown()) {
            INSTANCE = new ThreadPool();
        }
        return INSTANCE;
    }

    private static ThreadPool INSTANCE = new ThreadPool();
}