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

import static groove.graph.GraphRole.HOST;
import groove.algebra.Algebra;
import groove.graph.Edge;
import groove.graph.ElementMap;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.algebra.ValueNode;
import groove.view.FormatException;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;

/**
 * Class providing a default implementation of {@link HostGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultHostGraph extends NodeSetEdgeSetGraph<HostNode,HostEdge>
        implements HostGraph {
    /**
     * Constructs an empty host graph.
     * @param name name of the new host graph.
     */
    public DefaultHostGraph(String name) {
        this(name, HostFactory.newInstance());
    }

    /**
     * Constructs an empty host graph, with a given host factory.
     * @param name name of the new host graph
     */
    public DefaultHostGraph(String name, HostFactory factory) {
        super(name);
        this.factory = factory;
    }

    /**
     * Copies an existing host graph, including its element factory.
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
        this(graph.getName());
        ElementMap<N,E,HostNode,HostEdge> map =
            new ElementMap<N,E,HostNode,HostEdge>(getFactory());
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

    /**
     * Creates, adds and returns a value node created for a given
     * algebra and value.
     */
    public ValueNode addNode(Algebra<?> algebra, Object value) {
        ValueNode result = getFactory().createNode(algebra, value);
        addNode(result);
        return result;
    }

    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this);
    }

    @Override
    public GraphRole getRole() {
        return HOST;
    }

    /**
     * Returns a copy of this graph, which uses a given factory.
     * Also makes sure the elements already in this graph are known to the factory. 
     */
    public DefaultHostGraph clone(HostFactory factory) {
        DefaultHostGraph result = new DefaultHostGraph(getName(), factory);
        for (HostNode node : nodeSet()) {
            factory.addNode(node);
            result.addNode(node);
        }
        for (HostEdge edge : edgeSet()) {
            factory.addEdge(edge);
            result.addEdge(edge);
        }
        result.setInfo(GraphInfo.getInfo(this, true).clone());
        return result;
    }

    @Override
    public DefaultHostGraph newGraph(String name) {
        return new DefaultHostGraph(getName(), getFactory());
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof HostNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge<?> edge) {
        return edge instanceof HostEdge;
    }

    @Override
    public HostFactory getFactory() {
        return this.factory;
    }

    public HostToAspectMap toAspectMap() {
        AspectGraph targetGraph = AspectGraph.newInstance(getName(), HOST);
        HostToAspectMap result = new HostToAspectMap(targetGraph);
        for (HostNode node : nodeSet()) {
            AspectNode nodeImage = targetGraph.addNode(node.getNumber());
            result.putNode(node, nodeImage);
            if (node instanceof ValueNode) {
                // add the appropriate value aspect to the node
                ValueNode valueNode = (ValueNode) node;
                AspectLabel label = new AspectLabel(HOST);
                try {
                    label.addAspect(Aspect.getAspect(valueNode.getSignature()).newInstance(
                        valueNode.getSymbol()));
                    label.setInnerText("");
                    assert !label.hasErrors();
                    nodeImage.setAspects(label);
                } catch (FormatException e) {
                    // this is sure not to raise an exception
                    assert false;
                }
            }
        }
        // add edge images
        for (HostEdge edge : edgeSet()) {
            AspectEdge edgeImage = result.mapEdge(edge);
            try {
                edgeImage.setFixed();
            } catch (FormatException e) {
                // this is sure not to raise an exception
                assert false;
            }
            targetGraph.addEdge(edgeImage);
        }
        // now fix the nodes, insofar this was not achieved
        // by fixing the edges
        for (AspectNode node : result.nodeMap().values()) {
            try {
                node.setFixed();
            } catch (FormatException e) {
                // this is sure not to raise an exception
                assert false;
            }
        }
        GraphInfo.transfer(this, targetGraph, result);
        targetGraph.setFixed();
        return result;
    }

    /** The element factory of this host graph. */
    private final HostFactory factory;
}
