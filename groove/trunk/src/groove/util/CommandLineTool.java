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
package groove.util;

import groove.io.ExtensionFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    // The verbosity constants are taken from <tt>VerbosityOption</tt>.
    /**
     * Low verbosity.
     */
    static public final int LOW_VERBOSITY = VerbosityOption.LOW_VERBOSITY;
    /**
     * Medium verbosity.
     */
    static public final int MEDIUM_VERBOSITY = VerbosityOption.MEDIUM_VERBOSITY;
    /**
     * High verbosity.
     */
    static public final int HIGH_VERBOSITY = VerbosityOption.HIGH_VERBOSITY;
    /** Symbolic char constant. */
    static protected final char SPACE = ' ';
    /** Symbolic char constant. */
    static protected final char COLON = ':';
    /** Symbolic char constant. */
    static protected final char UNDERSCORE = '_';
    /** Symbolic char constant. */
    static protected final char DASH = '-';

    /**
     * Constructs an instance of the tool, with a given list of command line arguments.
     */
    public CommandLineTool(List<String> args) {
        this.args = args;
        if (supportsOutputOption()) {
            addOption(new OutputOption()); 
        }
        if (supportsVerbosityOption()) {
            addOption(new VerbosityOption(this)); 
        }
        if (supportsLogOption()) {
            addOption(new LogOption(this)); 
        }
    }
    
    /**
     * Adds a command line option class to the list of option classes.
     */
    protected void addOption(CommandLineOption option) {
        optionsList.add(option);
    }

    /**
     * Goes through the list of command line arguments and tries to find command line options.
     * The options and their parameters are subsequently removed from the argument list.
     * If an option cannot be parsed, the method prints an error message and terminates the program. 
     */
    protected void processArguments() {
        List<String> argsList = getArgs();
        while (argsList.size() > 0 && argsList.get(0).startsWith(OPTIONS_PREFIX)) {
            String optionName = argsList.get(0).substring(OPTIONS_PREFIX.length());
            argsList.remove(0);
            boolean validOption = false;
            Iterator<CommandLineOption> optionsIter = optionsList.iterator();
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
                        printError(exc.getMessage());
                    }
                    activeOptions.put(option.getClass(), option);
                }
            }
            if (!validOption) {
                printError("Unknown option " + optionName);
            }
        }
    }
    
    /** Initializes the log file. */
    protected void startLog() {
        if (isLogging()) {
            try {
                String logFileName = logFilter.addExtension(getLogFileName());
                setLogWriter(new PrintWriter(new FileWriter(new File(getLogDirName(), logFileName))));
            } catch (IOException e) {
                printError("Can't create log file in "+getLogDirName()+": "+e.getMessage());
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
     * Callback method to check whether the output command line option is supported.
     * This implementation returns <tt>true</tt> always.
     */
    protected boolean supportsOutputOption() {
        return true;
    }

    /**
     * Callback method to check whether the verbosity command line option is supported.
     * This implementation returns <tt>true</tt> always.
     */
    protected boolean supportsVerbosityOption() {
        return true;
    }

    /**
     * Callback method to check whether the log command line option is supported.
     * This implementation returns <tt>true</tt> always.
     */
    protected boolean supportsLogOption() {
        return true;
    }
    
    /**
     * Returns a string serving as an identifier for this invocation of the command line tool.
     * This implementation constructs a string from the time of construction,
     * by replacing all ' ' by '_' and ':' by '-'.
     */
    protected String getId() {
        return invocationTime.toString().replace(SPACE, UNDERSCORE).replace(COLON, DASH);
    }
    
    /**
     * Returns a usage message for the command line tool.
     */
    protected String getUsageMessage() {
        return "Usage: "+this.getClass().getName()+" [options]";
    }

    /**
     * Returns a name by querying for an active {@link OutputOption}.
     * @see OutputOption#getOutputFileName()
     */
    protected String getOutputFileName() {
    	OutputOption option = getActiveOption(OutputOption.class);
    	return option == null ? null : option.getOutputFileName();
    }

    /**
     * Indicates that the exploration should be logged, and sets the directory
     * where the log file should go.
     * The name of the log file will be constructed from the grammar and the time of invocation;
     * it will receive the extension <tt>{@link #LOG_FILE_EXTENSION}</tt>.
     */
    protected void setLogging(String logDirName) {
        this.logging = true;
        this.logDirName = logDirName;
    }

    /**
     * Callback method to generate a log file name, without extension.
     * Called from <tt>{@link #setLogging}</tt>.
     * This implementation constructs a name by concatenating 
     * the name of the class and an identifying string obtained from 
     * <tt>{@link #getId()}</tt>.
     */
    protected String getLogFileName() {
        return this.getClass().getName()+ID_SEPARATOR+getId();
    }
    
    /**
     * Returns the log directory name set in the logging option, if any.
     */
    protected String getLogDirName() {
        return logDirName;
    }

    /**
     * Sets the logfile to a given value.
     * If logging is enabled, all log messages will (from now on) be written to this file.
     */
    protected void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }
    /**
     * Returns the log file; <tt>null</tt> if logging has not been activated.
     */
    protected PrintWriter getLogWriter() {
        return logWriter;
    }
    
    /**
     * Indicates if logging has been enabled.
     */
    protected boolean isLogging() {
        return logging;
    }
    
    /**
     * Sets the verbosity level.
     * Only allowed if the verbosity option is supported.
     * @param verbosity the verbosity level; should be a legal verbosity value.
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Returns the verbosity level.
     * The default level is <tt>MEDIUM_VERBOSITY</tt>
     */
    public int getVerbosity() {
        return verbosity;
    }
    
    /** Returns the command line arguments. */
    protected List<String> getArgs() {
        return args;
    }

    /**
     * Prints an empty line to the standard output and, if logging is activated, to the log file.
     */
    protected void println() {
        System.out.println();
        if (logging) {
            logWriter.println();
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is activated, to the log file.
     */
    protected void println(String text) {
        System.out.println(text);
        if (logging) {
            logWriter.println(text);
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is activated, to the log file.
     */
    protected void printf(String text, Object... args) {
        System.out.printf(text, args);
        if (logging) {
            logWriter.printf(text, args);
        }
    }

    /**
     * Prints text (not ended by a line break)  to the standard output and, if logging is activated, to the log file.
     */
    protected void print(String text) {
        System.out.print(text);
        if (logging) {
            logWriter.print(text);
        }
    }

    /**
     * Prints an empty line to the standard output and, if logging is activated, to the log file,
     * provided the verbosity of the tool is at least the given verbosity.
     */
    protected void println(int verbosity) {
        if (getVerbosity() >= verbosity) {
            println();
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is activated, to the log file,
     * provided the verbosity of the tool is at least the given verbosity.
     */
    protected void println(String text, int verbosity) {
        if (getVerbosity() >= verbosity) {
            println(text);
        }
    }

    /**
     * Prints text (not ended by a line break)  to the standard output and, if logging is activated, to the log file,
     * provided the verbosity of the tool is at least the given verbosity.
     */
    protected void print(String text, int verbosity) {
        if (getVerbosity() >= verbosity) {
            print(text);
        }
    }

    /**
     * Prints a line of text to the standard output and, if logging is activated, to the log file,
     * provided the verbosity of the tool is at least <tt>{@link #MEDIUM_VERBOSITY}</tt>.
     * Convenience method for <tt>println(text, MEDIUM_VERBOSITY)</tt>.
     */
    protected void printlnMedium(String text) {
        println(text, MEDIUM_VERBOSITY);
    }

    /**
     * Prints a line of text to the standard output and, if logging is activated, to the log file,
     * provided the verbosity of the tool is at least <tt>{@link #HIGH_VERBOSITY}</tt>.
     * Convenience method for <tt>println(text, HIGH_VERBOSITY)</tt>.
     */
    protected void printlnHigh(String text) {
        println(text, HIGH_VERBOSITY);
    }

    /**
     * Prints a line of text to the log file if logging is activated.
     * @param text the line to be printed
     */
    protected void printLog(String text) {
        if (logging) {
            logWriter.println(text);
        }
    }

    /**
     * Prints an error message, followed by the help message, and exits.
     */
    protected void printError(String message) {
        printHelp();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // proceed
        }
        System.err.println();
        System.err.println("Error: " + message);
        if (logWriter != null) {
            logWriter.close();
        }
        System.exit(0);
    }

    /**
     * Prints a help message for this command.
     * The default help message consists of {@link #getUsageMessage()}, followed
     * by a list of options and their explanations, obtained from 
     * {@link CommandLineOption#getDescription()}.
     */
    protected void printHelp() {
        System.out.println(getUsageMessage());
        if (optionsList.size() > 0) {
        	System.out.println("Options: ");
        }
        // for layouting, compute the maximum prefix length
        String[] prefix = new String[optionsList.size()];
        int maxPrefixLength = 0;
        for (int i = 0; i < optionsList.size(); i++) {
            CommandLineOption option = optionsList.get(i);
            prefix[i] = OPTIONS_PREFIX + option.getName() + " " + option.getParameterName() + " ";
            maxPrefixLength = Math.max(maxPrefixLength, prefix[i].length());
        }
        String emptyPrefix = Groove.pad("", maxPrefixLength);
        for (int i = 0; i < optionsList.size(); i++) {
            CommandLineOption option = optionsList.get(i);
            String[] description = option.getDescription();
            System.out.println(Groove.pad(prefix[i], maxPrefixLength) + description[0]);
            for (int j = 1; j < description.length; j++) {
                System.out.println(emptyPrefix + description[j]);
            }
        }
    }

    /**
	 * Returns an <i>active option</i> of a given class type, i.e.,
	 * those that were actually invoked from the command line.
	 * @param <X> the type of the option
	 * @param optionClass the class type
	 * @return the option of type <code>X</code>, if it was invoked; <code>null</code> otherwise.
	 */
	protected <X extends CommandLineOption> X getActiveOption(Class<X> optionClass) {
	    return (X) activeOptions.get(optionClass);
	}

	/**
     * Time of invocations, initialised at construction time.
     */
    protected final Date invocationTime = new Date();
    /**
     * The verbosity with which the generation is carried out.
     */
    private int verbosity = VerbosityOption.LOW_VERBOSITY;
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
    protected final ExtensionFilter logFilter = new ExtensionFilter("Log files", LOG_FILE_EXTENSION);

    /** List of command options classes. */
    protected final List<CommandLineOption> optionsList = new LinkedList<CommandLineOption>();
    /**
     * List of instantiated options. This is initialised in {@link #processArguments()}.
     */
    protected final Map<Class<? extends CommandLineOption>, CommandLineOption> activeOptions = new HashMap<Class<? extends CommandLineOption>, CommandLineOption>();
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
	     * Returns the actual command line parameter, or <code>null</code> if the
	     * option is not active.
	     */
	    public String getOutputFileName() {
	    	return parameter;
	    }
	
	    public String[] getDescription() {
	        return new String[] {
	             "Save the result to '" + getParameterName() + "' (GXL format, default extension .gxl)" };
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
     * Command line option to specify the verbosity level of the standard output at run time.
     * @author Arend Rensink
     * @version $Revision $
     */
	static protected class VerbosityOption implements CommandLineOption {
		/** Name of the verbosity command line option. */
	    static public final String NAME = "v";
		/** Parameter name of the verbosity command line option. */
	    static public final String PARAMETER_NAME = "val";
	    /** Low verbosity argument value for the command option. */
	    static public final int LOW_VERBOSITY = 0;
	    /** Medium verbosity argument value for the command option. */
	    static public final int MEDIUM_VERBOSITY = 1;
	    /** High verbosity argument value for the command option. */
	    static public final int HIGH_VERBOSITY = 2;
	    /** Default verbosity argument value for the command option; set to medium. */
	    static public final int DEFAULT_VERBOSITY = MEDIUM_VERBOSITY;
	
	    /**
	     * Constructs an option that works upon a given generator.
	     */
	    public VerbosityOption(CommandLineTool tool) {
	        this.tool = tool;
	    }
	
	    public String[] getDescription() {
	        return new String[] {
	            "Set the verbosity to '"
	                + getParameterName()
	                + "', in the range "
	                + LOW_VERBOSITY
	                + "-"
	                + HIGH_VERBOSITY
	                + " (default = "
	                + DEFAULT_VERBOSITY
	                + ")" };
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
	     * Attempts to interpret the parameter as a verbosity value.
	     * Values outside the valid range or non-numeric values give rise to an exception.
	     */
	    public void parse(String parameter) throws IllegalArgumentException {
	        int verbosity = MEDIUM_VERBOSITY;
	        try {
	            verbosity = Integer.parseInt(parameter);
	        } catch (NumberFormatException exc) {
	            throw new IllegalArgumentException("verbosity value '" + parameter + "' must be numeric");
	        }
	        if (verbosity < LOW_VERBOSITY || verbosity > HIGH_VERBOSITY) {
	            throw new IllegalArgumentException(
	                "'" + parameter + "' is outside the range of valid verbosity values");
	        }
	        tool.setVerbosity(verbosity);
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
	        return new String[] {
	             "Log the generation process, writing the file to the directory '" + getParameterName() + "'" };
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
	        tool.setLogging(parameter);
	    }
	
	    /**
	     * The generator upon which this option works.
	     */
	    private final CommandLineTool tool;
	}
}
