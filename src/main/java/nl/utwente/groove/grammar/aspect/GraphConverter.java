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
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectKind.ABSTRACT;
import static nl.utwente.groove.grammar.aspect.AspectKind.COMPOSITE;
import static nl.utwente.groove.grammar.aspect.AspectKind.MULT_IN;
import static nl.utwente.groove.grammar.aspect.AspectKind.MULT_OUT;
import static nl.utwente.groove.grammar.aspect.AspectKind.SUBTYPE;
import static nl.utwente.groove.graph.GraphRole.HOST;
import static nl.utwente.groove.graph.GraphRole.TYPE;

import java.util.Map;
import java.util.Set;

import nl.utwente.groove.grammar.aspect.AspectContent.MultiplicityContent;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.AGraphMap;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;

/** Converter class to aspect graphs. */
public class GraphConverter {
    /** Constructs an aspect graph from an arbitrary graph. */
    static public AspectGraph toAspect(Graph graph) {
        AspectGraph result;
        if (graph instanceof AspectGraph ag) {
            result = ag;
        } else if (graph instanceof HostGraph hg) {
            result = toAspectMap(hg).getAspectGraph();
        } else if (graph instanceof TypeGraph tg) {
            result = toAspectMap(tg).getAspectGraph();
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
            if (node.isSort()) {
                nodeImage.set(AspectKind.toAspectKind(node.label().getSort()).getAspect());
            } else {
                target.addEdge(nodeImage, node.label().toParsableString(), nodeImage);
            }
            if (node.isAbstract()) {
                nodeImage.set(ABSTRACT.getAspect());
            }
        }
        Map<TypeNode,Set<TypeNode>> superMap = type.getDirectSupertypeMap();
        // add subtype relations
        for (TypeNode node : type.nodeSet()) {
            AspectNode nodeImage = result.getNode(node);
            assert nodeImage != null;
            for (TypeNode nodeSuper : superMap.get(node)) {
                var nodeSuperImage = result.getNode(nodeSuper);
                assert nodeSuperImage != null;
                target.addEdge(nodeImage, SUBTYPE.getPrefix(), nodeSuperImage);
            }
        }
        // add type edges
        for (TypeEdge edge : type.edgeSet()) {
            StringBuilder text = new StringBuilder();
            if (edge.label().getRole() == EdgeRole.FLAG) {
                text.append(EdgeRole.FLAG.getPrefix());
            }
            if (edge.isAbstract()) {
                text.append(ABSTRACT.getPrefix());
            }
            if (edge.isComposite()) {
                text.append(COMPOSITE.getPrefix());
            }
            if (edge.getInMult() != null) {
                text.append(new MultiplicityContent(edge.getInMult()).toParsableString(MULT_IN));
            }
            if (edge.getOutMult() != null) {
                text.append(new MultiplicityContent(edge.getInMult()).toParsableString(MULT_OUT));
            }
            text.append(edge.label().toParsableString());
            var sourceImage = result.getNode(edge.source());
            assert sourceImage != null;
            var targetImage = result.getNode(edge.target());
            assert targetImage != null;
            AspectEdge edgeImage = target.addEdge(sourceImage, text.toString(), targetImage);
            result.putEdge(edge, edgeImage);
        }
        GraphInfo.transferAll(type, target, result);
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
                TypeLabel typeLabel = node.getType().label();
                if (typeLabel != TypeLabel.NODE) {
                    targetGraph.addEdge(nodeImage, result.mapLabel(typeLabel), nodeImage);
                }
            }
        }
        // add edge images
        for (HostEdge edge : host.edgeSet()) {
            String edgeText = edge.label().text();
            AspectNode imageSource = result.getNode(edge.source());
            assert imageSource != null;
            AspectNode imageTarget;
            String text;
            if (edge.target() instanceof ValueNode vn) {
                imageTarget = imageSource;
                String constant = vn.getTerm().toParseString();
                text = AspectKind.LET.getPrefix() + edgeText + "=" + constant;
            } else if (edge.getRole() == EdgeRole.BINARY) {
                imageTarget = result.getNode(edge.target());
                assert imageTarget != null;
                // precede with literal aspect prefix if this is necessary
                // to parse the label
                AspectLabel tryLabel = AspectParser.getInstance().parse(edgeText, HOST);
                if (tryLabel.hasErrors() || !tryLabel.getInnerText().equals(edgeText)) {
                    text = AspectKind.LITERAL.getPrefix() + edgeText;
                } else {
                    text = edgeText;
                }
            } else {
                imageTarget = imageSource;
                text = edge.label().toString();
            }
            AspectEdge edgeImage = targetGraph.addEdge(imageSource, text, imageTarget);
            result.putEdge(edge, edgeImage);
        }
        GraphInfo.transferAll(host, targetGraph, result);
        targetGraph.setFixed();
        return result;
    }

    /**
     * Mapping from the elements of a host graph to those of a corresponding
     * aspect graph. For convenience, the aspect graph is bundled in with the map.
     */
    static public class HostToAspectMap extends AGraphMap<HostNode,HostEdge,AspectNode,AspectEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostToAspectMap(AspectGraph aspectGraph) {
            super(aspectGraph.getFactory());
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
    static public class TypeToAspectMap extends AGraphMap<TypeNode,TypeEdge,AspectNode,AspectEdge> {
        /**
         * Creates a new, empty map.
         */
        public TypeToAspectMap(AspectGraph aspectGraph) {
            super(aspectGraph.getFactory());
            this.aspectGraph = aspectGraph;
        }

        /** Returns the target aspect graph of this mapping. */
        public AspectGraph getAspectGraph() {
            return this.aspectGraph;
        }

        private final AspectGraph aspectGraph;
    }
}
