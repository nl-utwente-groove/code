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
 * $Id: Reporter.java,v 1.1.1.1 2007-03-20 10:05:18 kastenberg Exp $
 */
package groove.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class used to generate performance reports.
 * Performance reports concern number of calls made and time taken.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class Reporter {
    // ------------------------------- switches ----------------------------------
    static private final boolean REPORT = true;

    // ---------------------------- other constants ------------------------------
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
    /** Field name of the nesded method count */
    static public final String NESTED_COUNT_FIELD = "#nest";
    /** Field name of the total duration */
    static public final String TOT_TIME_FIELD = "tot(m)";
    /** Field name of the average duration */
    static public final String AVG_TIME_FIELD = "avg(mu)";

    static public Reporter register(Class<?> type) {
        return new Reporter(type);
    }

    /**
     * Collects the reports from all <tt>Reporter</tt> instances and
     * writes them to a specified output.
     * The combined report consists of a list of method data from each
     * individual reporter, followed by the total time measured by the
     * reporters.
     * @param out the output to which the report is to be written.
     */
    static public void report(PrintWriter out) {
        if (REPORT) {
            // first we compute the required (maximum) field widths for the method reports
            int methodNameLength = 0;
            int topCountLength = 0;
            int nestedCountLength = 0;
            int totTimeLength = 0;
            int avgTimeLength = 0;
            int classNameLength = 0;
            for (Reporter reporter: reporters) {
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
            for (Reporter reporter: new HashSet<Reporter>(reporters)) {
                reporter.myReport(
                    out,
                    methodNameLength,
                    topCountLength,
                    nestedCountLength,
                    totTimeLength,
                    avgTimeLength);
                out.println();
            }
            // print the total amounts of time measured by the reporters
            out.println("Total measured time spent in");
            for (Reporter reporter: reporters) {
                out.println(INDENT + Groove.pad(reporter.type.toString(), classNameLength, false) + ": " + reporter.totalTime + " ms");
            }
            out.println();

            // print the time spent inside the reporters, i.e., the time spent reporting
            if (TIME_METHODS) {
                out.println("Time spent collection information: " + getTotalTime() + " ms");
            }
            out.flush();
        } else {
            out.println("Method call reporting has been switched off");
        }
    }
    
    static public long getTotalTime() {
        return reportTime;
    }

    static public void report() {
        report(new PrintWriter(System.out));
    }
//
//    static private String pad(Object text, int length) {
//        StringBuffer res = new StringBuffer(text.toString());
//        while (res.length() < length)
//            res.insert(0," ");
//        return res.toString();
//    }

    /** The expected maximal nesting depth. */
    static private final int MAX_NESTING = 50;
    /** The expected maximal nesting depth. */
    static private final int MAX_METHODS = 200;
    /** Flag to control whether execution times are reported. */
    static private final boolean TIME_METHODS = true;
    /** Flag to control whether all executions or just top-level ones are reported. */
    static private final boolean TIME_TOP_ONLY = TIME_METHODS && false;
    /** List of all registered reporters */
    static private List<Reporter> reporters = new ArrayList<Reporter>();
    /** System time spent reporting */
    static private long reportTime;

    // ------------------------------- instance methods --------------------------

    public Reporter(Class<?> type) {
        this.type = type;
        reporters.add(this);
    }

    /**
     * Generates some reports on standard output, for the purpose of optimization.
     * Reports include:<ul>
     * <li> Counts of (top-level and nested) calls of various methods
     * <li> Total and average time spent executing various methods
     * </ul>
     */
    public void myReport(PrintWriter out) {
        calculateFieldWidths();
        myReport(out, methodNameLength, topCountLength, nestedCountLength, totTimeLength, avgTimeLength);
    }

    /**
     * Generates a new index in the array of call reports.
     * @return a new index in the array of call report
     */
    public int newMethod(String methodName) {
        methodNames.add(methodName);
        nrOfMethodsReported++;
        return nrOfMethodsReported - 1;
    }
    
    /**
     * Returns the total duration of a given method according to this reporter.
     */
    public long getTotalTime(int method) {
        return duration[method];
    }

    /**
     * Returns the average duration of a given method according to this reporter.
     */
    public long getAverageTime(int method) {
        return duration[method]/getCallCount(method);
    }

    /**
     * Returns the average duration of a given method according to this reporter.
     */
    public int getCallCount(int method) {
        return topCount[method]+nestedCount[method];
    }
    
    public String getMethodName(int method) {
    	return methodNames.get(method);
    }

    /**
     * Signals the start of a new method to be reported. 
     * @param methodIndex index of the method to be measured
     * @require currentNesting < MAX_NESTING
     */
    final public void start(int methodIndex) {
        if (REPORT) {
            long now = System.currentTimeMillis();
            // check if we have to extend the stack space
            if (currentNesting >= nestedMethodIndex.length) {
                int[] newNestedMethodIndex = new int[currentNesting * 2];
                long[] newStartTime = new long[currentNesting * 2];
                System.arraycopy(nestedMethodIndex, 0, newNestedMethodIndex, 0, currentNesting);
                System.arraycopy(startTime, 0, newStartTime, 0, currentNesting);
                startTime = newStartTime;
                nestedMethodIndex = newNestedMethodIndex;
            }
            nestedMethodIndex[currentNesting] = methodIndex;
            currentDepth[methodIndex]++;
            nestedCount[methodIndex]++;
            if (currentNesting == 0) {
                topCount[methodIndex]++;
                if (TIME_METHODS) {
                    totalTime -= now;
                    startTime[currentNesting] = now;
                }
            } else if (!TIME_TOP_ONLY)
                startTime[currentNesting] = now;
            currentNesting++;
            reportTime += System.currentTimeMillis() - now;
        }
    }

    /**
     * Signals the restart of a method to be reported.
     * A restart means the the invocation is not counted, but the time is measured 
     * @param methodIndex index of the method to be measured
     * @require currentNesting < MAX_NESTING
     */
    final public void restart(int methodIndex) {
        if (REPORT) {
            long now = System.currentTimeMillis();
            // check if we have to extend the stack space
            if (currentNesting >= nestedMethodIndex.length) {
                int[] newNestedMethodIndex = new int[currentNesting * 2];
                long[] newStartTime = new long[currentNesting * 2];
                System.arraycopy(nestedMethodIndex, 0, newNestedMethodIndex, 0, currentNesting);
                System.arraycopy(startTime, 0, newStartTime, 0, currentNesting);
                startTime = newStartTime;
                nestedMethodIndex = newNestedMethodIndex;
            }
            nestedMethodIndex[currentNesting] = methodIndex;
            currentDepth[methodIndex]++;
            if (currentNesting == 0) {
                if (TIME_METHODS) {
                    totalTime -= now;
                    startTime[currentNesting] = now;
                }
            } else if (!TIME_TOP_ONLY)
                startTime[currentNesting] = now;
            currentNesting++;
            reportTime += System.currentTimeMillis() - now;
        }
    }

    /**
     * Reports the end of the most deeply nested method.
     * @require <tt>currentNesting > 0</tt>
     */
    final public void stop() {
        if (REPORT) {
            currentNesting--;
            if (TIME_METHODS) {
                long now = System.currentTimeMillis();
                reportTime -= now;
                int methodIndex = nestedMethodIndex[currentNesting];
                if (currentNesting == 0) {
                    totalTime += now;
                    if (--currentDepth[methodIndex] == 0)
                        duration[methodIndex] += now - startTime[currentNesting];
                } else if (!TIME_TOP_ONLY)
                    if (--currentDepth[methodIndex] == 0)
                        duration[methodIndex] += now - startTime[currentNesting];
            }
            reportTime += System.currentTimeMillis();
        }
    }

    private void calculateFieldWidths() {
        // calculate the width of the required fields
        int maxTopCount = 1, maxNestedCount = 1;
        long maxTotTime = 1, maxAvgTime = 1;
        for (int i = 0; i < nrOfMethodsReported; i++) {
            methodNameLength = Math.max(methodNames.get(i).length(), methodNameLength);
            maxTopCount = Math.max(topCount[i], maxTopCount);
            maxNestedCount = Math.max(nestedCount[i] - topCount[i], maxNestedCount);
            maxTotTime = Math.max(duration[i], maxTotTime);
            long avgDuration = 0;
            if (TIME_TOP_ONLY) {
                avgDuration = (1000 * duration[i]) / topCount[i];
            } else if (nestedCount[i] > 0) {
                avgDuration = (1000 * duration[i]) / nestedCount[i];
            }
            maxAvgTime = Math.max(avgDuration, maxAvgTime);
        }
        double log10 = Math.log(10);
        topCountLength = (int) (Math.log(maxTopCount) / log10) + 1;
        nestedCountLength = (int) (Math.log(maxNestedCount) / log10) + 1;
        totTimeLength = (int) (Math.log(maxTotTime) / log10) + 1;
        avgTimeLength = (int) (Math.log(maxAvgTime) / log10) + 1;
    }

    /**
     * Generates some reports on standard output, for the purpose of optimization.
     * Reports include:<ul>
     * <li> Numbers of calls of various methods
     * </ul>
     */
    private void myReport(
        PrintWriter out,
        int methodNameLength,
        int topCountLength,
        int nestedCountLength,
        int totTimeLength,
        int avgTimeLength) {
        out.println("Reporting " + type);
        for (int i = 0; i < nrOfMethodsReported; i++) {
            out.print(INDENT + Groove.pad(methodNames.get(i), methodNameLength, false) + " ");
            out.print(TOP_COUNT_FIELD + "=" + Groove.pad("" + topCount[i], topCountLength, false) + " ");
            out.print(NESTED_COUNT_FIELD + "=" + Groove.pad("" + (nestedCount[i] - topCount[i]), nestedCountLength, false) + " ");
            if (TIME_METHODS) {
                out.print(TOT_TIME_FIELD + "=" + Groove.pad("" + duration[i], totTimeLength, false) + " ");
                long avgDuration;
                if (duration[i] > 0) {
                    if (TIME_TOP_ONLY)
                        avgDuration = (1000 * duration[i]) / topCount[i];
                    else
                        avgDuration = (1000 * duration[i]) / nestedCount[i];
                } else
                    avgDuration = 0;
                out.print(AVG_TIME_FIELD + "=" + Groove.pad("" + avgDuration, avgTimeLength, false) + " ");
            }
            out.println();
        }
    }

    /** Number of methods for which a report is requested */
    private int nrOfMethodsReported = 0;
    /** Names of the classes of the methods being reported. */
    private List<String> methodNames = new ArrayList<String>();
    /** The crrent recursive call depth. */
    private int[] currentDepth = new int[MAX_METHODS];
    /** The top-level (i.e., non-nested) method call count. */
    private int[] topCount = new int[MAX_METHODS];
    /** The nested method call count. */
    private int[] nestedCount = new int[MAX_METHODS];
    /** The method call duration */
    private long[] duration = new long[MAX_METHODS];
    /** The start times in a stack of method calls */
    private long startTime[] = new long[MAX_NESTING];
    /** The method indices in a stack of method calls */
    private int nestedMethodIndex[] = new int[MAX_NESTING];
    /** The current nesting depth. */
    private int currentNesting;
    /** Total time spent in the class being reported */
    private long totalTime;
    /** type for which we are reporting */
    private Class<?> type;

    // temporaty variables for report field width
    private int methodNameLength;
    private int topCountLength;
    private int nestedCountLength;
    private int totTimeLength;
    private int avgTimeLength;

}
