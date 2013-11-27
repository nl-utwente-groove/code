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
 * $Id: CommandLineTool.java,v 1.4 2008-01-30 09:32:13 iovka Exp $
 */
package groove.util.cli;

import groove.explore.Verbosity;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.util.Groove;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A base class for command line tools, with some pre-define option types:
 * <ul>
 * <li> <code>OutputOption</code>, to specify an optional output file
 * <li> <code>VerbosityOption</code>, to specify a verbosity level
 * <li> <code>LogOption</code>, to specify a log file
 * </ul>
 * Each of these options can be enabled or disabled in subclasses by overwriting
 * the respective <tt>supports...Option</tt> methods.
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class CommandLineTool {
    /**
     * Prefix for command line options.
     */
    static public final String OPTIONS_PREFIX = "-";
    /**
     * Separator used in constructing the log file name.
     */
    static public final String ID_SEPARATOR = "#";
    /**
     * Log file extension.
     */
    static public final String LOG_FILE_EXTENSION = ".log";

    /** Symbolic char constant. */
    static protected final char SPACE = ' ';
    /** Symbolic char constant. */
    static protected final char COLON = ':';
    /** Symbolic char constant. */
    static protected final char UNDERSCORE = '_';
    /** Symbolic char constant. */
    static protected final char DASH = '-';

    /** Local reference to the output option. **/
    protected final OutputOption outputOption;
    /** Local reference to the verbosity option. **/
    protected final VerbosityOption verbosityOption;
    /** Local reference to the log option. **/
    protected final LogOption logOption;

    /**
     * Constructs an instance of the tool, with a given list of command line
     * arguments.
     */
    public CommandLineTool(boolean addOptions, String... args) {
        this.args = new ArrayList<String>(Arrays.asList(args));

        this.outputOption = new OutputOption();
        this.verbosityOption = new VerbosityOption(this);
        this.logOption = new LogOption(this);

        if (addOptions) {
            if (supportsOutputOption()) {
                addOption(this.outputOption);
            }
            if (supportsVerbosityOption()) {
                addOption(this.verbosityOption);
            }
            if (supportsLogOption()) {
                addOption(this.logOption);
            }
        }
    }

    /**
     * Constructs an instance of the tool, with a given list of command line
     * arguments.
     */
    public CommandLineTool(String... args) {
        this(true, args);
    }

    /**
     * Adds a command line option class to the list of option classes.
     */
    protected void addOption(CommandLineOption option) {
        this.optionsList.add(option);
    }

    /**
     * Goes through the list of command line arguments and tries to find command
     * line options. The options and their parameters are subsequently removed
     * from the argument list. If an option cannot be parsed, the method prints
     * an error message and terminates the program.
     */
    protected void processArguments() {
        List<String> argsList = getArgs();
        while (argsList.size() > 0
            && argsList.get(0).startsWith(OPTIONS_PREFIX)) {
            String optionName =
                argsList.get(0).substring(OPTIONS_PREFIX.length());
            argsList.remove(0);
            boolean validOption = false;
            Iterator<CommandLineOption> optionsIter =
                this.optionsList.iterator();
            while (!validOption && optionsIter.hasNext()) {
                CommandLineOption option = optionsIter.next();
                if (option.getName().equals(optionName)) {
                    String parameter;
                    if (option.hasParameter()) {
                        parameter = argsList.get(0);
                        argsList.remove(0);
                    } else {
                        parameter = null;
                    }
                    try {
                        option.parse(parameter);
                        validOption = true;
                    } catch (IllegalArgumentException exc) {
                        printError(exc.getMessage(), true);
                    }
                    this.activeOptions.put(option.getName(), option);
                }
            }
            if (!validOption) {
                printError("Unknown option " + optionName, true);
            }
        }
    }

    /** Initializes the log file. */
    protected void startLog() {
        if (isLogging()) {
            try {
                String logFileName =
                    this.logFilter.addExtension(getLogFileName());
                setLogWriter(new PrintWriter(new FileWriter(new File(
                    getLogDirName(), logFileName))));
            } catch (IOException e) {
                printError("Can't create log file in " + getLogDirName() + ": "
                    + e.getMessage(), false);
            }
        }
    }

    /** Initializes the log file. */
    protected void endLog() {
        if (isLogging()) {
            getLogWriter().close();
        }
    }

    /**
     * Callback method to check whether the output command line option is
     * supported. This implementation returns <tt>true</tt> always.
     */
    protected boolean supportsOutputOption() {
        return true;
    }

    /**
     * Callback method to check whether the verbosity command line option is
     * supported. This implementation returns <tt>true</tt> always.
     */
    protected boolean supportsVerbosityOption() {
        return true;
    }

    /**
     * Callback method to check whether the log command line option is
     * supported. This implementation returns <tt>true</tt> always.
     */
    protected boolean supportsLogOption() {
        return true;
    }

    /**
     * Returns a string serving as an identifier for this invocation of the
     * command line tool. This implementation constructs a string from the time
     * of construction, by replacing all ' ' by '_' and ':' by '-'.
     */
    protected String getId() {
        return this.invocationTime.toString().replace(SPACE, UNDERSCORE).replace(
            COLON, DASH);
    }

    /**
     * Returns a usage message for the command line tool.
     */
    protected String getUsageMessage() {
        return "Usage: " + this.getClass().getName() + " [options]";
    }

    /**
     * Returns a name by querying for an active {@link OutputOption}.
     * @see OutputOption#getOutputFileName()
     */
    protected String getOutputFileName() {
        if (isOptionActive(this.outputOption)) {
            return this.outputOption.getOutputFileName();
        } else {
            return null;
        }
    }

    /**
     * Indicates that the exploration should be logged, and sets the directory
     * where the log file should go. The name of the log file will be
     * constructed from the grammar and the time of invocation; it will receive
     * the extension <tt>{@link #LOG_FILE_EXTENSION}</tt>.
     */
    protected void setLogging(String logDirName) {
        this.logging = true;
        this.logDirName = logDirName;
    }

    /**
     * Callback method to generate a log file name, without extension. Called
     * from <tt>{@link #setLogging}</tt>. This implementation constructs a
     * name by concatenating the name of the class and an identifying string
     * obtained from <tt>{@link #getId()}</tt>.
     */
    protected String getLogFileName() {
        return this.getClass().getName() + ID_SEPARATOR + getId();
    }

    /**
     * Returns the log directory name set in the logging option, if any.
     */
    protected String getLogDirName() {
        return this.logDirName;
    }

    /**
     * Sets the logfile to a given value. If logging is enabled, all log
     * messages will (from now on) be written to this file.
     */
    protected void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Returns the log file; <tt>null</tt> if logging has not been activated.
     */
    protected PrintWriter getLogWriter() {
        return this.logWriter;
    }

    /**
     * Indicates if logging has been enabled.
     */
    protected boolean isLogging() {
        return this.logging;
    }

    /**
     * Sets the verbosity level. Only allowed if the verbosity option is
     * supported.
     * @param verbosity the verbosity level; should be a legal verbosity value.
     */
    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Returns the verbosity level. The default level is
     * <tt>MEDIUM_VERBOSITY</tt>
     */
    public Verbosity getVerbosity() {
        return this.verbosity;
    }

    /** Returns the command line arguments. */
    protected List<String> getArgs() {
        return this.args;
    }

    /**
     * Prints an empty line to the standard output and, if logging is activated,
     * to the log file.
     */
    protected void println() {
        System.out.println();
        if (this.logging) {
            this.logWriter.println();
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is
     * activated, to the log file.
     */
    protected void println(String text) {
        System.out.println(text);
        if (this.logging) {
            this.logWriter.println(text);
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is
     * activated, to the log file.
     */
    protected void printf(String text, Object... args) {
        System.out.printf(text, args);
        if (this.logging) {
            this.logWriter.printf(text, args);
        }
    }

    /**
     * Prints text (not ended by a line break) to the standard output and, if
     * logging is activated, to the log file.
     */
    protected void print(String text) {
        System.out.print(text);
        if (this.logging) {
            this.logWriter.print(text);
        }
    }

    /**
     * Prints an empty line to the standard output and, if logging is activated,
     * to the log file, provided the verbosity of the tool is at least the given
     * verbosity.
     */
    protected void println(Verbosity verbosity) {
        if (getVerbosity().compareTo(verbosity) >= 0) {
            println();
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is
     * activated, to the log file, provided the verbosity of the tool is at
     * least the given verbosity.
     */
    protected void println(String text, Verbosity verbosity) {
        if (getVerbosity().compareTo(verbosity) >= 0) {
            println(text);
        }
    }

    /**
     * Prints text (not ended by a line break) to the standard output and, if
     * logging is activated, to the log file, provided the verbosity of the tool
     * is at least the given verbosity.
     */
    protected void print(String text, Verbosity verbosity) {
        if (getVerbosity().compareTo(verbosity) >= 0) {
            print(text);
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is
     * activated, to the log file, provided the verbosity of the tool is at
     * least {@link Verbosity#MEDIUM}. Convenience method for
     * <tt>println(text, Verbosity#MEDIUM)</tt>.
     */
    protected void printlnMedium(String text) {
        println(text, Verbosity.MEDIUM);
    }

    /**
     * Prints a line of text to the standard output and, if logging is
     * activated, to the log file, provided the verbosity of the tool is at
     * least {@link Verbosity#HIGH}. Convenience method for
     * <tt>println(text, Verbosity.HIGH)</tt>.
     */
    protected void printlnHigh(String text) {
        println(text, Verbosity.HIGH);
    }

    /**
     * Prints formatted text to the standard output and, if logging is
     * activated, to the log file, provided the verbosity of the tool is at
     * least {@link Verbosity#MEDIUM}. Convenience method for
     * <tt>println(text, Verbosity.MEDIUM)</tt>.
     */
    protected void printfMedium(String text, Object... args) {
        if (!this.verbosity.isLow()) {
            printf(text, args);
        }
    }

    /**
     * Prints formatted text to the standard output and, if logging is
     * activated, to the log file, provided the verbosity of the tool is at
     * least {@link Verbosity#HIGH}. Convenience method for
     * <tt>println(text, Verbosity.HIGH)</tt>.
     */
    protected void printfHigh(String text, Object... args) {
        if (this.verbosity.isHigh()) {
            printf(text, args);
        }
    }

    /**
     * Prints a line of text to the log file if logging is activated.
     * @param text the line to be printed
     */
    protected void printLog(String text) {
        if (this.logging) {
            this.logWriter.println(text);
        }
    }

    /**
     * Prints an error message, followed by the help message, and exits.
     */
    protected void printError(String message, boolean showHelp) {
        if (showHelp) {
            printHelp();
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // proceed
        }
        System.err.println();
        System.err.println("Error: " + message);
        if (this.logWriter != null) {
            this.logWriter.close();
        }
        System.exit(0);
    }

    /**
     * Prints a help message for this command. The default help message consists
     * of {@link #getUsageMessage()}, followed by a list of options and their
     * explanations, obtained from {@link CommandLineOption#getDescription()}.
     */
    protected void printHelp() {
        System.out.println(getUsageMessage());
        if (this.optionsList.size() > 0) {
            System.out.println("Options: ");
        }
        // for layouting, compute the maximum prefix length
        String[] prefix = new String[this.optionsList.size()];
        int maxPrefixLength = 0;
        for (int i = 0; i < this.optionsList.size(); i++) {
            CommandLineOption option = this.optionsList.get(i);
            if (option.hasParameter()) {
                prefix[i] =
                    OPTIONS_PREFIX + option.getName() + " "
                        + option.getParameterName() + " ";
            } else {
                prefix[i] = OPTIONS_PREFIX + option.getName() + " ";
            }
            maxPrefixLength = Math.max(maxPrefixLength, prefix[i].length());
        }
        String emptyPrefix = Groove.pad("", maxPrefixLength);
        for (int i = 0; i < this.optionsList.size(); i++) {
            CommandLineOption option = this.optionsList.get(i);
            String[] description = option.getDescription();
            if (description.length > 0) {
                System.out.println(Groove.pad(prefix[i], maxPrefixLength)
                    + description[0]);
                for (int j = 1; j < description.length; j++) {
                    System.out.println(emptyPrefix + description[j]);
                }
            }
        }
    }

    /**
     * Checks whether a given option was actually invoked from the command line
     * or not. Assumes that an external store of command line options exists
     * (of which the internal store is a mirror).
     */
    protected boolean isOptionActive(CommandLineOption option) {
        return this.activeOptions.containsValue(option);
    }

    /**
     * Time of invocations, initialised at construction time.
     */
    protected final Date invocationTime = new Date();
    /**
     * The verbosity with which the generation is carried out.
     */
    private Verbosity verbosity = Verbosity.MEDIUM;
    /**
     * Flag to indicate that the generation process should be logged.
     */
    private boolean logging;
    /** The directory into which the log is written. */
    private String logDirName;
    /** The actual log file. */
    private PrintWriter logWriter;

    /** The list of command line arguments. */
    private final List<String> args;

    /** Filter for log files. */
    protected final ExtensionFilter logFilter = FileType.LOG_FILTER;

    /** List of command options classes. */
    protected final List<CommandLineOption> optionsList =
        new LinkedList<CommandLineOption>();
    /**
     * List of instantiated options. This is initialised in
     * {@link #processArguments()}.
     */
    protected final Map<String,CommandLineOption> activeOptions =
        new HashMap<String,CommandLineOption>();

    /**
     * Command line option to specify an output file.
     */
    static protected class OutputOption implements CommandLineOption {
        /** Name of the option, as used on the command line. */
        static public final String NAME = "o";
        /** Name of the option parameter. */
        static public final String PARAMETER_NAME = "file";

        public String getName() {
            return NAME;
        }

        public boolean hasParameter() {
            return true;
        }

        public void parse(String parameter) {
            this.parameter = parameter;
        }

        /**
         * Returns the actual command line parameter, or <code>null</code> if
         * the option is not active.
         */
        public String getOutputFileName() {
            return this.parameter;
        }

        public String[] getDescription() {
            return new String[] {"Save the result to '" + getParameterName()
                + "' (default format GXL)"};
        }

        public String getParameterName() {
            return PARAMETER_NAME;
        }

        /**
         * The actual parameter value of the option.
         */
        private String parameter;
    }

    /**
     * Command line option to specify the verbosity level of the standard output
     * at run time.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class VerbosityOption implements CommandLineOption {
        /** Name of the verbosity command line option. */
        static public final String NAME = "v";
        /** Parameter name of the verbosity command line option. */
        static public final String PARAMETER_NAME = "val";

        /**
         * Constructs an option that works upon a given generator.
         */
        public VerbosityOption(CommandLineTool tool) {
            this.tool = tool;
        }

        public String[] getDescription() {
            return new String[] {"Set the verbosity to '" + getParameterName()
                + "', in the range " + Verbosity.LOWEST + "-"
                + Verbosity.HIGHEST + " (default = "
                + Verbosity.MEDIUM.ordinal() + ")"};
        }

        public String getParameterName() {
            return PARAMETER_NAME;
        }

        public String getName() {
            return NAME;
        }

        public boolean hasParameter() {
            return true;
        }

        /**
         * Attempts to interpret the parameter as a verbosity value. Values
         * outside the valid range or non-numeric values give rise to an
         * exception.
         */
        public void parse(String parameter) throws IllegalArgumentException {
            int verbosity;
            try {
                verbosity = Integer.parseInt(parameter);
            } catch (NumberFormatException exc) {
                throw new IllegalArgumentException("verbosity value '"
                    + parameter + "' must be numeric");
            }
            if (verbosity < Verbosity.LOWEST || verbosity > Verbosity.HIGHEST) {
                throw new IllegalArgumentException("'" + parameter
                    + "' is outside the range of valid verbosity values");
            }
            this.tool.setVerbosity(Verbosity.values()[verbosity]);
        }

        /**
         * The generator upon which this option works.
         */
        private final CommandLineTool tool;
    }

    /**
     * Command line option to specify a log file.
     * @author Arend Rensink
     * @version $Revision $
     */
    static protected class LogOption implements CommandLineOption {
        /** Name of the log option. */
        static public final String NAME = "l";
        /** Parameter name of the log option. */
        static public final String PARAMETER_NAME = "dir";

        /**
         * Constructs an option that works upon a given generator.
         */
        public LogOption(CommandLineTool tool) {
            this.tool = tool;
        }

        public String[] getDescription() {
            return new String[] {"Log the generation process, writing the file to the directory '"
                + getParameterName() + "'"};
        }

        public String getParameterName() {
            return PARAMETER_NAME;
        }

        public String getName() {
            return NAME;
        }

        public boolean hasParameter() {
            return true;
        }

        public void parse(String parameter) {
            this.tool.setLogging(parameter);
        }

        /**
         * The generator upon which this option works.
         */
        private final CommandLineTool tool;
    }
}
