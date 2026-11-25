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
package nl.utwente.groove.test.explore;

import static nl.utwente.groove.test.explore.ExploreTestSupport.loadGrammar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.util.StatisticsReporter;
import nl.utwente.groove.lts.AbstractGraphState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Verbosity;

/**
 * Tests what the {@link StatisticsReporter} does to the exploration it
 * reports on: below high verbosity it must not build the graphs of the
 * states that are added, since for a state that is neither stored nor
 * explored nothing else does.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class StatisticsReporterTest {
    /** Grammar without property rules, so that adding a state does not
     * involve matching. */
    static private final String GRAMMAR = "generate-binary-tree";

    /** Depth-first exploration that does not store its states, up to a
     * bound: the states beyond the bound are added but never explored. */
    static private final String CONFIG = "persistence=none next=newest cost=uniform bound=cost:3";

    /** Label of a report line that only the graph counter can provide. */
    static private final String NODE_AVERAGE = "Nodes/state (avg):";

    /**
     * Tests that a reporter of medium verbosity (the default of the
     * generator) leaves the added states without a cache, and so without a
     * graph, and that its report then omits the graph averages even if asked
     * at high verbosity.
     */
    @Test
    public void testMediumBuildsNoGraphs() throws Exception {
        Run run = new Run(Verbosity.MEDIUM);
        assertTrue(run.added > 0, "The exploration should have added states");
        assertEquals(0, run.cached, "No added state should have a cache at medium verbosity");
        assertFalse(run.reporter.getReport(Verbosity.HIGH).contains(NODE_AVERAGE),
                    "The graph averages should be omitted when they were not collected");
    }

    /**
     * Tests that a reporter of high verbosity counts the graphs of all added
     * states, and reports their average.
     */
    @Test
    public void testHighReportsGraphAverages() throws Exception {
        Run run = new Run(Verbosity.HIGH);
        assertEquals(run.added, run.cached,
                     "Every added state should have a cache at high verbosity");
        String average = String
            .format(Locale.ROOT, "%.1f", (double) run.nodes / run.added);
        String report = run.reporter.getReport();
        assertTrue(report.contains(NODE_AVERAGE),
                   "The graph averages should be reported at high verbosity");
        String line = report
            .lines()
            .filter(l -> l.contains(NODE_AVERAGE))
            .findFirst()
            .orElseThrow();
        // the reporter formats in the default locale
        assertEquals(average, line.substring(line.indexOf(':') + 1).trim().replace(',', '.'),
                     "The reported node average should be that of the added states");
    }

    /** An exploration of {@link #GRAMMAR} under {@link #CONFIG}, observed by
     * a statistics reporter of a given verbosity. */
    private static class Run {
        Run(Verbosity verbosity) throws Exception {
            GTS gts = new GTS(loadGrammar(GRAMMAR));
            // this adds the start state, which the listeners below
            // therefore do not see
            Exploration exploration = ExploreTypeConverter
                .toExploreType(ExploreConfig.parse(CONFIG))
                .newExploration(gts, null);
            this.reporter = new StatisticsReporter(verbosity, false);
            this.reporter.start(exploration, gts);
            // registered after the listeners of the reporter, hence notified
            // after them
            gts.addLTSListener(new GTSListener() {
                @Override
                public void addUpdate(GTS g, GraphState state) {
                    Run.this.added++;
                    if (((AbstractGraphState) state).hasCache()) {
                        Run.this.cached++;
                        Run.this.nodes += state.getGraph().nodeCount();
                    }
                }
            });
            exploration.play();
            this.reporter.report();
        }

        /** The reporter that observed the exploration. */
        final StatisticsReporter reporter;
        /** Number of states added during the exploration. */
        int added;
        /** Number of added states that had a cache once the reporter was
         * notified of them. */
        int cached;
        /** Number of nodes of the added states that
         * had a cache. */
        int nodes;
    }
}
