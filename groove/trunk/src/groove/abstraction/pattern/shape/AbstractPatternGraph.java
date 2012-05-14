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

import java.util.ArrayList;
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
            HostEdge sEdge = Util.getBinaryEdges(pattern).iterator().next();
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
        // EDUARDO: Implement this...
        return false;
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
            SimpleMorphism morph = pEdge.getMorphism();
            for (HostNode sNode : sNodes) {
                if (morph.getPreImage(sNode) != null) {
                    nodes.remove(sNode);
                }
            }
            if (sEdges != null) {
                for (HostEdge sEdge : sEdges) {
                    if (morph.getPreImage(sEdge.source()) != null
                        && morph.getPreImage(sEdge.target()) != null) {
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

}
