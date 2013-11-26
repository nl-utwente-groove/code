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
import groove.explore.Exploration;
import groove.explore.Verbosity;
import groove.io.FileType;
import groove.lts.GTS;
import groove.util.Groove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Reporter that logs the exploration process.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LogReporter extends ExplorationReporter {
    /** Constructs a log reporter with a verbosity level
     * and a (possibly empty) file name
     * @param verbosity the verbosity with which messages are printed on standard output
     * @param logDir if not {@code null}, the name of a directory into which a log file should be written
     */
    public LogReporter(String grammarName, List<String> startGraphNames,
            Verbosity verbosity, File logDir) {
        this.grammarName = grammarName;
        this.startGraphNames = startGraphNames;
        this.verbosity = verbosity;
        this.logDir = logDir;
        this.exploreStats = new ExplorationStatistics(verbosity);
    }

    @Override
    public void start(Exploration exploration, GTS gts) {
        super.start(exploration, gts);
        if (this.logDir != null) {
            this.log = new StringBuilder();
        }
        this.startTime = new Date();
        emit("Grammar:\t%s%n", this.grammarName);
        emit("Start graph:\t%s%n", this.startGraphNames == null ? "default"
                : Groove.toString(this.startGraphNames.toArray(), "", "", ", "));
        emit("Exploration:\t%s%n", getExploration().getIdentifier());
        emit("Timestamp:\t%s%n", this.startTime);
        emit("%n");
        this.exploreStats.start(exploration, gts);
    }

    @Override
    public void stop() {
        this.exploreStats.stop();
        super.stop();
    }

    @Override
    public void report() throws IOException {
        // First report the statistics
        String report = this.exploreStats.getReport();
        if (report.length() > 0) {
            System.out.printf("%n%s%n", report);
        }
        // now write to the log file, if any
        if (this.log != null) {
            // copy the (high-verbosity) exploration statistics to the log
            report = this.exploreStats.getReport(HIGH);
            if (report.length() > 0) {
                this.log.append(report);
                this.log.append("\n\n");
                this.log.append(getExploration().getLastMessage());
            }
            String logId =
                getGTS().getGrammar().getId()
                    + "-"
                    + this.startTime.toString().replace(' ', '_').replace(':',
                        '-');
            String logFileName = FileType.LOG_FILTER.addExtension(logId);
            PrintWriter logFile =
                new PrintWriter(new File(this.logDir, logFileName));
            try {
                // copy the initial messages
                logFile.print(this.log.toString());
                File gcLogFile = new File(GC_LOG_NAME);
                if (gcLogFile.exists()) {
                    BufferedReader gcLog =
                        new BufferedReader(new FileReader(gcLogFile));
                    List<String> gcList = new ArrayList<String>();
                    String nextLine = gcLog.readLine();
                    while (nextLine != null) {
                        gcList.add(nextLine);
                        nextLine = gcLog.readLine();
                    }
                    for (int i = 1; i < gcList.size() - 2; i++) {
                        logFile.println(gcList.get(i));
                    }
                    gcLog.close();
                }
            } finally {
                logFile.close();
            }
        }
        emit("%s%n", getExploration().getLastMessage());
    }

    /** Outputs a diagnostic message if allowed by the verbosity, and optionally logs it. */
    private void emit(Verbosity min, String message, Object... args) {
        String text = String.format(message, args);
        if (min.compareTo(this.verbosity) <= 0) {
            System.out.print(text);
        }
        if (this.log != null) {
            this.log.append(text);
        }
    }

    /** Outputs a diagnostic message under any verbosity except #NONE, and optionally logs it. */
    private void emit(String message, Object... args) {
        emit(Verbosity.LOW, message, args);
    }

    private final String grammarName;
    private final List<String> startGraphNames;
    private final Verbosity verbosity;
    private final File logDir;
    private final ExplorationStatistics exploreStats;

    /**
     * Time of invocation, initialised at start time.
     */
    private Date startTime;
    private StringBuilder log;
    /**
     * Fixed name of the gc log file. If a file with this name is found, and
     * logging is switched on, the gc log is appended to the generator log.
     */
    static public final String GC_LOG_NAME = "gc.log";
}
