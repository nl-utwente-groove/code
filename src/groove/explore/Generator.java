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

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.MapOptionHandler;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import groove.explore.encode.Serialized;
import groove.explore.util.CompositeReporter;
import groove.explore.util.ExplorationReporter;
import groove.explore.util.GenerateProgressListener;
import groove.explore.util.LTSLabels;
import groove.explore.util.LTSReporter;
import groove.explore.util.LogReporter;
import groove.explore.util.StateReporter;
import groove.grammar.GrammarKey;
import groove.lts.Filter;
import groove.transform.Transformer;
import groove.util.cli.DirectoryHandler;
import groove.util.cli.GrammarHandler;
import groove.util.cli.GrooveCmdLineParser;
import groove.util.cli.GrooveCmdLineTool;
import groove.util.parse.FormatException;

/**
 * New command-line Generator class, using the Agrs4J library.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Generator extends GrooveCmdLineTool<ExploreResult> {
    /**
     * Constructs the generator and processes the command-line arguments.
     * @throws CmdLineException if any error was found in the command-line arguments
     */
    public Generator(String... args) throws CmdLineException {
        super("Generator", args);
    }

    /**
     * Runs the exploration and returns the generated GTS.
     */
    @Override
    protected ExploreResult run() throws Exception {
        Transformer transformer = computeTransformer();
        transformer.addListener(getReporter());
        if (!getVerbosity().isLow()) {
            transformer.addListener(new GenerateProgressListener());
        }
        ExploreResult result = transformer.explore(getStartGraphs());
        getReporter().report();
        return result;
    }

    /* Adds a message about the -XX:SoftRefLRUPolicyMSPerMB JVM option. */
    @Override
    protected GrooveCmdLineParser createParser(String appName) {
        return new GrooveCmdLineParser(appName, this) {
            @Override
            public String getUsageLine() {
                return String.format(
                    "%s%n%nUse JVM option %s=10 for large state spaces, to avoid excessive garbage collection",
                    super.getUsageLine(),
                    SOFT_REF_POLICY_NAME);
            }
        };
    }

    private final static String SOFT_REF_POLICY_NAME = "-XX:SoftRefLRUPolicyMSPerMB";

    /**
     * Compute the exploration out of the command line options.
     * Uses the default exploration for components that were not specified.
     */
    private Transformer computeTransformer() throws IOException, FormatException {
        Transformer result = new Transformer(getGrammar());
        if (hasStrategy()) {
            Serialized strategy = StrategyEnumerator.parseCommandLineStrategy(getStrategy());
            result.setStrategy(strategy);
        }
        if (hasAcceptor()) {
            result.setAcceptor(AcceptorEnumerator.parseCommandLineAcceptor(getAcceptor()));
        }
        Map<GrammarKey,String> properties = getGrammarProperties();
        if (properties != null) {
            for (Map.Entry<GrammarKey,String> e : properties.entrySet()) {
                result.setProperty(e.getKey(), e.getValue());
            }
        }
        result.setResultCount(getResultCount());
        return result;
    }

    /**
     * Indicates if the log option has been set.
     * @return {@code true} if {@link #getLogDir()} does not return {@code null}
     */
    public boolean isLogging() {
        return getLogDir() != null;
    }

    /**
     * Sets the directory for the log file.
     * If {@code null}, no logging will take place.
     */
    public File getLogDir() {
        return this.logdir;
    }

    @Option(name = "-l", metaVar = "dir",
        usage = "Log the generation process in the directory <dir>",
        handler = DirectoryHandler.class)
    private File logdir;

    /**
     * Indicates if the strategy option is set.
     * @return {@code true} if {@link #getStrategy()} is not {@code null}
     */
    public boolean hasStrategy() {
        return getStrategy() != null;
    }

    /**
     * Returns the optional strategy value.
     */
    public String getStrategy() {
        return this.strategy;
    }

    @Option(name = STRATEGY_NAME, metaVar = STRATEGY_VAR, usage = STRATEGY_USAGE)
    private String strategy;

    /**
     * Indicates if the acceptor option is set.
     * @return {@code true} if {@link #getAcceptor()} is not {@code null}
     */
    public boolean hasAcceptor() {
        return getAcceptor() != null;
    }

    /**
     * Returns the optional acceptor value.
     */
    public String getAcceptor() {
        return this.acceptor;
    }

    @Option(name = ACCEPTOR_NAME, metaVar = ACCEPTOR_VAR, usage = ACCEPTOR_USAGE)
    private String acceptor;

    /**
     * Returns the result count.
     * If not set, the result count is {@code 0}.
     */
    public int getResultCount() {
        return this.resultCount;
    }

    @Option(name = RESULT_NAME, metaVar = RESULT_VAR, usage = RESULT_USAGE)
    private int resultCount;

    /** Returns the locally set grammar properties, if any. */
    public Map<GrammarKey,String> getGrammarProperties() {
        return this.grammarProperties;
    }

    @Option(name = "-D", metaVar = "key=val",
        usage = "" + "Set grammar property <key> to <val>. Legal settings are:\n"
            + "  - checkIsomorphism=boolean - switch isomorphism checking on or off\n"
            + "  - controlProgram=names - set the control program(s) to be used\n"
            + "See groove.grammar.GrammarProperties " + "for other allowed key/value pairs",
        handler = PropertiesHandler.class)
    private Map<GrammarKey,String> grammarProperties;

    /**
     * Returns the settings to be used in storing the LTS.
     */
    public LTSLabels getLtsLabels() {
        return this.ltsLabels;
    }

    @Option(name = "-ef", metaVar = "flags", depends = "-o",
        usage = "" + "Flags for the \"-o\" option. Legal values are:\n" //
            + "  s - label start state (default: 'start')\n" //
            + "  f - label final states (default: 'final')\n" //
            + "  o - label open states (default: 'open')\n" //
            + "  n - label state with number (default: 's#', '#' replaced by number)\n" //
            + "  t - include transient states (label: 't#', '#' replaced by depth)\n" //
            + "  r - result state label (default: 'result')\n" //
            + "Specify label to be used by appending flag with 'label' (single-quoted)",
        handler = LTSLabelsHandler.class)
    private LTSLabels ltsLabels;

    /**
     * Indicates if the LTS output option is set.
     * @return {@code true} if {@link #getLtsPattern()} is not {@code null}
     */
    public boolean isSaveLts() {
        return getLtsPattern() != null;
    }

    /**
     * Returns the (optional) file to be used for saving the generated LTS to.
     * @return the file to save the LTS to, or {@code null} if not set
     */
    public String getLtsPattern() {
        return this.ltsPattern;
    }

    @Option(name = "-o", metaVar = "file",
        usage = "Save the generated LTS to a file with name derived from <file>, "
            + "in which '#' is instantiated with the grammar ID. "
            + "The \"-ef\"-option controls some additional state labels. "
            + "The optional extension determines the output format (default is .gxl)")
    private String ltsPattern;

    /**
     * Indicates if the state save option is set.
     * @return {@code true} if {@link #getStatePattern()} is not {@code null}
     */
    public boolean isSaveState() {
        return getStatePattern() != null;
    }

    /**
     * Returns the (optional) filename pattern to be used for saving result states.
     * The pattern is of the form {@code [path/]pre#post[.ext]}, where
     * {@code #} is to be instantiated with the state number, and {@code .ext}
     * determines the output format.
     * @return the filename pattern, or {@code null} if not set
     */
    public String getStatePattern() {
        return this.statePattern;
    }

    @Option(name = "-f", metaVar = "file",
        usage = "Save result states in separate files, with names derived from <file>, "
            + "in which the mandatory '#' is instantiated with the state number. "
            + "The optional extension determines the output format (default is .gst)")
    private String statePattern;

    /** Returns the filter mode to be used when saving the LTS. */
    public Filter getFilter() {
        return this.traces ? Filter.RESULT : this.spanning ? Filter.SPANNING : Filter.NONE;
    }

    @Option(name = "-spanning",
        usage = "If switched on, only the spanning tree of the LTS will be saved")
    private boolean spanning;

    @Option(name = "-traces",
        usage = "If switched on, only the result traces of the LTS will be saved "
            + "(which may be only the start state, if there are no result states). "
            + "Overrides -spanning if both are given")
    private boolean traces;

    /**
     * Returns the grammar location.
     * The location is guaranteed to be an existing directory.
     * @return the (non-{@code null}) grammar location
     */
    public File getGrammar() {
        return this.grammar;
    }

    @Argument(metaVar = GrammarHandler.META_VAR, required = true, usage = GrammarHandler.USAGE,
        handler = GrammarHandler.class)
    private File grammar;

    /**
     * Returns the optional start graph, if set.
     * If set, the start graph is guaranteed to either the (qualified)
     * name of a host graph within the grammar, or the name of an existing file.
     * @return the start graph name; may be {@code null}
     */
    public List<String> getStartGraphs() {
        return this.startGraphs;
    }

    @Argument(index = 1, metaVar = "start", multiValued = true,
        usage = "Start graph names (defined in grammar, no extension) "
            + "or start graph files (extension .gst)")
    private List<String> startGraphs;

    /** Returns the exploration reporters enabled on the basis of the options. */
    public ExplorationReporter getReporter() {
        if (this.reporter == null) {
            this.reporter = computeReporter();
        }
        return this.reporter;
    }

    /** Factory method for the reporters associated with this invocation. */
    private CompositeReporter computeReporter() {
        CompositeReporter result = new CompositeReporter();
        LogReporter logger = new LogReporter(getVerbosity(), getLogDir());
        if (isSaveLts()) {
            result.add(new LTSReporter(getLtsPattern(), getLtsLabels(), logger, getFilter()));
        }
        if (isSaveState()) {
            result.add(new StateReporter(getStatePattern(), logger));
        }
        // add the logger last, to ensure that any messages from the
        // other reporters are included.
        result.add(logger);
        return result;
    }

    /** The reporters that can be built on the basis of the options. */
    private CompositeReporter reporter;

    /**
     * Name of the acceptor option.
     */
    public static final String ACCEPTOR_NAME = "-a";
    /**
     * Meta-variable of the acceptor option.
     */
    public static final String ACCEPTOR_VAR = "acc";

    /** Usage message for the acceptor option. */
    public final static String ACCEPTOR_USAGE = "" + "Set the acceptor to <acc>. "
        + "The acceptor determines when a state is counted as a result of the exploration. "
        + "Legal values are:\n" //
        + "    final      - When final (default)\n" //
        + "    inv:[!]id  - If rule <id> is [not] applicable\n" //
        + "    ruleapp:id - If there is an <id>-labelled transition\n" //
        + "    formula:f  - If <f> holds (a boolean formula of rules separated by &, |, !)\n" //
        + "    any        - Always (all states are results)\n" //
        + "    cycle      - If the state starts a cycle\n" //
        + "    none       - Never (no states are results)";

    /**
     * Name of the result option.
     */
    public static final String RESULT_NAME = "-r";

    /**
     * Meta-variable name for the result option.
     */
    public static final String RESULT_VAR = "num";

    /**
     * Usage message for the result option.
     */
    public static final String RESULT_USAGE =
        "Stop exploration after <num> result states (default is 0 for \"unbounded\")";

    /** Option name for the strategy option. */
    public final static String STRATEGY_NAME = "-s";
    /** Meta-variable name for the strategy option. */
    public final static String STRATEGY_VAR = "strgy";
    /** Usage message for the strategy option. */
    public final static String STRATEGY_USAGE =
        "" + "Set the exploration strategy to <strgy>. Legal values are:\n"
            + "  bfs         - Breadth-first Exploration\n"
            + "  dfs         - Depth-first Exploration\n" + "  linear      - Linear\n" //
            + "  random      - Random linear\n" + "  state       - Single-State\n" //
            + "  rete        - Rete-based DFS\n" + "  retelinear  - Rete-based Linear\n"
            + "  reterandom  - Rete-based Random Linear\n"
            + "  crule:[!]id - Conditional: stop when rule <id> [not] applicable\n"
            + "  cnbound:n   - Conditional: up to <n> nodes\n" + "  cebound:id>n,...\n"
            + "              - Conditional: up to <n> edges labelled <id>\n"
            + "  ltl:prop    - LTL Model Checking\n" //
            + "  ltlbounded:idn,...;prop\n" + "              - Bounded LTL Model Checking\n"
            + "  ltlpocket:idn,...;prop\n" + "              - Pocket LTL Model Checking\n"
            + "  remote:host - Remote";

    /**
     * Attempts to load a graph grammar from a given location provided as a
     * parameter with either default start state or a start state provided as a
     * second parameter.
     * This will always exit with {@link System#exit(int)}; see
     * {@link #execute(String[])} for programmatic use.
     * @param args generator options, grammar and start graph name
     */
    static public void main(String[] args) {
        GrooveCmdLineTool.tryExecute(Generator.class, args);
    }

    /**
     * Loads a graph grammar, and returns the generated transition system.
     * @param args generator options and arguments
     * @return the generated transition system
     * @throws Exception if any error occurred that prevented the GTS from being fully generated
     */
    static public ExploreResult execute(String... args) throws Exception {
        staticResult = new Generator(args).start();
        return staticResult;
    }

    /** Returns the most recently generated GTS. */
    static public ExploreResult getGts() {
        return staticResult;
    }

    /**
     * The GTS that is being constructed. We make it static to enable memory
     * profiling. The field is cleared in the constructor, so consecutive
     * Generator instances work as expected.
     */
    private static ExploreResult staticResult;

    /** Handler for the {@link #ltsLabels} option. */
    public static class LTSLabelsHandler extends OneArgumentOptionHandler<LTSLabels> {
        /** The required constructor. */
        public LTSLabelsHandler(CmdLineParser parser, OptionDef option,
            Setter<? super LTSLabels> setter) {
            super(parser, option, setter);
        }

        @Override
        protected LTSLabels parse(String argument) throws NumberFormatException, CmdLineException {
            try {
                return new LTSLabels(argument);
            } catch (FormatException e) {
                throw new CmdLineException(this.owner, e);
            }
        }
    }

    /** Handler for the {@link #grammarProperties} option. */
    public static class PropertiesHandler extends MapOptionHandler {
        /** The required constructor. */
        public PropertiesHandler(CmdLineParser parser, OptionDef option,
            Setter<? super Map<?,?>> setter) {
            super(parser, option, setter);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void addToMap(String argument, Map m) throws CmdLineException {
            super.addToMap(argument, m);
            if (this.error != null) {
                throw new CmdLineException(this.owner, this.error);
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected Map<GrammarKey,String> createNewCollection(Class<? extends Map> type) {
            return new EnumMap<>(GrammarKey.class);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        protected void addToMap(Map m, String key, String value) {
            this.error = null;
            GrammarKey property = GrammarKey.getKey(key);
            if (property == null) {
                this.error = String.format("Unknown property key '%s'", key);
            } else if (property.isSystem()) {
                this.error = String.format("Cannot set system property '%s'", key);
            } else if (!property.parser()
                .accepts(value)) {
                this.error = String.format("Incorrect value '%s' for property '%s'", value, key);
            } else {
                m.put(property, value);
            }
        }

        /**
         * Error detected in {@link #addToMap(Map, String, String)}
         * and to be reported in {@link #addToMap(String, Map)}.
         */
        private String error;
    }
}
