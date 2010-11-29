/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.explore.DefaultScenario;
import groove.graph.AbstractGraphShape;
import groove.graph.DeltaGraph;
import groove.graph.Edge;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.PartitionRefiner;
import groove.lts.AbstractGraphState;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.trans.DefaultApplication;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;
import groove.util.CommandLineTool.VerbosityOption;
import groove.util.Groove;
import groove.util.Reporter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * @author Eduardo Zambon
 */
public class ExplorationStatistics {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Number of bytes in a kilobyte */
    static private final int BYTES_PER_KB = 1024;

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Time stamp of the moment at which exploration was started. */
    private long startTime;

    /** Time stamp of the moment at which exploration was ended. */
    private long endTime;

    /** Amount of memory used at the moment at which exploration was started. */
    private long startUsedMemory;

    private GTS gts;
    private StringBuilder sb;
    private Formatter fm;
    private int verbosity = VerbosityOption.MEDIUM_VERBOSITY;
    private StatisticsListener statisticsListener = new StatisticsListener();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Standard constructor.
     * @param gts the GTS that will be explored.
     */
    public ExplorationStatistics(GTS gts) {
        assert gts != null;
        this.gts = gts;
        this.sb = new StringBuilder();
        this.fm = new Formatter(this.sb);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Configures the object to produce output to be used by the Simulator. */
    public void configureForSimulator() {
        this.setVerbosity(VerbosityOption.HIGH_VERBOSITY);
    }

    /** Configures the object to produce output to be used by the Generator. */
    public void configureForGenerator(int verbosity) {
        this.setVerbosity(verbosity);
    }

    /** Should be called right before the exploration starts. */
    public void start() {
        final Runtime runTime = Runtime.getRuntime();
        runTime.runFinalization();
        runTime.gc();
        this.startUsedMemory = runTime.totalMemory() - runTime.freeMemory();
        if (getVerbosity() == VerbosityOption.HIGH_VERBOSITY) {
            this.gts.addGraphListener(this.statisticsListener);
        }
        this.startTime = System.currentTimeMillis();
    }

    /** Should be called right after the exploration finishes. */
    public void stop() {
        this.endTime = System.currentTimeMillis();
        if (getVerbosity() == VerbosityOption.HIGH_VERBOSITY) {
            this.gts.removeGraphListener(this.statisticsListener);
        }
    }

    /**
     * @return the total running time of the exploration.
     */
    public long getRunningTime() {
        return this.endTime - this.startTime;
    }

    /**
     * Sets the verbosity level.
     * @param verbosity the verbosity level; should be a legal verbosity value.
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Returns the verbosity level.
     * The default level is <tt>MEDIUM_VERBOSITY</tt>
     */
    public int getVerbosity() {
        return this.verbosity;
    }

    /** Returns a string representation of a double as a percentage. */
    private String percentage(double fraction) {
        int percentage = (int) (fraction * 1000 + 0.5);
        String result = "" + (percentage / 10) + "." + (percentage % 10) + "%";
        if (result.length() == 4) {
            return " " + result;
        } else {
            return result;
        }
    }

    /** Prints an empty line to the output stream. */
    private void println() {
        this.sb.append("\n");
    }

    /** Prints a line of text to the output stream. */
    private void println(String text) {
        this.sb.append(text + "\n");
    }

    /** Prints a line of text to the output stream. */
    protected void printf(String text, Object... args) {
        this.fm.format(text, args);
    }

    /**
     * Returns a string describing the distribution of cache reconstruction
     * counts.
     */
    private String getCacheReconstructionDistribution() {
        List<Integer> sizes = new ArrayList<Integer>();
        boolean finished = false;
        for (int incarnation = 1; !finished; incarnation++) {
            int size = CacheReference.getFrequency(incarnation);
            finished = size == 0;
            if (!finished) {
                sizes.add(size);
            }
        }
        return Groove.toString(sizes.toArray());
    }

    /** Reports data on the LTS generated. */
    private void reportLTS() {
        println("\n\tStates:\t\t\t" + this.gts.nodeCount());
        int spuriousStateCount = this.gts.openStateCount();
        if (spuriousStateCount > 0) {
            println("\tExplored:\t\t"
                + (this.gts.nodeCount() - spuriousStateCount));
        }
        println("\tTransitions:\t" + this.gts.edgeCount());
    }

    /** Gives some statistics regarding the graphs and deltas. */
    private void reportGraphStatistics() {
        printf("\n\tGraphs:\n\t\tModifiable:\t\t%d%n",
            AbstractGraphShape.getModifiableGraphCount());
        printf("\t\tFrozen:\t\t\t%d%n",
            AbstractGraphState.getFrozenGraphCount());
        printf("\t\tBytes/state:\t%.1f%n", this.gts.getBytesPerState());
    }

    /** Gives some statistics regarding the generated transitions. */
    private void reportTransitionStatistics() {
        printf("\n\tTransitions:\n\t\tReused:\t\t%d%n",
            MatchSetCollector.getEventReuse());
        printf("\t\tConfluent:\t%d%n", MatchApplier.getConfluentDiamondCount());
        printf("\t\tEvents:\t\t%d%n", SystemRecord.getEventCount());
        printf("\tCoanchor reuse:\t%d/%d%n",
            SPOEvent.getCoanchorImageOverlap(),
            SPOEvent.getCoanchorImageCount());
    }

    /** Reports statistics on isomorphism checking. */
    private void reportIsomorphism() {
        int predicted = DefaultIsoChecker.getTotalCheckCount();
        int falsePos2 = DefaultIsoChecker.getDistinctSimCount();
        int falsePos1 =
            falsePos2 + DefaultIsoChecker.getDistinctSizeCount()
                + DefaultIsoChecker.getDistinctCertsCount();
        int equalGraphCount = DefaultIsoChecker.getEqualGraphsCount();
        int equalCertsCount = DefaultIsoChecker.getEqualCertsCount();
        int equalSimCount = DefaultIsoChecker.getEqualSimCount();
        int intCertOverlap = DefaultIsoChecker.getIntCertOverlap();
        printf("\n\tIsomorphism:\n\t\tPredicted:\t\t\t%d (-%d)%n", predicted,
            intCertOverlap);
        printf("\t\tFalse pos 1:\t\t%d (%s)%n", falsePos1,
            percentage((double) falsePos1 / (predicted - intCertOverlap)));
        printf("\t\tFalse pos 2:\t\t%d (%s)%n", falsePos2,
            percentage((double) falsePos2 / (predicted - intCertOverlap)));
        println("\t\tEqual graphs:\t\t" + equalGraphCount);
        println("\t\tEqual certificates:\t" + equalCertsCount);
        println("\t\tEqual simulation:\t" + equalSimCount);
        println("\t\tIterations:\t\t\t" + PartitionRefiner.getIterateCount());
        println("\t\tSymmetry breaking:\t"
            + PartitionRefiner.getSymmetryBreakCount());
    }

    /** Reports on the graph data. */
    private void reportGraphElementStatistics() {
        printf("\n\tDefault nodes:\t%d%n",
            groove.graph.DefaultNode.getNodeCount());
        printf("\tDefault labels:\t%d%n",
            groove.graph.DefaultLabel.getLabelCount());
        printf("\tFresh nodes:\t%d%n", DefaultApplication.getFreshNodeCount());
        printf("\tFresh edges:\t%d%n", groove.graph.DefaultEdge.getEdgeCount());
        double nodeAvg =
            (double) this.statisticsListener.getNodeCount()
                / this.gts.nodeCount();
        printf("\tAverage:\n\t\tNodes:\t%3.1f%n", nodeAvg);
        double edgeAvg =
            (double) this.statisticsListener.getEdgeCount()
                / this.gts.nodeCount();
        printf("\t\tEdges:\t%3.1f%n", edgeAvg);
    }

    /** Reports on the cache usage. */
    private void reportCacheStatistics() {
        println("\n\tCaches:\n\t\tCreated:\t\t"
            + CacheReference.getCreateCount());
        println("\t\tCleared:\t\t" + CacheReference.getClearCount());
        println("\t\tCollected:\t\t" + CacheReference.getCollectCount());
        println("\t\tReconstructed:\t" + CacheReference.getIncarnationCount());
        println("\t\tDistribution:\t" + getCacheReconstructionDistribution());
    }

    /** Reports on the time usage. */
    private void reportTime() {
        // Timing figures.
        long total = (this.endTime - this.startTime);
        long matching = SPORule.getMatchingTime();
        long running = DefaultScenario.getRunningTime();
        long overhead = total - running;
        long isoChecking = DefaultIsoChecker.getTotalTime();
        long generateTime = MatchApplier.getGenerateTime();
        long building = generateTime - isoChecking;
        long measuring = Reporter.getReportTime();

        // This calculation incorporates only transforming RuleMatches into
        // RuleApplications, bit weird maybe, but transforming is considered
        // everything besides the calculation of matches, isomorphisms, adding
        // to GTS, and reporter-duty: i.e. it's the "overhead" of the scenario.
        long transforming =
            running - matching - isoChecking - building - measuring;

        println("\nTime (ms):\t" + total);

        println("\tMatching:\t\t\t" + matching + "\t"
            + percentage(matching / (double) total));
        println("\tTransforming:\t\t" + transforming + "\t"
            + percentage(transforming / (double) total));
        println("\tIso checking:\t\t" + isoChecking + "\t"
            + percentage(isoChecking / (double) total));
        if (getVerbosity() == VerbosityOption.HIGH_VERBOSITY) {
            long certifying = DefaultIsoChecker.getCertifyingTime();
            long equalCheck = DefaultIsoChecker.getEqualCheckTime();
            long certCheck = DefaultIsoChecker.getCertCheckTime();
            long simCheck = DefaultIsoChecker.getSimCheckTime();
            println("\t\tCertifying:\t\t" + certifying + "\t"
                + percentage(certifying / (double) isoChecking));
            println("\t\tEquals check:\t" + equalCheck + "\t"
                + percentage(equalCheck / (double) isoChecking));
            println("\t\tCert check:\t\t" + certCheck + "\t"
                + percentage(certCheck / (double) isoChecking));
            println("\t\tSim check:\t\t" + simCheck + "\t"
                + percentage(simCheck / (double) isoChecking));
        }
        println("\tBuilding GTS:\t\t" + building + "\t"
            + percentage(building / (double) total));
        println("\tMeasuring:\t\t\t" + measuring + "\t"
            + percentage(measuring / (double) total));
        println("\tInitialization:\t\t" + overhead + "\t"
            + percentage(overhead / (double) total));
    }

    /**
     * Reports on the time usage.
     * @param usedMemory the final memory after generation, cache clearing and
     *        garbage collection.
     */
    private void reportSpace(long usedMemory) {
        println("\nSpace (kB):\t" + (usedMemory / BYTES_PER_KB));
    }

    /** Prints a report of the exploration to the output stream. */
    private void report() {
        // Clear the string builder before we start.
        this.sb.delete(0, this.sb.length());

        if (getVerbosity() == VerbosityOption.HIGH_VERBOSITY) {
            StringWriter sw = new StringWriter();
            Reporter.report(new PrintWriter(sw));
            this.sb.append(sw.toString());
            println();
            println("===============================================================================");
            println();
        }

        if (getVerbosity() > VerbosityOption.LOW_VERBOSITY) {
            final Runtime runTime = Runtime.getRuntime();
            // Clear all caches to see all available memory.
            for (GraphState state : this.gts.nodeSet()) {
                if (state instanceof AbstractCacheHolder<?>) {
                    ((AbstractCacheHolder<?>) state).clearCache();
                }
                if (state instanceof GraphNextState) {
                    ((AbstractCacheHolder<?>) ((GraphNextState) state).getEvent()).clearCache();
                }
            }
            // The following is to make sure that the graph reference queue gets
            // flushed.
            new DeltaGraph().nodeSet();
            System.runFinalization();
            System.gc();
            long usedMemory = runTime.totalMemory() - runTime.freeMemory();

            println("Statistics:");
            reportLTS();
            if (getVerbosity() == VerbosityOption.HIGH_VERBOSITY
                && Groove.GATHER_STATISTICS) {
                reportGraphStatistics();
                reportTransitionStatistics();
                reportIsomorphism();
                reportGraphElementStatistics();
                reportCacheStatistics();
            }
            reportTime();
            reportSpace(usedMemory - this.startUsedMemory);
        }
    }

    /** Prints the statistics report to stdout and returns it. */
    public String printReport() {
        String report = this.getReport();
        System.out.print(report);
        return report;
    }

    /** Returns the statistics report string. */
    public String getReport() {
        this.report();
        return this.sb.toString();
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    /** Listener to an LTS that counts the nodes and edges of the states. */
    private static class StatisticsListener extends GraphAdapter {
        /** Empty constructor with the correct visibility. */
        StatisticsListener() {
            // Empty.
        }

        @Override
        public void addUpdate(GraphShape graph, Node node) {
            GraphState state = (GraphState) node;
            this.nodeCount += state.getGraph().nodeCount();
            this.edgeCount += state.getGraph().edgeCount();
        }

        @Override
        public void addUpdate(GraphShape graph, Edge edge) {
            // Does nothing by design.
        }

        /** Returns the number of nodes in the added states. */
        public int getNodeCount() {
            return this.nodeCount;
        }

        /** Returns the number of edges in the added states. */
        public int getEdgeCount() {
            return this.edgeCount;
        }

        private int nodeCount;
        private int edgeCount;
    }
}
