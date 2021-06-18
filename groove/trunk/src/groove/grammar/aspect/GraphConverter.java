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
package groove.grammar.aspect;

import static groove.grammar.aspect.AspectKind.ABSTRACT;
import static groove.grammar.aspect.AspectKind.COMPOSITE;
import static groove.grammar.aspect.AspectKind.MULT_IN;
import static groove.grammar.aspect.AspectKind.MULT_OUT;
import static groove.grammar.aspect.AspectKind.SUBTYPE;
import static groove.grammar.aspect.AspectKind.ContentKind.MULTIPLICITY;
import static groove.graph.GraphRole.HOST;
import static groove.graph.GraphRole.TYPE;

import java.util.Map;
import java.util.Set;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.AElementMap;
import groove.graph.EdgeRole;
import groove.graph.Graph;
import groove.graph.GraphInfo;

/** Converter class to aspect graphs. */
public class GraphConverter {
    /** Constructs an aspect graph from an arbitrary graph. */
    static public AspectGraph toAspect(Graph graph) {
        AspectGraph result;
        if (graph instanceof AspectGraph) {
            result = (AspectGraph) graph;
        } else if (graph instanceof HostGraph) {
            result = toAspectMap((HostGraph) graph).getAspectGraph();
        } else if (graph instanceof TypeGraph) {
            result = toAspectMap((TypeGraph) graph).getAspectGraph();
        } else {
            result = AspectGraph.newInstance(graph);
        }
        return result;
    }

    /**
     * Converts a type graph to an aspect graph.
     * @return the resulting aspect graph, together with an element map
     * from the type graph to the aspect graph.
     */
    static public TypeToAspectMap toAspectMap(TypeGraph type) {
        AspectGraph target = new AspectGraph(type.getName(), TYPE);
        TypeToAspectMap result = new TypeToAspectMap(target);
        for (TypeNode node : type.nodeSet()) {
            AspectNode nodeImage = target.addNode(node.getNumber());
            result.putNode(node, nodeImage);
            target.addEdge(nodeImage,
                node.label()
                    .toParsableString(),
                nodeImage);
            if (node.isAbstract()) {
                target.addEdge(nodeImage, ABSTRACT.getPrefix(), nodeImage);
            }
        }
        Map<TypeNode,Set<TypeNode>> superMap = type.getDirectSupertypeMap();
        // add subtype relations
        for (TypeNode node : type.nodeSet()) {
            AspectNode nodeImage = result.getNode(node);
            for (TypeNode nodeSuper : superMap.get(node)) {
                target.addEdge(nodeImage, SUBTYPE.getPrefix(), result.getNode(nodeSuper));
            }
        }
        // add type edges
        for (TypeEdge edge : type.edgeSet()) {
            StringBuilder text = new StringBuilder();
            if (edge.label()
                .getRole() == EdgeRole.FLAG) {
                text.append(EdgeRole.FLAG.getPrefix());
            }
            if (edge.isAbstract()) {
                text.append(ABSTRACT.getPrefix());
            }
            if (edge.isComposite()) {
                text.append(COMPOSITE.getPrefix());
            }
            if (edge.getInMult() != null) {
                text.append(MULTIPLICITY.toString(MULT_IN, edge.getInMult()));
            }
            if (edge.getOutMult() != null) {
                text.append(MULTIPLICITY.toString(MULT_OUT, edge.getOutMult()));
            }
            text.append(edge.text());
            AspectEdge edgeImage = target.addEdge(result.getNode(edge.source()),
                text.toString(),
                result.getNode(edge.target()));
            result.putEdge(edge, edgeImage);
        }
        GraphInfo.transfer(type, target, result);
        target.setFixed();
        return result;
    }

    /**
     * Converts a host graph to an aspect graph.
     * @return the resulting aspect graph, together with an element map
     * from the host graph to the aspect graph.
     */
    static public HostToAspectMap toAspectMap(HostGraph host) {
        AspectGraph targetGraph = new AspectGraph(host.getName(), HOST);
        HostToAspectMap result = new HostToAspectMap(targetGraph);
        for (HostNode node : host.nodeSet()) {
            if (!(node instanceof ValueNode)) {
                AspectNode nodeImage = targetGraph.addNode(node.getNumber());
                result.putNode(node, nodeImage);
                TypeLabel typeLabel = node.getType()
                    .label();
                if (typeLabel != TypeLabel.NODE) {
                    targetGraph.addEdge(nodeImage, result.mapLabel(typeLabel), nodeImage);
                }
            }
        }
        // add edge images
        for (HostEdge edge : host.edgeSet()) {
            String edgeText = edge.label()
                .text();
            AspectNode imageSource = result.getNode(edge.source());
            AspectNode imageTarget;
            String text;
            if (edge.target() instanceof ValueNode) {
                imageTarget = imageSource;
                String constant = ((ValueNode) edge.target()).getTerm()
                    .toParseString();
                text = AspectKind.LET.getPrefix() + edgeText + "=" + constant;
            } else if (edge.getRole() == EdgeRole.BINARY) {
                imageTarget = result.getNode(edge.target());
                // precede with literal aspect prefix if this is necessary
                // to parse the label
                AspectLabel tryLabel = AspectParser.getInstance()
                    .parse(edgeText, HOST);
                if (tryLabel.hasErrors() || !tryLabel.getInnerText()
                    .equals(edgeText)) {
                    text = AspectKind.LITERAL.getPrefix() + edgeText;
                } else {
                    text = edgeText;
                }
            } else {
                imageTarget = imageSource;
                text = edge.label()
                    .toString();
            }
            AspectEdge edgeImage = targetGraph.addEdge(imageSource, text, imageTarget);
            result.putEdge(edge, edgeImage);
        }
        GraphInfo.transfer(host, targetGraph, result);
        targetGraph.setFixed();
        return result;
    }

    /**
     * Mapping from the elements of a host graph to those of a corresponding
     * aspect graph. For convenience, the aspect graph is bundled in with the map.
     */
    static public class HostToAspectMap
        extends AElementMap<HostNode,HostEdge,AspectNode,AspectEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostToAspectMap(AspectGraph aspectGraph) {
            super(new AspectGraph.AspectFactory(HOST));
            this.aspectGraph = aspectGraph;
        }

        /** Returns the target aspect graph of this mapping. */
        public AspectGraph getAspectGraph() {
            return this.aspectGraph;
        }

        private final AspectGraph aspectGraph;
    }

    /**
     * Mapping from the elements of a type graph to those of a corresponding
     * aspect graph. For convenience, the aspect graph is bundled in with the map.
     */
    static public class TypeToAspectMap
        extends AElementMap<TypeNode,TypeEdge,AspectNode,AspectEdge> {
        /**
         * Creates a new, empty map.
         */
        public TypeToAspectMap(AspectGraph aspectGraph) {
            super(new AspectGraph.AspectFactory(TYPE));
            this.aspectGraph = aspectGraph;
        }

        /** Returns the target aspect graph of this mapping. */
        public AspectGraph getAspectGraph() {
            return this.aspectGraph;
        }

        private final AspectGraph aspectGraph;
    }
}
