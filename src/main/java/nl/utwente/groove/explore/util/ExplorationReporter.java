/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.util;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import nl.utwente.groove.explore.ExplorationListener;

/**
 * Listener that will produce output after invoking a method {@link #report()}.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface ExplorationReporter extends ExplorationListener {
    /**
     * Produces the result of the most recently recorded exploration.
     * This method will only be called after {@link #start}.
     * @throws IOException if an error occurred during reporting
     */
    public abstract void report() throws IOException;

    /** Prints a timed message on stdout. */
    static public void time(String message) {
        if (TIME) {
            System.out.println(formatter.format(LocalTime.now()) + ": " + message);
        }
    }

    /** Flag determining whether timing information should be emitted. */
    static final boolean TIME = false;
    /** Formatter for the time stamp of timing information. */
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
}