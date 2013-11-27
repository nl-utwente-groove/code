/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.abstraction.pattern.explore;

import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.pattern.PatternAbstraction;
import groove.abstraction.pattern.explore.strategy.PatternDFSStrategy;
import groove.abstraction.pattern.explore.strategy.PatternStrategy;
import groove.abstraction.pattern.explore.util.TransSystemChecker;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PGTSListener;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.lts.PatternTransition;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.shape.TypeGraphFactory;
import groove.abstraction.pattern.trans.PatternGraphGrammar;
import groove.explore.Args4JGenerator;
import groove.explore.Verbosity;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.Strategy;
import groove.grammar.Grammar;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.io.FileType;
import groove.lts.GTS;
import groove.util.cli.GrammarHandler;
import groove.util.cli.GrooveCmdLineParser;
import groove.util.cli.HelpHandler;
import groove.util.cli.VerbosityHandler;

import java.io.File;
import java.io.PrintStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 * Counterpart of {@link Args4JGenerator} for pattern graph state space exploration.
 * See also {@link ShapeGenerator}.
 * 
 * @author Eduardo Zambon
 */
public class Args4JPatternGraphGenerator {
    /**
     * Constructs the generator with a given name and for a given set of arguments.
     */
    protected Args4JPatternGraphGenerator(String name, String... args)
        throws CmdLineException {
        this.parser = new GrooveCmdLineParser(name, this);
        this.parser.parseArgument(args);
    }

    /**
     * Constructs the generator for a given set of arguments.
     */
    public Args4JPatternGraphGenerator(String... args) throws CmdLineException {
        this("PatternGraphGenerator", args);
    }

    /**
     * Explores the state space.
     */
    public PGTS run() throws Exception {
        if (isHelp()) {
            this.parser.printHelp();
            return null;
        } else {
            return generate(getGrammar());
        }
    }

    private PGTS generate(PatternGraphGrammar grammar) {
        PatternAbstraction.initialise();
        PatternStrategy strategy = new PatternDFSStrategy();
        PGTS pgts = computeGTS(grammar);
        announce(pgts);
        strategy.prepare(pgts);
        // start working until done or nothing to do
        while (strategy.next()) {
            // Empty
        }
        report(pgts);
        return pgts;
    }

    /** Factory method for creating a GTS object. */
    protected PGTS computeGTS(PatternGraphGrammar grammar) {
        return new PGTS(grammar);
    }

    /** Writes an exploration prelude to stdout. */
    protected void announce(PGTS pgts) {
        if (!getVerbosity().isLow()) {
            PrintStream out = System.out;
            out.println("Grammar:\t" + getGrammarLocation());
            out.println("Start graph:\t"
                + (this.startGraphName == null ? "default"
                        : this.startGraphName));
            out.println("Type graph:\t" + this.typeGraphName);
            pgts.addLTSListener(new GenerateProgressMonitor());
        }
    }

    /** Writes output accordingly to options given to the generator. */
    protected void report(PGTS pgts) {
        GTS SGTS = exploreSimpleGrammar(pgts.getGrammar());
        TransSystemChecker checker = new TransSystemChecker(pgts, SGTS);
        System.out.println();
        checker.report();
    }

    /** Writes output accordingly to options given to the generator. */
    public boolean compareGTSs(PGTS pgts) {
        GTS SGTS = exploreSimpleGrammar(pgts.getGrammar());
        TransSystemChecker checker = new TransSystemChecker(pgts, SGTS);
        return checker.compare();
    }

    /**
     * Returns the grammar used for generating the state space. The grammar is
     * lazily loaded in. The method throws an error and returns
     * <code>null</code> if the grammar could not be loaded.
     */
    protected PatternGraphGrammar getGrammar() throws Exception {
        if (this.grammar == null) {
            this.grammar = loadGrammar();
        }
        return this.grammar;
    }

    /** Loads a grammar from a given grammar location and a start graph. */
    private PatternGraphGrammar loadGrammar() throws Exception {
        GrammarModel model = GrammarModel.newInstance(getGrammarLocation());
        model.setLocalActiveNames(ResourceKind.HOST, getStartGraphName());
        Grammar sGrammar = model.toGrammar();
        sGrammar.setFixed();
        String typeGraphName =
            FileType.STATE_FILTER.addExtension(getTypeGraphName());
        File typeGraphFile = new File(getGrammarLocation(), typeGraphName);
        TypeGraph type = TypeGraphFactory.unmarshalTypeGraph(typeGraphFile);
        return new PatternGraphGrammar(sGrammar, type);
    }

    /** Explores the grammar using the normal simple graph method. */
    private GTS exploreSimpleGrammar(PatternGraphGrammar grammar) {
        Grammar sGrammar = grammar.getSimpleGrammar();
        GTS result = new GTS(sGrammar);
        Strategy strategy = new DFSStrategy();
        strategy.setGTS(result);
        strategy.play();
        return result;
    }

    /** Returns the grammar location. */
    private File getGrammarLocation() {
        return this.grammarLocation;
    }

    /** Returns the start graph name, relative to the grammar location. */
    private String getStartGraphName() {
        return this.startGraphName;
    }

    /** Returns the type graph name, relative to the grammar location. */
    private String getTypeGraphName() {
        return this.typeGraphName;
    }

    /** Indicates if the help option has been invoked. */
    private boolean isHelp() {
        return this.help;
    }

    /** Returns the verbosity level. */
    protected Verbosity getVerbosity() {
        return this.verbosity;
    }

    /** The command-line parser. */
    private final GrooveCmdLineParser parser;
    /** The graph grammar used for the generation. */
    private PatternGraphGrammar grammar;

    @Argument(metaVar = "grammar", required = true,
            usage = "grammar location (default extension .gps)",
            handler = GrammarHandler.class)
    private File grammarLocation;

    @Argument(index = 1, metaVar = "start", required = true,
            usage = "Start graph name (defined in grammar, no extension)")
    private String startGraphName;

    @Argument(index = 2, metaVar = "type", required = true,
            usage = "Type graph name (defined in grammar, no extension)")
    private String typeGraphName;

    @Option(name = HelpHandler.NAME, usage = HelpHandler.USAGE,
            handler = HelpHandler.class)
    private boolean help;

    @Option(name = VerbosityHandler.NAME, metaVar = VerbosityHandler.VAR,
            usage = VerbosityHandler.USAGE, handler = VerbosityHandler.class)
    private Verbosity verbosity = Verbosity.MEDIUM;

    // ------------------------------------------------------------------------
    // Main method
    // ------------------------------------------------------------------------

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * @param args generator options, grammar and start graph name
     */
    public static void main(String[] args) {
        try {
            generate(args);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                e.printStackTrace();
            } else {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Loads a graph grammar, and returns the generated transition system.
     * @param args generator options, grammar and start graph name
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public PGTS generate(String[] args) throws Exception {
        staticPGTS = new Args4JPatternGraphGenerator(args).run();
        return staticPGTS;
    }

    /** Returns the most recently generated GTS. */
    static public PGTS getPGTS() {
        return staticPGTS;
    }

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static PGTS staticPGTS;

    // ------------------------------------------------------------------------
    // Inner class
    // ------------------------------------------------------------------------

    /**
     * Class that implements a visualisation of the progress of a GTS generation
     * process. 
     * See {@link GenerateProgressMonitor}
     */
    private class GenerateProgressMonitor extends
            groove.explore.util.GenerateProgressMonitor implements PGTSListener {
        @Override
        public void addUpdate(PGTS gts, PatternState state) {
            addState(gts.nodeCount(), gts.edgeCount(), gts.openStateCount());
        }

        @Override
        public void addUpdate(PGTS gts, PatternTransition transition) {
            addTransition(gts.nodeCount(), gts.edgeCount(),
                gts.openStateCount());
        }
    }
}
