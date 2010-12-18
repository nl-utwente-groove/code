/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.trans;

import groove.graph.Edge;
import groove.graph.ElementMap;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.TypeLabel;

/**
 * Class providing a default implementation of {@link HostGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultHostGraph extends
        NodeSetEdgeSetGraph<HostNode,TypeLabel,HostEdge> implements HostGraph {
    /**
     * Constructs an empty host graph.
     */
    public DefaultHostGraph() {
        // empty
    }

    /**
     * Copies an existing host graph.
     */
    public DefaultHostGraph(HostGraph graph) {
        super(graph);
    }

    /** 
     * Turns a given graph into a host graph,
     * by creating the appropriate types of nodes and edges.
     */
    public <N extends Node,L extends Label,E extends Edge> DefaultHostGraph(
            Graph<N,L,E> graph) {
        ElementMap<N,L,E,HostNode,TypeLabel,HostEdge> map =
            new ElementMap<N,L,E,HostNode,TypeLabel,HostEdge>(
                HostFactory.instance());
        for (N node : graph.nodeSet()) {
            HostNode newNode = addNode(node.getNumber());
            map.putNode(node, newNode);
        }
        for (E edge : graph.edgeSet()) {
            @SuppressWarnings("unchecked")
            HostNode sourceImage = map.getNode((N) edge.source());
            @SuppressWarnings("unchecked")
            HostNode targetImage = map.getNode((N) edge.target());
            HostEdge edgeImage =
                addEdge(sourceImage, edge.label().text(), targetImage);
            map.putEdge(edge, edgeImage);
        }
        GraphInfo.transfer(graph, this, map);
    }

    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this);
    }

    @Override
    public DefaultHostGraph newGraph() {
        return new DefaultHostGraph();
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof HostNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof HostEdge;
    }

    @Override
    public HostFactory getFactory() {
        return HostFactory.instance();
    }
}
