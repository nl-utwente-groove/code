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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.graph.GraphExportListener;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.io.FileType;

/**
 * Class that implements saving graphs using a {@link GraphExportListener}.
 * Every export creates a fresh listener, since listeners hold per-write state.
 *
 * @author Arend Rensink
 */
@NonNullByDefault
public class ListenerExporter extends AbstractExporter {
    private ListenerExporter(Supplier<? extends GraphExportListener> factory) {
        super(Exporter.ExportKind.GRAPH);
        register(factory.get().getFileType());
        this.factory = factory;
    }

    /** Factory for the listeners doing the actual writing. */
    private final Supplier<? extends GraphExportListener> factory;

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        Graph graph = exportable.graph();
        if (graph == null) {
            throw new PortException(String
                .format("'%s' does not contain a graph and hence cannot be exported to %s",
                        exportable.qualName(), fileType));
        }
        // a GTS passed in directly is exported without its internal states and transitions
        Collection<? extends Node> nodes = graph instanceof GTS gts
            ? gts.getStates()
            : graph.nodeSet();
        Collection<? extends Edge> edges = graph instanceof GTS gts
            ? gts.getTransitions()
            : graph.edgeSet();
        try (java.io.Writer out
            = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            this.factory.get().write(graph, nodes, edges, out);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    /** Creates and returns an exporter for the listeners produced by a given factory. */
    static public ListenerExporter instance(Supplier<? extends GraphExportListener> factory) {
        return new ListenerExporter(factory);
    }
}
