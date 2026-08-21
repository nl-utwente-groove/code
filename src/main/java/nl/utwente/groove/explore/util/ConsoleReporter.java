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

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.cli.Verbosity;

/**
 * Reporter that describes the exploration run on the standard output,
 * at a given verbosity: the run header at start, and the statistics,
 * the exploration outcome and the collected messages at report time.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class ConsoleReporter extends AExplorationReporter {
    /**
     * Constructs a console reporter.
     * @param context the shared report context; must precede this reporter in the composite
     * @param verbosity the verbosity with which messages are printed on standard output
     * @param statistics the statistics reporter whose report is printed
     */
    public ConsoleReporter(ReportContext context, Verbosity verbosity,
                           StatisticsReporter statistics) {
        this.context = context;
        this.verbosity = verbosity;
        this.statistics = statistics;
    }

    @Override
    public void start(Exploration exploration, GTS gts) {
        super.start(exploration, gts);
        emit(Verbosity.MEDIUM, "%s%n", this.context.getHeader());
    }

    @Override
    public void report() {
        if (!this.verbosity.isLow()) {
            System.out.printf("%n%s%n", this.statistics.getReport(this.verbosity));
        }
        emit(Verbosity.MEDIUM, "%s%n", getExploration().getLastMessage());
        String messages = this.context.getMessages(this.verbosity);
        if (!messages.isEmpty()) {
            emit(Verbosity.LOW, "%n%s%n", messages);
        }
    }

    /** Prints a formatted message if allowed by the verbosity. */
    private void emit(Verbosity min, String format, Object... args) {
        if (min.compareTo(this.verbosity) <= 0) {
            System.out.printf(format, args);
        }
    }

    private final ReportContext context;
    private final Verbosity verbosity;
    private final StatisticsReporter statistics;
}
