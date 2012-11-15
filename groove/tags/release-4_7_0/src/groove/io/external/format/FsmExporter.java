/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io.external.format;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.io.external.AbstractFormatExporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.PortException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 
 * Class that implements saving graphs in the FSM (Finite State Machine)
 * format.
 * Loading in this format is unsupported.
 * 
 * @author Arend Rensink 
 */
public final class FsmExporter extends AbstractFormatExporter {
    private FsmExporter() {
        this.fsmformat = new Format(this, "FSM layout files", ".fsm");
    }

    @Override
    public Kind getFormatKind() {
        return Kind.GRAPH;
    }

    @Override
    public Collection<? extends Format> getSupportedFormats() {
        return Collections.singletonList(this.fsmformat);
    }

    @Override
    public void doExport(File file, Format format, Exportable exportable)
        throws PortException {
        Graph<?,?> graph = exportable.getGraph();
        try {
            PrintWriter writer = new PrintWriter(file);
            this.save(graph, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new PortException(e);
        }
    }

    private void save(Graph<?,?> graph, PrintWriter writer) {
        // mapping from nodes of graphs to integers
        Map<Node,Integer> nodeMap = new HashMap<Node,Integer>();
        writer.println("NodeNumber(0)");
        writer.println("---");
        int nr = 1;
        for (Node node : graph.nodeSet()) {
            nodeMap.put(node, nr);
            writer.println(nr);
            nr++;
        }
        writer.println("---");
        for (Edge edge : graph.edgeSet()) {
            writer.println(nodeMap.get(edge.source()) + " "
                + nodeMap.get(edge.target()) + " " + "\"" + edge.label() + "\"");
        }
    }

    private final Format fsmformat;

    /** Returns the singleton instance of this class. */
    public static final FsmExporter getInstance() {
        return instance;
    }

    private static final FsmExporter instance = new FsmExporter();

}
