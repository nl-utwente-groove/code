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
import groove.graph.NodeSetEdgeSetGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Pair;

import java.util.ArrayList;
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

    /** Maximal depth of the graph. */
    protected int depth;

    private final List<Set<N>> layers;

    /** Default constructor. */
    protected AbstractPatternGraph(String name) {
        super(name);
        this.depth = 0;
        this.layers = new ArrayList<Set<N>>();
    }

    /** Checks if this pattern graph is well-formed. */
    public boolean isWellFormed() {
        assert isFixed();
        // Check node layer.
        for (N pNode : getLayerNodes(0)) {
            HostGraph pattern = pNode.getPattern();
            if (pattern.nodeCount() != 1
                && Util.getBinaryEdgesCount(pattern) == 0) {
                return false;
            }
        }
        // Check edge layer.
        for (N pNode : getLayerNodes(1)) {
            HostGraph pattern = pNode.getPattern();
            if (Util.getBinaryEdgesCount(pattern) != 1) {
                return false;
            }
            HostEdge sEdge = pNode.getSimpleEdge();
            @SuppressWarnings("unchecked")
            Set<HostNode> sNodes = (Set<HostNode>) pattern.nodeSet();
            Set<HostNode> edgeNodes = new MyHashSet<HostNode>();
            edgeNodes.add(sEdge.source());
            edgeNodes.add(sEdge.target());
            if (!sNodes.containsAll(edgeNodes)
                || !edgeNodes.containsAll(sNodes)) {
                return false;
            }
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
        // Iterate from layer 2 on.
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
        Set<N> result = null;
        boolean create = false;
        if (layer >= this.layers.size()) {
            create = true;
        } else {
            result = this.layers.get(layer);
            if (result == null) {
                create = true;
            }
        }
        if (create) {
            result = new MyHashSet<N>();
            this.layers.add(layer, result);
        }
        assert result != null;
        return result;
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
    protected void addToLayer(N node) {
        int layer = node.getLayer();
        getLayerNodes(layer).add(node);
        if (this.depth < layer) {
            this.depth = layer;
        }
    }

    /** Returns the set of pattern edges that cover the given simple node. */
    private Set<E> getCoveringEdges(N target, HostNode sNode) {
        Set<E> result = new MyHashSet<E>();
        for (E edge : inEdgeSet(target)) {
            if (edge.isCod(sNode)) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the pattern edge that cover the given simple node. */
    protected E getCoveringEdge(N target, HostNode sNode) {
        assert target.isEdgePattern();
        for (E edge : inEdgeSet(target)) {
            if (edge.isCod(sNode)) {
                return edge;
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
        if (ancestors.size() == 1) {
            return ancestors.iterator().next();
        } else {
            return null;
        }
    }
}
