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

import groove.explore.util.ExplorationReporter;
import groove.explore.util.LTSLabels;
import groove.explore.util.LTSReporter;
import groove.explore.util.LogReporter;
import groove.explore.util.StateReporter;
import groove.grammar.model.FormatException;
import groove.util.cli.CommandLineOptions;
import groove.util.cli.GrammarHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class GeneratorOptions extends CommandLineOptions {
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

    /**
     * Returns the result count.
     * If not set, the result count is {@code 0}.
     */
    public int getResultCount() {
        return this.resultCount;
    }

    /**
     * Returns the settings to be used in generating the LTS.
     */
    public LTSLabels getLtsLabels() {
        return this.ltsLabels;
    }

    /**
     * Returns the (optional) file to be used for saving the generated LTS to.
     * @return the file to save the LTS to, or {@code null} if not set 
     */
    public String getLtsPattern() {
        return this.ltsPattern;
    }

    /** 
     * Indicates if the LTS output option is set.
     * @return {@code true} if {@link #getLtsPattern()} is not {@code null} 
     */
    public boolean isSaveLts() {
        return getLtsPattern() != null;
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

    /** 
     * Indicates if the state save option is set.
     * @return {@code true} if {@link #getStatePattern()} is not {@code null} 
     */
    public boolean isSaveState() {
        return getStatePattern() != null;
    }

    /**
     * Returns the grammar location.
     * The location is guaranteed to be an existing directory.
     * @return the (non-{@code null}) grammar location
     */
    public File getGrammar() {
        return this.grammar;
    }

    /**
     * Returns the optional start graph, if set.
     * If set, the start graph is guaranteed to either the (qualified) 
     * name of a host graph within the grammar, or the name of an existing file.
     * @return the start graph name; may be {@code null} 
     */
    public List<String> getStartGraphs() {
        return this.startGraphs;
    }

    /** Returns the exploration reporters enabled on the basis of the options. */
    public List<ExplorationReporter> getReporters() {
        if (this.reporters == null) {
            this.reporters = computeReporters();
        }
        return this.reporters;
    }

    /** Factory method for the reporters associated with this invocation. */
    protected List<ExplorationReporter> computeReporters() {
        List<ExplorationReporter> result = new ArrayList<ExplorationReporter>();
        result.add(new LogReporter(getGrammar().getPath(), getStartGraphs(),
            getVerbosity(), getLogDir()));
        if (isSaveLts()) {
            result.add(new LTSReporter(getLtsPattern(), getLtsLabels()));
        }
        if (isSaveState()) {
            result.add(new StateReporter(getStatePattern()));
        }
        return result;
    }

    @Option(name = STRATEGY_NAME, metaVar = STRATEGY_VAR,
            usage = STRATEGY_USAGE)
    private String strategy;

    /** Option name for the strategy option. */
    public final static String STRATEGY_NAME = "-s";
    /** Meta-variable name for the strategy option. */
    public final static String STRATEGY_VAR = "strgy";
    /** Usage message for the strategy option. */
    public final static String STRATEGY_USAGE = ""
        + "Set the exploration strategy to <strgy>. Legal values are:\n"
        + "  bfs         - Breadth-first Exploration\n"
        + "  dfs         - Depth-first Exploration\n"
        + "  linear      - Linear\n" //
        + "  random      - Random linear\n"
        + "  state       - Single-State\n" //
        + "  rete        - Rete-based DFS\n"
        + "  retelinear  - Rete-based Linear\n"
        + "  reterandom  - Rete-based Random Linear\n"
        + "  crule:[!]id - Conditional (where rule <id> [not] applicable))\n"
        + "  cnbound:n   - Conditional (up to <n> nodes)\n"
        + "  cebound:id>n,...\n"
        + "              - Conditional (up to <n> edges with label <id>)\n"
        + "  ltl:prop    - LTL Model Checking\n" //
        + "  ltlbounded:idn,...;prop\n"
        + "              - Bounded LTL Model Checking\n"
        + "  ltlpocket:idn,...;prop\n"
        + "              - Pocket LTL Model Checking\n"
        + "  remote:host - Remote";

    @Option(name = ACCEPTOR_NAME, metaVar = ACCEPTOR_VAR,
            usage = ACCEPTOR_USAGE)
    private String acceptor;

    /**
     * Name of the acceptor option.
     */
    private static final String ACCEPTOR_NAME = "-a";
    /**
     * Meta-variable of the acceptor option.
     */
    public static final String ACCEPTOR_VAR = "acc";

    /** Usage message for the acceptor option. */
    public final static String ACCEPTOR_USAGE =
        ""
            + "Set the acceptor to <acc>. "
            + "The acceptor determines when a state is counted as a result of the exploration. "
            + "Legal values are:\n" //
            + "    final      - When final (default)\n" //
            + "    inv:[!]id  - If rule <id> is [not] applicable\n" //
            + "    ruleapp:id - If there is an <id>-labelled transition\n" //
            + "    formula:f  - If <f> holds (a boolean formula of rules separated by &, |, !)\n" //
            + "    any        - Always (all states are results)\n" //
            + "    cycle      - If the state starts a cycle\n" //
            + "    none       - Never (no states are results)";

    @Option(name = RESULT_NAME, metaVar = RESULT_VAR, usage = RESULT_USAGE)
    private int resultCount = 0;

    @Option(
            name = "-ef",
            metaVar = "flags",
            depends = "-o",
            usage = ""
                + "Flags for the \"-o\" option. Legal values are:\n" //
                + "  s - label start state (default: 'start')\n" //
                + "  f - label final states (default: 'final')\n" //
                + "  o - label open states (default: 'open')\n" //
                + "  n - label state with number (default: 's#', '#' replaced by number)"
                + "Specify label to be used by appending flag with 'label' (single-quoted)",
            handler = LTSLabelsHandler.class)
    private LTSLabels ltsLabels;

    @Option(
            name = "-o",
            metaVar = "file",
            usage = "Save the generated LTS to a file with name derived from <file>, "
                + "in which '#' is instantiated with the grammar ID. "
                + "The \"-ef\"-option controls some additional state labels. "
                + "The optional extension determines the output format (default is .gxl)")
    private String ltsPattern;

    @Option(
            name = "-f",
            metaVar = "file",
            usage = "Save result states in separate files, with names derived from <file>, "
                + "in which the mandatory '#' is instantiated with the state number. "
                + "The optional extension determines the output format (default is .gst)")
    private String statePattern;

    @Argument(metaVar = "grammar", required = true,
            usage = "grammar location (default extension .gps)",
            handler = GrammarHandler.class)
    private File grammar;

    @Argument(index = 1, metaVar = "start", multiValued = true,
            usage = "Start graph names (defined in grammar, no extension) "
                + "or start graph files (extension .gst)")
    private List<String> startGraphs;

    /** The reporters that can be built on the basis of the options. */
    private List<ExplorationReporter> reporters;

    /** Handler for the {@link #ltsLabels} option. */
    public static class LTSLabelsHandler extends
            OneArgumentOptionHandler<LTSLabels> {
        /** The required constructor. */
        public LTSLabelsHandler(CmdLineParser parser, OptionDef option,
                Setter<? super LTSLabels> setter) {
            super(parser, option, setter);
        }

        @Override
        protected LTSLabels parse(String argument)
            throws NumberFormatException, CmdLineException {
            try {
                return new LTSLabels(argument);
            } catch (FormatException e) {
                throw new CmdLineException(this.owner, e);
            }
        }
    }
}
