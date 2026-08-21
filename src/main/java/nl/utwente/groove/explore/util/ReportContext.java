/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.explore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.cli.Verbosity;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Shared context of the reporters that emit a run description:
 * the command-line arguments, the start time of the exploration,
 * and messages added by other reporters (such as the names of saved files).
 * Must precede the reporters that use it in the composite, so that the
 * start time is recorded before they start.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class ReportContext extends AExplorationReporter {
    /** Constructs a context for a given array of command-line arguments. */
    public ReportContext(String[] args) {
        this.args = args;
    }

    @Override
    public void start(Exploration exploration, GTS gts) {
        super.start(exploration, gts);
        this.startTime = new Date();
        this.messages.clear();
    }

    /** Returns the time at which the exploration was started. */
    public Date getStartTime() {
        return this.startTime;
    }

    /** Returns the text announcing the parameters of the exploration, one line per parameter. */
    public String getHeader() {
        StringBuilder result = new StringBuilder();
        var grammar = getGTS().getGrammar();
        result.append(String.format("Grammar:\t%s%n", grammar.getName()));
        result.append(String.format("Start graph:\t%s%n", grammar.getStartGraph().getName()));
        result.append(String.format("Control:\t%s%n", grammar.getControl().getQualName()));
        result
            .append(String
                .format("Exploration:\t%s%n", getExploration().getType().getIdentifier()));
        result
            .append(String
                .format("Max mem (-Xmx):\t%sM%n", Runtime.getRuntime().maxMemory() / B_PER_MB));
        result.append(String.format("Timestamp:\t%s%n", this.startTime));
        String[] args = Arrays.copyOf(this.args, this.args.length);
        for (int i = 0; i < args.length; i++) {
            if (args[i].indexOf(' ') >= 0) {
                args[i] = StringHandler.toQuoted(args[i], '"');
            }
        }
        result.append(String.format("Command line:\t%s%n", Strings.toString(args, "", "", " ")));
        return result.toString();
    }

    /**
     * Adds a message to be included in the report, if the verbosity is at least at
     * a given level.
     * @param minVerbosity the minimum verbosity at which the message is emitted
     * @param format the string format for the message
     * @param args the format arguments for the message
     */
    public void add(Verbosity minVerbosity, String format, Object... args) {
        this.messages.add(new Message(minVerbosity, String.format(format, args)));
    }

    /**
     * Adds a message to be included in the report, if the verbosity is at least
     * {@link Verbosity#MEDIUM}.
     * @param format the string format for the message
     * @param args the format arguments for the message
     */
    public void add(String format, Object... args) {
        add(Verbosity.MEDIUM, format, args);
    }

    /** Returns the concatenation of the added messages that are emitted at a given verbosity. */
    public String getMessages(Verbosity verbosity) {
        StringBuilder result = new StringBuilder();
        for (Message message : this.messages) {
            if (message.minVerbosity().compareTo(verbosity) <= 0) {
                result.append(message.text());
            }
        }
        return result.toString();
    }

    private final String[] args;
    /** Time of invocation, initialised at start time. */
    private Date startTime;
    private final List<Message> messages = new ArrayList<>();

    /** Message with the minimum verbosity at which it is to be emitted. */
    private record Message(Verbosity minVerbosity, String text) {
        // no additional functionality
    }

    /** Number of bytes per kB */
    static private final long B_PER_KB = 1024;
    /** Number of bytes per MB */
    static private final long B_PER_MB = B_PER_KB * B_PER_KB;
}
