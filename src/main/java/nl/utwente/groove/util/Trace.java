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
package nl.utwente.groove.util;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Timestamped tracing helpers for the GROOVE tools.
 * @version $Revision$
 * @author Arend Rensink
 */
@NonNullByDefault
public class Trace {
    /**
     * Gives the current time as a number-formatted string with given
     * parameters.
     * @param lossfactor the multiple of milliseconds by which time should be
     *        measured; i.e. a value of 10 means measure by centiseconds, 100
     *        means by deciseconds
     * @param modulo the multiple of the measured time unit (after taking loss
     *        into account) above which time should be cut off
     * @param fraction the fraction of the measured time that should appear
     *        after the decimal point
     */
    public static String currentTime(int lossfactor, int modulo, int fraction) {
        long time = (System.currentTimeMillis() / lossfactor);
        StringBuffer res = new StringBuffer();
        while (modulo > 1) {
            res
                .insert(0, time > 0
                    ? "" + time % 10
                    : "");
            time /= 10;
            fraction /= 10;
            if (fraction == 1) {
                res.insert(0, ".");
            }
            modulo /= 10;
        }
        return res.toString();
    }

    /**
     * Gives the current time as a number-formatted string of the form "ss.cc",
     * where ss are seconds and cc centiseconds.
     */
    public static String currentTime() {
        return currentTime(10, 10000, 100);
    }

    /**
     * Prints a timestamped message.
     */
    public static void message(Object obj) {
        System.out.println(currentTime() + ": " + obj);
    }

    /**
     * Prints a timestamped message regarding the time of starting a given
     * method.
     */
    public static void startMessage(String method) {
        message("Starting " + method);
    }

    /**
     * Prints a timestamped message regarding the time of ending a given method.
     */
    public static void endMessage(String method) {
        message("Ending " + method);
    }

    private Trace() {
        // private constructor to prevent instantiation of this class
    }
}
