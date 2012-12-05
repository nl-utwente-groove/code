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
import groove.algebra.AlgebraFamily;
import groove.graph.Edge;
import groove.graph.EdgeMultiplicityVerifier;
import groove.graph.ElementMap;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

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
        assert factory != null;
        this.factory = factory;
    }

    /**
     * Copies an existing host graph, including its element factory.
     */
    public DefaultHostGraph(HostGraph graph, AlgebraFamily family) {
        this(graph.getName(), graph.getFactory());
        HostGraphMorphism morphism = getFactory().createMorphism();
        for (HostNode sn : graph.nodeSet()) {
            HostNode tn;
            if (sn instanceof ValueNode && family != null) {
                ValueNode vn = (ValueNode) sn;
                tn =
                    getFactory().createNodeFromString(
                        family.getAlgebra(vn.getSignature()), vn.getSymbol());
            } else {
                tn = sn;
            }
            addNode(tn);
            morphism.putNode(sn, tn);
        }
        for (HostEdge se : graph.edgeSet()) {
            addEdgeContext(morphism.mapEdge(se));
        }
    }

    /** 
     * Turns a given graph into a host graph,
     * by creating the appropriate types of nodes and edges.
     */
    public <N extends Node,E extends Edge> DefaultHostGraph(Graph<N,E> graph) {
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
        ValueNode result = getFactory().createValueNode(algebra, value);
        addNode(result);
        return result;
    }

    @Override
    public HostGraph clone(AlgebraFamily family) {
        return new DefaultHostGraph(this, family);
    }

    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this, null);
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
            result.addEdgeContext(edge);
        }
        GraphInfo.transfer(this, result, null);
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
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof HostEdge;
    }

    @Override
    public HostFactory getFactory() {
        return this.factory;
    }

    @Override
    public TypeGraph getTypeGraph() {
        return getFactory().getTypeFactory().getGraph();
    }

    @Override
    public HostGraph retype(TypeGraph typeGraph) throws FormatException {
        HostGraph result = typeGraph.analyzeHost(this).createImage(getName());
        EdgeMultiplicityVerifier.verifyMultiplicities(result, typeGraph);
        return result;
    }

    public HostToAspectMap toAspectMap() {
        AspectGraph targetGraph = new AspectGraph(getName(), HOST);
        HostToAspectMap result = new HostToAspectMap(targetGraph);
        for (HostNode node : nodeSet()) {
            if (!(node instanceof ValueNode)) {
                AspectNode nodeImage = targetGraph.addNode(node.getNumber());
                result.putNode(node, nodeImage);
                TypeLabel typeLabel = node.getType().label();
                if (typeLabel != TypeLabel.NODE) {
                    targetGraph.addEdge(nodeImage, result.mapLabel(typeLabel),
                        nodeImage);
                }
            }
        }
        // add edge images
        for (HostEdge edge : edgeSet()) {
            if (edge.target() instanceof ValueNode) {
                AspectNode sourceImage = result.getNode(edge.source());
                String constant = ((ValueNode) edge.target()).getSymbol();
                String let =
                    AspectKind.LET.getPrefix() + edge.label().text() + "="
                        + constant;
                AspectLabel label = AspectParser.getInstance().parse(let, HOST);
                targetGraph.addEdge(sourceImage, label, sourceImage);
            } else {
                AspectEdge edgeImage = result.mapEdge(edge);
                edgeImage.setFixed();
                targetGraph.addEdgeContext(edgeImage);
            }
        }
        GraphInfo.transfer(this, targetGraph, result);
        targetGraph.setFixed();
        return result;
    }

    /** The element factory of this host graph. */
    private final HostFactory factory;
}
