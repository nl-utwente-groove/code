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
package groove.util.cli;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import groove.explore.Verbosity;

/**
 * Command-line tool superclass that implements help and verbosity options.
 * A prototypical implementation should have the following static methods:<p>
 * <pre>
   static public void main(String[] args) {
       GrooveCmdLineTool.tryExecute(MyClass.class, args);
   }

   static public T execute(String[] args) throws Exception {
       return new MyClass(args).start();
   }
 * </pre>
 * @param <T> return type for the {@link #start()} method.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class GrooveCmdLineTool<T> {
    /**
     * Constructs an instance of a tool,
     * with a given application name.
     */
    public GrooveCmdLineTool(String appName) {
        this.parser = createParser(appName);
    }

    /**
     * Constructs an instance of a tool,
     * with a given application name and list of arguments.
     * Parsing the arguments is deferred to {@link #start()},
     * in order to avoid that they are overridden by
     * default initialisers in the field declarations.
     */
    public GrooveCmdLineTool(String appName, String... args) {
        this(appName);
        this.args = args;
    }

    /**
     * Starts the tool, parses the arguments and returns the result.
     * If the help option has been invoked (see {@link #isHelp()},
     * prints the help message and exits immediately with return value {@code null};
     * otherwise, invokes {@link #run()} and passes up its result (return value
     * or exception).
     * @throws CmdLineException if such an exception occurred while parsing the arguments
     * @throws Exception if the exception is thrown by {@link #run()}.
     */
    public final @Nullable T start() throws Exception {
        parseArguments();
        if (isHelp()) {
            getParser().printHelp();
            return null;
        } else {
            return run();
        }
    }

    /**
     * Callback method implementing the actual functionality of the tool.
     * This method can be invoked instead of {@link #start()} to bypass the
     * help option.
     */
    protected abstract T run() throws Exception;

    /** Indicates whether the help option has been invoked. */
    protected final boolean isHelp() {
        return this.help;
    }

    @Option(name = HelpHandler.NAME, usage = HelpHandler.USAGE,
        handler = HelpHandler.class) private boolean help;

    /** Returns the verbosity of the tool. */
    protected final Verbosity getVerbosity() {
        return this.verbosity;
    }

    /** Sets the verbosity of the tool to a given level. */
    protected final void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    @Option(name = VerbosityHandler.NAME, metaVar = VerbosityHandler.VAR,
        usage = VerbosityHandler.USAGE,
        handler = VerbosityHandler.class) private Verbosity verbosity = Verbosity.MEDIUM;

    /**
     * Returns the parser used for parsing the command-line arguments
     * passed in to the constructor.
     */
    protected final GrooveCmdLineParser getParser() {
        return this.parser;
    }

    private final GrooveCmdLineParser parser;

    /** Callback factory name for the command-line parser. */
    protected GrooveCmdLineParser createParser(String appName) {
        return new GrooveCmdLineParser(appName, this);
    }

    /**
     * Callback method to apply the parser of this tool
     * to the list of arguments passed in at construction time.
     * Calling this method more than once will have no effect.
     * @throws CmdLineException if such an exception occurred while parsing
     */
    protected void parseArguments() throws CmdLineException {
        if (this.args != null) {
            getParser().parseArgument(this.args);
            this.args = null;
        }
    }

    /** The arguments originally provided for the tool. */
    private String[] args;

    /**
     * Convenience method for {@link #emit(Verbosity, String, String...)}
     * with verbosity {@link Verbosity#MEDIUM}.
     */
    protected final void emit(String format, Object... args) {
        emit(Verbosity.MEDIUM, format, args);
    }

    /**
     * Prints a message to standard output,
     * provided the verbosity level is at least equal to the first parameter.
     * @param min minimum verbosity level required in order to print the message.
     * @param format Format part of the message, with syntax as in {@link String#format(String, Object...)}
     * @param args arguments to the message
     */
    protected void emit(Verbosity min, String format, Object... args) {
        if (min.compareTo(getVerbosity()) <= 0) {
            System.out.printf(format, args);
        }
    }

    /**
     * Tries to invoke the static method "execute" of a given {@link GrooveCmdLineTool}
     * implementation, with the given arguments, and
     * handles the exceptions by printing the stack trace (for {@link RuntimeException}s)
     * or printing the error on the error output (for other exceptions).
     * Exits with {@link System#exit(int)}.
     * This method can be used to get uniform functionality across command-line tools.
     * @throws IllegalArgumentException if the class passed in does not have a
     * static method with the right signature
     */
    public static void tryExecute(Class<?> claz, String... args) throws IllegalArgumentException {
        try {
            Method execute = claz.getMethod("execute", String[].class);
            execute.invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause instanceof RuntimeException) {
                cause.printStackTrace();
            } else {
                System.err.println(cause.getMessage());
            }
            System.exit(1);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        waitForWindows();
        System.exit(0);
    }

    static private void waitForWindows() {
        boolean exit;
        do {
            exit = true;
            for (Window win : Window.getWindows()) {
                if (win.isShowing()) {
                    exit = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                    break;
                }
            }
        } while (!exit);
    }
}
