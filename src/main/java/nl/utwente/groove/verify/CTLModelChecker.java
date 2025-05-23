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

import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.explore.Generator.LTSLabelsHandler;
import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSLabels.Flag;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
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
            var grammar = gtsModel.gts.getGrammar();
            var errors = new FormatErrorSet();
            for (var formula : this.ctlProps) {
                try {
                    formula.check(grammar);
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
            outcome.put(property, marker.hasValue(true));
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
        /** Returns the number of (exposed) nodes of the model. */
        int nodeCount();

        /** Returns the set of (exposed) nodes of the model. */
        Set<? extends Node> nodeSet();

        /** Returns the set of (exposed) outgoing edges of a node. */
        Set<? extends Edge> outEdgeSet(Node node);

        /** Tests if a given node satisfies the special property expressed by a given flag. */
        boolean isSpecial(Node node, Flag flag);

        // EZ says: change for SF bug #442. See below.
        /**
         * Return the proper index of the given node to be used in the arrays.
         * Usually the index is the same as the node number, but this can change
         * when the GTS has absent states.
         */
        int nodeIndex(Node node);

        /** Returns the special-label-flag corresponding to a given edge label, if any. */
        Flag getFlag(String label);
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
            if (this.gts.hasAbsentStates() || this.gts.hasTransientStates()) {
                this.nodeIdxMap = new HashMap<>();
                int nr = 0;
                for (GraphState state : this.gts.getStates()) {
                    this.nodeIdxMap.put(state, nr);
                    nr++;
                }
            } else {
                this.nodeIdxMap = null;
            }
        }

        private final ExploreResult result;
        private final GTS gts;

        @Override
        public int nodeCount() {
            return this.nodeSet().size();
        }

        @Override
        public Set<? extends Node> nodeSet() {
            return this.gts.getStates();
        }

        @Override
        public Set<? extends Edge> outEdgeSet(Node node) {
            return ((GraphState) node).getTransitions();
        }

        @Override
        public boolean isSpecial(Node node, Flag flag) {
            GraphState state = (GraphState) node;
            boolean result = switch (flag) {
            case FINAL -> state.isFinal();
            case OPEN -> !state.isClosed();
            case START -> state == this.gts.startState();
            case RESULT -> this.result != null && this.result.contains(state);
            default -> false;
            };
            return result;
        }

        @Override
        public int nodeIndex(Node node) {
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
        public Flag getFlag(String label) {
            return null;
        }
    }

    /** Model facade from a graph and a special labels mapping. */
    private static class GraphFacade implements ModelFacade {
        /** Wraps a graph and a special labels mapping into a model.
         * @throws FormatException if the graph is not compatible with the special labels.
         */
        public GraphFacade(Graph graph, LTSLabels ltsLabels) throws FormatException {
            this.graph = graph;
            this.ltsLabels = ltsLabels == null
                ? LTSLabels.DEFAULT
                : ltsLabels;
            testFormat();
        }

        /** Tests if the model is consistent with the special state markers.
         * @throws FormatException if the model has special state markers that occur
         * on edge labels
         */
        private void testFormat() throws FormatException {
            boolean startFound = false;
            for (Node node : nodeSet()) {
                Set<? extends Edge> outEdges = outEdgeSet(node);
                for (Edge outEdge : outEdges) {
                    Flag flag = getFlag(outEdge.label().text());
                    if (flag == null) {
                        continue;
                    }
                    if (!outEdge.isLoop()) {
                        throw new FormatException(
                            "Special state marker '%s' occurs as edge label in model",
                            outEdge.label());
                    }
                    if (flag == Flag.START) {
                        if (startFound) {
                            throw new FormatException(
                                "Start state marker '%s' occurs more than once in model",
                                outEdge.label());
                        } else {
                            startFound = true;
                        }
                    }
                }
            }
            if (!startFound) {
                throw new FormatException("Start state marker '%s' does not occur in model",
                    this.ltsLabels.getLabel(Flag.START));
            }
        }

        @Override
        public int nodeCount() {
            return this.graph.nodeCount();
        }

        @Override
        public Set<? extends Node> nodeSet() {
            return this.graph.nodeSet();
        }

        @Override
        public Set<? extends Edge> outEdgeSet(Node node) {
            return this.graph.outEdgeSet(node);
        }

        @Override
        public boolean isSpecial(Node node, Flag flag) {
            return false;
        }

        private final Graph graph;

        @Override
        public int nodeIndex(Node node) {
            return node.getNumber();
        }

        @Override
        public Flag getFlag(String label) {
            Flag result = null;
            if (this.ltsLabels != null) {
                return this.ltsLabels.getFlag(label);
            }
            return result;
        }

        private final LTSLabels ltsLabels;
    }
}