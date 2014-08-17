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
package groove.verify;

import groove.explore.Generator;
import groove.explore.Generator.LTSLabelsHandler;
import groove.explore.util.LTSLabels;
import groove.graph.Graph;
import groove.util.Groove;
import groove.util.cli.GrooveCmdLineParser;
import groove.util.cli.GrooveCmdLineTool;
import groove.util.parse.FormatException;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
        @SuppressWarnings("rawtypes")
        List<OptionHandler> handlers = result.getOptions();
        OptionHandler<?> genHandler = null;
        for (OptionHandler<?> handler : handlers) {
            if (handler instanceof GeneratorHandler) {
                genHandler = handler;
            }
        }
        handlers.remove(genHandler);
        handlers.add(genHandler);
        return result;
    }

    /**
     * Method managing the actual work to be done.
     */
    @Override
    protected Object run() throws Exception {
        modelCheck(this.genArgs == null ? null : this.genArgs.get());
        return null;
    }

    private void modelCheck(String[] genArgs) throws Exception {
        long genStartTime = System.currentTimeMillis();
        Graph model;
        if (genArgs != null) {
            try {
                model = Generator.execute(genArgs);
            } catch (Exception e) {
                throw new Exception("Error while invoking Generator\n" + e.getMessage(), e);
            }
        } else {
            emit("Model: %s%n", this.modelGraph);
            model = Groove.loadGraph(this.modelGraph);
        }
        long mcStartTime = System.currentTimeMillis();
        int maxWidth = 0;
        Map<Formula,Boolean> outcome = new HashMap<Formula,Boolean>();
        for (Formula property : this.properties) {
            maxWidth = Math.max(maxWidth, property.toString().length());
            CTLMarker marker = new CTLMarker(property, model, this.ltsLabels);
            outcome.put(property, marker.hasValue(true));
        }
        emit("%nModel checking outcome:%n");
        for (Formula property : this.properties) {
            emit("    %-" + maxWidth + "s : %s%n", property, outcome.get(property) ? "satisfied"
                : "violated");
        }
        long endTime = System.currentTimeMillis();

        emit("%n** Model Checking Time (ms):\t%d%n", endTime - mcStartTime);
        emit("** Total Running Time (ms):\t%d%n", endTime - genStartTime);
    }

    @Option(name = "-ef", metaVar = "flags", usage = "" + "Special GTS labels. Legal values are:\n" //
        + "  s - start state label (default: 'start')\n" //
        + "  f - final states label (default: 'final')\n" //
        + "  o - open states label (default: 'open')\n" //
        + "  r - result state label (default: 'result')" //
        + "Specify label to be used by appending flag with 'label' (single-quoted)",
        handler = LTSLabelsHandler.class)
    private LTSLabels ltsLabels;

    @Option(name = "-ctl", metaVar = "form", usage = "Check the formula <form> (multiple allowed)",
        handler = FormulaHandler.class, required = true)
    private List<Formula> properties;
    @Option(name = "-g", metaVar = "args",
        usage = "Invoke the generator using <args> as options + arguments",
        handler = GeneratorHandler.class)
    private GeneratorArgs genArgs;

    @Argument(metaVar = "graph", usage = "File name of graph to be checked",
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
    public static class FormulaHandler extends OneArgumentOptionHandler<Formula> {
        /**
         * Required constructor.
         */
        public FormulaHandler(CmdLineParser parser, OptionDef option, Setter<? super Formula> setter) {
            super(parser, option, setter);
        }

        @Override
        protected Formula parse(String argument) throws CmdLineException {
            try {
                return Formula.parse(Logic.CTL, argument);
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
            ArrayList<String> genArgs = new ArrayList<String>();
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
    private static class GeneratorArgs {
        GeneratorArgs(Parameters params) throws CmdLineException {
            this.args = new ArrayList<String>();
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
}