/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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

import static nl.utwente.groove.explore.util.ExplorationReporter.time;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.cli.Verbosity;
import nl.utwente.groove.util.io.FileType;

/**
 * Reporter that writes a record of the exploration run to a file
 * in a given directory, named after the grammar and the start time.
 * The record consists of the run header, the high-verbosity statistics,
 * the exploration outcome and the collected messages.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class RunLogReporter extends AExplorationReporter {
    /**
     * Constructs a run log reporter.
     * @param context the shared report context; must precede this reporter in the composite
     * @param logDir the directory into which the log file is written
     * @param statistics the statistics reporter whose report is recorded
     */
    public RunLogReporter(ReportContext context, File logDir, StatisticsReporter statistics) {
        this.context = context;
        this.logDir = logDir;
        this.statistics = statistics;
    }

    @Override
    public void report() throws IOException {
        StringBuilder log = new StringBuilder();
        log.append(this.context.getHeader());
        log.append(String.format("%n"));
        String report = this.statistics.getReport(Verbosity.HIGH);
        if (!report.isEmpty()) {
            log.append(report);
            log.append(String.format("%n%n"));
        }
        log.append(String.format("%s%n", getExploration().getLastMessage()));
        String messages = this.context.getMessages(Verbosity.HIGH);
        if (!messages.isEmpty()) {
            log.append(String.format("%n%s", messages));
        }
        String logId = getGTS().getGrammar().getId() + "-"
            + this.context.getStartTime().toString().replace(' ', '_').replace(':', '-');
        String logFileName = FileType.LOG.addExtension(logId);
        time("Exporting log to " + logFileName);
        try (PrintWriter logFile = new PrintWriter(new File(this.logDir, logFileName))) {
            logFile.print(log);
        }
    }

    private final ReportContext context;
    private final File logDir;
    private final StatisticsReporter statistics;
}
