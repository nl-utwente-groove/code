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
 * $Id$
 */
package groove.util;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.util.cli.GrooveCmdLineTool;
import groove.util.collect.Bag;
import groove.util.collect.TreeBag;

import java.util.Map;

import org.kohsuke.args4j.Argument;

/**
 * Tool to test and report various characteristics of a saved graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GraphReporter extends GrooveCmdLineTool<String> {
    /**
     * Constructs a new graph reporter with a given list of arguments. The
     * arguments consist of a list of options followed by a graph file name.
     */
    private GraphReporter(String... args) {
        super("GraphReporter", args);
    }

    /** Starts the reporter, for the given list of arguments. */
    @Override
    protected String run() throws Exception {
        Graph graph = Groove.loadGraph(this.graphLocation);
        String result = getReport(graph).toString();
        emit("%s%n", result);
        return result;
    }

    /**
     * Generates a report for a given graph. The report depends on the
     * parameters of this reporter, and is returned in the form of a
     * StringBuilder.
     */
    public StringBuilder getReport(Graph graph) {
        StringBuilder result = new StringBuilder();
        // count the labels
        Bag<Label> labels = new TreeBag<>();
        for (Edge edge : graph.edgeSet()) {
            labels.add(edge.label());
        }
        for (Map.Entry<Label,? extends Bag.Multiplicity> labelEntry : labels.multiplicityMap().entrySet()) {
            result.append(String.format("%s\t%s%n", labelEntry.getKey(),
                labelEntry.getValue()));
        }
        return result;
    }

    @Argument(metaVar = "graph", required = true, usage = "graph location")
    private String graphLocation;

    /**
     * Starts a new graph reporter with the given arguments.
     * Always exits with {@link System#exit(int)}; see
     * {@link #execute(String...)} for programmatic use.
     */
    public static void main(String[] args) {
        tryExecute(GraphReporter.class, args);
    }

    /**
     * Starts a new graph reporter with the given arguments.
     */
    public static String execute(String... args) throws Exception {
        return new GraphReporter(args).start();
    }
}
