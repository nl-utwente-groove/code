/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.abstraction.neigh.explore;

import groove.abstraction.neigh.Abstraction;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.lts.AGTS;
import groove.explore.AcceptorEnumerator;
import groove.explore.Exploration;
import groove.explore.Generator;
import groove.explore.Generator.ResultOption;
import groove.explore.Generator.TemplatedOption;
import groove.explore.StrategyEnumerator;
import groove.explore.StrategyValue;
import groove.explore.encode.Serialized;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.explore.util.ExplorationStatistics;
import groove.graph.DefaultGraph;
import groove.graph.Node;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.util.CommandLineOption;
import groove.util.CommandLineTool;
import groove.util.GenerateProgressMonitor;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.IOException;
import java.util.List;

/**
 * Counterpart of {@link Generator} for abstract state space exploration.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeGenerator extends CommandLineTool {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Usage message for the generator. */
    private static final String USAGE_MESSAGE =
        "Usage: ShapeGenerator [options] <grammar> <start-graph-name>";

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static AGTS gts;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** String describing the location where the grammar is to be found. */
    private String grammarLocation;
    /** String describing the start graph within the grammar. */
    private String startGraphName;
    /** The graph grammar used for the generation. */
    private GraphGrammar grammar;
    /** The exploration to be used for the state space generation. */
    private Exploration exploration;
    /** The exploration statistics for the generated state space. */
    private ExplorationStatistics explorationStats;
    /** Flag that indicates the statistics should be printed. */
    private boolean isPrintStats;
    /** Flag that indicates if we should go to reachability mode. */
    private boolean isReachability;
    /** Local references to the command line options. */
    private final TemplatedOption<Strategy> strategyOption;
    private final TemplatedOption<Acceptor> acceptorOption;
    private final ResultOption resultOption;
    /** Reduced GTS. */
    private AGTS reducedGTS;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public ShapeGenerator(String... args) {
        super(args);
        this.strategyOption =
            new TemplatedOption<Strategy>(
                "s",
                "str",
                StrategyEnumerator.newInstance(StrategyValue.ABSTRACT_STRATEGIES));
        this.acceptorOption =
            new TemplatedOption<Acceptor>("a", "acc",
                AcceptorEnumerator.newInstance());
        this.resultOption = new ResultOption();
        addOption(this.strategyOption);
        addOption(this.acceptorOption);
        addOption(this.resultOption);
        addOption(new MultiplicityBoundOption(MultKind.NODE_MULT));
        addOption(new MultiplicityBoundOption(MultKind.EDGE_MULT));
        addOption(new StatsOption());
        addOption(new ThreeMultValOption());
        addOption(new ReachabilityOption());
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    /**
     * This implementation returns <tt>{@link #USAGE_MESSAGE}</tt>.
     */
    @Override
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    /**
     * Goes through the list of command line arguments and tries to find command
     * line options. The options and their parameters are subsequently removed
     * from the argument list. If an option cannot be parsed, the method prints
     * an error message and terminates the program.
     */
    @Override
    public void processArguments() {
        super.processArguments();

        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
            setGrammarLocation(argsList.remove(0));
        }
        if (argsList.size() > 0) {
            setStartGraph(argsList.remove(0));
        }
        if (this.grammarLocation == null) {
            printError("No grammar location specified", true);
        }

        Serialized strategy;
        if (isOptionActive(this.strategyOption)) {
            strategy = this.strategyOption.getValue();
        } else {
            strategy = new Serialized("shapedfs");
        }

        Serialized acceptor = null;
        if (isOptionActive(this.acceptorOption)) {
            acceptor = this.acceptorOption.getValue();
        } else {
            acceptor = new Serialized("final");
        }

        int nrResults;
        if (isOptionActive(this.resultOption)) {
            nrResults = this.resultOption.getValue();
        } else {
            nrResults = 0;
        }

        this.exploration = new Exploration(strategy, acceptor, nrResults);
    }

    /**
     * Callback method to check whether the log command line option is
     * supported. This implementation returns <tt>false</tt> always.
     */
    // EZ says: if you want logs, use pipes... ;-)
    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Sets the grammar to be used for state space generation.
     * @param grammarLocation the file name of the grammar (with or without file
     *        name extension)
     */
    private void setGrammarLocation(String grammarLocation) {
        this.grammarLocation = grammarLocation;
    }

    /**
     * Sets the start graph to be used for state space generation.
     * @param startGraphName the name of the start graph (without file name
     *        extension)
     */
    private void setStartGraph(String startGraphName) {
        this.startGraphName = startGraphName;
    }

    /** Resets the generator. */
    private void reset() {
        Abstraction.initialise();
        gts = null;
        this.reducedGTS = null;
        this.explorationStats = new ExplorationStatistics(getGTS());
        this.explorationStats.configureForGenerator(this.getVerbosity());
    }

    /**
     * Returns the GTS that is being generated. The GTS is lazily obtained from
     * the grammar if it had not yet been initialised.
     * @see #getGrammar()
     */
    public AGTS getGTS() {
        if (gts == null) {
            gts = new AGTS(getGrammar(), this.isReachability);
        }
        return gts;
    }

    /**
     * Starts the state space generation process. Before invoking this method,
     * all relevant parameters should be set.
     */
    public void start() {
        processArguments();
        explore();
        report();
    }

    /**
     * Explores the state space.
     */
    public void explore() {
        reset();
        if (getVerbosity() > LOW_VERBOSITY) {
            println("\n======================================================\n");
            println("Grammar:\t" + this.grammarLocation);
            println("Start graph:\t"
                + (this.startGraphName == null ? "default"
                        : this.startGraphName));
            println("Exploration:\t" + this.exploration.getIdentifier());
            print("Node bound:\t" + Parameters.getNodeMultBound()
                + "\tEdge bound:\t" + Parameters.getEdgeMultBound());
            if (Parameters.isUseThreeValues()) {
                println("\tLIMITING MULTIPLICITIES TO 0, 1 and 0+");
            } else {
                println();
            }
            if (this.isReachability) {
                println("Reachability mode ON.");
            }
            println("Timestamp:\t" + this.invocationTime);
            print("\nProgress:\n\n");
            getGTS().addLTSListener(new GenerateProgressMonitor());
        }
        this.explorationStats.start();
        try {
            this.exploration.play(getGTS(), null);
            if (this.exploration.isInterrupted()) {
                new Exception().printStackTrace();
            }
        } catch (FormatException e) {
            e.printStackTrace();
        }
        this.explorationStats.stop();
    }

    /**
     * Returns the grammar used for generating the state space. The grammar is
     * lazily loaded in. The method throws an error and returns
     * <code>null</code> if the grammar could not be loaded.
     */
    private GraphGrammar getGrammar() {
        if (this.grammar == null) {
            this.loadGrammar(this.grammarLocation, this.startGraphName);
        }
        return this.grammar;
    }

    /** Loads a grammar from a given grammar location and a start graph. */
    private void loadGrammar(String grammarFile, String startGraph) {
        try {
            GrammarModel model = Groove.loadGrammar(grammarFile);
            model.localSetStartGraph(startGraph);
            this.grammar = model.toGrammar();
            this.grammar.setFixed();
        } catch (FormatException exc) {
            printError("Grammar format error: " + exc.getMessage(), false);
        } catch (IOException exc) {
            printError("I/O error while loading grammar: " + exc.getMessage(),
                false);
        }
        // Check if the grammar can be used in abstract mode.
        checkGrammarForAbstraction();
    }

    private void checkGrammarForAbstraction() {
        if (!this.grammar.getProperties().isInjective()) {
            printError(
                "Grammar is not injective! Abstraction can only work with injective rules...",
                false);
        }
        for (Node node : this.grammar.getStartGraph().nodeSet()) {
            if (node instanceof ValueNode) {
                printError(
                    "Grammar start graph has attributes! Abstraction cannot handle attributes...",
                    false);
            }
        }
        for (Rule rule : this.grammar.getAllRules()) {
            for (RuleNode node : rule.lhs().nodeSet()) {
                if (node instanceof OperatorNode
                    || node instanceof VariableNode) {
                    printError(
                        "Grammar rules operate on attributes! Abstraction cannot handle attributes...",
                        false);
                }
            }
            for (RuleEdge edge : rule.lhs().edgeSet()) {
                if (!edge.label().isAtom()) {
                    printError(
                        "Grammar rules have regular expressions that the abstraction cannot handle!",
                        false);
                }
            }
        }
    }

    /** Basic setter method. */
    private void setPrintStats() {
        this.isPrintStats = true;
    }

    /** Basic setter method. */
    private void setReachability() {
        this.isReachability = true;
    }

    /** Writes output accordingly to options given to the generator. */
    public void report() {
        reportGTS(getGTS(), "Original GTS");
        AGTS reducedGTS = getReducedGTS();
        reportGTS(reducedGTS, "Reduced GTS");
        printfMedium("\nResult count: "
            + this.exploration.getLastResult().getValue().size());
        // See if we have to save the GTS into a file.
        if (getOutputFileName() != null) {
            DefaultGraph gtsGraph =
                this.reducedGTS.toPlainGraph(true, true, true, false);
            try {
                Groove.saveGraph(gtsGraph, getOutputFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // See if we have to print the statistics.
        if (this.isPrintStats) {
            String report = this.explorationStats.getReport();
            if (report.length() > 0) {
                println();
                println(report);
            }
        }
    }

    private void reportGTS(AGTS gts, String header) {
        printfMedium(
            "\n"
                + header
                + ": States: %d (%d final) -- %d subsumed (%d discarded) / Transitions: %d (%d subsumed)\n",
            gts.getStateCount(), gts.getFinalStates().size(),
            gts.getSubsumedStatesCount(), gts.openStateCount(),
            gts.getTransitionCount(), gts.getSubsumedTransitionsCount());
    }

    /** Basic getter method. */
    public AGTS getReducedGTS() {
        if (this.reducedGTS == null) {
            this.reducedGTS = getGTS().reduceGTS();
        }
        return this.reducedGTS;
    }

    // ------------------------------------------------------------------------
    // Main method
    // ------------------------------------------------------------------------

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * @param args generator options, grammar and start graph name
     */
    public static void main(String[] args) {
        new ShapeGenerator(args).start();
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /**
     * Command line option to specify a multiplicity bound.
     * 
     * @author Eduardo Zambon
     */
    private static class MultiplicityBoundOption implements CommandLineOption {

        final MultKind kind;

        MultiplicityBoundOption(MultKind kind) {
            this.kind = kind;
        }

        @Override
        public String getName() {
            String name = null;
            switch (this.kind) {
            case NODE_MULT:
                name = "n";
                break;
            case EDGE_MULT:
                name = "m";
                break;
            default:
                assert false;
            }
            return name;
        }

        @Override
        public String[] getDescription() {
            String type = null;
            switch (this.kind) {
            case NODE_MULT:
                type = "node";
                break;
            case EDGE_MULT:
                type = "edge";
                break;
            default:
                assert false;
            }
            return new String[] {
                "Set the " + type + " multiplicity bound to "
                    + "the given value.",
                "Argument '" + getParameterName()
                    + "' must be greater than zero (default value is 1)."};
        }

        @Override
        public String getParameterName() {
            return "val";
        }

        @Override
        public boolean hasParameter() {
            return true;
        }

        @Override
        public void parse(String parameter) throws IllegalArgumentException {
            int bound = 0;
            try {
                bound = Integer.parseInt(parameter);
            } catch (NumberFormatException exc) {
                throw new IllegalArgumentException("verbosity value '"
                    + parameter + "' must be numeric");
            }
            if (bound < 1) {
                throw new IllegalArgumentException("'" + parameter
                    + "' bound must be >= 1.");
            }
            switch (this.kind) {
            case NODE_MULT:
                Parameters.setNodeMultBound(bound);
                break;
            case EDGE_MULT:
                Parameters.setEdgeMultBound(bound);
                break;
            default:
                assert false;
            }
        }
    }

    /**
     * Command line option to specify logging of exploration statistics.
     * 
     * @author Eduardo Zambon
     */
    private class StatsOption implements CommandLineOption {

        @Override
        public String[] getDescription() {
            return new String[] {"Print the exploration statistics to stdout."};
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public String getName() {
            return "p";
        }

        @Override
        public boolean hasParameter() {
            return false;
        }

        @Override
        public void parse(String parameter) {
            ShapeGenerator.this.setPrintStats();
        }

    }

    /**
     * Command line option to specify the use of three values of multiplicity
     * only.
     * 
     * @author Eduardo Zambon
     */
    private class ThreeMultValOption implements CommandLineOption {

        @Override
        public String[] getDescription() {
            return new String[] {"Limit the possible multiplicity values to three: 0, 1, or 0+."};
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public String getName() {
            return "t";
        }

        @Override
        public boolean hasParameter() {
            return false;
        }

        @Override
        public void parse(String parameter) {
            Parameters.setUseThreeValues(true);
        }

    }

    /**
     * Command line option to specify that the exploration is based on
     * reachability only.
     * 
     * @author Eduardo Zambon
     */
    private class ReachabilityOption implements CommandLineOption {

        @Override
        public String[] getDescription() {
            return new String[] {"Reachability exploration, disables model checking afterwards."};
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public String getName() {
            return "c";
        }

        @Override
        public boolean hasParameter() {
            return false;
        }

        @Override
        public void parse(String parameter) {
            ShapeGenerator.this.setReachability();
        }

    }

}
