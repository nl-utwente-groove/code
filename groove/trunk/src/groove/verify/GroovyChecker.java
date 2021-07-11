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
 * $Id: PrologChecker.java 5787 2016-08-04 10:36:41Z rensink $
 */
package groove.verify;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import groove.explore.ExploreResult;
import groove.explore.Generator;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GroovyModel;
import groove.grammar.model.ResourceKind;
import groove.lts.GTS;
import groove.util.cli.GrooveCmdLineParser;
import groove.util.cli.GrooveCmdLineTool;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * Command-line tool for running Prolog queries after state space exploration.
 *
 * @author Eduardo Zambon
 */
public class GroovyChecker extends GrooveCmdLineTool<Object> {
    /**
     * Constructor.
     * @param args the command-line arguments for tool.
     */
    public GroovyChecker(String... args) {
        super("GroovyGen", args);
    }

    @Override
    protected GrooveCmdLineParser createParser(String appName) {
        GrooveCmdLineParser result = new GrooveCmdLineParser(appName, this);
        // move -g to the final position
        @SuppressWarnings("rawtypes") List<OptionHandler> handlers = result.getOptions();
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
        groovyCheck(this.genArgs.get());
        return null;
    }

    private void runScript(String scriptName, ExploreResult result) {
        GTS gts = result.getGTS();
        Grammar grammar = gts.getGrammar();
        GrammarModel grammarModel = result.getTransformer()
            .getGrammarModel();

        GroovyModel model = null;
        for (QualName qualName : grammarModel.getNames(ResourceKind.GROOVY)) {
            if (qualName.toString()
                .equals(scriptName)) {
                model = (GroovyModel) grammarModel.getResource(ResourceKind.GROOVY, qualName);
                break;
            }
        }
        if (model == null) {
            System.err.printf("Groovy script '%s' does not exist!\n", scriptName);
            return;
        }

        Binding binding = new Binding();
        binding.setVariable("gts", gts);
        binding.setVariable("grammar", grammar);
        binding.setVariable("grammarModel", grammarModel);
        binding.setVariable("out", System.out);

        GroovyShell shell = new GroovyShell(binding);
        try {
            shell.evaluate(model.getProgram());
        } catch (CompilationFailedException e) {
            System.err.println("Failed to compile Groovy script!");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during execution of Groovy script!");
            System.err.println(e.getMessage());
        }
    }

    private void groovyCheck(String[] genArgs) throws Exception {
        long genStartTime = System.currentTimeMillis();
        ExploreResult result;
        try {
            result = Generator.execute(genArgs);
        } catch (Exception e) {
            throw new Exception("Error while invoking Generator\n" + e.getMessage(), e);
        }

        emit("%nGroovy outcome:%n");
        for (String script : this.scripts) {
            emit("%nRunning script: %s%n%n", script);
            runScript(script, result);
            emit("%n");
        }

        long endTime = System.currentTimeMillis();

        emit("** Total Running Time (ms):\t%d%n", endTime - genStartTime);
    }

    @Option(name = "-p", metaVar = "script", usage = "Runs the given script (multiple allowed)",
        handler = GroovyHandler.class, required = true)
    private List<String> scripts;
    @Option(name = "-g", metaVar = "args",
        usage = "Invoke the generator using <args> as options + arguments",
        handler = GeneratorHandler.class, required = true)
    private GeneratorArgs genArgs;

    /**
     * Main method.
     * Always exits with {@link System#exit(int)}; see {@link #execute(String[])}
     * for programmatic use.
     * @param args the list of command-line arguments
     */
    public static void main(String args[]) {
        tryExecute(GroovyChecker.class, args);
    }

    /**
     * Constructs and invokes a Prolog checker instance.
     * @param args the list of command-line arguments
     */
    public static void execute(String args[]) throws Exception {
        new GroovyChecker(args).start();
    }

    /** Option handler for Groovy scripts. */
    public static class GroovyHandler extends OneArgumentOptionHandler<String> {
        /**
         * Required constructor.
         */
        public GroovyHandler(CmdLineParser parser, OptionDef option,
            Setter<? super String> setter) {
            super(parser, option, setter);
        }

        @Override
        protected String parse(String argument) {
            return argument;
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
    private static class GeneratorArgs {
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
}