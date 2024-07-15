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

import java.util.ArrayList;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.HTMLConverter;

/**
 * Listener superclass for graph exporters.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class GraphExportListener {
    /** Constructs a listener for a given file type. */
    GraphExportListener(FileType fileType) {
        this.fileType = fileType;
    }

    /** Returns the file type for which this is a listener. */
    FileType getFileType() {
        return this.fileType;
    }

    /** The file type for which this is a listener. */
    private final FileType fileType;

    /** Sets the exporter associated with this listener. */
    public void setExporter(ListenerExporter exporter) {
        this.exporter = exporter;
    }

    private ListenerExporter exporter;

    /** Prints a line to the output file. */
    void emit(String line) {
        this.exporter.emit(line);
    }

    /** Starts processing a graph. */
    abstract public void enterGraph(Graph graph);

    /** Finishes processing a graph. */
    abstract public void exitGraph(Graph graph);

    /** Emits the description of a graph node. */
    abstract public void visitNode(Node node);

    /** Emits the description of a binary graph edge. */
    abstract public void visitEdge(Edge edge);

    /** Listener class for the {@link FileType#DOT} format. */
    static public class DotListener extends GraphExportListener {
        /** Constructor for the singleton instance of this listener. */
        private DotListener() {
            super(FileType.DOT);
        }

        @Override
        public void enterGraph(Graph graph) {
            emit("digraph {");
            this.graph = graph;
        }

        @Override
        public void exitGraph(Graph graph) {
            emit("}");
        }

        private Graph graph;

        @Override
        public void visitNode(Node node) {
            var typeLabels = new ArrayList<String>();
            var flagLabels = new ArrayList<String>();
            StringBuilder label = new StringBuilder();
            if (node instanceof AspectNode an) {
                // empty
            } else {
                label.append("\\N<br/>");
            }
            for (var edge : this.graph.outEdgeSet(node)) {
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
        public void visitEdge(Edge edge) {
            emit(edge.source().toString() + "->" + edge.target().toString() + "[label=<"
                + edge.label().text() + ">]");
        }

        /** Returns the singleton instance of this listener. */
        static public DotListener instance() {
            return INSTANCE;
        }

        /** The singleton instance of this listener. */
        private static final DotListener INSTANCE = new DotListener();
    }
}
