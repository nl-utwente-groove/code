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
import groove.io.ExtensionFilter;
import groove.io.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * New command-line Generator class, using the Agrs4J library. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class GeneratorOptions {
    /** 
     * Indicates if the help option has been invoked.
     * If this is the case, then none of the other options have been processed. 
     * @return {@code true} if the help option has been invoked
     */
    public boolean isHelp() {
        return this.help;
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
     * Returns the verbosity level, as a value between 0 and 2 (inclusive).
     */
    public Verbosity getVerbosity() {
        return this.verbosity;
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

    private List<ExplorationReporter> computeReporters() {
        List<ExplorationReporter> result = new ArrayList<ExplorationReporter>();
        if (isSaveLts()) {
            result.add(new LTSReporter(getLtsPattern(), getLtsLabels()));
        }
        if (isSaveState()) {
            result.add(new StateReporter(getStatePattern()));
        }
        result.add(new LogReporter(getGrammar().getPath(), getStartGraphs(),
            getVerbosity(), getLogDir()));
        return result;
    }

    @Option(name = "-h", aliases = "--help",
            usage = "Print this help message and exit",
            handler = HelpHandler.class)
    private boolean help;

    @Option(name = "-l", metaVar = "dir",
            usage = "Log the generation process in the directory <dir>",
            handler = DirectoryHandler.class)
    private File logdir;

    @Option(name = "-s", metaVar = "strgy", usage = ""
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
        + "  remote:host - Remote")
    private String strategy;

    @Option(
            name = "-a",
            metaVar = "acc",
            usage = ""
                + "Set the acceptor to <acc>. "
                + "The acceptor determines when a state is counted as a result of the exploration. "
                + "Legal values are:\n" //
                + "    final      - When final (default)\n" //
                + "    inv:[!]id  - If rule <id> is [not] applicable\n" //
                + "    ruleapp:id - If there is an <id>-labelled transition\n" //
                + "    formula:f  - If <f> holds (a boolean formula of rules separated by &, |, !)\n" //
                + "    any        - Always (all states are results)\n" //
                + "    cycle      - If the state starts a cycle\n" //
                + "    none       - Never (no states are results)")
    private String acceptor;

    @Option(
            name = "-r",
            metaVar = "num",
            usage = "Stop exploration after <num> result states (default is infinite)")
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
            handler = LTSFlagsHandler.class)
    private LTSLabels ltsLabels;

    @Option(
            name = "-o",
            metaVar = "file",
            usage = "Save the generated LTS to a file with name derived from <file>, "
                + "in which '#' is instantiated with the grammar ID. "
                + "The \"-ef\"-option controls some additional state labels. "
                + "The optional extension determines the output format (default is .gxl)")
    private String ltsPattern;

    @Option(name = "-v", metaVar = "level",
            usage = "Set verbosity level (range = -1 to 2, default = 1)",
            handler = VerbosityHandler.class)
    private Verbosity verbosity = Verbosity.MEDIUM;

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

    /**
     * Option handler for the help option.
     * Stops option parsing when invoked.
     */
    public static class HelpHandler extends OptionHandler<Boolean> {
        /** Required constructor. */
        public HelpHandler(CmdLineParser parser, OptionDef option,
                Setter<? super Boolean> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            this.owner.stopOptionParsing();
            this.setter.addValue(true);
            return params.size();
        }

        @Override
        public String getDefaultMetaVariable() {
            return null;
        }
    }

    /**
     * Option handler that adds a {@code .gxl} extension to a file name,
     * if there is no extension.
     */
    public static class GxlFileHandler extends FileOptionHandler {
        /** Required constructor. */
        public GxlFileHandler(CmdLineParser parser, OptionDef option,
                Setter<? super File> setter) {
            super(parser, option, setter);
        }

        @Override
        protected File parse(String argument) throws CmdLineException {
            File result = super.parse(argument);
            String filename = result.getPath();
            if (!FileType.GXL_FILTER.hasExtension(filename)) {
                result = new File(FileType.GXL_FILTER.addExtension(filename));
            }
            return result;
        }
    }

    /**
     * Option handler that checks whether a value is an existing directory.
     */
    public static class DirectoryHandler extends FileOptionHandler {
        /** Constructor that allows an optional extension filter to be specified. */
        protected DirectoryHandler(CmdLineParser parser, OptionDef option,
                Setter<? super File> setter, ExtensionFilter filter) {
            super(parser, option, setter);
            this.filter = filter;
        }

        /** Required constructor. */
        public DirectoryHandler(CmdLineParser parser, OptionDef option,
                Setter<? super File> setter) {
            this(parser, option, setter, null);
        }

        @Override
        protected File parse(String argument) throws CmdLineException {
            File result = super.parse(argument);
            if (!result.isDirectory()) {
                // see if we can set an extension
                if (this.filter != null && !this.filter.acceptExtension(result)) {
                    result =
                        new File(this.filter.addExtension(result.getPath()));
                }
                if (!result.isDirectory()) {
                    throw new CmdLineException(this.owner, String.format(
                        "File name \"%s\" must be an existing directory",
                        result));
                }
            }
            return result;
        }

        private final ExtensionFilter filter;
    }

    /**
     * Option handler that adds a {@code .gps} extension to a directory name,
     * if the directory has no extension and does not exist.
     */
    public static class GrammarHandler extends DirectoryHandler {
        /** Required constructor. */
        public GrammarHandler(CmdLineParser parser, OptionDef option,
                Setter<? super File> setter) {
            super(parser, option, setter, FileType.GRAMMAR_FILTER);
        }
    }

    /** Hangler for the {@link #ltsLabels} option. */
    public static class LTSFlagsHandler extends
            OneArgumentOptionHandler<LTSLabels> {
        /** The required constructor. */
        public LTSFlagsHandler(CmdLineParser parser, OptionDef option,
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

    /**
     * Option handler that checks whether a verbosity value is in the correct range.
     */
    public static class VerbosityHandler extends
            OneArgumentOptionHandler<Verbosity> {
        /** Required constructor. */
        public VerbosityHandler(CmdLineParser parser, OptionDef option,
                Setter<? super Verbosity> setter) {
            super(parser, option, setter);
        }

        @Override
        public Verbosity parse(String value) throws NumberFormatException {
            int result = Integer.parseInt(value);
            if (result < Verbosity.LOWEST || result >= Verbosity.HIGHEST) {
                throw new NumberFormatException();
            }
            return Verbosity.getVerbosity(result);
        }

        /** Lowest verbosity value. */
        public final static int LOWER = 0;
        /** Highest verbosity value. */
        public final static int UPPER = 2;
    }

}
