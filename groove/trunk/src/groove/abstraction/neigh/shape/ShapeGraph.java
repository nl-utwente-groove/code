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
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.algebra.Algebra;
import groove.graph.AbstractGraph;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
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
import java.util.Collections;
import java.util.Iterator;
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
        result = getNodeSet().add((ShapeNode) node);
        return result;
    }

    /**
     * Improved implementation taking advantage of the edge set.
     */
    @Override
    public boolean removeNode(HostNode node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean removed = getNodeSet().contains(node);
        if (removed) {
            Iterator<ShapeEdge> edgeIter = getEdgeSet().iterator();
            while (edgeIter.hasNext()) {
                ShapeEdge edge = edgeIter.next();
                if (edge.source().equals(node) || edge.target().equals(node)) {
                    edgeIter.remove();
                }
            }
            removeNodeWithoutCheck(node);
        }
        return removed;
    }

    public boolean removeEdge(HostEdge edge) {
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        return getEdgeSet().remove(edge);
    }

    @Override
    public boolean removeNodeSet(Collection<? extends HostNode> nodeSet) {
        boolean result;
        // first remove edges that depend on a node to be removed
        Iterator<ShapeEdge> edgeIter = getEdgeSet().iterator();
        while (edgeIter.hasNext()) {
            ShapeEdge other = edgeIter.next();
            if (nodeSet.contains(other.source())
                || nodeSet.contains(other.target())) {
                edgeIter.remove();
            }
        }
        // now remove the nodes
        result = removeNodeSetWithoutCheck(nodeSet);
        return result;
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(HostEdge edge) {
        assert isTypeCorrect(edge);
        boolean result;
        result = getEdgeSet().add((ShapeEdge) edge);
        return result;
    }

    public boolean removeNodeWithoutCheck(HostNode node) {
        assert isTypeCorrect(node);
        boolean result;
        result = getNodeSet().remove(node);
        return result;
    }

    @Override
    public boolean removeNodeSetWithoutCheck(
            Collection<? extends HostNode> nodeSet) {
        return getNodeSet().removeAll(nodeSet);
    }

    // ------------- general methods (see AbstractGraph) ----------

    public Set<? extends HostEdge> edgeSet() {
        return Collections.unmodifiableSet(getEdgeSet());
    }

    public Set<? extends HostNode> nodeSet() {
        return Collections.unmodifiableSet(getNodeSet());
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
            getEdgeMultMap(dir).clear();
        }
    }

    /** Retrieves the node set from the cache. */
    Set<ShapeNode> getNodeSet() {
        return getCache().getNodeSet();
    }

    /** Retrieves the edge set from the cache. */
    public Set<ShapeEdge> getEdgeSet() {
        return getCache().getEdgeSet();
    }

    /** Retrieves the edge set from the cache. */
    public Map<ShapeNode,Multiplicity> getNodeMultMap() {
        return getCache().getNodeMultMap();
    }

    /** Retrieves the edge set from the cache. */
    public EquivRelation<ShapeNode> getEquivRelation() {
        return getCache().getEquivRel();
    }

    /** Retrieves the edge set from the cache. */
    public Map<EdgeSignature,Multiplicity> getEdgeMultMap(EdgeMultDir dir) {
        return getCache().getEdgeMultMap(dir);
    }

    /** Flattened set of nodes, filled when the shape is fixed. */
    ShapeNode[] nodes;
    /** Flattened set of edges, filled when the shape is fixed. */
    ShapeEdge[] edges;
    /** Flattened node equivalence relation, filled when the shape is fixed. */
    byte[] nodeEquiv;
    /** Flattened node multiplicity map, filled when the shape is fixed. */
    byte[] nodeMult;
    /** Flattened incoming edge multiplicity map, filled when the shape is fixed. */
    EdgeRecord[] inEdgeMult;
    /** Flattened outgoing edge multiplicity map, filled when the shape is fixed. */
    EdgeRecord[] outEdgeMult;

    /** The element factory of this host graph. */
    private final ShapeFactory factory;

    /** Flag controlling if a memory-optimal implementation should be preferred. */
    private final static boolean COMPACT = true;

    /** Data structure holding the essentials of a single edge signature multiplicity. */
    static class EdgeRecord {
        public EdgeRecord(EdgeSignature sig, Multiplicity mult,
                ShapeFactory factory) {
            this.source = sig.getNode().getNumber();
            this.label = sig.getLabel();
            this.targets = new boolean[factory.getMaxNodeNr() + 1];
            for (ShapeNode node : sig.getEquivClass()) {
                this.targets[node.getNumber()] = true;
            }
            this.multIndex = mult.getIndex();
        }

        public EdgeSignature getSig(EdgeMultDir dir, ShapeFactory factory) {
            EquivClass<ShapeNode> cell = new NodeEquivClass<ShapeNode>(factory);
            for (int i = 0; i < this.targets.length; i++) {
                if (this.targets[i]) {
                    cell.add(factory.getNode(i));
                }
            }
            return new EdgeSignature(dir, factory.getNode(this.source),
                this.label, cell);
        }

        public Multiplicity getMult() {
            return Multiplicity.getMultiplicity(this.multIndex,
                MultKind.EDGE_MULT);
        }

        final int source;
        final TypeLabel label;
        final boolean[] targets;
        final byte multIndex;
    }
}
