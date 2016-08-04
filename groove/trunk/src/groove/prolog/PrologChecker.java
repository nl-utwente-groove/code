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
package groove.prolog;

import groove.explore.Generator;
import groove.grammar.Grammar;
import groove.lts.GTS;
import groove.util.cli.GrooveCmdLineParser;
import groove.util.cli.GrooveCmdLineTool;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Command-line tool for running Prolog queries after state space exploration.
 *
 * @author Eduardo Zambon
 */
public class PrologChecker extends GrooveCmdLineTool<Object> {
    /**
     * Constructor.
     * @param args the command-line arguments for tool.
     */
    public PrologChecker(String... args) {
        super("PrologGen", args);
    }

    @Override
    protected GrooveCmdLineParser createParser(String appName) {
        GrooveCmdLineParser result = new GrooveCmdLineParser(appName, this);
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
        prologCheck(this.genArgs.get());
        return null;
    }

    private void prologCheck(String[] genArgs) throws Exception {
        long genStartTime = System.currentTimeMillis();
        GTS gts;
        try {
            gts = Generator.execute(genArgs).getGTS();
        } catch (Exception e) {
            throw new Exception("Error while invoking Generator\n" + e.getMessage(), e);
        }

        long prologStartTime = System.currentTimeMillis();

        Grammar grammar = gts.getGrammar();
        GrooveEnvironment prologEnv = grammar.getPrologEnvironment();
        PrologEngine prologEngine = new PrologEngine(prologEnv);
        prologEngine.setGrooveState(new GrooveState(grammar, gts, null, null));

        emit("%nProlog outcome:%n");
        for (String query : this.queries) {
            emit("%nRunning query: ?- %s%n", query);
            prologEngine.newQuery(query);
        }

        long endTime = System.currentTimeMillis();

        emit("%n** Prolog Querying Time (ms):\t%d%n", endTime - prologStartTime);
        emit("** Total Running Time (ms):\t%d%n", endTime - genStartTime);
    }

    @Option(name = "-p", metaVar = "query", usage = "Performs the given query (multiple allowed)",
        handler = QueryHandler.class, required = true)
    private List<String> queries;
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
        tryExecute(PrologChecker.class, args);
    }

    /**
     * Constructs and invokes a Prolog checker instance.
     * @param args the list of command-line arguments
     */
    public static void execute(String args[]) throws Exception {
        new PrologChecker(args).start();
    }

    /** Option handler for Prolog queries. */
    public static class QueryHandler extends OneArgumentOptionHandler<String> {
        /**
         * Required constructor.
         */
        public QueryHandler(CmdLineParser parser, OptionDef option, Setter<? super String> setter) {
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