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
package nl.utwente.groove.explore;

import static nl.utwente.groove.util.cli.Verbosity.LOW;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.LTSLabels;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.cli.GrooveCmdLineParser;
import nl.utwente.groove.util.cli.GrooveCmdLineTool;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.verify.CTLMarker;
import nl.utwente.groove.verify.Formula;
import nl.utwente.groove.verify.Logic;
import nl.utwente.groove.verify.CTLModelFacade;
import picocli.CommandLine.IParameterConsumer;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.TypeConversionException;

/**
 * Command-line tool directing the model checking process.
 *
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-03-28 07:03:03 $
 */
public class CTLModelChecker extends GrooveCmdLineTool<Map<Formula,Boolean>> {
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
     * @return the model checking outcome per property, for the initial
     * state of the model
     */
    @Override
    protected Map<Formula,Boolean> run() throws Exception {
        var genArgs = this.genArgs;
        return ctlCheck(genArgs == null
            ? null
            : genArgs.get());
    }

    private Map<Formula,Boolean> ctlCheck(String @Nullable [] genArgs) throws Exception {
        long genStartTime = System.currentTimeMillis();
        CTLModelFacade model;
        var modelGraph = this.modelGraph;
        if (genArgs != null) {
            emit("Generator:\t%s%n", Strings.toString(genArgs, " ", ""));
            model = generateModel(genArgs);
        } else if (modelGraph == null) {
            throw new Exception(
                "Either generator argument -g or model file name should be provided");
        } else if (modelGraph.isDirectory()) {
            emit("Rule system:\t%s%n", modelGraph);
            // we have to generate the transition system, exhaustively: the
            // exploration saved with the grammar may cover only part of the
            // state space, which would corrupt the outcome (gh #863)
            model = generateModel(Generator.EXPLORE_NAME, getFullExploration(modelGraph),
                                  modelGraph.getPath());
        } else {
            emit("Model:\t%s%n", modelGraph);
            model = CTLModelFacade.newModel(Groove.loadGraph(modelGraph), this.ltsLabels);
            emit("Model loaded:\t%s states%n", model.nodeSet().size());
        }
        var ctlProps = this.ctlProps;
        if (ctlProps == null) {
            ctlProps = List.of();
        }
        // check if the formulas match the model
        GTS gts = model.getGTS();
        if (gts != null) {
            var errors = new FormatErrorSet();
            for (var formula : ctlProps) {
                try {
                    formula.check(gts);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
            errors.throwException();
        }
        long mcStartTime = System.currentTimeMillis();
        int maxWidth = 0;
        Map<Formula,Boolean> outcome = new LinkedHashMap<>();
        for (Formula property : ctlProps) {
            emit("Formula %s: ", property.toString());
            maxWidth = Math.max(maxWidth, property.getParseString().length());
            CTLMarker marker = new CTLMarker(property, model);
            emit("[initialised] ");
            outcome.put(property, marker.hasValue());
            emit("[checked]\n");
        }
        emit("%n");
        emit(LOW, "Model checking outcome (for the initial state of the model):%n");
        for (var entry : outcome.entrySet()) {
            emit(LOW, "    %-" + maxWidth + "s : %s%n", entry.getKey().getParseString(),
                 entry.getValue()
                     ? "satisfied"
                     : "violated");
        }
        long endTime = System.currentTimeMillis();

        emit("%n** Model Checking Time (ms):\t%d%n", endTime - mcStartTime);
        emit("** Total Running Time (ms):\t%d%n", endTime - genStartTime);
        return outcome;
    }

    /**
     * Computes the textual form of the exhaustive exploration of the grammar
     * in a given directory (see {@link ConfiguredExploreType#fullExploration}),
     * suitable as argument of the Generator's exploration option.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private String getFullExploration(File grammarDir) throws IOException {
        var grammar = Groove.loadGrammar(grammarDir.getPath());
        return ConfiguredExploreType.fullExploration(grammar).getConfig().unparse();
    }

    /**
     * Generates a model by invoking the Generator with a given list of arguments.
     */
    private CTLModelFacade generateModel(String... genArgs) throws Exception {
        List<String> args = new ArrayList<>();
        args.add("-v");
        args.add("" + getVerbosity().getLevel());
        args.addAll(Arrays.asList(genArgs));
        try {
            return CTLModelFacade.newModel(Generator.execute(args.toArray(new String[] {})));
        } catch (Exception e) {
            throw new Exception("Error in state space generation:\n" + e.getMessage(), e);
        }
    }

    @Option(names = "-ef", paramLabel = "flags", description = ""
        + "Special GTS labels. Legal values are:\n" //
        + "  s - start state label (default: 'start')\n" //
        + "  f - final states label (default: 'final')\n" //
        + "  o - open states label (default: 'open')\n" //
        + "  r - result state label (default: 'result')\n" //
        + "Specify the label to be used by appending flag with 'label' (single-quoted)\n"
        + "Example: -ef s'begin'f'end' specifies that the start state is labelled 'begin' and final states are labelled 'end'",
        converter = LTSLabels.Handler.class)
    private @Nullable LTSLabels ltsLabels;

    @Option(names = "-ctl", paramLabel = "prop",
        description = "Check the CTL property <prop> (multiple allowed)",
        converter = CLTFormulaHandler.class)
    private @Nullable List<Formula> ctlProps;
    @Option(names = "-g", paramLabel = "args",
        description = "Invoke the generator using <args> as options + arguments;\n"
            + "the exploration is then determined by <args> (and the grammar) rather than being exhaustive",
        parameterConsumer = GeneratorHandler.class)
    private @Nullable GeneratorArgs genArgs;

    @Parameters(index = "0", arity = "0..1", paramLabel = "model",
        description = "File name of GXL graph (CTL only) or directory of production system to be checked;\n"
            + "the state space of a production system is explored exhaustively, regardless of the exploration saved with it")
    private @Nullable File modelGraph;

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
                throw new TypeConversionException(
                    "Error while parsing '%s': %s".formatted(value, e.getMessage()));
            }
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
}
