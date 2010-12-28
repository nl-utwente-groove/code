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
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.ElementMap;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.algebra.ValueNode;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.HashSet;
import java.util.Set;

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
        this(HostFactory.newInstance());
    }

    /**
     * Constructs an empty host graph, with a given host factory.
     */
    public DefaultHostGraph(HostFactory factory) {
        super();
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
        this();
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

    /**
     * Returns a copy of this graph, which uses a given factory.
     * Also makes sure the elements already in this graph are known to the factory. 
     */
    public DefaultHostGraph clone(HostFactory factory) {
        DefaultHostGraph result = new DefaultHostGraph(factory);
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

    @Override
    public HostFactory getFactory() {
        return this.factory;
    }

    public AspectGraph toAspectGraph() {
        AspectGraph result = AspectGraph.newInstance(HOST);
        int maxNodeNr = -1;
        // we have to renumber the value nodes; just store them separately for now
        Set<ValueNode> valueNodeSet = new HashSet<ValueNode>();
        HostToAspectMap elementMap = new HostToAspectMap();
        for (HostNode node : nodeSet()) {
            if (node instanceof DefaultNode) {
                maxNodeNr = Math.max(maxNodeNr, node.getNumber());
                AspectNode nodeImage = result.addNode(node.getNumber());
                elementMap.putNode(node, nodeImage);
            } else {
                assert node instanceof ValueNode;
                valueNodeSet.add((ValueNode) node);
            }
        }
        // now add images of the value nodes
        for (ValueNode node : valueNodeSet) {
            // number them high enough
            // (value nodes have negative numbers)
            int nr = maxNodeNr - node.getNumber();
            AspectNode nodeImage = result.addNode(nr);
            // add the value information as aspect to the node
            AspectLabel label =
                AspectParser.getInstance(HOST).parse(
                    node.getSignature() + AspectParser.SEPARATOR
                        + node.getSymbol());
            assert !label.hasErrors();
            try {
                nodeImage.setAspects(label);
            } catch (FormatException e) {
                // this is sure not to raise an exception
                assert false;
            }
            elementMap.putNode(node, nodeImage);
        }
        // add edge images
        for (HostEdge edge : edgeSet()) {
            AspectEdge edgeImage = elementMap.mapEdge(edge);
            try {
                edgeImage.setFixed();
            } catch (FormatException e) {
                // this is sure not to raise an exception
                assert false;
            }
            result.addEdge(edgeImage);
        }
        // now fix the nodes, insofar this was not achieved
        // by fixing the edges

        for (AspectNode node : elementMap.nodeMap().values()) {
            try {
                node.setFixed();
            } catch (FormatException e) {
                // this is sure not to raise an exception
                assert false;
            }
        }
        GraphInfo.transfer(this, result, elementMap);
        result.setFixed();
        return result;
    }

    private HostFactory factory;

    /** 
     * A factory that knows how to create AspectLabels (and hence how to
     * map AspectEdges). 
     */
    private class AspectFactory extends AspectGraph.AspectFactory {
        /**
         * Constructs a new factory.
         */
        public AspectFactory() {
            super(HOST);
        }

        @Override
        public AspectLabel createLabel(String text) {
            return AspectParser.getInstance(HOST).parse(text);
        }
    }

    private class HostToAspectMap extends
            ElementMap<HostNode,HostEdge,AspectNode,AspectEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostToAspectMap() {
            super(new AspectFactory());
        }
    }
}
