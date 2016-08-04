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
package groove.explore.util;

import static groove.explore.Verbosity.HIGH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import groove.explore.Exploration;
import groove.explore.Verbosity;
import groove.io.FileType;
import groove.lts.GTS;

/**
 * Reporter that logs the exploration process.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LogReporter extends AExplorationReporter {
    /**
     * Constructs a log reporter with a verbosity level
     * and a (possibly empty) file name
     * @param verbosity the verbosity with which messages are printed on standard output
     * @param logDir if not {@code null}, the name of a directory into which a log file should be written
     */
    public LogReporter(Verbosity verbosity, File logDir) {
        this.verbosity = verbosity;
        this.logDir = logDir;
        this.exploreStats = new StatisticsReporter(verbosity);
    }

    @Override
    public void start(Exploration exploration, GTS gts) {
        super.start(exploration, gts);
        this.exploreStats.start(exploration, gts);
        if (this.logDir != null) {
            this.log = new StringBuilder();
        }
        this.extra = new StringBuilder();
        this.startTime = new Date();
        emitStartMessage();
        emit("%n");
    }

    @Override
    public void stop(GTS gts) {
        super.stop(gts);
        this.exploreStats.stop(gts);
    }

    /**
     * Adds a line to the end of the log, if the verbosity is at least at
     * a given level.
     * @param minVerbosity the minimum verbosity to add the line
     * @param format the string format for the line
     * @param args the format arguments for the line
     */
    public void append(Verbosity minVerbosity, String format, Object... args) {
        if (this.verbosity.compareTo(minVerbosity) >= 0) {
            this.extra.append(String.format(format, args));
        }
    }

    /**
     * Adds a line to the end of the log, if the verbosity is at least {@link Verbosity#MEDIUM}.
     * @param format the string format for the line
     * @param args the format arguments for the line
     */
    public void append(String format, Object... args) {
        append(Verbosity.MEDIUM, format, args);
    }

    @Override
    public void report() throws IOException {
        this.exploreStats.report();
        // First report the statistics on the standard output
        // Note that this is not done using emit because the log
        // gets a more verbose version of the statistics.
        if (!this.verbosity.isLow()) {
            System.out.printf("%n%s%n", this.exploreStats.getReport());
        }
        // now write to the log file, if any
        if (this.log != null) {
            // copy the (high-verbosity) exploration statistics to the log
            String report = this.exploreStats.getReport(HIGH);
            if (report.length() > 0) {
                this.log.append(report);
                this.log.append("\n\n");
                this.log.append(getExploration().getLastMessage());
            }
            String logId = getGTS().getGrammar()
                .getId() + "-"
                + this.startTime.toString()
                    .replace(' ', '_')
                    .replace(':', '-');
            String logFileName = FileType.LOG.addExtension(logId);
            try (PrintWriter logFile = new PrintWriter(new File(this.logDir, logFileName))) {
                // copy the initial messages
                logFile.print(this.log.toString());
                // copy the garbage collector log, if any, to the log file
                File gcLogFile = new File(GC_LOG_NAME);
                if (gcLogFile.exists()) {
                    try (BufferedReader gcLog = new BufferedReader(new FileReader(gcLogFile))) {
                        List<String> gcList = new ArrayList<>();
                        String nextLine = gcLog.readLine();
                        while (nextLine != null) {
                            gcList.add(nextLine);
                            nextLine = gcLog.readLine();
                        }
                        for (int i = 1; i < gcList.size() - 2; i++) {
                            logFile.println(gcList.get(i));
                        }
                    }
                }
            }
        }
        emit("%s%n", getExploration().getLastMessage());
        if (this.extra.length() > 0) {
            emit(Verbosity.LOW, "%n%s%n", this.extra.toString());
        }
    }

    /** Emits the message announcing the parameters of the exploration. */
    protected void emitStartMessage() {
        emit("Grammar:\t%s%n", getGTS().getGrammar()
            .getName());
        emit("Start graph:\t%s%n", getGTS().getGrammar()
            .getStartGraph()
            .getName());
        emit("Control:\t%s%n", getGTS().getGrammar()
            .getControl()
            .getQualName());
        emit("Exploration:\t%s%n", getExploration().getType()
            .getIdentifier());
        emit("Timestamp:\t%s%n", this.startTime);
    }

    /** Outputs a diagnostic message if allowed by the verbosity, and optionally logs it. */
    protected void emit(Verbosity min, String message, Object... args) {
        String text = String.format(message, args);
        if (min.compareTo(this.verbosity) <= 0) {
            System.out.print(text);
        }
        if (this.log != null) {
            this.log.append(text);
        }
    }

    /** Outputs a diagnostic message under any verbosity except {@link Verbosity#LOW}, and optionally logs it. */
    protected void emit(String message, Object... args) {
        emit(Verbosity.MEDIUM, message, args);
    }

    private final Verbosity verbosity;
    private final File logDir;
    private final StatisticsReporter exploreStats;

    /**
     * Time of invocation, initialised at start time.
     */
    private Date startTime;
    private StringBuilder log;
    private StringBuilder extra;
    /**
     * Fixed name of the gc log file. If a file with this name is found, and
     * logging is switched on, the gc log is appended to the generator log.
     */
    static public final String GC_LOG_NAME = "gc.log";
}
