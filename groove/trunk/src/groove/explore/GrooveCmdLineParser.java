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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ResourceBundle;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.spi.OptionHandler;

/**
 * Overwrites 
 * @author Arend Rensink
 * @version $Revision $
 */
public class GrooveCmdLineParser extends CmdLineParser {
    /** Constructor from superclass. */
    public GrooveCmdLineParser(Object bean) {
        super(bean);
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

        w.println("ARGUMENTS");
        for (OptionHandler<?> h : getArguments()) {
            printOption(w, h, len, rb, filter);
        }
        w.println("\nOPTIONS");
        for (OptionHandler<?> h : getOptions()) {
            printOption(w, h, len, rb, filter);
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

    /* Modified from superclass to add parameter controlling
     * closing bracket printing. */
    private void printSingleLineOption(PrintWriter pw, OptionHandler<?> h,
            ResourceBundle rb, boolean closeOpt) {
        pw.print(' ');
        if (!h.option.required()) {
            pw.print('[');
        }
        pw.print(h.getNameAndMeta(rb));
        if (h.option.isMultiValued()) {
            pw.print(" ...");
        }
        if (!h.option.required() && closeOpt) {
            pw.print(']');
        }
    }
}
