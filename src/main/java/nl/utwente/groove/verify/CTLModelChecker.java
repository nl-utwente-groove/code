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
import static nl.utwente.groove.lts.StateProperty.isStateProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import picocli.CommandLine.IParameterConsumer;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.TypeConversionException;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.explore.Generator.LTSLabelsHandler;
import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSLabels.Flag;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.StateProperty;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.cli.GrooveCmdLineParser;
import nl.utwente.groove.util.cli.GrooveCmdLineTool;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

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
            public String getSingleLineUsage() {
                StringBuilder usage = new StringBuilder();
                var options = getOptions();
                int optionCount = options.size();
                for (int ix = 0; ix < optionCount - 1; ix++) {
                    appendSingleLineOption(usage, options.get(ix), true);
                }
                usage.append(" [");
                usage.append(getNameAndMeta(options.get(optionCount - 1)));
                usage.append(" | ");
                usage.append(getNameAndMeta(getArguments().get(0)));
                usage.append(']');
                return usage.toString();
            }
        };
        // move -g to the final position
        result.setLastOption("-g");
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

    @Option(names = "-ef", paramLabel = "flags",
        description = "" + "Special GTS labels. Legal values are:\n" //
            + "  s - start state label (default: 'start')\n" //
            + "  f - final states label (default: 'final')\n" //
            + "  o - open states label (default: 'open')\n" //
            + "  r - result state label (default: 'result')\n" //
            + "Specify the label to be used by appending flag with 'label' (single-quoted)\n"
            + "Example: -ef s'begin'f'end' specifies that the start state is labelled 'begin' and final states are labelled 'end'",
        converter = LTSLabelsHandler.class)
    private LTSLabels ltsLabels;

    @Option(names = "-ltl", paramLabel = "prop",
        description = "Check the LTL property <prop> (multiple allowed)",
        converter = LTLFormulaHandler.class)
    private List<gov.nasa.ltl.trans.Formula<String>> ltlProps;
    @Option(names = "-ctl", paramLabel = "prop",
        description = "Check the CTL property <prop> (multiple allowed)",
        converter = CLTFormulaHandler.class)
    private List<Formula> ctlProps;
    @Option(names = "-g", paramLabel = "args",
        description = "Invoke the generator using <args> as options + arguments",
        parameterConsumer = GeneratorHandler.class)
    private GeneratorArgs genArgs;

    @Parameters(index = "0", arity = "0..1", paramLabel = "model",
        description = "File name of GXL graph (CTL only) or directory of production system to be checked")
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
    public static class CLTFormulaHandler implements ITypeConverter<Formula> {
        @Override
        public Formula convert(String value) throws TypeConversionException {
            try {
                return Formula.parse(Logic.CTL, value).toCtlFormula();
            } catch (FormatException e) {
                throw new TypeConversionException("Error while parsing '%s': %s"
                    .formatted(value, e.getMessage()));
            }
        }
    }

    /** Option handler for LTL formulas. */
    public static class LTLFormulaHandler
        implements ITypeConverter<gov.nasa.ltl.trans.Formula<Proposition>> {
        @Override
        public gov.nasa.ltl.trans.Formula<Proposition> convert(String value) throws FormatException {
            return Formula.parse(Logic.LTL, value).toLtlFormula();
        }
    }

    /** Option handler for the '-g' option, consuming all remaining arguments. */
    public static class GeneratorHandler implements IParameterConsumer {
        @Override
        public void consumeParameters(Stack<String> args, ArgSpec argSpec,
                                      CommandSpec commandSpec) {
            List<String> genArgs = new ArrayList<>();
            while (!args.isEmpty()) {
                genArgs.add(args.pop());
            }
            argSpec.setValue(new GeneratorArgs(genArgs));
        }
    }

    /**
     * Option value class collecting all remaining arguments.
     * Wrapped into a class so that the option is not treated as multi-valued.
     */
    public static class GeneratorArgs {
        GeneratorArgs(List<String> args) {
            this.args = new ArrayList<>(args);
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
            return Proposition.prop(((GraphTransition) edge).label());
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
                .map(StateProperty::getName)
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
                .filter(e -> !isStateProperty(e.label()))
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
            // parse the label as an ID or CALL if possible, else wrap it in a literal
            return Proposition.parse(edge.label().text());
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
                .filter(StateProperty::isStateProperty)
                .map(Proposition::derived)
                .forEach(result::add);
            return result;
        }
    }
}