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
package nl.utwente.groove.io.graph;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.HTMLConverter;
import nl.utwente.groove.util.io.FileType;

/**
 * Listener class for the {@link FileType#DOT} format.
 * Node type and flag edges are folded into the node labels;
 * only binary edges are emitted as edges.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class DotListener extends GraphExportListener {
    /** Constructs a listener for a single write. */
    public DotListener() {
        super(FileType.DOT);
    }

    @Override
    protected void enterGraph(Graph graph) throws IOException {
        emit("digraph {");
        this.graph = graph;
    }

    @Override
    protected void exitGraph(Graph graph) throws IOException {
        emit("}");
    }

    /** Returns the graph set by {@link #enterGraph(Graph)}. */
    private Graph getGraph() {
        var result = this.graph;
        assert result != null : "Graph not entered";
        return result;
    }

    /** The graph currently being visited; only set from {@link #enterGraph(Graph)} on. */
    private @Nullable Graph graph;

    @Override
    protected void visitNode(Node node) throws IOException {
        var typeLabels = new ArrayList<String>();
        var flagLabels = new ArrayList<String>();
        StringBuilder label = new StringBuilder();
        if (node instanceof AspectNode) {
            // empty
        } else {
            label.append("\\N<br/>");
        }
        for (var edge : getGraph().outEdgeSet(node)) {
            String line;
            if (edge instanceof AspectEdge ae) {
                line = ae.toLine(true, ae.source().getAspects()).toHTMLString();
            } else {
                line = switch (edge.getRole()) {
                case NODE_TYPE -> "<b>" + HTMLConverter.toHtml(edge.label().text()) + "</b>";
                case FLAG -> "<i>" + HTMLConverter.toHtml(edge.label().text()) + "</i>";
                default -> "";
                };
            }
            switch (edge.getRole()) {
            case NODE_TYPE:
                typeLabels.add(line);
                break;
            case FLAG:
                flagLabels.add(line);
                break;
            default:
                // empty
            }
        }
        typeLabels.forEach(s -> label.append(s + "<br/>"));
        flagLabels.forEach(s -> label.append(s + "<br/>"));
        emit(node.toString() + "[label=<" + label.toString() + ">]");
    }

    @Override
    protected void visitEdge(Edge edge) throws IOException {
        // node type and flag edges have been folded into the node labels
        if (edge.hasRole(EdgeRole.BINARY)) {
            emit(edge.source().toString() + "->" + edge.target().toString() + "[label=<"
                + edge.label().text() + ">]");
        }
    }
}
