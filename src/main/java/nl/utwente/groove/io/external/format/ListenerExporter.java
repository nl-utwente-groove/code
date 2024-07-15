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
import java.util.Collection;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.lts.GTS;

/**
 * Class that implements saving graphs using a {@link GraphExportListener}.
 *
 * @author Arend Rensink
 */
public class ListenerExporter extends AbstractExporter {
    private ListenerExporter(GraphExportListener listener) {
        super(Kind.GRAPH);
        listener.setExporter(this);
        register(listener.getFileType());
        this.listener = listener;
    }

    private final GraphExportListener listener;

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        Graph graph = exportable.getGraph();
        if (graph == null) {
            throw new PortException(String
                .format("'%s' does not contain a graph and hence cannot be exported to %s",
                        exportable.getQualName(), fileType));
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            this.writer = writer;
            this.listener.enterGraph(graph);
            Collection<? extends Node> nodeSet = graph instanceof GTS gts
                ? gts.getStates()
                : graph.nodeSet();
            nodeSet.forEach(this.listener::visitNode);
            Collection<? extends Edge> edgeSet = graph instanceof GTS gts
                ? gts.getTransitions()
                : graph.edgeSet();
            edgeSet
                .stream()
                .filter(e -> e.hasRole(EdgeRole.BINARY))
                .forEach(this.listener::visitEdge);
            this.listener.exitGraph(graph);
        } catch (FileNotFoundException e) {
            throw new PortException(e);
        }
    }

    /** Writes a line to the export file. */
    void emit(String line) {
        this.writer.println(line);
    }

    private PrintWriter writer;

    /** Creates and returns an instance for a given listener. */
    static public ListenerExporter instance(GraphExportListener listener) {
        return new ListenerExporter(listener);
    }
}
