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

import groove.abstraction.pattern.PatternAbstraction;
import groove.abstraction.pattern.explore.strategy.PatternDFSStrategy;
import groove.abstraction.pattern.explore.strategy.PatternStrategy;
import groove.abstraction.pattern.explore.util.TransSystemChecker;
import groove.abstraction.pattern.io.xml.TypeGraphJaxbGxlIO;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PGTSAdapter;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.lts.PatternTransition;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.PatternGraphGrammar;
import groove.explore.Generator;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.trans.GraphGrammar;
import groove.util.CommandLineTool;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Counterpart of {@link Generator} for pattern graph state space exploration.
 * 
 * @author Eduardo Zambon
 */
public class PatternGraphGenerator extends CommandLineTool {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Usage message for the generator. */
    private static final String USAGE_MESSAGE =
        "Usage: PatternGraphGenerator [options] <grammar> <start-graph-name> <type-graph-name>";

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    protected static PGTS pgts;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** String describing the location where the grammar is to be found. */
    private String grammarLocation;
    /** String describing the start graph within the grammar. */
    private String startGraphName;
    /** String describing the type graph within the grammar. */
    private String typeGraphName;
    /** The graph grammar used for the generation. */
    private PatternGraphGrammar grammar;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public PatternGraphGenerator(String... args) {
        super(args);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    /**
     * This implementation returns <tt>{@link #USAGE_MESSAGE}</tt>.
     */
    @Override
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    /**
     * Goes through the list of command line arguments and tries to find command
     * line options. The options and their parameters are subsequently removed
     * from the argument list. If an option cannot be parsed, the method prints
     * an error message and terminates the program.
     */
    @Override
    public void processArguments() {
        super.processArguments();

        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
            setGrammarLocation(argsList.remove(0));
        }
        if (argsList.size() > 0) {
            setStartGraph(argsList.remove(0));
        }
        if (argsList.size() > 0) {
            setTypeGraph(argsList.remove(0));
        }
        if (this.grammarLocation == null) {
            printError("No grammar location specified", true);
        }
    }

    /**
     * Callback method to check whether the log command line option is
     * supported. This implementation returns <tt>false</tt> always.
     */
    // EZ says: if you want logs, use pipes... ;-)
    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Sets the grammar to be used for state space generation.
     * @param grammarLocation the file name of the grammar (with or without file
     *        name extension)
     */
    private void setGrammarLocation(String grammarLocation) {
        this.grammarLocation = grammarLocation;
    }

    /**
     * Sets the start graph to be used for state space generation.
     * @param startGraphName the name of the start graph (without file name
     *        extension)
     */
    private void setStartGraph(String startGraphName) {
        this.startGraphName = startGraphName;
    }

    /**
     * Sets the type graph to be used for state space generation.
     * @param typeGraphName the name of the type graph (with file name
     *        extension)
     */
    private void setTypeGraph(String typeGraphName) {
        this.typeGraphName = typeGraphName;
    }

    /** Resets the generator. */
    private void reset() {
        PatternAbstraction.initialise();
        pgts = null;
    }

    /**
     * Returns the GTS that is being generated. The GTS is lazily obtained from
     * the grammar if it had not yet been initialised.
     * @see #getGrammar()
     */
    public PGTS getPGTS() {
        if (pgts == null) {
            pgts = new PGTS(getGrammar());
        }
        return pgts;
    }

    /**
     * Starts the state space generation process. Before invoking this method,
     * all relevant parameters should be set.
     */
    public void start() {
        processArguments();
        explore();
        report();
    }

    /**
     * Explores the state space.
     */
    public void explore() {
        reset();

        if (getVerbosity() > LOW_VERBOSITY) {
            println("\n======================================================\n");
            println("Grammar:\t" + this.grammarLocation);
            println("Start graph:\t"
                + (this.startGraphName == null ? "default"
                        : this.startGraphName));
            println("Type graph:\t" + this.typeGraphName);
            print("\nProgress:\n\n");
            getPGTS().addLTSListener(new GenerateProgressMonitor());
        }

        PatternStrategy strategy = new PatternDFSStrategy();
        strategy.prepare(getPGTS());
        // start working until done or nothing to do
        while (strategy.next()) {
            // Empty
        }
    }

    /** Writes output accordingly to options given to the generator. */
    public void report() {
        GTS SGTS = exploreSimpleGrammar();
        TransSystemChecker checker = new TransSystemChecker(getPGTS(), SGTS);
        checker.report();
    }

    /** Writes output accordingly to options given to the generator. */
    public boolean compareGTSs() {
        GTS SGTS = exploreSimpleGrammar();
        TransSystemChecker checker = new TransSystemChecker(getPGTS(), SGTS);
        return checker.compare();
    }

    /**
     * Returns the grammar used for generating the state space. The grammar is
     * lazily loaded in. The method throws an error and returns
     * <code>null</code> if the grammar could not be loaded.
     */
    protected PatternGraphGrammar getGrammar() {
        if (this.grammar == null) {
            loadGrammar(this.grammarLocation, this.startGraphName,
                this.typeGraphName);
        }
        return this.grammar;
    }

    /** Loads a grammar from a given grammar location and a start graph. */
    private void loadGrammar(String grammarFile, String startGraph,
            String typeGraph) {
        try {
            GrammarModel model = Groove.loadGrammar(grammarFile);
            model.localSetStartGraph(startGraph);
            GraphGrammar sGrammar = model.toGrammar();
            sGrammar.setFixed();
            File typeGraphFile = new File(grammarFile + ".gps/" + typeGraph);
            TypeGraph type =
                TypeGraphJaxbGxlIO.getInstance().unmarshalTypeGraph(
                    typeGraphFile);
            this.grammar = new PatternGraphGrammar(sGrammar, type);
        } catch (FormatException exc) {
            printError("Grammar format error: " + exc.getMessage(), false);
        } catch (IOException exc) {
            printError("I/O error while loading grammar: " + exc.getMessage(),
                false);
        }
    }

    private GTS exploreSimpleGrammar() {
        GraphGrammar sGrammar = getGrammar().getSimpleGrammar();
        GTS result = new GTS(sGrammar);
        Strategy strategy = new DFSStrategy();
        strategy.prepare(result);
        // start working until done or nothing to do
        while (strategy.next()) {
            // Empty
        }
        return result;
    }

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
        new PatternGraphGenerator(args).start();
    }

    // ------------------------------------------------------------------------
    // Inner class
    // ------------------------------------------------------------------------

    /**
     * Class that implements a visualisation of the progress of a GTS generation
     * process. 
     */
    private class GenerateProgressMonitor extends PGTSAdapter {
        /**
         * Creates a monitor that reports on states and transitions generated.
         */
        public GenerateProgressMonitor() {
            // empty
        }

        @Override
        public void addUpdate(PGTS gts, PatternState state) {
            if (gts.nodeCount() % UNIT == 0) {
                System.out.print("s");
                this.printed++;
            }
            endLine(gts);
        }

        @Override
        public void addUpdate(PGTS gts, PatternTransition transition) {
            if (gts.edgeCount() % UNIT == 0) {
                System.out.print("t");
                this.printed++;
            }
            endLine(gts);
        }

        private void endLine(PGTS gts) {
            if (this.printed == WIDTH) {
                int nodeCount = gts.nodeCount();
                int edgeCount = gts.edgeCount();
                int explorableCount = gts.openStateCount();
                System.out.println(" " + nodeCount + "s (" + explorableCount
                    + "x) " + edgeCount + "t ");
                this.printed = 0;
            }
        }

        /**
         * The number of indications printed on the current line.
         */
        private int printed = 0;
        /**
         * The number of additions after which an indication is printed to screen.
         */
        static private final int UNIT = 100;
        /**
         * Number of indications on one line.
         */
        static private final int WIDTH = 100;
    }

}
