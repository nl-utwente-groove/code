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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ResourceBundle;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.spi.MapOptionHandler;
import org.kohsuke.args4j.spi.OptionHandler;

/**
 * Specialised command-line parser that provides better help support.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GrooveCmdLineParser extends CmdLineParser {
    /** 
     * Constructs an instance for a given options object.
     * @param appName the name of the application, printed in the usage message
     * @param bean the options object
     */
    public GrooveCmdLineParser(String appName, Object bean) {
        super(bean);
        this.appName = appName;
    }

    /**
     * Called when the help option has been invoked.
     * This causes the parser to stop parsing and never give a {@link CmdLineException}. 
     */
    public void setHelp() {
        this.help = true;
        stopOptionParsing();
    }

    /** Returns a single-line description of the tool usage. */
    public String getUsageLine() {
        StringBuilder result = new StringBuilder();
        OutputStream stream = new ByteArrayOutputStream();
        printSingleLineUsage(stream);
        result.append("Usage: \"");
        result.append(this.appName);
        result.append(stream.toString());
        result.append("\"");
        return result.toString();
    }

    /** Prints a help message, consisting of the usage line and arguments and options descriptions. */
    public void printHelp() {
        System.out.println(getUsageLine());
        System.out.println();
        setUsageWidth(100);
        printUsage(System.out);
    }

    /* Copied from superclass; Adds a few intermediate lines. */
    @Override
    public void printUsage(Writer out, ResourceBundle rb,
            OptionHandlerFilter filter) {
        PrintWriter w = new PrintWriter(out);
        // determine the length of the option + metavar first
        int len = 0;
        for (OptionHandler<?> h : getArguments()) {
            int curLen = getPrefixLen(h, rb);
            len = Math.max(len, curLen);
        }
        for (OptionHandler<?> h : getOptions()) {
            int curLen = getPrefixLen(h, rb);
            len = Math.max(len, curLen);
        }

        // then print
        if (!getArguments().isEmpty()) {
            w.println("ARGUMENTS");
            for (OptionHandler<?> h : getArguments()) {
                printOption(w, h, len, rb, filter);
            }
            w.println();
        }
        if (!getOptions().isEmpty()) {
            w.println("OPTIONS");
            for (OptionHandler<?> h : getOptions()) {
                printOption(w, h, len, rb, filter);
            }
            w.println();
        }

        w.flush();
    }

    /* Copied from superclass (unfortunately not accessible). */
    private int getPrefixLen(OptionHandler<?> h, ResourceBundle rb) {
        if (h.option.usage().length() == 0) {
            return 0;
        }

        return h.getNameAndMeta(rb).length();
    }

    /** Returns a single-line string describing the usage of a command. */
    public String getSingleLineUsage() {
        Writer stringWriter = new StringWriter();
        printSingleLineUsage(stringWriter, null);
        return stringWriter.toString();
    }

    /* First prints options, then arguments. */
    @Override
    public void printSingleLineUsage(Writer w, ResourceBundle rb) {
        PrintWriter pw = new PrintWriter(w);
        for (OptionHandler<?> h : getOptions()) {
            printSingleLineOption(pw, h, rb, true);
        }
        int optArgCount = 0;
        // nest the argument meta-variables
        for (OptionHandler<?> h : getArguments()) {
            printSingleLineOption(pw, h, rb, false);
            if (!h.option.required()) {
                optArgCount++;
            }
        }
        for (int i = 0; i < optArgCount; i++) {
            pw.print(']');
        }
        pw.flush();
    }

    /** Modified from superclass to add parameter controlling
     * closing bracket printing. */
    protected void printSingleLineOption(PrintWriter pw, OptionHandler<?> h,
            ResourceBundle rb, boolean closeOpt) {
        pw.print(' ');
        boolean multiOccurrences =
            (h instanceof MapOptionHandler) || h.option.isMultiValued();
        boolean brackets = !h.option.required() || multiOccurrences;
        if (brackets) {
            pw.print('[');
        }
        pw.print(h.getNameAndMeta(rb));
        if (h.option.isArgument() && h.option.isMultiValued()) {
            pw.print(" ...");
        }
        if (brackets && closeOpt) {
            pw.print(']');
        }
        if (!h.option.isArgument() && multiOccurrences) {
            pw.print(h.option.required() ? '+' : '*');
        }
    }

    /* Does not generate an exception if the help has been invoked. */
    @Override
    public void parseArgument(String... args) throws CmdLineException {
        try {
            super.parseArgument(args);
        } catch (CmdLineException e) {
            if (!this.help) {
                throw new CmdLineException(this, String.format("Error: %s%n%s",
                    e.getMessage(), getUsageLine()));
            }
        }
    }

    /** Name of the application of which the command-line options are parsed. */
    private final String appName;
    /** Flag indicating that the help option has been invoked. */
    private boolean help;
}
