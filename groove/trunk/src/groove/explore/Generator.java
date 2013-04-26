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
package groove.explore;

import static groove.io.FileType.GRAMMAR_FILTER;
import groove.explore.encode.Serialized;
import groove.explore.encode.TemplateList;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.explore.util.ExplorationStatistics;
import groove.grammar.Grammar;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.plain.PlainGraph;
import groove.io.FileType;
import groove.io.external.Exporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.FormatExporter;
import groove.io.external.PortException;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.CommandLineTool;
import groove.util.Groove;
import groove.util.StoreCommandLineOption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A class that takes care of loading in a rule system consisting of a set of
 * individual files containing graph rules, from a given location | presumably
 * the top level directory containing the rule files.
 * @author Arend Rensink
 * @version $Revision: 3108 $
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

    private final ResultSaveOption resultSaveOption;

    private final ExportSimulationOption exportSimulationOption;
    private final ExportSimulationFlagsOption exportSimulationFlagsOption;

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * @param args generator options, grammar and start graph name
     */
    static public void main(String[] args) {
        generate(args);
    }

    /**
     * Loads a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter, and returns the generated transition system.
     * @param args generator options, grammar and start graph name
     * @return the generated transition system
     */
    static public GTS generate(String[] args) {
        return new Generator(args).start();
    }

    /**
     * Constructs the generator. In particular, initializes the command line
     * option classes.
     */
    public Generator(String... args) {
        super(false, args);
        this.startGraphs = new ArrayList<String>();

        this.strategyOption =
            new TemplatedOption<Strategy>(
                "s",
                "str",
                StrategyEnumerator.newInstance(StrategyValue.CONCRETE_STRATEGIES));
        this.acceptorOption =
            new TemplatedOption<Acceptor>("a", "acc",
                AcceptorEnumerator.newInstance());
        this.resultOption = new ResultOption();

        this.resultSaveOption = new ResultSaveOption();

        this.exportSimulationOption = new ExportSimulationOption();
        this.exportSimulationFlagsOption = new ExportSimulationFlagsOption();

        addOption(this.verbosityOption);
        addOption(this.logOption);
        addOption(this.strategyOption);
        addOption(this.acceptorOption);
        addOption(this.resultOption);
        addOption(this.exportSimulationOption);
        addOption(this.exportSimulationFlagsOption);
        addOption(this.outputOption);
        addOption(this.resultSaveOption);

        // clear the static field gts
        gts = null;
    }

    /**
     * Starts the state space generation process. Before invoking this method,
     * all relevant parameters should be set. The method successively calls
     * <tt>{@link #init}</tt>, <tt>{@link #generate}</tt> and
     * <tt>{@link #exit}</tt>.
     */
    public GTS start() {
        processArguments();
        try {
            startLog();
            init();
            generate();
            report();
            exit();
            endLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gts;
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
     * Adds an active start graph to be used for state space generation.
     * Also processes the name, removing the extension and the leading
     * grammar location.
     * @param startGraphName the name of the start graph (as processed by the
     *    command line)
     */
    private void addStartGraph(String startGraphName) {
        String name = startGraphName;
        if (name.startsWith(this.grammarLocation)) {
            name = name.substring(this.grammarLocation.length() + 1);
        }
        if (name.endsWith(".gst")) {
            name = name.substring(0, name.length() - 4);
        }
        this.startGraphs.add(name);
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
     * Creates and returns a grammar model for the (supposedly initialised)
     * grammar location and start graph name.
     */
    public GrammarModel getGrammarModel() {
        if (this.grammarModel == null) {
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
                new File(GRAMMAR_FILTER.addExtension(this.grammarLocation));
            try {
                if (f.exists()) {
                    url = Groove.toURL(f);
                } else {
                    url = new URL(this.grammarLocation);
                }
                /*
                 * Disabled adding ?START_GRAPH_NAME to the URL. MdM.
                 * MdM.
                if (this.startGraphs != null) {
                    url =
                        new URL(url.toExternalForm() + "?" + this.startGraphs);
                }
                */
            } catch (MalformedURLException e) {
                printError("Can't load grammar: " + e.getMessage(), false);
                return null;
            }
            // now we are guaranteed to have a URL

            try {
                List<String> startGraphNames =
                    new ArrayList<String>(this.startGraphs);
                if (this.startGraphs.isEmpty()) {
                    startGraphNames.add(Groove.DEFAULT_START_GRAPH_NAME);
                }
                this.grammarModel = GrammarModel.newInstance(url);
                this.grammarModel.setLocalActiveNames(ResourceKind.HOST,
                    startGraphNames);
                this.grammarModel.getStore().addObserver(loadObserver);
            } catch (IOException exc) {
                printError("Can't load grammar: " + exc.getMessage(), false);
            }
        }
        return this.grammarModel;
    }

    /**
     * Returns the grammar used for generating the state space. The grammar is
     * lazily loaded in. The method throws an error and returns
     * <code>null</code> if the grammar could not be loaded.
     */
    public Grammar getGrammar() {
        if (this.grammar == null) {
            try {
                this.grammar = getGrammarModel().toGrammar();
                this.grammar.setFixed();
            } catch (FormatException exc) {
                printError("Grammar format error: " + exc.getMessage(), false);
            }
        }
        return this.grammar;
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
        while (argsList.size() > 0) {
            String arg = argsList.remove(0);
            if (arg.startsWith(OPTIONS_PREFIX)) {
                if (argsList.size() > 0) {
                    argsList.remove(0);
                }
            } else {
                addStartGraph(arg);
            }
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
        Exploration result = getGrammarModel().getDefaultExploration();
        if (result == null) {
            result = new Exploration();
        }

        Serialized strategy;
        if (isOptionActive(this.strategyOption)) {
            strategy = this.strategyOption.getValue();
        } else {
            strategy = result.getStrategy();
        }

        Serialized acceptor;
        if (isOptionActive(this.acceptorOption)) {
            acceptor = this.acceptorOption.getValue();
        } else {
            acceptor = result.getAcceptor();
        }

        int nrResults;
        if (isOptionActive(this.resultOption)) {
            nrResults = this.resultOption.getValue();
        } else {
            nrResults = 0;
        }

        return new Exploration(strategy, acceptor, nrResults);
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
    protected void generate() {
        if (getVerbosity() > LOW_VERBOSITY) {
            println("Grammar:\t" + this.grammarLocation);
            println("Start graph:\t"
                + (this.startGraphs == null ? "default" : this.startGraphs));
            println("Exploration:\t" + getExploration().getIdentifier());
            println("Timestamp:\t" + this.invocationTime);
            print("\nProgress:\t");
            getGTS().addLTSListener(new GenerateProgressMonitor());
        }
        this.explorationStats.start();
        try {
            getExploration().play(getGTS(), null);
        } catch (FormatException e) {
            printError("The specified exploration is not "
                + "valid for the loaded grammar.\n" + e.getMessage(), false);
        }
        this.explorationStats.stop();
    }

    /**
     * Export the performed simulation to an output file, if either the -e or
     * the -ep was specified on the command line.
     */
    protected void exportSimulation() {
        // Local variables for the related active command line options. 
        boolean e = isOptionActive(this.exportSimulationOption);
        boolean ef = isOptionActive(this.exportSimulationFlagsOption);

        String path;
        if (e) {
            path = this.exportSimulationOption.getValue();
        } else {
            path = ".";
        }

        // Compute the export simulation flags.
        ExportSimulationFlags flags;
        if (ef) {
            flags = this.exportSimulationFlagsOption.getValue();
        } else {
            flags = new ExportSimulationFlags();
        }

        // Create the LTS view to be exported.
        PlainGraph lts =
            getGTS().toPlainGraph(flags.labelFinalStates,
                flags.labelStartState, flags.labelOpenStates,
                flags.exportStateNames);

        // Perform the export.
        try {
            // Export GTS.
            if (isOptionActive(this.outputOption)) {
                String outFilename = getOutputFileName();
                String ltsFilename;
                if (outFilename == null) {
                    ltsFilename = path + "/gts" + FileType.GXL.getExtension();
                } else {
                    ltsFilename = path + "/" + outFilename;
                }
                File ltsFile = new File(ltsFilename);
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

            // Export results.
            if (isOptionActive(this.resultSaveOption)) {
                ResultSave rs = this.resultSaveOption.getValue();
                Collection<? extends GraphState> export =
                    getExploration().getLastResult().getValue();
                for (GraphState state : export) {
                    String stateFilename =
                        path + "/" + rs.prefix + state.getNumber() + rs.suffix
                            + rs.type.getExtension();
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
                        Groove.saveGraph(
                            GraphConverter.toAspect(state.getGraph()),
                            stateFile);
                    }
                }
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
        String report = this.explorationStats.getReport();
        if (report.length() > 0) {
            println(report);
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
     * The finalisation phase of state space generation. Called from
     * <tt>{@link #start}</tt>.
     */
    protected void exit() {
        // Does nothing.
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
            new StringBuilder(new File(
                GRAMMAR_FILTER.stripExtension(this.grammarLocation)).getName());
        if (this.startGraphs != null) {
            result.append(START_STATE_SEPARATOR);
            result.append(this.startGraphs);
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
    private static GTS gts;

    /**
     * The exploration to be used for the state space generation.
     */
    private Exploration exploration;

    /** The exploration statistics for the generated state space. */
    private ExplorationStatistics explorationStats;

    /** String describing the location where the grammar is to be found. */
    private String grammarLocation;
    /** The set of start graphs to be used in the grammar. May be empty. */
    private final List<String> startGraphs;
    /** The model of the graph grammar used for the generation. */
    private GrammarModel grammarModel;

    /** The graph grammar used for the generation. */
    private Grammar grammar;

    /**
     * The <code>ExportSimulationPathOption</code> is the command line option
     * to export the simulation to an explicitly specified absolute path. It is
     * implemented by means <code>StoreCommandLineOption</code> that stores a
     * <code>String</code> with the path.
     * 
     * @see StoreCommandLineOption
     */
    protected static class ExportSimulationOption extends
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
                + "path (default is grammar path)"};
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
    static private class ExportSimulationFlags {
        /** Flag to indicate that the start state must be labeled. */
        public boolean labelStartState = false;
        /** Flag to indicate that the final states must be labeled. */
        public boolean labelFinalStates = false;
        /** Flag to indicate that the open states must be labeled. */
        public boolean labelOpenStates = false;
        /** Flag to indicate that the state names must be exported. */
        public boolean exportStateNames = false;
    }

    /**
     * The <code>ExportSimulationFlagsOption</code> is the command line option
     * for additional flags for the export simulation option.
     * It is implemented as a <code>StoreCommandLineOption</code>.
     * 
     * @see StoreCommandLineOption
     */
    protected static class ExportSimulationFlagsOption extends
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
            String[] desc = new String[5];

            desc[0] =
                "Flags for the export simulation option. Legal flags are:";
            desc[1] = "  s - label start state";
            desc[2] = "  f - label final states";
            desc[3] = "  o - label open states";
            desc[4] = "  n - export state names";
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
                case 'n':
                    result.exportStateNames = true;
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

    static class ResultSave {
        String prefix = "";
        String suffix = "";
        FileType type = FileType.GXL;
    }

    /**
     * The <code>ResultSaveOption</code> is the command line option for saving
     * all final states in separate files. It is implemented by means of a
     * <code>StoreCommandLineOption</code> that stores a <code>String</code>
     * to indicate the names of the files to be written.
     * 
     * @see StoreCommandLineOption 
     */
    protected static class ResultSaveOption extends
            StoreCommandLineOption<ResultSave> {

        /** 
         * Default constructor. Defines '-f' to be the name of the command
         * line option, and 'file' to be the name of its argument.
         */
        public ResultSaveOption() {
            super("f", "a#b.e");
        }

        @Override
        public String[] getDescription() {
            return new String[] {
                "Save all result states using '" + getParameterName()
                    + "' with # being state number.",
                "Saving format is determined by extension 'e' (default is GXL)."};
        }

        @Override
        public ResultSave parseParameter(String parameter) {
            ResultSave result = new ResultSave();
            try {
                String[] s = parameter.split("#");
                result.prefix = s[0];
                String[] t = s[1].split("\\.");
                result.suffix = t[0];
                for (FileType type : FileType.layoutless) {
                    if (t[1].equals(type.getExtensionName())) {
                        result.type = type;
                        break;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // Do nothing.
            }
            return result;
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
    public static class ResultOption extends StoreCommandLineOption<Integer> {

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
    public static class TemplatedOption<A> extends
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

}
