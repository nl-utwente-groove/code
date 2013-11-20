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
package groove.explore;

import groove.explore.encode.Serialized;
import groove.explore.encode.TemplateList;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.explore.util.ExplorationStatistics;
import groove.grammar.Grammar;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.GrammarModel;
import groove.graph.plain.PlainGraph;
import groove.io.FileType;
import groove.io.external.Exporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.FormatExporter;
import groove.io.external.PortException;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Groove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.kohsuke.args4j.CmdLineException;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class Args4JGenerator {
    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * @param args generator options, grammar and start graph name
     */
    static public void main(String[] args) {
        try {
            generate(args);
        } catch (Exception e) {
            System.err.println("Generator failed: " + e.getMessage());
        }
    }

    /**
     * Loads a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter, and returns the generated transition system.
     * @param args generator options, grammar and start graph name
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public GTS generate(String[] args) throws Exception {
        return new Args4JGenerator(args).run();
    }

    /**
     * Constructs the generator and processes the command-line arguments.
     * @throws Exception if any error was found in the command-line arguments
     */
    public Args4JGenerator(String... args) throws Exception {
        this.options = new GeneratorOptions();
        this.parser = new GrooveCmdLineParser(this.options);
        try {
            this.parser.parseArgument(args);
        } catch (CmdLineException e) {
            // only throw the exception if the help option is not invoked
            // because if it is invoked then only the help message will be printed
            if (!this.options.isHelp()) {
                throw e;
            } else {
                emitError("%s%n%s", e.getMessage(),
                    this.parser.getSingleLineUsage());
            }
        }
        this.grammarModel = computeGrammarModel();
        this.grammar = this.grammarModel.toGrammar();
        this.grammar.setFixed();
        this.logWriter = computeLogWriter();
        this.invocationTime = new Date();
    }

    /** Outputs and optionally logs an error message, and throws a corresponding exception. */
    public void emitError(String message, Object... args) throws Exception {
        String error = String.format("Error: " + message, args);
        if (!getVerbosity().isNone()) {
            System.err.println(error);
        }
        if (this.logWriter != null) {
            this.logWriter.println(error);
        }
        throw new Exception(error);
    }

    /** Outputs a diagnostic message if allowed by the verbosity, and optionally logs it. */
    public void emit(Verbosity min, String message, Object... args) {
        String text = String.format(message, args);
        if (getVerbosity().subsumes(min)) {
            System.out.print(text);
        }
        if (this.logWriter != null) {
            this.logWriter.print(text);
        }
    }

    /** Outputs a diagnostic message under any verbosity except #NONE, and optionally logs it. */
    public void emit(String message, Object... args) {
        emit(Verbosity.NONE, message, args);
    }

    /**
     * Runs the state space generation process.
     * @throws Exception if anything goes wrong during generation.
     */
    public GTS run() throws Exception {
        if (this.options.isHelp()) {
            printHelp();
        } else {
            init();
            generate();
            report();
            exit();
        }
        return getGTS();
    }

    /** Closes the log file. */
    protected void exit() {
        if (this.logWriter != null) {
            this.logWriter.close();
        }
    }

    /**
     * Initialises the state space generation.
     */
    protected void init() {
        this.explorationStats = new ExplorationStatistics(getGTS());
        this.explorationStats.configureForGenerator(this.options.getVerbosity());
    }

    /**
     * The processing phase of state space generation.
     */
    protected void generate() throws Exception {
        emit("Grammar:\t%s%n", this.options.getGrammar());
        List<String> startGraphs = this.options.getStartGraphs();
        emit(
            "Start graph:\t%s%n",
            startGraphs.isEmpty() ? "default" : Groove.toString(
                startGraphs.toArray(), "", "", ", "));
        emit("Exploration:\t%s%n" + getExploration().getIdentifier());
        emit("Timestamp:\t%s%n", this.invocationTime);
        emit("\nProgress:%n");
        getGTS().addLTSListener(new GenerateProgressMonitor());
        this.explorationStats.start();
        getExploration().play(getGTS(), null);
        this.explorationStats.stop();
    }

    /** Prints a report of the run on the standard output. */
    protected void report() throws Exception {
        // Advance 2 lines (after the progress).
        emit("%n%n");
        String report = this.explorationStats.getReport();
        if (report.length() > 0) {
            emit("%s%n", report);
        }
        emit("%s%n", getExploration().getLastMessage());
        // transfer the garbage collector log (if any) to the log file (if any)
        if (this.logWriter != null) {
            File gcLogFile = new File(GC_LOG_NAME);
            if (gcLogFile.exists()) {
                try {
                    BufferedReader gcLog =
                        new BufferedReader(new FileReader(gcLogFile));
                    List<String> gcList = new ArrayList<String>();
                    String nextLine = gcLog.readLine();
                    while (nextLine != null) {
                        gcList.add(nextLine);
                        nextLine = gcLog.readLine();
                    }
                    for (int i = 1; i < gcList.size() - 2; i++) {
                        this.logWriter.println(gcList.get(i));
                    }
                    gcLog.close();
                } catch (FileNotFoundException exc) {
                    System.err.println("Error while opening GC log");
                } catch (IOException exc) {
                    System.err.println("Error while reading from GC log");
                }
            }
        }
        exportSimulation();
    }

    /**
     * @return the total running time of the generator.
     */
    public long getRunningTime() {
        return this.explorationStats.getRunningTime();
    }

    /**
     * Export the performed simulation to an output file, if either the -e or
     * the -ep was specified on the command line.
     */
    protected void exportSimulation() throws Exception {
        if (this.options.isSaveLts()) {
            boolean labelFinalStates = false;
            boolean labelStartState = false;
            boolean labelOpenStates = false;
            boolean exportStateNames = false;
            String flags = this.options.getLtsFlags();
            if (flags != null) {
                labelStartState = flags.indexOf('s') >= 0;
                labelFinalStates = flags.indexOf('f') >= 0;
                labelOpenStates = flags.indexOf('o') >= 0;
                exportStateNames = flags.indexOf('n') >= 0;
            }
            // Create the LTS view to be exported.
            PlainGraph lts =
                getGTS().toPlainGraph(labelFinalStates, labelStartState,
                    labelOpenStates, exportStateNames);

            // Export GTS.
            String outFilename =
                this.options.getLtsPattern().replace(PLACEHOLDER,
                    this.grammarModel.getId());
            File ltsFile = new File(outFilename);
            Format gtsFormat = Exporter.getAcceptingFormat(lts, ltsFile);
            if (gtsFormat != null) {
                try {
                    ((FormatExporter) gtsFormat.getFormatter()).doExport(
                        ltsFile, gtsFormat, new Exportable(lts));
                } catch (PortException e1) {
                    throw new IOException(e1);
                }
            } else {
                Groove.saveGraph(lts, ltsFile);
            }
        }

        if (this.options.isSaveState()) {
            // Export results.
            String statePattern = this.options.getStatePattern();
            Collection<? extends GraphState> export =
                getExploration().getLastResult().getValue();
            for (GraphState state : export) {
                String stateFilename =
                    statePattern.replace(PLACEHOLDER, "" + state.getNumber());
                File stateFile = new File(stateFilename);
                Format stateFormat =
                    Exporter.getAcceptingFormat(state.getGraph(), stateFile);
                if (stateFormat != null) {
                    try {
                        ((FormatExporter) stateFormat.getFormatter()).doExport(
                            stateFile, stateFormat,
                            new Exportable(state.getGraph()));
                    } catch (PortException e1) {
                        throw new IOException(e1);
                    }
                } else {
                    Groove.saveGraph(GraphConverter.toAspect(state.getGraph()),
                        stateFile);
                }
            }
        }
    }

    /**
     * Creates and returns a grammar model, using the options
     * to find the location and other settings.
     */
    private GrammarModel computeGrammarModel() throws Exception {
        GrammarModel result;
        Observer loadObserver = new Observer() {
            public void update(Observable o, Object arg) {
                if (!getVerbosity().isLow()) {
                    if (arg instanceof String) {
                        System.out.printf("%s .", arg);
                    } else if (arg == null) {
                        System.out.println(" done");
                    } else {
                        System.out.print(".");
                    }
                }
            }
        };

        File grammarFile = this.options.getGrammar();
        try {
            result = GrammarModel.newInstance(grammarFile);
            List<String> startGraphs =
                new ArrayList<String>(this.options.getStartGraphs());
            AspectGraph externalStartGraph =
                getStartGraph(grammarFile, startGraphs);
            if (externalStartGraph != null) {
                result.setStartGraph(externalStartGraph);
            }
            result.getStore().addObserver(loadObserver);
        } catch (IOException exc) {
            emitError("Can't load grammar: " + exc.getMessage());
            result = null;
        }
        return result;
    }

    private AspectGraph getStartGraph(File grammarFile, List<String> startGraphs)
        throws IOException {
        AspectGraph result = null;
        if (!startGraphs.isEmpty()) {
            List<AspectGraph> graphs = new ArrayList<AspectGraph>();
            for (String startGraphName : startGraphs) {
                startGraphName =
                    FileType.STATE_FILTER.addExtension(startGraphName);
                File startGraphFile = new File(grammarFile, startGraphName);
                if (!startGraphFile.exists()) {
                    startGraphFile = new File(startGraphName);
                }
                if (!startGraphFile.exists()) {
                    throw new IOException("Can't find start graph "
                        + startGraphName);
                }
                graphs.add(GraphConverter.toAspect(Groove.loadGraph(startGraphFile)));
            }
            result = AspectGraph.mergeGraphs(graphs);
        }
        return result;
    }

    /**
     * Lazily constructs and returns the GTS to be generated.
     */
    public GTS getGTS() {
        if (gts == null) {
            gts = new GTS(this.grammar);
        }
        return gts;
    }

    /**
      * Returns the exploration strategy set for the generator. The strategy is
      * lazily retrieved from the command line options, or set to the default
      * exploration if nothing was specified.
      */
    private Exploration getExploration() {
        if (this.exploration == null) {
            this.exploration = computeExploration();
        }
        return this.exploration;
    }

    /**
     * Compute the exploration out of the command line options.
     * Uses the default exploration for components that were not specified.
     */
    private Exploration computeExploration() {
        Exploration result = this.grammarModel.getDefaultExploration();
        if (result == null) {
            result = new Exploration();
        }

        Serialized strategy;
        if (this.options.hasStrategy()) {
            TemplateList<Strategy> enumerator =
                StrategyEnumerator.newInstance();
            strategy = enumerator.parseCommandline(this.options.getAcceptor());
        } else {
            strategy = result.getStrategy();
        }

        Serialized acceptor;
        if (this.options.hasAcceptor()) {
            TemplateList<Acceptor> enumerator =
                AcceptorEnumerator.newInstance();
            acceptor = enumerator.parseCommandline(this.options.getAcceptor());
        } else {
            acceptor = result.getAcceptor();
        }

        int nrResults = this.options.getResultCount();

        return new Exploration(strategy, acceptor, nrResults);
    }

    /** Convenience methods to return the verbosity level from the generator options. */
    private Verbosity getVerbosity() {
        return this.options.getVerbosity();
    }

    /** Returns the writer used for logging, or {@code null} if logging is not set. */
    private PrintWriter computeLogWriter() throws Exception {
        PrintWriter result = null;
        if (this.options.isLogging()) {
            File logDir = this.options.getLogDir();
            String logId =
                ID_PREFIX
                    + ID_SEPARATOR
                    + this.grammarModel.getId()
                    + ID_SEPARATOR
                    + this.invocationTime.toString().replace(' ', '_').replace(
                        ':', '-');
            String logFileName = FileType.LOG_FILTER.addExtension(logId);
            try {
                result =
                    new PrintWriter(new FileWriter(
                        new File(logDir, logFileName)));
            } catch (IOException e) {
                emitError(
                    "Can't create log file in " + logDir + ": "
                        + e.getMessage(), false);
            }
        }
        return result;
    }

    private void printUsage() {
        System.out.print("Usage: \"Generator");
        this.parser.printSingleLineUsage(System.out);
        System.out.println("\"");
    }

    private void printHelp() {
        printUsage();
        this.parser.setUsageWidth(100);
        this.parser.printUsage(System.out);
    }

    /**
     * Time of invocations, initialised at construction time.
     */
    protected final Date invocationTime;
    /** The command-line parser. */
    private final GrooveCmdLineParser parser;
    /** The generator options. */
    private final GeneratorOptions options;
    /** The model of the graph grammar used for the generation. */
    private final GrammarModel grammarModel;
    /** The graph grammar used for the generation. */
    private final Grammar grammar;

    /** The log file, if any. */
    private final PrintWriter logWriter;
    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static GTS gts;

    /**
     * The exploration to be used for the state space generation.
     */
    private Exploration exploration;

    /** The exploration statistics for the generated state space. */
    private ExplorationStatistics explorationStats;

    /**
     * Fixed prefix for the identity string.
     */
    static private final String ID_PREFIX = "gts";
    /**
     * Separator used in constructing the log file name.
     */
    static private final String ID_SEPARATOR = "#";
    /** Placeholder in LTS and state filename patterns to insert further information. */
    static private final String PLACEHOLDER = "#";
    /** Separator between grammar name and start state name in reporting. */
    static public final char START_STATE_SEPARATOR = '@';
    /**
     * Fixed name of the gc log file. If a file with this name is found, and
     * logging is switched on, the gc log is appended to the generator log.
     */
    static public final String GC_LOG_NAME = "gc.log";
}
