// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id $
 */
package groove.util;

import groove.explore.AcceptorEnumerator;
import groove.explore.Exploration;
import groove.explore.StrategyEnumerator;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.Serialized;
import groove.explore.encode.TemplateList;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.explore.util.ExplorationStatistics;
import groove.io.ExtensionFilter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTSGraph;
import groove.lts.State;
import groove.trans.GraphGrammar;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

/**
 * A class that takes care of loading in a rule system consisting of a set of
 * individual files containing graph rules, from a given location | presumably
 * the top level directory containing the rule files.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Generator extends CommandLineTool {
    /**
     * Fixed name of the gc log file. If a file with this name is found, and
     * logging is switched on, the gc log is appended to the generator log.
     */
    static public final String GC_LOG_NAME = "gc.log";

    /**
     * Fixed prefix for the identity string.
     */
    static public final String ID_PREFIX = "gts";

    /**
     * Graphs file extension.
     */
    static public final String GRAPH_FILE_EXTENSION = ".graphs";
    /** Error message in case grammar cannot be found. */
    static public final String LOAD_ERROR = "Can't load graph grammar";
    /** Usage message for the generator. */
    static public final String USAGE_MESSAGE =
        "Usage: Generator [options] <grammar> [<start-graph-name> | <start-graphs-dir>]";

    /**
     * Value for the output file name to indicate that the name should be
     * computed from the grammar name.
     * @see #getOutputFileName()
     */
    static public final String GRAMMAR_NAME_VAR = "@";
    /** Separator between grammar name and start state name in reporting. */
    static public final char START_STATE_SEPARATOR = '@';

    /** Local references to the command line options. */
    private final TemplatedOption<Strategy> strategyOption;
    private final TemplatedOption<Acceptor> acceptorOption;
    private final ResultOption resultOption;
    private final ScenarioOption scenarioOption;

    private final FinalSaveOption finalSaveOption;

    private final ExportSimulationOption exportSimulationOption;
    private final ExportSimulationFlagsOption exportSimulationFlagsOption;

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * @param args the first argument is the grammar location name; if provided,
     *        the second argument is the start graph filename.
     */
    static public void main(String[] args) {
        new Generator(new LinkedList<String>(Arrays.asList(args))).start();
    }

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public Generator(List<String> argsList) {
        super(argsList);

        this.strategyOption =
            new TemplatedOption<Strategy>("s", "str", new StrategyEnumerator());
        this.acceptorOption =
            new TemplatedOption<Acceptor>("a", "acc", new AcceptorEnumerator());
        this.resultOption = new ResultOption();
        this.scenarioOption = new ScenarioOption();

        this.finalSaveOption = new FinalSaveOption();

        this.exportSimulationOption = new ExportSimulationOption();
        this.exportSimulationFlagsOption = new ExportSimulationFlagsOption();

        addOption(this.strategyOption);
        addOption(this.acceptorOption);
        addOption(this.resultOption);
        addOption(this.scenarioOption);
        addOption(this.finalSaveOption);
        addOption(this.exportSimulationOption);
        addOption(this.exportSimulationFlagsOption);

        // clear the static field gts
        gts = null;
    }

    /**
     * Starts the state space generation process. Before invoking this method,
     * all relevant parameters should be set. The method successively calls
     * <tt>{@link #init}</tt>, <tt>{@link #generate}</tt> and
     * <tt>{@link #exit}</tt>.
     */
    public void start() {
        processArguments();
        verifyExportOptions();
        verifyExplorationOptions();
        try {
            startLog();
            init();
            Collection<? extends Object> result = generate();
            report();
            exit(result);
            endLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies that a valid combination of export simulation options has been
     * specified. Aborts with an error message otherwise.
     */
    private void verifyExportOptions() {
        // Local variables for the existence of the -e and -ef options.
        boolean e = isOptionActive(this.exportSimulationOption);
        boolean ef = isOptionActive(this.exportSimulationFlagsOption);

        // Verify that -ef only occurs if -e also occurs.
        if (ef && !e) {
            printError("The -ef option may only be specified in conjunction "
                + "with the -e option.", false);
        }
    }

    /**
     * Verifies that a valid combination of exploration options has been
     * specified. Aborts with an error message otherwise.
     * Also displays a warning message if the deprecated -x option is used.
     */
    private void verifyExplorationOptions() {
        // Local variables for the existence of the -x, -s, -a and -r options.
        boolean x = isOptionActive(this.scenarioOption);
        boolean s = isOptionActive(this.strategyOption);
        boolean a = isOptionActive(this.acceptorOption);
        boolean r = isOptionActive(this.resultOption);

        // Verify that -x only occurs when -s, -a and -r are all absent.
        if (x && (s || a || r)) {
            printError("The deprecated -x option may not be combined with the"
                + "-s, -a and -r options.", false);
        }

        // Print a warning message when the deprecated -x feature is used.
        if (x) {
            System.err.println("Warning: the -x option has been deprecated, "
                + "please use -s, -a and -r instead.");
            System.err.println("Automatically replacing it with equivalent"
                + " -s, -a and -r options.");
        }
    }

    /**
     * Sets the grammar to be used for state space generation.
     * @param grammarLocation the file name of the grammar (with or without file
     *        name extension)
     */
    public void setGrammarLocation(String grammarLocation) {
        this.grammarLocation = grammarLocation;
    }

    /**
     * Sets the start graph to be used for state space generation.
     * @param startGraphName the name of the start graph (without file name
     *        extension)
     */
    public void setStartGraph(String startGraphName) {
        this.startStateName = startGraphName;
    }

    /**
     * This implementation lazily creates and returns the id of this generator
     * run.
     */
    @Override
    protected String getId() {
        if (this.id == null) {
            this.id = computeId();
        }
        return this.id;
    }

    /** Computes an ID from the grammar location and the time. */
    protected String computeId() {
        return ID_PREFIX + ID_SEPARATOR + getGrammarName() + ID_SEPARATOR
            + super.getId();
    }

    /**
     * Returns the GTS that is being generated. The GTS is lazily obtained from
     * the grammar if it had not yet been initialised.
     * @see #getGrammar()
     */
    public GTS getGTS() {
        if (gts == null) {
            gts = new GTS(getGrammar());
        }
        return gts;
    }

    /**
     * Returns the grammar used for generating the state space. The grammar is
     * lazily loaded in. The method throws an error and returns
     * <code>null</code> if the grammar could not be loaded.
     */
    public GraphGrammar getGrammar() {
        if (this.grammar == null) {
            computeGrammar();
        }
        return this.grammar;
    }

    /** Loads in and returns a grammar. */
    private void computeGrammar() {
        Observer loadObserver = new Observer() {
            public void update(Observable o, Object arg) {
                if (getVerbosity() > LOW_VERBOSITY) {
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

        URL url;

        File f =
            new File(this.ruleSystemFilter.addExtension(this.grammarLocation));
        try {
            if (f.exists()) {
                url = Groove.toURL(f);
            } else {
                url = new URL(this.grammarLocation);
            }
            if (this.startStateName != null) {
                url = new URL(url.toExternalForm() + "?" + this.startStateName);
            }
        } catch (MalformedURLException e) {
            printError("Can't load grammar: " + e.getMessage(), false);
            return;
        }
        // now we are guaranteed to have a URL

        try {
            StoredGrammarView grammarView = StoredGrammarView.newInstance(url);

            if (grammarView.getStore() instanceof Observable) {
                ((Observable) grammarView.getStore()).addObserver(loadObserver);
            }
            this.grammar = grammarView.toGrammar();
            this.grammar.setFixed();
        } catch (IOException exc) {
            printError("Can't load grammar: " + exc.getMessage(), false);
        } catch (FormatException exc) {
            printError("Grammar format error: " + exc.getMessage(), false);
        }

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
        if (this.grammarLocation == null) {
            printError("No grammar location specified", true);
        }
    }

    /**
      * Returns the exploration strategy set for the generator. The strategy is
      * lazily retrieved from the command line options, or set to the default
      * exploration if nothing was specified.
      */
    protected Exploration getExploration() {
        if (this.exploration == null) {
            this.exploration = computeExploration();
        }
        return this.exploration;
    }

    /**
     * Compute the exploration out of the command line options.
     * Uses the default exploration for components that were not specified.
     */
    protected Exploration computeExploration() {

        Serialized strategy, acceptor;
        int nrResults;
        Exploration defaultExploration = new Exploration();

        if (isOptionActive(this.scenarioOption)) {
            return this.scenarioOption.getValue();
        }

        if (isOptionActive(this.strategyOption)) {
            strategy = this.strategyOption.getValue();
        } else {
            strategy = defaultExploration.getStrategy();
        }

        if (isOptionActive(this.acceptorOption)) {
            acceptor = this.acceptorOption.getValue();
        } else {
            acceptor = defaultExploration.getAcceptor();
        }

        if (isOptionActive(this.resultOption)) {
            nrResults = this.resultOption.getValue();
        } else {
            nrResults = 0;
        }

        return new Exploration(strategy, acceptor, nrResults);
    }

    /**
     * Returns the prefix of the filename to save all final states. If
     * <tt>null</tt>, final states will not be saved (default).
     * @see FinalSaveOption
     */
    protected String getFinalSaveName() {
        if (!isOptionActive(this.finalSaveOption)) {
            return null;
        } else {
            return this.finalSaveOption.getValue();
        }
    }

    /**
     * The initialisation phase of state space generation. Called from
     * <tt>{@link #start}</tt>.
     */
    protected void init() {
        this.explorationStats = new ExplorationStatistics(getGTS());
        this.explorationStats.configureForGenerator(this.getVerbosity());
    }

    /**
     * The processing phase of state space generation. Called from
     * <tt>{@link #start}</tt>.
     */
    protected Collection<? extends Object> generate() {
        Collection<? extends Object> result;
        if (getVerbosity() > LOW_VERBOSITY) {
            println("Grammar:\t" + this.grammarLocation);
            println("Start graph:\t"
                + (this.startStateName == null ? "default"
                        : this.startStateName));
            println("Exploration:\t" + getExploration().getIdentifier());
            println("Timestamp:\t" + this.invocationTime);
            print("\nProgress:\t");
            getGTS().addGraphListener(new GenerateProgressMonitor());
        }
        this.explorationStats.start();
        try {
            getExploration().play(getGTS(), null);
        } catch (FormatException e) {
            printError("The specified exploration is not "
                + "valid for the loaded grammar.\n" + e.getMessage(), false);
        }
        this.explorationStats.stop();
        result = getExploration().getLastResult().getValue();
        exportSimulation();

        return result;
    }

    /**
     * Export the performed simulation to an output file, if either the -e or
     * the -ep was specified on the command line.
     */
    protected void exportSimulation() {
        // Local variables for the related active command line options. 
        boolean e = isOptionActive(this.exportSimulationOption);
        boolean ef = isOptionActive(this.exportSimulationFlagsOption);

        // Do nothing if -e was not specified.
        if (!e) {
            return;
        }

        // Compute the export path, which is the argument of the -e option.
        String path = this.exportSimulationOption.getValue();

        // Compute the export simulation flags.
        ExportSimulationFlags flags;
        if (ef) {
            flags = this.exportSimulationFlagsOption.getValue();
        } else {
            flags = new ExportSimulationFlags();
        }

        // Create the LTS view to be exported.
        LTSGraph lts =
            new LTSGraph(getGTS(), flags.labelFinalStates,
                flags.labelStartState, flags.labelOpenStates,
                flags.exportStateNames);

        // Compute the set of states to be exported separately.
        Collection<GraphState> export = new HashSet<GraphState>(0);
        if (flags.exportFinalStates) {
            export = getGTS().getFinalStates();
        }
        if (flags.exportAllStates) {
            export = getGTS().getStateSet();
        }

        // Perform the export itself.
        try {
            Groove.saveGraph(lts, path + "/lts.gxl");
            for (GraphState state : export) {
                String name = state.toString();
                Groove.saveGraph(state.getGraph(), path + "/" + name + ".gst");
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /** Prints a report of the run on the standard output. */
    protected void report() {
        // Advance 2 lines (after the progress).
        if (getVerbosity() > LOW_VERBOSITY) {
            println();
            println();
        }
        println(this.explorationStats.getReport());

        if (getVerbosity() > LOW_VERBOSITY) {
            if (getOutputFileName() != null) {
                println();
                println("LTS stored in: \t" + getOutputFileName());
            }
        }
        // transfer the garbage collector log (if any) to the log file (if any)
        if (isLogging()) {
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
                        getLogWriter().println(gcList.get(i));
                    }
                } catch (FileNotFoundException exc) {
                    System.err.println("Error while opening GC log");
                } catch (IOException exc) {
                    System.err.println("Error while reading from GC log");
                }
            }
        }
    }

    /**
     * @return the total running time of the generator.
     */
    public long getRunningTime() {
        return this.explorationStats.getRunningTime();
    }

    /**
     * The finalisation phase of state space generation. Called from
     * <tt>{@link #start}</tt>.
     */
    protected void exit(Collection<? extends Object> result) throws IOException {
        if (getFinalSaveName() != null) {
            if (result.isEmpty()) {
                System.out.println("No resulting graphs");
            } else {
                for (State finalState : getGTS().getFinalStates()) {
                    String outFileName = getFinalSaveName() + "-" + finalState;
                    outFileName = this.gstFilter.addExtension(outFileName);
                    Groove.saveGraph(((GraphState) finalState).getGraph(),
                        outFileName);
                }
                System.out.printf("Resulting graphs saved: %s%n",
                    getGTS().getFinalStates());
            }
        }
        if (getOutputFileName() != null) {
            if (getVerbosity() == HIGH_VERBOSITY) {
                print(GraphReporter.createInstance().getReport(getGTS()).toString());
            }
            if (!Groove.exportGraph(new LTSGraph(getGTS()), getOutputFileName())) {
                Groove.saveGraph(new LTSGraph(getGTS()), getOutputFileName());
            }
        }
    }

    /**
     * This implementation expands any occurrence of {@link #GRAMMAR_NAME_VAR}
     * into the grammar name.
     */
    @Override
    protected String getOutputFileName() {
        String result = super.getOutputFileName();
        if (result != null) {
            String grammarNameRegExpr = GRAMMAR_NAME_VAR;
            result = result.replaceAll(grammarNameRegExpr, getGrammarName());
        }
        return result;
    }

    /**
     * Convenience method to derive the name of a grammar from its location.
     * Strips the directory and extension.
     */
    protected String getGrammarName() {
        StringBuilder result =
            new StringBuilder(
                new File(
                    this.ruleSystemFilter.stripExtension(this.grammarLocation)).getName());
        if (this.startStateName != null) {
            result.append(START_STATE_SEPARATOR);
            result.append(this.startStateName);
        }
        return result.toString();
    }

    /** This implementation returns <tt>getId()</tt>. */
    @Override
    protected String getLogFileName() {
        return getId();
    }

    /**
     * This implementation returns <tt>{@link #USAGE_MESSAGE}</tt>.
     */
    @Override
    protected String getUsageMessage() {
        return USAGE_MESSAGE;
    }

    /**
     * The identity for this generator, constructed from grammar name and time
     * of invocation.
     */
    private String id;

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    protected static GTS gts;

    /**
     * The exploration to be used for the state space generation.
     */
    private Exploration exploration;

    /** The exploration statistics for the generated state space. */
    private ExplorationStatistics explorationStats;

    /** String describing the location where the grammar is to be found. */
    private String grammarLocation;
    /** String describing the start graph within the grammar. */
    private String startStateName;
    /** The graph grammar used for the generation. */
    private GraphGrammar grammar;

    /** File filter for rule systems. */
    protected final ExtensionFilter ruleSystemFilter =
        Groove.createRuleSystemFilter();
    /** File filter for graph files (GXL). */
    protected final ExtensionFilter gxlFilter = Groove.createGxlFilter();
    /** File filter for graph state files (GST). */
    protected final ExtensionFilter gstFilter = Groove.createStateFilter();
    /** File filter for graph files (GXL or GST). */
    protected final ExtensionFilter graphFilter = new ExtensionFilter(
        "Serialized graph files", GRAPH_FILE_EXTENSION);

    /**
     * The <code>ExportSimulationPathOption</code> is the command line option
     * to export the simulation to an explicitly specified absolute path. It is
     * implemented by means <code>StoreCommandLineOption</code> that stores a
     * <code>String</code> with the path.
     * 
     * @see StoreCommandLineOption
     */
    protected class ExportSimulationOption extends
            StoreCommandLineOption<String> {

        /** 
         * Default constructor. Defines '-e' to be the name of the command
         * line option, and 'path' to be the name of its argument.
         */
        public ExportSimulationOption() {
            super("e", "path");
        }

        @Override
        public String[] getDescription() {
            return new String[] {"Export the simulation to the specified "
                + "(absolute) path"};
        }

        @Override
        public String parseParameter(String parameter) {
            return parameter; // no parsing needed
        }
    }

    /**
     * The <code>ExportSimulationFlags</code> class is a record that holds all
     * the flags that can be set with the -ef (export simulation flags) option.
     * The default value for all flags is <code>false</code>.
     * 
     * @see ExportSimulationFlagsOption
     */
    static public class ExportSimulationFlags {

        /** Flag to indicate that the start state must be labeled. */
        public boolean labelStartState = false;

        /** Flag to indicate that the final states must be labeled. */
        public boolean labelFinalStates = false;

        /** Flag to indicate that the open states must be labeled. */
        public boolean labelOpenStates = false;

        /** Flag to indicate that the state names must be exported. */
        public boolean exportStateNames = false;

        /** Flag to indicate that the final states must be exported. */
        public boolean exportFinalStates = false;

        /** Flag to indicate that all states must be exported. */
        public boolean exportAllStates = false;
    }

    /**
     * The <code>ExportSimulationFlagsOption</code> is the command line option
     * for additional flags for the export simulation option.
     * It is implemented as a <code>StoreCommandLineOption</code>.
     * 
     * @see StoreCommandLineOption
     */
    protected class ExportSimulationFlagsOption extends
            StoreCommandLineOption<ExportSimulationFlags> {

        /** 
         * Default constructor. Defines '-ef' to be the name of the command
         * line option, and 'flags' to be the name of its argument.
         */
        public ExportSimulationFlagsOption() {
            super("ef", "flags");
        }

        @Override
        public String[] getDescription() {
            String[] desc = new String[7];

            desc[0] =
                "Flags for the export simulation option. Legal flags are:";
            desc[1] = "  s - label start state";
            desc[2] = "  f - label final states";
            desc[3] = "  o - label open states";
            desc[4] = "  N - export state names";
            desc[5] = "  A - export all states (in separate files)";
            desc[6] = "  F - export final states (in separate files)";
            return desc;
        }

        @Override
        public ExportSimulationFlags parseParameter(String parameter) {
            ExportSimulationFlags result = new ExportSimulationFlags();
            for (int i = 0; i < parameter.length(); i++) {
                switch (parameter.charAt(i)) {
                case 's':
                    result.labelStartState = true;
                    break;
                case 'f':
                    result.labelFinalStates = true;
                    break;
                case 'o':
                    result.labelOpenStates = true;
                    break;
                case 'N':
                    result.exportStateNames = true;
                    break;
                case 'A':
                    result.exportAllStates = true;
                    break;
                case 'F':
                    result.exportFinalStates = true;
                    break;
                default:
                    throw new IllegalArgumentException("'"
                        + parameter.charAt(i)
                        + "' is not a valid export simulation flag.");
                }
            }
            return result;
        }
    }

    /**
     * The grammar loader.
     */
    // protected final FileGps loader = createGrammarLoader();
    /**
     * The <code>FinalSaveOption</code> is the command line option for saving
     * all final states in separate files. It is implemented by means of a
     * <code>StoreCommandLineOption</code> that stores a <code>String</code>
     * to indicate the names of the files to be written.
     * 
     * @see StoreCommandLineOption 
     */
    protected class FinalSaveOption extends StoreCommandLineOption<String> {

        /** 
         * Default constructor. Defines '-f' to be the name of the command
         * line option, and 'file' to be the name of its argument.
         */
        public FinalSaveOption() {
            super("f", "file");
        }

        @Override
        public String[] getDescription() {
            return new String[] {"Save all final states using 'file' + number as filenames"};
        }

        @Override
        public String parseParameter(String parameter) {
            return parameter; // no parsing necessary
        }
    }

    /**
     * The <code>ResultOption</code> is the command line option for setting the
     * nrResults stored by the exploration. It is implemented by means of a
     * <code>StoreCommandLineOption<code> that stores a <code>Integer</code> to
     * indicate the number of results to be stored.
     * 
     * @see StoreCommandLineOption
     */
    protected class ResultOption extends StoreCommandLineOption<Integer> {

        /** 
         * Default constructor. Defines '-r' to be the name of the command
         * line option, and 'num' to be the name of its argument.
         */
        public ResultOption() {
            super("r", "num");
        }

        @Override
        public String[] getDescription() {
            return new String[] {"The number of accepted exploration results (default is infinite)"};
        }

        @Override
        public Integer parseParameter(String parameter) {
            Integer result;
            try {
                result = Integer.parseInt(parameter);
                if (result <= 0) {
                    throw new IllegalArgumentException("'" + parameter
                        + "' is not a valid positive number.");
                } else {
                    return result;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + parameter
                    + "' is not a valid positive number.");
            }
        }
    }

    /**
     * A <code>TemplatedOption</code> is a command line option for a
     * <code>TemplateList</code>. It is implemented by means of a
     * <code>StoreCommandLineOption</code> that stores a <code>Serialized</code>. 
     * 
     * @see TemplateList
     * @see Serialized
     * @see StoreCommandLineOption
     */
    protected class TemplatedOption<A> extends
            StoreCommandLineOption<Serialized> {

        // Enumerator of all allowed options.
        private final TemplateList<A> enumerator;

        /** 
         * Generic constructor that takes as arguments the name of the option,
         * the name of its parameter and an enumerator that describes the valid
         * values of this parameter.
         */
        public TemplatedOption(String name, String parameterName,
                TemplateList<A> enumerator) {
            super(name, parameterName);
            this.enumerator = enumerator;
        }

        @Override
        public String[] getDescription() {
            String[] lines = this.enumerator.describeCommandlineGrammar();
            String[] desc = new String[lines.length + 1];

            desc[0] =
                "The " + this.enumerator.getTypeIdentifier()
                    + ". Legal values for '" + getParameterName() + "' are:";
            desc[1] = "  " + lines[0] + " (default value)";
            for (int i = 1; i < lines.length; i++) {
                desc[i + 1] = "  " + lines[i];
            }
            return desc;
        }

        @Override
        public Serialized parseParameter(String parameter) {
            Serialized result = this.enumerator.parseCommandline(parameter);
            if (result == null) {
                throw new IllegalArgumentException("Unable to parse "
                    + getName() + " argument '" + parameter + "'.");
            } else {
                return result;
            }
        }
    }

    /**
     * A <code>ScenarioOption</code> is a command line option with which a
     * predefined combination of a strategy, acceptor and result can be
     * specified. This option is provided for backwards compatibility only, and
     * will always result in a 'deprecated feature' warning when used.
     * It is implemented by means of a <code>StoreCommandLineOption</code>,
     * which converts its argument to a <code>Serialized</code>.
     */
    protected class ScenarioOption extends StoreCommandLineOption<Exploration> {

        /** 
         * Default constructor. Defines '-x' to be the name of the command
         * line option, and 'scenario' to be the name of its argument.
         */
        public ScenarioOption() {
            super("x", "scenario");
        }

        /**
         * As this is a deprecated option, no help is displayed for it (to
         * encourage users to make use of the new options).
         */
        @Override
        public String[] getDescription() {
            return new String[0];
        }

        /**
         * Removes keyword, plus a ':' separator, from the start of parameter.
         * Throws an <code>IllegalArgumentException</code> if the ':' is not
         * there, or if there are no characters behind it.
         */
        public String removeKeyword(String keyword, String parameter) {
            if (parameter.length() <= keyword.length()) {
                throw new IllegalArgumentException("':' expected after '"
                    + keyword + "' in the -x option.");
            }
            if (parameter.length() == keyword.length() + 1) {
                throw new IllegalArgumentException("Expected an argument"
                    + " after '" + parameter + "' in the -x option.");
            }
            return parameter.substring(keyword.length() + 1);
        }

        /**
         * Parse a textual rule option into the 'rule' and 'mode' arguments of
         * a given <code>Serialized</code>.
         */
        public void setRuleArgument(Serialized serialized, String ruleArg) {
            if (ruleArg.startsWith("!")) {
                serialized.setArgument("mode", EncodedRuleMode.NEGATIVE);
                serialized.setArgument("rule", ruleArg.substring(1));
            } else {
                serialized.setArgument("mode", EncodedRuleMode.NEGATIVE);
                serialized.setArgument("rule", ruleArg);
            }
        }

        @Override
        public Exploration parseParameter(String parameter) {
            Serialized strategy, acceptor;
            String argValue;

            // Convert the scenario's without arguments by means of a lookup
            // in a fixed Map<String,String>.
            Map<String,String> convTable = new TreeMap<String,String>();
            convTable.put("barbed", "dfs");
            convTable.put("branching", "bfs");
            convTable.put("linear", "linear");
            convTable.put("random", "random");
            convTable.put("full", "bfs");
            if (convTable.keySet().contains(parameter)) {
                strategy = new Serialized(convTable.get(parameter));
                acceptor = new Serialized("final");
                return new Exploration(strategy, acceptor, 0);
            }

            // Convert the scenario 'node-bounded'. Its argument can be re-used
            // without parsing or changing it.
            if (parameter.startsWith("node-bounded")) {
                strategy = new Serialized("cnbound");
                argValue = removeKeyword("node-bounded", parameter);
                strategy.setArgument("node-bound", argValue);
                acceptor = new Serialized("final");
                return new Exploration(strategy, acceptor, 0);
            }

            // Convert the scenario 'edge-bounded'. In its argument, all '='
            // signs need to be replaced with '>'.
            if (parameter.startsWith("edge-bounded")) {
                strategy = new Serialized("cebound");
                argValue =
                    removeKeyword("edge-bounded", parameter).replaceAll("=",
                        ">");
                strategy.setArgument("edge-bound", argValue);
                acceptor = new Serialized("final");
                return new Exploration(strategy, acceptor, 0);
            }

            // Convert the scenario 'bounded'. In its argument, a possible
            // leading '!' needs to be parsed too (see parseRuleArgument).
            if (parameter.startsWith("bounded")) {
                strategy = new Serialized("crule");
                argValue = removeKeyword("bounded", parameter);
                setRuleArgument(strategy, argValue);
                acceptor = new Serialized("final");
                return new Exploration(strategy, acceptor, 0);
            }

            // Convert the scenario 'invariant'. In its argument, a possible
            // leading '!' needs to be parsed too (see parseRuleArgument).
            if (parameter.startsWith("invariant")) {
                strategy = new Serialized("bfs");
                acceptor = new Serialized("inv");
                argValue = removeKeyword("invariant", parameter);
                setRuleArgument(acceptor, argValue);
                return new Exploration(strategy, acceptor, 1);
            }

            throw new IllegalArgumentException("'" + parameter
                + "' is not a legal value for the deprecated -x option.");
        }
    }

}
