/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: GraphReporter.java,v 1.2 2007-11-30 08:29:28 rensink Exp $
 */
package groove.util;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.util.cli.CommandLineTool;
import groove.util.collect.Bag;
import groove.util.collect.TreeBag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Tool to test and report various characteristics of a saved graph.
 * @author Arend Rensink
 * @version $Revision $
 * @deprecated use {@link GraphReporter} instead
 */
@Deprecated
public class DeprecatedGraphReporter extends CommandLineTool {
    /**
     * Constructs a new graph reporter with a given list of arguments. The
     * arguments consist of a list of options followed by a graph file name.
     */
    private DeprecatedGraphReporter(String... args) {
        super(args);
    }

    /**
     * Constructs a new reporter, with default settings. This reporter should
     * exclusively be used to call {@link #getReport(Graph)}.
     */
    private DeprecatedGraphReporter() {
        super();
    }

    /** Starts the reporter, for the given list of arguments. */
    public void start() {
        processArguments();
        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
            String graphLocation = argsList.remove(0);
            if (argsList.size() > 0) {
                printError(
                    String.format("Spurious parameters '%s'",
                        argsList.toString()), true);
            }
            try {
                report(Groove.loadGraph(graphLocation));
            } catch (IOException e) {
                printError(e.getMessage(), true);
            }
        } else {
            printError("No graph specified", true);
        }
    }

    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    @Override
    protected boolean supportsOutputOption() {
        return false;
    }

    @Override
    protected boolean supportsVerbosityOption() {
        return false;
    }

    @Override
    protected String getUsageMessage() {
        return super.getUsageMessage() + " graph-file";
    }

    /**
     * Does the actual reporting for a given graph. The report depends on the
     * parameters of this reporter, and is sent to the standard output.
     */
    public void report(Graph graph) {
        // count the labels
        println(getReport(graph).toString());
    }

    /**
     * Generates a report for a given graph. The report depends on the
     * parameters of this reporter, and is returned in the form of a
     * StringBuilder.
     */
    public StringBuilder getReport(Graph graph) {
        StringBuilder result = new StringBuilder();
        // count the labels
        Bag<Label> labels = new TreeBag<Label>();
        for (Edge edge : graph.edgeSet()) {
            labels.add(edge.label());
        }
        for (Map.Entry<Label,? extends Bag.Multiplicity> labelEntry : labels.multiplicityMap().entrySet()) {
            result.append(String.format("%s\t%s%n", labelEntry.getKey(),
                labelEntry.getValue()));
        }
        return result;
    }

    /**
     * Starts a new graph reporter with the given arguments.
     */
    public static void main(String[] args) {
        new DeprecatedGraphReporter(args).start();
    }

    /** Creates a fresh instance of a reporter. */
    public static DeprecatedGraphReporter createInstance() {
        return new DeprecatedGraphReporter();
    }
}
