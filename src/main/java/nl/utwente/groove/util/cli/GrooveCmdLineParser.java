/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util.cli;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import picocli.CommandLine.ParameterException;

/**
 * Specialised command-line parser (wrapping a picocli {@link CommandLine})
 * that provides better help support.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GrooveCmdLineParser {
    /**
     * Constructs an instance for a given options object.
     * @param appName the name of the application, printed in the usage message
     * @param bean the options object
     */
    public GrooveCmdLineParser(String appName, Object bean) {
        this.appName = appName;
        this.commandLine = new CommandLine(bean);
        this.commandLine.setCommandName(appName);
        // do not let (e.g.) "-ef" be mistaken for a cluster of "-e" and "-f"
        this.commandLine.setPosixClusteredShortOptionsAllowed(false);
    }

    /** Returns a single-line description of the tool usage. */
    public String getUsageLine() {
        StringBuilder result = new StringBuilder();
        result.append("Usage: \"");
        result.append(this.appName);
        result.append(getSingleLineUsage());
        result.append("\"");
        return result.toString();
    }

    /** Prints a help message, consisting of the usage line and arguments and options descriptions. */
    public void printHelp() {
        System.out.println(getUsageLine());
        System.out.println();
        PrintWriter w = new PrintWriter(System.out);
        // determine the length of the option + metavar first
        int len = 0;
        for (PositionalParamSpec arg : getArguments()) {
            len = Math.max(len, getNameAndMeta(arg).length());
        }
        for (OptionSpec option : getOptions()) {
            len = Math.max(len, getNameAndMeta(option).length());
        }

        // then print
        if (!getArguments().isEmpty()) {
            w.println("ARGUMENTS");
            for (PositionalParamSpec arg : getArguments()) {
                printUsage(w, getNameAndMeta(arg), getDescription(arg.description()), len);
            }
            w.println();
        }
        if (!getOptions().isEmpty()) {
            w.println("OPTIONS");
            for (OptionSpec option : getOptions()) {
                printUsage(w, getNameAndMeta(option), getDescription(option.description()), len);
            }
            w.println();
        }

        w.flush();
    }

    /** Prints the aligned name + description of a single option or argument. */
    private void printUsage(PrintWriter w, String nameAndMeta, String usage, int len) {
        w.print(' ');
        w.print(nameAndMeta);
        for (int i = nameAndMeta.length(); i < len; i++) {
            w.print(' ');
        }
        w.print(" : ");
        boolean first = true;
        for (String line : wrapLines(usage, USAGE_WIDTH - len - 4)) {
            if (!first) {
                for (int i = 0; i < len + 4; i++) {
                    w.print(' ');
                }
            }
            w.println(line);
            first = false;
        }
    }

    /** Joins the (potentially multi-part) description of an option or argument. */
    private String getDescription(String[] description) {
        return String.join("\n", description);
    }

    /** Breaks a description into lines, wrapping at a given maximum length. */
    static private List<String> wrapLines(String text, int maxLength) {
        List<String> result = new ArrayList<>();
        for (String line : text.split("\n")) {
            while (line.length() > maxLength) {
                int ix = line.lastIndexOf(' ', maxLength);
                if (ix <= 0) {
                    ix = maxLength;
                }
                result.add(line.substring(0, ix));
                line = line.substring(ix).trim();
            }
            result.add(line);
        }
        return result;
    }

    /** Returns a single-line string describing the usage of a command. */
    public String getSingleLineUsage() {
        StringBuilder result = new StringBuilder();
        for (OptionSpec option : getOptions()) {
            appendSingleLineOption(result, option, true);
        }
        int optArgCount = 0;
        // nest the argument meta-variables
        for (PositionalParamSpec arg : getArguments()) {
            result.append(' ');
            if (!arg.required()) {
                result.append('[');
                optArgCount++;
            }
            result.append(arg.paramLabel());
            if (arg.isMultiValue()) {
                result.append(" ...");
            }
        }
        for (int i = 0; i < optArgCount; i++) {
            result.append(']');
        }
        return result.toString();
    }

    /** Appends the single-line usage of an option, with a parameter controlling
     * closing bracket printing. */
    protected void appendSingleLineOption(StringBuilder result, OptionSpec option,
                                          boolean closeOpt) {
        result.append(' ');
        boolean multiOccurrences = Map.class.isAssignableFrom(option.type());
        boolean brackets = !option.required() || multiOccurrences;
        if (brackets) {
            result.append('[');
        }
        result.append(getNameAndMeta(option));
        if (brackets && closeOpt) {
            result.append(']');
        }
        if (multiOccurrences) {
            result.append(option.required()
                ? '+'
                : '*');
        }
    }

    /** Returns the name of an option, followed by its meta-variable if it has one. */
    protected String getNameAndMeta(OptionSpec option) {
        String result = option.names()[0];
        if (option.arity().max() > 0) {
            result += " " + option.paramLabel();
        }
        return result;
    }

    /** Returns the meta-variable of an argument. */
    protected String getNameAndMeta(PositionalParamSpec arg) {
        return arg.paramLabel();
    }

    /**
     * Parses the given command-line arguments into the options object
     * passed in at construction time.
     * @throws CmdLineException if the arguments could not be parsed; the
     * exception message includes the usage line
     */
    public void parseArgument(String... args) throws CmdLineException {
        try {
            this.commandLine.parseArgs(args);
        } catch (ParameterException e) {
            throw new CmdLineException(String
                .format("Error: %s%n%s", e.getMessage(), getUsageLine()), e);
        }
    }

    /** Moves the option with a given name to the final position
     * in the usage message. */
    public void setLastOption(String name) {
        this.lastOption = name;
    }

    /** Name of the option to be listed last in the usage message, if any. */
    private @Nullable String lastOption;

    /** Returns the options of the wrapped command, in the order
     * in which they are listed in the usage message. */
    protected List<OptionSpec> getOptions() {
        List<OptionSpec> result = new ArrayList<>(getCommandSpec().options());
        Comparator<OptionSpec> byName = Comparator.comparing(o -> o.names()[0]);
        String last = this.lastOption;
        if (last == null) {
            result.sort(byName);
        } else {
            result
                .sort(Comparator
                    .<OptionSpec,Boolean>comparing(o -> last.equals(o.names()[0]))
                    .thenComparing(byName));
        }
        return result;
    }

    /** Returns the positional arguments of the wrapped command. */
    protected List<PositionalParamSpec> getArguments() {
        return getCommandSpec().positionalParameters();
    }

    /** Returns the command model of the wrapped command line. */
    protected CommandSpec getCommandSpec() {
        return this.commandLine.getCommandSpec();
    }

    /** The wrapped picocli command line. */
    private final CommandLine commandLine;

    /** Name of the application of which the command-line options are parsed. */
    private final String appName;

    /** Width (in characters) at which the help message is wrapped. */
    static private final int USAGE_WIDTH = 100;
}
