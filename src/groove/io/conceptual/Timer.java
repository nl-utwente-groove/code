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
package groove.io.conceptual;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** Helper class to record elapsed time. */
public class Timer {
    private static int[] counts = new int[128];
    private static long[] times = new long[128];
    private static long[] wall = new long[128];

    /** Boolean flag indicating that the timer is enabled. */
    private static boolean ENABLED = true;
    /** Flag switching on diagnostic messages. */
    public static boolean PRINT_MSG = true;

    // Probably list suffices but this works too
    private static Map<Integer,String> messages = new HashMap<>();

    /** Starts the timer for a given message. */
    public static int start(String message) {
        int index = messages.size();
        if (messages.containsValue(message)) {
            for (Entry<Integer,String> e : messages.entrySet()) {
                if (e.getValue().equals(message)) {
                    index = e.getKey();
                    break;
                }
            }
        }

        start(index, message);
        return index;
    }

    /** Starts a numbered timer. */
    public static void start(int timer) {
        if (!messages.containsKey(timer)) {
            throw new IllegalArgumentException("Nonexistant timer");
        }
        start(timer, "");
    }

    /** Starts a numbered timer for a given message. */
    public static void start(int timer, String message) {
        if (!messages.containsKey(timer)) {
            messages.put(timer, message);
        }
        //times[timer] = 0;//reset does this
        counts[timer]++;
        wall[timer] = System.nanoTime();
    }

    /** Stops a numbered timer and stores the elapsed time. */
    public static void stop(int timer) {
        long delta = System.nanoTime() - wall[timer];
        times[timer] += delta;
    }

    /** Continues a numbered timer. */
    public static void cont(int timer) {
        wall[timer] = System.nanoTime();
    }

    /** Continues the timer for a given message. */
    public static int cont(String message) {
        int index = messages.size();
        if (messages.containsValue(message)) {
            for (Entry<Integer,String> e : messages.entrySet()) {
                if (e.getValue().equals(message)) {
                    index = e.getKey();
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        wall[index] = System.nanoTime();

        return index;
    }

    /** Prints the recorded times and resets all timers. */
    public static void reset() {
        for (Entry<Integer,String> e : messages.entrySet()) {
            printTime(e.getKey(), e.getValue());
        }
        messages.clear();
        times = new long[128];
        wall = new long[128];
        counts = new int[128];
    }

    /** Prints the recorded time of a numbered timer. */
    public static void printTime(int timer) {
        printTime(timer, "");
    }

    /** Prints the recorded time of a numbered timer and a given message. */
    public static void printTime(int timer, String message) {
        if (!Timer.ENABLED) {
            return;
        }

        if (Timer.PRINT_MSG && message.length() > 0) {
            System.out.print(message + ": ");
        }
        double time = (times[timer] / (double) counts[timer]) / 1000000.0;
        System.out.println(new DecimalFormat("##0.00").format(time));
    }

}
