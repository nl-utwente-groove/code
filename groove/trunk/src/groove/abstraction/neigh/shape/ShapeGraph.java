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
package groove.abstraction.neigh.shape;

import static groove.graph.GraphRole.HOST;
import static groove.graph.GraphRole.SHAPE;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.algebra.Algebra;
import groove.graph.AbstractGraph;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Class providing a default implementation of {@link HostGraph}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ShapeGraph extends AbstractGraph<HostNode,HostEdge> implements
        HostGraph {
    /**
     * Constructs an empty host graph.
     */
    public ShapeGraph(String name) {
        this(name, ShapeFactory.newInstance());
    }

    /**
     * Constructs an empty host graph, with a given host factory.
     */
    public ShapeGraph(String name, ShapeFactory factory) {
        super(COMPACT ? "shape graph" : name);
        assert factory != null;
        this.factory = factory;
    }

    public boolean addNode(HostNode node) {
        boolean result;
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        result = nodeSet().add((ShapeNode) node);
        return result;
    }

    public boolean removeEdge(HostEdge edge) {
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        return edgeSet().remove(edge);
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(HostEdge edge) {
        assert !isFixed();
        assert isTypeCorrect(edge);
        boolean result;
        result = edgeSet().add((ShapeEdge) edge);
        return result;
    }

    public boolean removeNodeWithoutCheck(HostNode node) {
        assert !isFixed();
        assert isTypeCorrect(node);
        boolean result;
        result = nodeSet().remove(node);
        return result;
    }

    @Override
    public boolean removeNodeSetWithoutCheck(
            Collection<? extends HostNode> nodeSet) {
        return nodeSet().removeAll(nodeSet);
    }

    // ------------- general methods (see AbstractGraph) ----------

    public Set<ShapeEdge> edgeSet() {
        return getCache().getEdgeSet();
    }

    public Set<ShapeNode> nodeSet() {
        return getCache().getNodeSet();
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
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> edgeSet(Node node) {
        return (Set<ShapeEdge>) super.edgeSet(node);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> outEdgeSet(Node node) {
        return (Set<ShapeEdge>) super.outEdgeSet(node);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<ShapeEdge> inEdgeSet(Node node) {
        return (Set<ShapeEdge>) super.inEdgeSet(node);
    }

    @Override
    public ShapeEdge createEdge(HostNode source, Label label, HostNode target) {
        return (ShapeEdge) super.createEdge(source, label, target);
    }

    @Override
    public ShapeGraph clone() {
        ShapeGraph result = newGraph(getName());
        result.getCache().copyFrom(this);
        GraphInfo.transfer(this, result, null);
        return result;
    }

    @Override
    public GraphRole getRole() {
        return SHAPE;
    }

    @Override
    public ShapeGraph newGraph(String name) {
        return new ShapeGraph(name, getFactory());
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof ShapeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof ShapeEdge;
    }

    @Override
    public ShapeFactory getFactory() {
        return this.factory;
    }

    @Override
    public TypeGraph getTypeGraph() {
        return getFactory().getTypeFactory().getGraph();
    }

    @Override
    public ShapeGraph retype(TypeGraph typeGraph) throws FormatException {
        throw new UnsupportedOperationException();
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
                targetGraph.addEdge(edgeImage);
            }
        }
        GraphInfo.transfer(this, targetGraph, result);
        targetGraph.setFixed();
        return result;
    }

    @Override
    public void setFixed() {
        getCache().flatten();
        super.setFixed();
    }

    @Override
    public ShapeCache getCache() {
        return (ShapeCache) super.getCache();
    }

    @Override
    protected ShapeCache createCache() {
        return new ShapeCache(this);
    }

    /**
     * Clears all non-graph structures of the shape so they can be loaded from
     * a file. Be very careful with this method, since it destroys all
     * additional information in the shape apart from the graph structure.
     */
    public void clearStructuresForLoading() {
        getEquivRelation().clear();
        getNodeMultMap().clear();
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            getEdgeSigStore(dir).clear();
        }
    }

    /** Returns the node multiplicity map. */
    public Map<ShapeNode,Multiplicity> getNodeMultMap() {
        return getCache().getNodeMultMap();
    }

    /** Returns the edge signature multiplicity map for a given direction. */
    public Map<EdgeSignature,Multiplicity> getEdgeMultMap(EdgeMultDir dir) {
        return getEdgeSigStore(dir).getMultMap();
    }

    /** Returns the set of all edge signatures for a given direction. */
    public Set<EdgeSignature> getEdgeSigSet(EdgeMultDir dir) {
        return getEdgeMultMap(dir).keySet();
    }

    /** Returns the node equivalence relation of the shape. */
    public EquivRelation<ShapeNode> getEquivRelation() {
        return getCache().getEquivRel();
    }

    /**
     * Returns the equivalence class of the given node. 
     * Fails in an assertion if the given node is in the shape. 
     */
    public EquivClass<ShapeNode> getEquivClassOf(ShapeNode node) {
        assert this.nodeSet().contains(node) : "Node " + node
            + " is not in the shape!";
        return getEquivRelation().getEquivClassOf(node);
    }

    /** Returns the edge signature store for a given direction. */
    public EdgeSignatureStore getEdgeSigStore(EdgeMultDir dir) {
        return getCache().getEdgeSigStore(dir);
    }

    /** Factory method for a given edge direction. */
    EdgeSignatureStore createEdgeSigStore() {
        return new EdgeSignatureStore(this);
    }

    /** Factory method for edge signatures based on this shape. */
    EdgeSignature createEdgeSignature(EdgeMultDir direction, ShapeNode node,
            TypeLabel label, EquivClass<ShapeNode> ec) {
        return new EdgeSignature(direction, node, label, ec);
    }

    /** Factory method for an edge signature covering a given shape edge. */
    EdgeSignature createEdgeSignature(EdgeMultDir direction, ShapeEdge edge) {
        ShapeNode node = direction.incident(edge);
        TypeLabel label = edge.label();
        EquivClass<ShapeNode> ec =
            this.getEquivClassOf(direction.opposite(edge));
        return createEdgeSignature(direction, node, label, ec);
    }

    /** Flattened representation of the shape graph, filled when the shape is fixed. */
    ShapeStore store;
    /** The element factory of this host graph. */
    private final ShapeFactory factory;

    /** Flag controlling if a memory-optimal implementation should be preferred. */
    private final static boolean COMPACT = true;
}
