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
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.Set;

/**
 * Class providing a default implementation of {@link HostGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultHostGraph extends NodeSetEdgeSetGraph implements HostGraph {
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
    public DefaultHostGraph(GraphShape graph) {
        GenericNodeEdgeMap<Node,HostNode,Edge,HostEdge> map =
            new GenericNodeEdgeHashMap<Node,HostNode,Edge,HostEdge>();
        for (Node node : graph.nodeSet()) {
            HostNode newNode = addNode(node.getNumber());
            map.putNode(node, newNode);
        }
        for (Edge edge : graph.edgeSet()) {
            HostNode sourceImage = map.getNode(edge.source());
            HostNode targetImage = map.getNode(edge.target());
            HostEdge edgeImage =
                addEdge(sourceImage, edge.label().text(), targetImage);
            map.putEdge(edge, edgeImage);
        }
        GraphInfo.transfer(graph, this, map);
    }

    @Override
    public HostNode addNode() {
        return (HostNode) super.addNode();
    }

    @Override
    public HostNode addNode(int nr) {
        return (HostNode) super.addNode(nr);
    }

    @Override
    public HostEdge addEdge(Node source, String label, Node target) {
        return (HostEdge) super.addEdge(source, label, target);
    }

    @Override
    public HostEdge addEdge(Node source, Label label, Node target) {
        return (HostEdge) super.addEdge(source, label, target);
    }

    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this);
    }

    @Override
    public DefaultHostGraph newGraph() {
        return new DefaultHostGraph();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends HostNode> nodeSet() {
        return (Set<HostNode>) super.nodeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends HostEdge> edgeSet() {
        return (Set<HostEdge>) super.edgeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends HostEdge> edgeSet(Node node) {
        return (Set<HostEdge>) super.edgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends HostEdge> outEdgeSet(Node node) {
        return (Set<HostEdge>) super.outEdgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends HostEdge> inEdgeSet(Node node) {
        return (Set<HostEdge>) super.inEdgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<? extends HostEdge> labelEdgeSet(Label label) {
        return (Set<HostEdge>) super.labelEdgeSet(label);
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
        return HostFactory.INSTANCE;
    }
}
