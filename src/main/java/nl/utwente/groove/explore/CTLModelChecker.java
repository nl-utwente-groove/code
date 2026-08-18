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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import nl.utwente.groove.io.Groove;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.LTSLabels;
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
        CTLModelFacade model;
        if (genArgs != null) {
            emit("Generator:\t%s%n", Strings.toString(genArgs, " ", ""));
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
            model = CTLModelFacade.newModel(Groove.loadGraph(this.modelGraph), this.ltsLabels);
            emit("Model loaded:\t%s states%n", model.nodeSet().size());
        }
        // check if the formulas match the model
        GTS gts = model.getGTS();
        if (gts != null) {
            var errors = new FormatErrorSet();
            for (var formula : this.ctlProps) {
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
    private LTSLabels ltsLabels;

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
