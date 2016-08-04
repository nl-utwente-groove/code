// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.util;

import groove.util.parse.StringHandler;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class used to generate performance reports. Performance reports concern
 * number of calls made and time taken.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Reporter {
    /**
     * Constructs a new reporter, for a certain class and event name.
     * @param parent the parent reporter
     * @param name the name of the event being reported
     */
    private Reporter(Reporter parent, String name) {
        this.parent = parent;
        this.type = parent.type;
        this.name = name;
        this.subreporters = null;
    }

    /** Constructs a top-level reporter for a given class. */
    private Reporter(Class<?> type) {
        this.parent = null;
        this.type = type;
        this.name = null;
        this.subreporters = new TreeMap<>();
    }

    /**
     * Generates a new index in the array of call reports.
     * @return a new index in the array of call report
     */
    public Reporter register(String name) {
        Reporter result = this.subreporters.get(name);
        if (result == null) {
            this.subreporters.put(name, result = new Reporter(this, name));
        }
        return result;
    }

    /**
     * Returns the total duration of a given method according to this reporter.
     */
    public long getTotalTime() {
        return this.duration;
    }

    /**
     * Returns the average duration of a given method according to this
     * reporter.
     */
    public long getAverageTime() {
        return this.duration / getCallCount();
    }

    /**
     * Returns the average duration of a given method according to this
     * reporter.
     */
    public int getCallCount() {
        return this.topCount + this.nestedCount;
    }

    /** Returns the type on which this reporter is based. */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Returns the event name associated this reporter.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Signals the start of a new method to be reported.
     * @require currentNesting < MAX_NESTING
     */
    final public synchronized void start() {
        if (REPORT) {
            long now = System.currentTimeMillis();
            this.nestedCount++;
            if (this.currentNesting == 0) {
                this.topCount++;
                if (TIME_METHODS) {
                    this.duration -= now;
                    this.parent.totalTime -= now;
                }
            }
            this.currentNesting++;
            reportTime += System.currentTimeMillis() - now;
        }
    }

    /**
     * Signals the restart of a method to be reported. A restart means the the
     * invocation is not counted, but the time is measured
     */
    final public synchronized void restart() {
        if (REPORT) {
            long now = System.currentTimeMillis();
            if (this.currentNesting == 0) {
                if (TIME_METHODS) {
                    this.duration -= now;
                    this.parent.totalTime -= now;
                }
            }
            this.currentNesting++;
            reportTime += System.currentTimeMillis() - now;
        }
    }

    /**
     * Reports the end of the most deeply nested method.
     * @require <tt>currentNesting > 0</tt>
     */
    final public synchronized void stop() {
        if (REPORT) {
            this.currentNesting--;
            long now = System.currentTimeMillis();
            if (TIME_METHODS) {
                if (this.currentNesting == 0) {
                    this.duration += now;
                    this.parent.totalTime += now;
                }
            }
            reportTime += System.currentTimeMillis() - now;
        }
    }

    private void calculateFieldWidths() {
        // calculate the width of the required fields
        int maxTopCount = 1, maxNestedCount = 1;
        long maxTotTime = 1, maxAvgTime = 1;
        for (Reporter subreporter : this.subreporters.values()) {
            this.methodNameLength = Math.max(subreporter.getName().length(), this.methodNameLength);
            maxTopCount = Math.max(subreporter.topCount, maxTopCount);
            maxNestedCount =
                Math.max(subreporter.nestedCount - subreporter.topCount, maxNestedCount);
            maxTotTime = Math.max(subreporter.duration, maxTotTime);
            long avgDuration = 0;
            if (TIME_TOP_ONLY) {
                avgDuration = (1000 * subreporter.duration) / subreporter.topCount;
            } else if (subreporter.nestedCount > 0) {
                avgDuration = (1000 * subreporter.duration) / subreporter.nestedCount;
            }
            maxAvgTime = Math.max(avgDuration, maxAvgTime);
        }
        double log10 = Math.log(10);
        this.topCountLength = (int) (Math.log(maxTopCount) / log10) + 1;
        this.nestedCountLength = (int) (Math.log(maxNestedCount) / log10) + 1;
        this.totTimeLength = (int) (Math.log(maxTotTime) / log10) + 1;
        this.avgTimeLength = (int) (Math.log(maxAvgTime) / log10) + 1;
    }

    /**
     * Generates some reports on standard output, for the purpose of
     * optimization. Reports include:
     * <ul>
     * <li> Numbers of calls of various methods
     * </ul>
     */
    private void myReport(PrintWriter out, int methodNameLength, int topCountLength,
            int nestedCountLength, int totTimeLength, int avgTimeLength) {
        out.println("Reporting " + this.type);
        for (Reporter subreporter : this.subreporters.values()) {
            out.print(INDENT + StringHandler.pad(subreporter.getName(), methodNameLength, false) + " ");
            out.print(TOP_COUNT_FIELD + "="
                + StringHandler.pad("" + subreporter.topCount, topCountLength, false) + " ");
            out.print(NESTED_COUNT_FIELD
                + "="
                + StringHandler.pad("" + (subreporter.nestedCount - subreporter.topCount),
                    nestedCountLength, false) + " ");
            if (TIME_METHODS) {
                out.print(TOT_TIME_FIELD + "="
                    + StringHandler.pad("" + subreporter.duration, totTimeLength, false) + " ");
                long avgDuration;
                if (subreporter.duration > 0) {
                    if (TIME_TOP_ONLY) {
                        avgDuration = (1000 * subreporter.duration) / subreporter.topCount;
                    } else {
                        avgDuration = (1000 * subreporter.duration) / subreporter.nestedCount;
                    }
                } else {
                    avgDuration = 0;
                }
                out.print(AVG_TIME_FIELD + "=" + StringHandler.pad("" + avgDuration, avgTimeLength, false)
                    + " ");
            }
            out.println();
        }
    }

    /** The top-level (i.e., non-nested) method call count. */
    private int topCount;
    /** The nested method call count. */
    private int nestedCount;
    /** The method call duration */
    private long duration;
    /** The current nesting depth. */
    private int currentNesting;
    /** Total time spent in the class being reported */
    private long totalTime;
    /** Parent reporter (if any). */
    private Reporter parent;
    /** type for which we are reporting */
    private final Class<?> type;
    /** The name of the event being reported. */
    private final String name;
    /** Set of subreporters of this reporter. */
    private final Map<String,Reporter> subreporters;

    // temporary variables for report field width
    private int methodNameLength;
    private int topCountLength;
    private int nestedCountLength;
    private int totTimeLength;
    private int avgTimeLength;

    /** Returns a reporter for a given type. */
    static public synchronized Reporter register(Class<?> type) {
        Reporter result = reporters.get(type);
        if (result == null) {
            result = new Reporter(type);
            reporters.put(type, result);
        }
        return result;
    }

    /**
     * Collects the reports from all <tt>Reporter</tt> instances and writes
     * them to a specified output. The combined report consists of a list of
     * method data from each individual reporter, followed by the total time
     * measured by the reporters.
     * @param out the output to which the report is to be written.
     */
    static public synchronized void report(PrintWriter out) {
        if (REPORT) {
            // first we compute the required (maximum) field widths for the
            // method reports
            int methodNameLength = 0;
            int topCountLength = 0;
            int nestedCountLength = 0;
            int totTimeLength = 0;
            int avgTimeLength = 0;
            int classNameLength = 0;
            for (Reporter reporter : getAllReporters()) {
                reporter.calculateFieldWidths();
                methodNameLength = Math.max(reporter.methodNameLength, methodNameLength);
                topCountLength = Math.max(reporter.topCountLength, topCountLength);
                nestedCountLength = Math.max(reporter.nestedCountLength, nestedCountLength);
                totTimeLength = Math.max(reporter.totTimeLength, totTimeLength);
                avgTimeLength = Math.max(reporter.avgTimeLength, avgTimeLength);
                classNameLength = Math.max(reporter.type.toString().length(), classNameLength);
            }
            // print the report title
            String title = "Method call reporting: " + new java.util.Date();
            StringBuffer line = new StringBuffer();
            for (int i = 0; i < title.length(); i++) {
                line.append("=");
            }
            out.println(title);
            out.println(line);
            out.println();
            // print the method reports from the individual reporters
            for (Reporter reporter : getAllReporters()) {
                reporter.myReport(out, methodNameLength, topCountLength, nestedCountLength,
                    totTimeLength, avgTimeLength);
                out.println();
            }
            // print the total amounts of time measured by the reporters
            out.println("Total measured time spent in");
            for (Reporter reporter : getAllReporters()) {
                out.println(INDENT + StringHandler.pad(reporter.type.toString(), classNameLength, false)
                    + ": " + reporter.totalTime + " ms");
            }
            out.println();

            // print the time spent inside the reporters, i.e., the time spent
            // reporting
            if (TIME_METHODS) {
                out.println("Time spent collection information: " + getReportTime() + " ms");
            }
            out.flush();
        } else {
            out.println("Method call reporting has been switched off");
        }
    }

    /** Returns a view on all registered reporters. */
    private static Iterable<Reporter> getAllReporters() {
        return reporters.values();
    }

    /**
     * Returns the total time spent in measuring.
     */
    static public long getReportTime() {
        return reportTime;
    }

    /**
     * Prints a report of the measured data on the standard output.
     * @see #report(PrintWriter)
     */
    static public void report() {
        report(new PrintWriter(System.out));
    }

    // ---------------------------- other constants
    // ------------------------------
    /** Length of a count field */
    static public final int COUNT_LENGTH = 7;
    /** Length of a time field */
    static public final int TIME_LENGTH = 6;
    /** Indentation before every method line */
    static public final String INDENT = "  ";
    /** Field name of the method identifier */
    static public final String METHOD_FIELD = "m";
    /** Field name of the top method count */
    static public final String TOP_COUNT_FIELD = "#top";
    /** Field name of the nested method count */
    static public final String NESTED_COUNT_FIELD = "#nest";
    /** Field name of the total duration */
    static public final String TOT_TIME_FIELD = "tot(m)";
    /** Field name of the average duration */
    static public final String AVG_TIME_FIELD = "avg(mu)";
    /** Flag to control whether execution times are reported. */
    static private final boolean TIME_METHODS = true;
    /**
     * Flag to control whether all executions or just top-level ones are
     * reported.
     */
    static private final boolean TIME_TOP_ONLY = TIME_METHODS && false;
    static private final boolean REPORT = true;
    /** Sorted map of all registered reporters */
    static private Map<Class<?>,Reporter> reporters = new TreeMap<>(
        new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    /** System time spent reporting */
    static private long reportTime;
}
