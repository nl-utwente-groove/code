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
package nl.utwente.groove.io.external.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter;
import nl.utwente.groove.io.external.PortException;

/**
 * Class that implements saving graphs in the FSM (Finite State Machine)
 * format.
 * Loading in this format is unsupported.
 *
 * @author Arend Rensink
 */
public final class FsmExporter extends AbstractExporter {
    private FsmExporter() {
        super(Exporter.ExportKind.GRAPH);
        register(FileType.FSM);
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        try (PrintWriter writer = new PrintWriter(file)) {
            this.save(exportable.graph(), writer);
        } catch (FileNotFoundException e) {
            throw new PortException(e);
        }
    }

    private void save(Graph graph, PrintWriter writer) {
        // mapping from nodes of graphs to integers
        Map<Node,Integer> nodeMap = new HashMap<>();
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
            writer
                .println(nodeMap.get(edge.source()) + " " + nodeMap.get(edge.target()) + " " + "\""
                    + edge.label() + "\"");
        }
    }

    /** Returns the singleton instance of this class. */
    public static final FsmExporter getInstance() {
        return instance;
    }

    private static final FsmExporter instance = new FsmExporter();

}
