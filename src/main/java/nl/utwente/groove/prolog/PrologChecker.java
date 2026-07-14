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
package nl.utwente.groove.prolog;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import picocli.CommandLine.IParameterConsumer;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;

import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.cli.GrooveCmdLineParser;
import nl.utwente.groove.util.cli.GrooveCmdLineTool;

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
        result.setLastOption("-g");
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
            gts = Generator.execute(genArgs)
                .getGTS();
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

    @Option(names = "-p", paramLabel = "query",
        description = "Performs the given query (multiple allowed)", required = true)
    private List<String> queries;
    @Option(names = "-g", paramLabel = "args",
        description = "Invoke the generator using <args> as options + arguments",
        parameterConsumer = GeneratorHandler.class, required = true)
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