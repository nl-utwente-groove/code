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
 * $Id: Analyzer.java,v 1.5 2008-01-30 09:32:03 iovka Exp $
 */
package groove.util;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.iso.Bisimulator;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to analyse the effectiveness and efficiency of bisimilarity checking.
 * Works upon a stream file of graphs.
 * @author Arend Rensink
 * @version $Revision$
 * @deprecated no longer maintained
 */
@Deprecated
public class Analyzer extends CommandLineTool {

    /** The message used for printing usage information. */
    static public final String USAGE_MESSAGE =
        "Usage: Analyzer <graph stream files>";

    /**
     * Boundary for the number of graphs after which a progress signal is given.
     */
    static private final int PROGRESS_UNIT = 10;
    static private final String ID_PREFIX = "iso";

    /**
     * Boundary for the number of progress signals after which a line break is
     * given.
     */
    static private final int LINE_UNIT = 100;

    static private final Reporter reporter = Reporter.register(Analyzer.class);
    static private final int EQUAL = reporter.newMethod("Equality check");
    static private final int ISO = reporter.newMethod("Full isomorphism check");
    static private final int CERT = reporter.newMethod("Certificate check");
    static private final IsoChecker isoChecker =
        DefaultIsoChecker.getInstance(true);

    /** main method for the Analyzer */
    public static void main(String[] args) {
        Analyzer analyzer =
            new Analyzer(new LinkedList<String>(Arrays.asList(args)));
        analyzer.processArguments();
        analyzer.start();
    }

    /** Initializes a new Analayzer instance with the given arguments */
    public Analyzer(List<String> args) {
        super(args);
    }

    /** Starts the Analyzer */
    public void start() {
        init();
        boolean eof = false;
        while (!eof) {
            try {
                @SuppressWarnings("unchecked")
                Collection<Node> nodeSet =
                    (Collection<Node>) this.graphStream.readObject();
                @SuppressWarnings("unchecked")
                Collection<Edge> edgeSet =
                    (Collection<Edge>) this.graphStream.readObject();
                Graph graph = new NodeSetEdgeSetGraph();
                graph.addNodeSet(nodeSet);
                graph.addEdgeSet(edgeSet);
                graph.setFixed();
                analyze(graph);
            } catch (IOException e1) {
                eof = true;
            } catch (ClassNotFoundException e1) {
                printError("File contains wrong objects: " + e1.getMessage());
            }
        }
        printResults();
        exit();
    }

    /**
     * After processing the options, this implementation requires a list of file
     * and directory names. If the list is empty, or one of the files does not
     * exist, an error is printed and the program exits.
     */
    @Override
    protected void processArguments() {
        super.processArguments();
        if (getArgs().size() == 0) {
            printError("No files or directoies provided");
        }
        try {
            this.graphStreamName = getArgs().get(0);
            this.graphStream =
                new ObjectInputStream(new FileInputStream(this.graphStreamName));
        } catch (IOException e) {
            printError("Error while opening graph stream file "
                + this.graphStreamName + ": " + e.getMessage());
        }
    }

    /**
     * The tool generates no output; this implementation always returns
     * <tt>false</tt>.
     */
    @Override
    protected boolean supportsOutputOption() {
        return false;
    }

    /**
     * The tool has not verbosity level; this implementation always returns
     * <tt>false</tt>.
     */
    @Override
    protected boolean supportsVerbosityOption() {
        return false;
    }

    /**
     * Initialises the objects necessary for the analysis.
     */
    @SuppressWarnings("unchecked")
    protected void init() {
        startLog();
        println("Bisimulation analysis for " + this.graphStreamName);
        try {
            DefaultLabel.putTextList((List<String>) this.graphStream.readObject());
        } catch (IOException e) {
            printError("Error reading from graph stream file: "
                + e.getMessage());
        } catch (ClassNotFoundException e) {
            printError("Error reading from graph stream file: "
                + e.getMessage());
        }
        this.bisBag = new HashBag<Object>();
    }

    /**
     * Finalizes the objects necessary for the analysis.
     */
    protected void exit() {
        endLog();
    }

    @Override
    protected String getLogFileName() {
        return ID_PREFIX
            + this.graphStreamName.substring(this.graphStreamName.indexOf(ID_SEPARATOR));
    }

    /**
     * Performs the analysis
     */
    private void analyze(final Graph graph) {
        if (this.graphCount % PROGRESS_UNIT == 0) {
            System.out.print("g");
            this.progress++;
            if (this.progress % LINE_UNIT == 0) {
                System.out.println(" " + this.graphCount);
            }
        }
        this.graphCount++;
        Bisimulator bisimulator = new Bisimulator(graph);
        Object cert = null;
        reporter.start(CERT);
        cert = bisimulator.getGraphCertificate();
        reporter.stop();
        this.bisBag.add(cert);
        HashBag<Graph> isoCandidates = this.isoCandidateMap.get(cert);
        if (isoCandidates == null) {
            this.isoCandidateMap.put(cert, isoCandidates = new HashBag<Graph>());
            isoCandidates.add(graph);
        } else {
            boolean isoFound = false;
            for (Graph isoCandidate : isoCandidates.elementSet()) {
                reporter.start(EQUAL);
                isoFound =
                    graph.nodeSet().equals(isoCandidate.nodeSet())
                        && graph.edgeSet().equals(isoCandidate.edgeSet());
                reporter.stop();
                if (isoFound) {
                    this.equalCount++;
                    assert isoChecker.areIsomorphic(graph, isoCandidate) : "Two equal graphs are not isomorphic: "
                        + graph + " and " + isoCandidate;
                } else {
                    reporter.start(ISO);
                    isoFound = isoChecker.areIsomorphic(graph, isoCandidate);
                    reporter.stop();
                }
                this.isoChecks++;
                if (isoFound) {
                    this.isoCount++;
                    isoCandidates.add(isoCandidate);
                }
            }
            if (!isoFound) {
                isoCandidates.add(graph);
            }
            this.equalGraphs.add(new GraphObject(graph));
        }
    }

    private class GraphObject {
        GraphObject(final Graph graph) {
            this.graph = graph;
        }

        @Override
        public int hashCode() {
            return this.graph.nodeSet().hashCode()
                + this.graph.edgeSet().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.graph.nodeSet().equals(
                ((GraphObject) obj).graph.nodeSet())
                && this.graph.edgeSet().equals(
                    ((GraphObject) obj).graph.edgeSet());
        }

        private final Graph graph;
    }

    private void printResults() {
        println();
        println();
        printMatrix(new boolean[] {true, false},
            new String[][] {
                new String[] {"Number of graphs:", "" + this.graphCount},
                new String[] {"Number of isomorphism checks:",
                    "" + this.isoChecks},
                new String[] {"    equal:", "" + this.equalCount},
                new String[] {"    inequal but isomorphic:",
                    "" + (this.isoCount - this.equalCount)},
                new String[] {"    not isomorphic:",
                    "" + (this.isoChecks - this.isoCount)}});
        println();

        Collection<Integer> certDistribution = new TreeBag<Integer>();
        Collection<Object> isoDistribution = new TreeBag<Object>();
        Collection<Integer> certThroughIso = new TreeBag<Integer>();
        for (HashBag<Graph> graphs : this.isoCandidateMap.values()) {
            certThroughIso.add(new Integer(graphs.elementSet().size()));
            certDistribution.add(new Integer(graphs.size()));
            isoDistribution.addAll(graphs.multiplicityMap().values());
        }

        String[][] results = new String[4][];
        results[0] =
            new String[] {"Equivalence:", "#Partitions:", "Time (ms):",
                "Avg. (mus):"};
        long certTime = reporter.getTotalTime(CERT);
        results[1] =
            new String[] {"Certificate",
                "" + this.bisBag.multiplicityMap().size() + "  ",
                "" + certTime + "  ",
                "" + 1000 * certTime / this.graphCount + "  "};
        long isoTime = reporter.getTotalTime(ISO);
        long equalTime = reporter.getTotalTime(EQUAL);
        results[2] =
            new String[] {"Isomorphism", "" + isoDistribution.size() + "  ",
                "" + isoTime + "  ", "" + 1000 * isoTime / this.isoCount + "  "};
        results[3] =
            new String[] {"Equality", "" + this.equalGraphs.size() + "  ",
                "" + equalTime + "  ",
                "" + 1000 * equalTime / this.equalCount + "  "};
        printMatrix(new boolean[] {true, false, false, false}, results);
        println();

        println("Partition distributions (partition size = nr. of partitions of this size)");
        printMatrix(new boolean[] {true, true}, new String[][] {
            new String[] {"Level 2 equivalence:", "" + certDistribution},
            new String[] {"Isomorphism:", "" + isoDistribution},
            new String[] {"Intersection:", "" + certThroughIso}});
        report(Bisimulator.reporter);
        // report(TotalSimulation.reporter);
        // report(DefaultMorphism.reporter);
    }

    private void printMatrix(boolean[] orientation, String[][] text) {
        // compute the field widths
        int[] widths = new int[text[0].length];
        for (String[] row : text) {
            for (int c = 0; c < row.length; c++) {
                String field = row[c];
                widths[c] = Math.max(widths[c], field.length());
            }
        }
        // print the rows, padding the fields as required
        for (String[] row : text) {
            for (int c = 0; c < row.length; c++) {
                print(Groove.pad(row[c], widths[c], orientation[c]) + " ");
            }
            println();
        }
    }

    private void report(Reporter reporter) {
        println();
        reporter.myReport(new PrintWriter(System.out, true));
        if (isLogging()) {
            reporter.myReport(getLogWriter());
        }
    }

    private String graphStreamName;
    private ObjectInputStream graphStream;
    /**
     * Array of multisets to store the results of the bisimulation analysis.
     */
    private HashBag<Object> bisBag;
    /**
     * Map from most precise certificates to graphs having that certificate.
     */
    private final Map<Object,HashBag<Graph>> isoCandidateMap =
        new HashMap<Object,HashBag<Graph>>();
    /**
     * The total number of isomorphism checks.
     */
    private int isoChecks;
    // /**
    // * The number of failed isomorphism checks.
    // */
    // private int failedIsoChecks;
    /**
     * The set of equal graphs.
     */
    private final Set<GraphObject> equalGraphs = new HashSet<GraphObject>();
    /**
     * The number of succesful equality tests
     */
    private int equalCount;
    /**
     * The number of succesful isomorphism tests
     */
    private int isoCount;
    /**
     * The number of grpahs analyzed.
     */
    private int graphCount;

    /**
     * Counter for the number of progress signals given during analysis
     */
    private int progress;
}