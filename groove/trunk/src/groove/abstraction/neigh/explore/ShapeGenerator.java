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

import static groove.explore.GeneratorOptions.RESULT_NAME;
import static groove.explore.GeneratorOptions.RESULT_USAGE;
import static groove.explore.GeneratorOptions.RESULT_VAR;
import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.NeighAbstraction;
import groove.abstraction.neigh.lts.AGTS;
import groove.abstraction.pattern.explore.PatternShapeGenerator.MultiplicityHandler;
import groove.explore.AcceptorEnumerator;
import groove.explore.Exploration;
import groove.explore.Generator;
import groove.explore.StrategyEnumerator;
import groove.explore.encode.Serialized;
import groove.explore.util.ExplorationReporter;
import groove.explore.util.LTSLabels;
import groove.explore.util.LTSLabels.Flag;
import groove.explore.util.LTSReporter;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.host.ValueNode;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.graph.Node;
import groove.util.cli.GrammarHandler;
import groove.util.cli.GrooveCmdLineTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 * Counterpart of {@link Generator} for abstract state space exploration.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeGenerator extends GrooveCmdLineTool<AGTS> {
    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public ShapeGenerator(String... args) throws CmdLineException {
        super("Generator", args);
    }

    /**
     * Starts the state space generation process. Before invoking this method,
     * all relevant parameters should be set.
     */
    @Override
    public AGTS run() throws Exception {
        return explore();
    }

    /**
     * Explores the state space.
     */
    private AGTS explore() throws Exception {
        NeighAbstraction.initialise();
        if (hasNodeMult()) {
            NeighAbsParam.getInstance().setNodeMultBound(getNodeMult());
        }
        if (hasEdgeMult()) {
            NeighAbsParam.getInstance().setEdgeMultBound(getEdgeMult());
        }
        if (isThreeWay()) {
            NeighAbsParam.getInstance().setUseThreeValues(true);
        }
        GrammarModel grammarModel = computeGrammarModel();
        Grammar grammar = grammarModel.toGrammar().setFixed();
        checkGrammarForAbstraction(grammar);
        AGTS gts = new AGTS(grammar, isReachability());
        Exploration exploration = computeExploration();
        for (ExplorationReporter reporter : getReporters()) {
            reporter.start(exploration, gts);
        }
        exploration.play(gts, null);
        if (exploration.isInterrupted()) {
            throw new Exception("Exploration interrupted");
        }
        gts.setResult(exploration.getResult());
        for (ExplorationReporter reporter : getReporters()) {
            reporter.report();
        }
        return gts;
    }

    /**
     * Creates and returns a grammar model, using the options
     * to find the location and other settings.
     */
    private GrammarModel computeGrammarModel() throws IOException {
        GrammarModel result;
        result = GrammarModel.newInstance(getGrammar());
        if (hasStartGraphName()) {
            result.setLocalActiveNames(ResourceKind.HOST, getStartGraphName());
        }
        return result;
    }

    /** 
     * Returns the exploration strategy to be used, based 
     * on the command-line settings.
     */
    private Exploration computeExploration() throws FormatException {
        Serialized strategy =
            StrategyEnumerator.newInstance().parseCommandline(getStrategy());
        if (strategy == null) {
            throw new FormatException("Strategy '%s' cannot be parsed",
                getStrategy());
        }
        Serialized acceptor =
            AcceptorEnumerator.newInstance().parseCommandline(getAcceptor());
        if (acceptor == null) {
            throw new FormatException("Acceptor '%s' cannot be parsed",
                getAcceptor());
        }
        return new Exploration(strategy, acceptor, getResultCount());
    }

    /** Tests whether a given graph grammar is suitable for shape generation. */
    private void checkGrammarForAbstraction(Grammar grammar)
        throws FormatException {
        if (!grammar.getProperties().isInjective()) {
            throw new FormatException(
                "Grammar %s is not injective! Abstraction can only work with injective rules...",
                grammar.getName());
        }
        for (Node node : grammar.getStartGraph().nodeSet()) {
            if (node instanceof ValueNode) {
                throw new FormatException(
                    "Grammar start graph has attributes! Abstraction cannot handle attributes...",
                    grammar.getName());
            }
        }
        for (Rule rule : grammar.getAllRules()) {
            for (RuleNode node : rule.lhs().nodeSet()) {
                if (node instanceof OperatorNode
                    || node instanceof VariableNode) {
                    throw new FormatException(
                        "Grammar rule %s operates on attributes! Abstraction cannot handle attributes...",
                        rule.getFullName());
                }
            }
            for (RuleEdge edge : rule.lhs().edgeSet()) {
                if (!edge.label().isAtom()) {
                    throw new FormatException(
                        "Grammar rule %s has regular expression %s that the abstraction cannot handle!",
                        rule.getFullName(), edge.label());
                }
            }
        }
    }

    /** Returns the exploration reporters enabled on the basis of the options. */
    public List<ExplorationReporter> getReporters() {
        if (this.reporters == null) {
            this.reporters = computeReporters();
        }
        return this.reporters;
    }

    /** Factory method for the reporters associated with this invocation. */
    private List<ExplorationReporter> computeReporters() {
        List<ExplorationReporter> result = new ArrayList<ExplorationReporter>();
        result.add(new ShapeLogReporter(getGrammar().getPath(),
            getStartGraphName(), getVerbosity(), isReachability()));
        if (hasLtsPattern()) {
            LTSLabels ltsLabels =
                new LTSLabels(Flag.START, Flag.FINAL, Flag.OPEN);
            result.add(new LTSReporter(getLtsPattern(), ltsLabels));
        }
        return result;
    }

    /** The reporters that can be built on the basis of the options. */
    private List<ExplorationReporter> reporters;

    /** Returns the grammar location. */
    private File getGrammar() {
        return this.grammar;
    }

    @Argument(metaVar = GrammarHandler.META_VAR, required = true,
            usage = GrammarHandler.USAGE, handler = GrammarHandler.class)
    private File grammar;

    /** Indicates if the start graph name has been set. */
    private boolean hasStartGraphName() {
        return getStartGraphName() != null;
    }

    /** Returns the start graph name, relative to the grammar location. */
    private String getStartGraphName() {
        return this.startGraphName;
    }

    @Argument(index = 1, metaVar = "start",
            usage = "Start graph name (defined in grammar, no extension)")
    private String startGraphName;

    private String getStrategy() {
        return this.strategy;
    }

    @Option(name = "-s", metaVar = "str")
    private String strategy = "shapedfs";

    private String getAcceptor() {
        return this.acceptor;
    }

    @Option(name = "-a", metaVar = "acc")
    private String acceptor = "final";

    private int getResultCount() {
        return this.resultCount;
    }

    @Option(name = RESULT_NAME, metaVar = RESULT_VAR, usage = RESULT_USAGE)
    private int resultCount = 0;

    private boolean hasNodeMult() {
        return getNodeMult() != 0;
    }

    private int getNodeMult() {
        return this.nodeMult;
    }

    @Option(name = "-n", metaVar = MultiplicityHandler.VAR,
            usage = MultiplicityHandler.NODE_USAGE,
            handler = MultiplicityHandler.class)
    private int nodeMult;

    private boolean hasEdgeMult() {
        return getEdgeMult() != 0;
    }

    private int getEdgeMult() {
        return this.edgeMult;
    }

    @Option(name = "-m", metaVar = MultiplicityHandler.VAR,
            usage = MultiplicityHandler.EDGE_USAGE,
            handler = MultiplicityHandler.class)
    private int edgeMult;

    private boolean hasLtsPattern() {
        return getLtsPattern() != null;
    }

    private String getLtsPattern() {
        return this.ltsPattern;
    }

    @Option(
            name = "-o",
            metaVar = "file",
            usage = "Save the generated LTS to a file with name derived from <file>, "
                + "in which '#' is instantiated with the grammar ID. "
                + "The \"-ef\"-option controls some additional state labels. "
                + "The optional extension determines the output format (default is .gxl)")
    private String ltsPattern;

    /** Indicates if the three-valued exploration option has been invoked. */
    private boolean isThreeWay() {
        return this.threeWay;
    }

    @Option(
            name = "-t",
            usage = "Limit the possible multiplicity values to three: 0, 1, or 0+.")
    private boolean threeWay;

    /** Indicates if the reachability option has been invoked. */
    private boolean isReachability() {
        return this.reachability;
    }

    @Option(
            name = "-c",
            usage = "Reachability exploration; disables model checking afterwards")
    private boolean reachability;

    // ------------------------------------------------------------------------
    // Main method
    // ------------------------------------------------------------------------

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * Always exits with {@link System#exit(int)}; for programmatic use
     * see {@link #execute(String[])}.
     * @param args generator options, grammar and start graph name
     */
    public static void main(String[] args) {
        tryExecute(ShapeGenerator.class, args);
    }

    /**
     * Loads a graph grammar, and returns the generated transition system.
     * @param args generator options and arguments
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public AGTS execute(String... args) throws Exception {
        staticGTS = new ShapeGenerator(args).run();
        return staticGTS;
    }

    /** Returns the most recently generated GTS. */
    static public AGTS getGts() {
        return staticGTS;
    }

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static AGTS staticGTS;

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

}
