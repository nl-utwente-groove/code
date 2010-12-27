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
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;

/**
 * Class providing a default implementation of {@link HostGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultHostGraph extends NodeSetEdgeSetGraph<HostNode,HostEdge>
        implements HostGraph {
    /**
     * Constructs an empty host graph.
     */
    public DefaultHostGraph() {
        this(HostFactory.instance());
    }

    /**
     * Constructs an empty host graph, with a given host factory.
     */
    protected DefaultHostGraph(HostFactory factory) {
        super();
        this.factory = factory;
    }

    /**
     * Copies an existing host graph.
     */
    public DefaultHostGraph(HostGraph graph) {
        super(graph);
        this.factory = graph.getFactory();
    }

    /** 
     * Turns a given graph into a host graph,
     * by creating the appropriate types of nodes and edges.
     */
    public <N extends Node,E extends Edge<N>> DefaultHostGraph(Graph<N,E> graph) {
        this();
        ElementMap<N,E,HostNode,HostEdge> map =
            new ElementMap<N,E,HostNode,HostEdge>(HostFactory.instance());
        for (N node : graph.nodeSet()) {
            HostNode newNode = addNode(node.getNumber());
            map.putNode(node, newNode);
        }
        for (E edge : graph.edgeSet()) {
            HostNode sourceImage = map.getNode(edge.source());
            HostNode targetImage = map.getNode(edge.target());
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
        return new DefaultHostGraph(getFactory());
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof HostNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge<?> edge) {
        return edge instanceof HostEdge;
    }

    /** 
     * Refreshes the element factory of this graph, by replacing it with 
     * the new factory obtained by calling {@link HostFactory#newFactory(HostGraph)}
     * (with this graph as parameter).
     */
    final public void renewFactory() {
        if (this.factory == null) {
            throw new UnsupportedOperationException();
        } else {
            this.factory = this.factory.newFactory(this);
        }
    }

    @Override
    public HostFactory getFactory() {
        return this.factory;
    }

    private HostFactory factory;
}
