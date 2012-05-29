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
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.NodeSetEdgeSetGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    private final List<Set<N>> layers;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    protected AbstractPatternGraph(String name) {
        super(name);
        this.depth = 0;
        this.layers = new ArrayList<Set<N>>();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pattern graph: " + getName() + "\n");
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

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

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
        // Check edge layer.
        for (N pNode : getLayerNodes(1)) {
            HostGraph pattern = pNode.getPattern();
            if (Util.getBinaryEdgesCount(pattern) != 1) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Set<HostNode> sNodes = (Set<HostNode>) pattern.nodeSet();
            if (!isCovered(pNode, sNodes, null)) {
                return false;
            }
        }
        // Check the other layers.
        for (int i = 2; i <= this.depth; i++) {
            for (N pNode : getLayerNodes(i)) {
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
        layerOneLoop: for (N pNode : getLayerNodes(1)) {
            if (pNode.getSimpleEdge().isLoop()) {
                // This is self-edge, nothing to do.
                continue layerOneLoop;
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
                    Set<E> coverEdges = getCoveringEdges(pNode, sNode);
                    if (!hasCommonAncestor(coverEdges, sNode)) {
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

    @SuppressWarnings("unchecked")
    private boolean isCovered(N pNode) {
        HostGraph pattern = pNode.getPattern();
        return isCovered(pNode, (Set<HostNode>) pattern.nodeSet(),
            Util.getBinaryEdges(pattern));
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
    protected void addToLayer(N pNode) {
        int layer = pNode.getLayer();
        getLayerNodes(layer).add(pNode);
        if (this.depth < layer) {
            this.depth = layer;
        }
    }

    /** Returns the set of pattern edges that cover the given simple node. */
    private Set<E> getCoveringEdges(N pNode, HostNode sNode) {
        Set<E> result = new MyHashSet<E>();
        for (E pEdge : inEdgeSet(pNode)) {
            if (pEdge.isCod(sNode)) {
                result.add(pEdge);
            }
        }
        return result;
    }

    /** Returns the pattern edge that covers the given simple node. */
    protected E getCoveringEdge(N pNode, HostNode sNode) {
        assert pNode.isEdgePattern();
        for (E pEdge : inEdgeSet(pNode)) {
            if (pEdge.isCod(sNode)) {
                return pEdge;
            }
        }
        return null;
    }

    private boolean hasCommonAncestor(Set<E> coverEdges, HostNode sNode) {
        return getCommonAncestor(coverEdges, sNode) != null;
    }

    private HostNode getCommonAncestor(Set<E> coverEdges, HostNode sNode) {
        Set<HostNode> ancestors = new MyHashSet<HostNode>();
        List<Pair<N,HostNode>> queue = new LinkedList<Pair<N,HostNode>>();
        for (E coverEdge : coverEdges) {
            HostNode preImage = coverEdge.getPreImage(sNode);
            queue.add(new Pair<N,HostNode>(coverEdge.source(), preImage));
            ancestors.add(preImage);
        }
        while (ancestors.size() > 1) {
            Pair<N,HostNode> pair = queue.remove(0);
            N pNode = pair.one();
            HostNode ancestor = pair.two();
            ancestors.remove(ancestor);
            Set<E> newCoverEdges = getCoveringEdges(pNode, ancestor);
            assert newCoverEdges.size() <= 1;
            if (!newCoverEdges.isEmpty()) {
                E newCoverEdge = newCoverEdges.iterator().next();
                HostNode newAncestor = newCoverEdge.getPreImage(ancestor);
                queue.add(new Pair<N,HostNode>(newCoverEdge.source(),
                    newAncestor));
                ancestors.add(newAncestor);
            }
        }
        if (ancestors.size() == 1 && queue.size() == 1) {
            return ancestors.iterator().next();
        } else {
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public Set<? extends E> labelEdgeSet(Label label) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected GraphInfo<N,E> createInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphInfo<N,E> setInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

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
