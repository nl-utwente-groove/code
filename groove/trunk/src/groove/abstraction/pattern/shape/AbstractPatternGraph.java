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
package groove.abstraction.pattern.shape;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.Util;
import groove.graph.EdgeRole;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Duo;
import groove.util.Pair;
import groove.util.UnmodifiableSetView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Common implementation of pattern graphs.
 * 
 * @author Eduardo Zambon
 */
public abstract class AbstractPatternGraph<N extends AbstractPatternNode,E extends AbstractPatternEdge<N>>
        extends NodeSetEdgeSetGraph<N,E> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Maximal depth of the graph. */
    protected int depth;
    /** Layers of pattern nodes. */
    protected final List<Set<N>> layers;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    protected AbstractPatternGraph(String name) {
        super(name);
        this.depth = 0;
        this.layers = new ArrayList<Set<N>>();
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.PATTERN;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Set<N> nodeSet() {
        return (Set<N>) super.nodeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<E> edgeSet() {
        return (Set<E>) super.edgeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<E> edgeSet(Node node) {
        return (Set<E>) super.edgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<E> outEdgeSet(Node node) {
        return (Set<E>) super.outEdgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<E> inEdgeSet(Node node) {
        return (Set<E>) super.inEdgeSet(node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pattern graph: " + getName() + " (depth = " + depth()
            + ")\n");
        sb.append("Nodes: [");
        for (N node : nodeSet()) {
            sb.append(node.toString() + ", ");
        }
        if (nodeSet().isEmpty()) {
            sb.append("]\n");
        } else {
            sb.replace(sb.length() - 2, sb.length(), "]\n");
        }
        sb.append("Edges: [");
        for (E edge : edgeSet()) {
            sb.append(edge.toString() + ", ");
        }
        if (edgeSet().isEmpty()) {
            sb.append("]\n");
        } else {
            sb.replace(sb.length() - 2, sb.length(), "]\n");
        }
        return sb.toString();
    }

    @Override
    public boolean addNode(N node) {
        boolean result = super.addNode(node);
        if (result) {
            addToLayer(node);
        }
        return result;
    }

    @Override
    public boolean removeNode(N node) {
        boolean result = super.removeNode(node);
        if (result) {
            removeFromLayer(node);
        }
        return result;
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            if (getInfo() == null) {
                setInfo(new GraphInfo<N,E>());
            }
            super.setFixed();
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Checks if the conditions for each pattern graph type are satisfied.
     * To be overriden in subclasses.
     */
    public boolean isWellDefined() {
        return isWellFormed();
    }

    /** Checks if this pattern graph is well-formed. */
    public boolean isWellFormed() {
        // Check node layer.
        for (N pNode : getLayerNodes(0)) {
            HostGraph pattern = pNode.getPattern();
            if (pattern.nodeCount() != 1
                || Util.getBinaryEdgesCount(pattern) != 0) {
                return false;
            }
        }
        // Check the other layers.
        for (int i = 1; i <= this.depth; i++) {
            for (N pNode : getLayerNodes(i)) {
                HostGraph pattern = pNode.getPattern();
                if (Util.getBinaryEdgesCount(pattern) != i) {
                    return false;
                }
                if (!isCovered(pNode)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Checks if this pattern graph is commuting. */
    public boolean isCommuting() {
        // This method assumes that the pattern graph is well formed.
        // First, check layer 1.
        for (N pNode : getLayerNodes(1)) {
            if (pNode.getSimpleEdge().isLoop()) {
                // This is self-edge, nothing to do.
                continue;
            }
            HostNode sSrc = pNode.getSource();
            HostNode sTgt = pNode.getTarget();
            N pSrc = getCoveringEdge(pNode, sSrc).source();
            N pTgt = getCoveringEdge(pNode, sTgt).source();
            if (pSrc.equals(pTgt)) {
                // We have a binary edge with both source and target nodes
                // covered by the same node at layer 0.
                return false;
            }
        }
        // Now, iterate from layer 2 on.
        for (int layer = 2; layer <= depth(); layer++) {
            // For each pattern node on this layer.
            for (N pNode : getLayerNodes(layer)) {
                // For each simple node in the pattern.
                for (HostNode sNode : pNode.getPattern().nodeSet()) {
                    if (!hasCommonAncestor(pNode, sNode)) {
                        return false;
                    }
                }
                // For each simple edge in the pattern.
                for (HostEdge sEdge : pNode.getPattern().edgeSet()) {
                    if (!hasCommonAncestor(pNode, sEdge)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Returns the depth of this pattern graph. */
    public int depth() {
        return this.depth;
    }

    /** Returns the set of nodes for the given layer number. */
    public Set<N> getLayerNodes(int layer) {
        assert layer >= 0;
        if (layer >= this.layers.size()) {
            for (int i = this.layers.size(); i <= layer; i++) {
                this.layers.add(i, new MyHashSet<N>());
            }
        }
        return this.layers.get(layer);
    }

    /** Returns the set of edges incoming to the nodes in the given layer. */
    public Set<E> getLayerInEdges(int layer) {
        Set<E> result = new MyHashSet<E>();
        for (N pNode : getLayerNodes(layer)) {
            result.addAll(inEdgeSet(pNode));
        }
        return result;
    }

    /** Returns the set of edges outgoing from the nodes in the given layer. */
    public Set<E> getLayerOutEdges(int layer) {
        Set<E> result = new MyHashSet<E>();
        for (N pNode : getLayerNodes(layer)) {
            result.addAll(outEdgeSet(pNode));
        }
        return result;
    }

    /** Checks if the given node is properly covered. */
    @SuppressWarnings("unchecked")
    private boolean isCovered(N pNode) {
        HostGraph pattern = pNode.getPattern();
        Set<HostEdge> sEdges;
        if (pNode.getLayer() == 1) {
            sEdges = null;
        } else {
            sEdges = Util.getBinaryEdges(pattern);
        }
        return isCovered(pNode, (Set<HostNode>) pattern.nodeSet(), sEdges);
    }

    private boolean isCovered(N pNode, Set<HostNode> sNodes,
            Set<HostEdge> sEdges) {
        Set<HostNode> nodes = new MyHashSet<HostNode>();
        nodes.addAll(sNodes);
        Set<HostEdge> edges = new MyHashSet<HostEdge>();
        if (sEdges != null) {
            edges.addAll(sEdges);
        }
        for (E pEdge : inEdgeSet(pNode)) {
            for (HostNode sNode : sNodes) {
                if (pEdge.isCod(sNode)) {
                    nodes.remove(sNode);
                }
            }
            if (sEdges != null) {
                for (HostEdge sEdge : sEdges) {
                    if (pEdge.isCod(sEdge.source())
                        && pEdge.isCod(sEdge.target())) {
                        edges.remove(sEdge);
                    }
                }
            }
        }
        return nodes.isEmpty() && edges.isEmpty();
    }

    /** Adds the given node to the appropriate layer. */
    final void addToLayer(N pNode) {
        int layer = pNode.getLayer();
        getLayerNodes(layer).add(pNode);
        if (this.depth < layer) {
            this.depth = layer;
        }
    }

    /** Removes the given node from the appropriate layer. */
    private void removeFromLayer(N pNode) {
        int layer = pNode.getLayer();
        getLayerNodes(layer).remove(pNode);
        if (this.depth == layer) {
            while (getLayerNodes(layer).isEmpty() && layer >= 0) {
                layer--;
            }
            this.depth = layer;
        }
    }

    /** Returns the set of pattern edges that cover the given simple node. */
    private Set<E> getCoveringEdges(N pNode, final HostNode sNode) {
        return new UnmodifiableSetView<E>(inEdgeSet(pNode)) {
            @Override
            public boolean approves(Object obj) {
                if (!(obj instanceof AbstractPatternEdge<?>)) {
                    return false;
                }
                AbstractPatternEdge<?> pEdge = (AbstractPatternEdge<?>) obj;
                return pEdge.isCod(sNode);
            }
        };
    }

    /** Returns the set of pattern edges that cover the given simple edge. */
    private Set<E> getCoveringEdges(N pNode, final HostEdge sNode) {
        return new UnmodifiableSetView<E>(inEdgeSet(pNode)) {
            @Override
            public boolean approves(Object obj) {
                if (!(obj instanceof AbstractPatternEdge<?>)) {
                    return false;
                }
                AbstractPatternEdge<?> pEdge = (AbstractPatternEdge<?>) obj;
                return pEdge.isCod(sNode);
            }
        };
    }

    /** Returns the pattern edge that covers the given simple node. */
    public E getCoveringEdge(N pNode, HostNode sNode) {
        assert pNode.isEdgePattern();
        for (E pEdge : inEdgeSet(pNode)) {
            if (pEdge.isCod(sNode)) {
                return pEdge;
            }
        }
        return null;
    }

    /**
     * Checks if the given simple node (from the given pattern node) has a
     * single common ancestor.
     */
    private boolean hasCommonAncestor(N pNode, HostNode sNode) {
        return getAncestors(pNode, sNode).size() == 1;
    }

    /**
     * Returns a list of ancestors for the given simple node (from the given
     * pattern node).
     */
    public Set<N> getAncestors(N pNode, HostNode sNode) {
        Set<N> result = new MyHashSet<N>();
        List<Pair<N,HostNode>> queue = new LinkedList<Pair<N,HostNode>>();
        Set<E> coveringEdges = getCoveringEdges(pNode, sNode);
        for (E pEdge : coveringEdges) {
            N pN = pEdge.source();
            if (pN.getLayer() == 0) {
                result.add(pN);
            } else {
                HostNode sN = pEdge.getPreImage(sNode);
                queue.add(new Pair<N,HostNode>(pN, sN));
            }
        }
        while (!queue.isEmpty()) {
            Pair<N,HostNode> pair = queue.remove(0);
            N pN = pair.one();
            HostNode sN = pair.two();
            coveringEdges = getCoveringEdges(pN, sN);
            for (E pEdge : coveringEdges) {
                N newPN = pEdge.source();
                if (newPN.getLayer() == 0) {
                    result.add(newPN);
                } else {
                    HostNode newSN = pEdge.getPreImage(sN);
                    Pair<N,HostNode> newPair =
                        new Pair<N,HostNode>(newPN, newSN);
                    if (!queue.contains(newPair)) {
                        queue.add(newPair);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks if the given simple edge (from the given pattern node) has a
     * single common ancestor.
     */
    private boolean hasCommonAncestor(N pNode, HostEdge sEdge) {
        if (sEdge.getRole() != EdgeRole.BINARY) {
            // We don't care about types and flags.
            return true;
        }
        return getAncestors(pNode, sEdge).size() == 1;
    }

    /**
     * Returns a list of ancestors for the given simple edge (from the given
     * pattern node).
     */
    public Set<N> getAncestors(N pNode, HostEdge sEdge) {
        Set<N> result = new MyHashSet<N>();
        List<Pair<N,HostEdge>> queue = new LinkedList<Pair<N,HostEdge>>();
        Set<E> coveringEdges = getCoveringEdges(pNode, sEdge);
        for (E pEdge : coveringEdges) {
            N pN = pEdge.source();
            HostEdge sE = pEdge.getPreImage(sEdge);
            if (pN.getLayer() == 1 && pN.introduces(sE)) {
                result.add(pN);
            } else {
                queue.add(new Pair<N,HostEdge>(pN, sE));
            }
        }
        while (!queue.isEmpty()) {
            Pair<N,HostEdge> pair = queue.remove(0);
            N pN = pair.one();
            HostEdge sE = pair.two();
            coveringEdges = getCoveringEdges(pN, sE);
            for (E pEdge : coveringEdges) {
                N newPN = pEdge.source();
                HostEdge newSE = pEdge.getPreImage(sE);
                if (newPN.getLayer() == 1 && newPN.introduces(newSE)) {
                    result.add(newPN);
                } else {
                    Pair<N,HostEdge> newPair =
                        new Pair<N,HostEdge>(newPN, newSE);
                    if (!queue.contains(newPair)) {
                        queue.add(newPair);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the set of pattern nodes that are reachable from the ones given
     * on the list. Elements of the list are also included in the result set. 
     */
    public SortedSet<N> getDownwardTraversal(List<N> toTraverse) {
        SortedSet<N> result = new TreeSet<N>(AbstractPatternNode.comparator);
        while (!toTraverse.isEmpty()) {
            N node = toTraverse.remove(toTraverse.size() - 1);
            if (!result.contains(node)) {
                for (E edge : outEdgeSet(node)) {
                    toTraverse.add(edge.target());
                }
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns the set of pattern nodes that are reachable from the ones given
     * on the list. Elements of the list are also included in the result set. 
     */
    public SortedSet<N> getDownwardTraversal(N node) {
        List<N> toTraverse = new LinkedList<N>();
        toTraverse.add(node);
        return getDownwardTraversal(toTraverse);
    }

    /** Returns the set of ancestors of the given node from the edge layer.*/
    public Set<N> getEdgeLayerAncestors(N node) {
        assert node.getLayer() > 1;
        Set<N> result = new MyHashSet<N>();
        List<E> toTraverse = new LinkedList<E>();
        toTraverse.addAll(inEdgeSet(node));
        while (!toTraverse.isEmpty()) {
            E edge = toTraverse.remove(0);
            N source = edge.source();
            if (source.getLayer() == 1) {
                result.add(source);
            } else {
                toTraverse.addAll(inEdgeSet(source));
            }
        }
        return result;
    }

    /** Returns the two incoming edges of the node. */
    public Duo<E> getIncomingEdges(N node) {
        Iterator<E> iter = inEdgeSet(node).iterator();
        E d1 = iter.next();
        E d2;
        if (iter.hasNext()) {
            d2 = iter.next();
        } else {
            // Special case for level 1 nodes with only one incoming edge.
            d2 = d1;
        }
        return new Duo<E>(d1, d2);
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    protected E createEdge(N source, Label label, N target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public N addNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E addEdge(N source, String label, N target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E addEdge(N source, Label label, N target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addNodeSet(Collection<? extends N> nodeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeSet(Collection<? extends E> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdgeSet(Collection<? extends E> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mergeNodes(N from, N to) {
        throw new UnsupportedOperationException();
    }
}
