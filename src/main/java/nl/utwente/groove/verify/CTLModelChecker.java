/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.verify;

import static nl.utwente.groove.explore.Verbosity.LOW;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.explore.Generator.LTSLabelsHandler;
import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSLabels.Flag;
import nl.utwente.groove.grammar.host.DefaultHostNode;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.cli.GrooveCmdLineParser;
import nl.utwente.groove.util.cli.GrooveCmdLineTool;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.verify.Proposition.Arg;

/**
 * Command-line tool directing the model checking process.
 *
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-03-28 07:03:03 $
 */
public class CTLModelChecker extends GrooveCmdLineTool<Object> {
    /**
     * Constructor.
     * @param args the command-line arguments for the model checker
     */
    public CTLModelChecker(String... args) {
        super("ModelChecker", args);
    }

    @Override
    protected GrooveCmdLineParser createParser(String appName) {
        GrooveCmdLineParser result = new GrooveCmdLineParser(appName, this) {
            @Override
            public void printSingleLineUsage(Writer w, ResourceBundle rb) {
                int optionCount = getOptions().size();
                PrintWriter pw = new PrintWriter(w);
                for (int ix = 0; ix < optionCount - 1; ix++) {
                    printSingleLineOption(pw, getOptions().get(ix), rb, true);
                }
                pw.print(" [");
                pw.print(getOptions().get(optionCount - 1).getNameAndMeta(rb));
                pw.print(" | ");
                pw.print(getArguments().get(0).getNameAndMeta(rb));
                pw.print(']');
                pw.flush();
            }
        };
        // move -g to the final position
        var handlers = result.getOptions();
        var genHandler
            = handlers.stream().filter(h -> h instanceof GeneratorHandler).findFirst().get();
        handlers.remove(genHandler);
        handlers.add(genHandler);
        return result;
    }

    /**
     * Method managing the actual work to be done.
     */
    @Override
    protected Object run() throws Exception {
        ctlCheck(this.genArgs == null
            ? null
            : this.genArgs.get());
        return null;
    }

    private void ctlCheck(String[] genArgs) throws Exception {
        long genStartTime = System.currentTimeMillis();
        ModelFacade model;
        if (genArgs != null) {
            emit("Generator:\t%s%n", Groove.toString(genArgs, " ", ""));
            model = generateModel(genArgs);
        } else if (this.modelGraph == null) {
            throw new Exception(
                "Either generator argument -g or model file name should be provided");
        } else if (this.modelGraph.isDirectory()) {
            emit("Rule system:\t%s%n", this.modelGraph);
            // we have to generate the transition system
            model = generateModel(this.modelGraph.getPath());
        } else {
            emit("Model:\t%s%n", this.modelGraph);
            model = new GraphFacade(Groove.loadGraph(this.modelGraph), this.ltsLabels);
            emit("Model loaded:\t%s states%n", model.nodeSet().size());
        }
        // check if the formulas match the model
        if (model instanceof GTSFacade gtsModel) {
            var errors = new FormatErrorSet();
            for (var formula : this.ctlProps) {
                try {
                    formula.check(gtsModel.gts);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
            errors.throwException();
        }
        long mcStartTime = System.currentTimeMillis();
        int maxWidth = 0;
        Map<Formula,Boolean> outcome = new HashMap<>();
        for (Formula property : this.ctlProps) {
            emit("Formula %s: ", property.toString());
            maxWidth = Math.max(maxWidth, property.getParseString().length());
            CTLMarker marker = new CTLMarker(property, model);
            emit("[initialised] ");
            outcome.put(property, marker.hasValue());
            emit("[checked]\n");
        }
        emit("%n");
        emit(LOW, "Model checking outcome (for the initial state of the model):%n");
        for (Formula property : this.ctlProps) {
            emit(LOW, "    %-" + maxWidth + "s : %s%n", property.getParseString(),
                 outcome.get(property)
                     ? "satisfied"
                     : "violated");
        }
        long endTime = System.currentTimeMillis();

        emit("%n** Model Checking Time (ms):\t%d%n", endTime - mcStartTime);
        emit("** Total Running Time (ms):\t%d%n", endTime - genStartTime);
    }

    /**
     * Generates a model by invoking the Generator with a given list of arguments.
     */
    private GTSFacade generateModel(String... genArgs) throws Exception {
        List<String> args = new ArrayList<>();
        args.add("-v");
        args.add("" + getVerbosity().getLevel());
        args.addAll(Arrays.asList(genArgs));
        try {
            return new GTSFacade(Generator.execute(args.toArray(new String[] {})));
        } catch (Exception e) {
            throw new Exception("Error in state space generation:\n" + e.getMessage(), e);
        }
    }

    @Option(name = "-ef", metaVar = "flags", usage = "" + "Special GTS labels. Legal values are:\n" //
        + "  s - start state label (default: 'start')\n" //
        + "  f - final states label (default: 'final')\n" //
        + "  o - open states label (default: 'open')\n" //
        + "  r - result state label (default: 'result')\n" //
        + "Specify the label to be used by appending flag with 'label' (single-quoted)\n"
        + "Example: -ef s'begin'f'end' specifies that the start state is labelled 'begin' and final states are labelled 'end'",
        handler = LTSLabelsHandler.class)
    private LTSLabels ltsLabels;

    @Option(name = "-ltl", metaVar = "prop",
        usage = "Check the LTL property <prop> (multiple allowed)",
        handler = LTLFormulaHandler.class)
    private List<gov.nasa.ltl.trans.Formula<String>> ltlProps;
    @Option(name = "-ctl", metaVar = "prop",
        usage = "Check the CTL property <prop> (multiple allowed)",
        handler = CLTFormulaHandler.class)
    private List<Formula> ctlProps;
    @Option(name = "-g", metaVar = "args",
        usage = "Invoke the generator using <args> as options + arguments",
        handler = GeneratorHandler.class)
    private GeneratorArgs genArgs;

    @Argument(metaVar = "model",
        usage = "File name of GXL graph (CTL only) or directory of production system to be checked",
        handler = FileOptionHandler.class)
    private File modelGraph;

    /**
     * Main method.
     * Always exits with {@link System#exit(int)}; see {@link #execute(String[])}
     * for programmatic use.
     * @param args the list of command-line arguments
     */
    public static void main(String args[]) {
        tryExecute(CTLModelChecker.class, args);
    }

    /**
     * Constructs and invokes a model checker instance.
     * @param args the list of command-line arguments
     */
    public static void execute(String args[]) throws Exception {
        new CTLModelChecker(args).start();
    }

    /** Option handler for CTL formulas. */
    public static class CLTFormulaHandler extends OneArgumentOptionHandler<Formula> {
        /**
         * Required constructor.
         */
        public CLTFormulaHandler(CmdLineParser parser, OptionDef option,
                                 Setter<? super Formula> setter) {
            super(parser, option, setter);
        }

        @Override
        protected Formula parse(String argument) throws CmdLineException {
            try {
                return Formula.parse(Logic.CTL, argument).toCtlFormula();
            } catch (FormatException e) {
                throw new CmdLineException(this.owner,
                    "Error while parsing '%s': %s".formatted(argument, e.getMessage()));
            }
        }
    }

    /** Option handler for LTL formulas. */
    public static class LTLFormulaHandler
        extends OneArgumentOptionHandler<gov.nasa.ltl.trans.Formula<Proposition>> {
        /**
         * Required constructor.
         */
        public LTLFormulaHandler(CmdLineParser parser, OptionDef option,
                                 Setter<? super gov.nasa.ltl.trans.Formula<Proposition>> setter) {
            super(parser, option, setter);
        }

        @Override
        protected gov.nasa.ltl.trans.Formula<Proposition> parse(String argument) throws CmdLineException {
            try {
                return Formula.parse(Logic.LTL, argument).toLtlFormula();
            } catch (FormatException e) {
                throw new CmdLineException(this.owner, e);
            }
        }
    }

    /** Option handler for the '-g' option. */
    public static class GeneratorHandler extends OptionHandler<GeneratorArgs> {
        /** Required constructor. */
        public GeneratorHandler(CmdLineParser parser, OptionDef option,
                                Setter<? super GeneratorArgs> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            ArrayList<String> genArgs = new ArrayList<>();
            for (int ix = 0; ix < params.size(); ix++) {
                genArgs.add(params.getParameter(ix));
            }
            this.setter.addValue(new GeneratorArgs(params));
            return params.size();
        }

        @Override
        public String getDefaultMetaVariable() {
            return "generator-args";
        }
    }

    /**
     * Option value class collecting all remaining arguments.
     * Wrapped into a class to fool Args4J into understanding this is not a multiple value.
     */
    public static class GeneratorArgs {
        GeneratorArgs(Parameters params) throws CmdLineException {
            this.args = new ArrayList<>();
            for (int ix = 0; ix < params.size(); ix++) {
                this.args.add(params.getParameter(ix));
            }
        }

        /** Returns the content of this argument, as a string array. */
        public String[] get() {
            return this.args.toArray(new String[0]);
        }

        private final List<String> args;
    }

    /** Creates a CTL-checkable model from an exploration result. */
    public static ModelFacade newModel(ExploreResult result) {
        return new GTSFacade(result);
    }

    /** Creates a CTL-checkable model from a graph plus special labels mapping.
     * @throws FormatException if the graph is not compatible with the special labels.
     */
    public static ModelFacade newModel(Graph graph, LTSLabels ltsLabels) throws FormatException {
        return new GraphFacade(graph, ltsLabels == null
            ? LTSLabels.DEFAULT
            : ltsLabels);
    }

    /** Facade for models, with the functionality required for CTL model checking. */
    public static interface ModelFacade {
        /** Returns the root node of the model. */
        public Node getRoot();

        /** Returns the number of (exposed) nodes of the model. */
        default public int nodeCount() {
            return nodeSet().size();
        }

        /** Returns the set of (exposed) nodes of the model. */
        public Set<? extends Node> nodeSet();

        /**
         * Returns the exposed outgoing edges of a node.
         */
        public Iterable<? extends Edge> outEdges(Node node);

        // EZ says: change for SF bug #442. See below.
        /**
         * Return the proper index of the given node to be used in the arrays.
         * Usually the index is the same as the node number, but this can change
         * when the GTS has absent states.
         */
        public int toIndex(Node node);

        /** Returns the node for a given index. */
        public Node toNode(int ix);

        /** Converts a model edge to a proposition that holds for its source. */
        public Proposition toProp(Edge edge);

        /**
         * Converts a model node to a set of propositions that hold for it,
         * without investigating its outgoing edges.
         */
        public List<Proposition> toProps(Node node);
    }

    /*
     * EZ says: this is a hack to fix SF bug #442.
     * The new level of indirection introduced by having to check the node
     * index with the model obviously hurts performance a bit. But... this
     * change touched just a few parts of the code and mainly at the
     * initialization. So I'd say that this is not so bad...
     */
    /** Model facade built from an exploration result. */
    private static class GTSFacade implements ModelFacade {
        /** Maps an exploration result into a model. */
        public GTSFacade(ExploreResult result) {
            this.gts = result.getGTS();
            this.result = result;
            this.nodeIdxMap = new HashMap<>();
            this.ixNodeArray = new GraphState[this.gts.getStates().size()];
            int nr = 0;
            for (GraphState state : this.gts.getStates()) {
                this.nodeIdxMap.put(state, nr);
                this.ixNodeArray[nr] = state;
                nr++;
            }
        }

        private final ExploreResult result;
        private final GTS gts;

        @Override
        public GraphState getRoot() {
            return this.gts.startState();
        }

        @Override
        public Set<? extends GraphState> nodeSet() {
            return this.gts.getStates();
        }

        @Override
        public Iterable<? extends Edge> outEdges(Node node) {
            return ((GraphState) node).getTransitions();
        }

        @Override
        public int toIndex(Node node) {
            if (this.nodeIdxMap == null) {
                return node.getNumber();
            } else {
                return this.nodeIdxMap.get(node);
            }
        }

        /** Mapping from nodes to their numbers,
         * used in preference to the natural node number in case of absent or transient states
         */
        private final Map<GraphState,Integer> nodeIdxMap;

        @Override
        public GraphState toNode(int ix) {
            return this.ixNodeArray[ix];
        }

        /** Graph states, in the order of their index. */
        private final GraphState[] ixNodeArray;

        /** Converts a model edge to a proposition that holds for its source. */
        @Override
        public Proposition toProp(Edge edge) {
            var trans = (GraphTransition) edge;
            var label = trans.label();
            var id = label.getAction().getQualName();
            var args = new LinkedList<Arg>();
            Arrays.stream(label.getArguments()).map(this::toArg).forEach(args::add);
            return Proposition.call(id, args);
        }

        /** Returns a proposition argument for a host node. */
        private Arg toArg(HostNode n) {
            return switch (n) {
            case DefaultHostNode d -> Arg.arg(d.toString());
            case ValueNode v -> Arg.arg(v.toTerm());
            };
        }

        @Override
        public List<Proposition> toProps(Node node) {
            var result = new LinkedList<Proposition>();
            GraphState state = (GraphState) node;
            if (state.isFinal()) {
                result.add(Proposition.derived(Flag.FINAL));
            }
            if (!state.isClosed()) {
                result.add(Proposition.derived(Flag.OPEN));
            }
            if (state == this.gts.startState()) {
                result.add(Proposition.derived(Flag.START));
            }
            if (this.result.contains(state)) {
                result.add(Proposition.derived(Flag.RESULT));
            }
            this.gts
                .getSatisfiedProps(state)
                .stream()
                .map(Proposition::derived)
                .forEach(result::add);
            return result;
        }
    }

    /** Model facade from a graph and a special labels mapping. */
    private static class GraphFacade implements ModelFacade {
        /** Wraps a graph and a special labels mapping into a model.
         * @throws FormatException if the graph is not compatible with the special labels.
         */
        public GraphFacade(Graph graph, LTSLabels ltsLabels) throws FormatException {
            this.graph = graph;
            this.ixNodeArray = new Node[graph.nodeCount()];
            for (var node : graph.nodeSet()) {
                this.ixNodeArray[node.getNumber()] = node;
            }
            this.ltsLabels = ltsLabels == null
                ? LTSLabels.DEFAULT
                : ltsLabels;
            this.root = testFormat();
        }

        private final LTSLabels ltsLabels;

        /** Tests if the model is consistent with the special state markers.
         * Returns the (unique) root node of the model.
         * @throws FormatException if the model has special state markers that occur
         * on edge labels
         */
        private Node testFormat() throws FormatException {
            Node result = null;
            for (Edge edge : this.graph.edgeSet()) {
                var label = edge.label().text();
                if (this.ltsLabels.getDerived().contains(label)) {
                    if (!edge.isLoop()) {
                        throw new FormatException(
                            "Special state marker '%s' occurs as edge label in model",
                            edge.label());
                    }
                    if (label.equals(this.ltsLabels.getStartLabel())) {
                        if (result != null) {
                            throw new FormatException(
                                "Start state marker '%s' occurs more than once in model",
                                edge.label());
                        } else {
                            result = edge.source();
                        }
                    }
                }
            }
            if (result == null) {
                throw new FormatException("Start state marker '%s' does not occur in model",
                    this.ltsLabels.getLabel(Flag.START));
            }
            return result;
        }

        @Override
        public Node getRoot() {
            return this.root;
        }

        private final Node root;

        @Override
        public Set<? extends Node> nodeSet() {
            return this.graph.nodeSet();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public Iterable outEdges(Node node) {
            return () -> this.graph
                .outEdgeSet(node)
                .stream()
                .filter(e -> !isDerived(e.label().text()))
                .iterator();
        }

        private final Graph graph;

        @Override
        public int toIndex(Node node) {
            return node.getNumber();
        }

        @Override
        public Node toNode(int ix) {
            return this.ixNodeArray[ix];
        }

        /** Graph states, in the order of their index. */
        private final Node[] ixNodeArray;

        @Override
        public Proposition toProp(Edge edge) {
            var label = edge.label().toString();
            // try the label as a parsable ID or CALL
            return FormulaParser.instance().parse(label).getProp();
        }

        /** Checks whether a given label is a flag according to {@link #ltsLabels}. */
        private boolean isDerived(String label) {
            return this.ltsLabels.getDerived().contains(label);
        }

        /**
         * Returns the flag corresponding to a given label, if any.
         */
        private @Nullable Flag getSpecialFlag(String label) {
            return this.ltsLabels.getFlag(label);
        }

        @Override
        public List<Proposition> toProps(Node node) {
            var result = new LinkedList<Proposition>();
            this.graph
                .outEdgeSet(node)
                .stream()
                .map(Edge::label)
                .map(Label::text)
                .map(Proposition::derived)
                .forEach(result::add);
            return result;
        }
    }
}