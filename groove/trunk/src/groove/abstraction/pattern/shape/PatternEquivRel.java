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

import groove.abstraction.Multiplicity;
import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;

import java.util.Map;
import java.util.Set;

/**
 * Pattern equivalence relation computed over pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public final class PatternEquivRel {

    private final PatternShape pShape;
    private final Map<PatternNode,NodeEquivClass> nodeToCellMap;
    private final Map<PatternEdge,EdgeEquivClass> edgeToCellMap;
    private final Set<NodeEquivClass> nodeRel;
    private final Set<EdgeEquivClass> edgeRel;

    /** Default constructor. */
    public PatternEquivRel(PatternShape pShape) {
        this.pShape = pShape;
        this.nodeToCellMap = new MyHashMap<PatternNode,NodeEquivClass>();
        this.edgeToCellMap = new MyHashMap<PatternEdge,EdgeEquivClass>();
        this.nodeRel = new MyHashSet<NodeEquivClass>();
        this.edgeRel = new MyHashSet<EdgeEquivClass>();
        compute();
    }

    @Override
    public String toString() {
        return String.format(
            "Pattern equivalence relation:\nNodes: %s\nEdges:%s", this.nodeRel,
            this.edgeRel);
    }

    private void compute() {
        for (int i = this.pShape.depth; i >= 0; i--) {
            computeNodeEquiv(i);
            computeEdgeEquiv(i);
        }
    }

    private void computeNodeEquiv(int layer) {
        Map<NodeInfo,NodeEquivClass> partition =
            new MyHashMap<NodeInfo,NodeEquivClass>();
        for (PatternNode pNode : this.pShape.getLayerNodes(layer)) {
            NodeInfo nInfo = computeNodeInfo(pNode);
            NodeEquivClass nEc = partition.get(nInfo);
            if (nEc == null) {
                nEc = new NodeEquivClass();
                partition.put(nInfo, nEc);
            }
            nEc.add(pNode);
            this.nodeToCellMap.put(pNode, nEc);
        }
        this.nodeRel.addAll(partition.values());
    }

    private void computeEdgeEquiv(int layer) {
        Map<EdgeInfo,EdgeEquivClass> partition =
            new MyHashMap<EdgeInfo,EdgeEquivClass>();
        for (PatternEdge pEdge : this.pShape.getLayerEdges(layer)) {
            EdgeInfo eInfo = computeEdgeInfo(pEdge);
            EdgeEquivClass eEc = partition.get(eInfo);
            if (eEc == null) {
                eEc = new EdgeEquivClass();
                partition.put(eInfo, eEc);
            }
            eEc.add(pEdge);
            this.edgeToCellMap.put(pEdge, eEc);
        }
        this.edgeRel.addAll(partition.values());
    }

    private NodeInfo computeNodeInfo(PatternNode pNode) {
        NodeInfo nInfo = new NodeInfo(pNode);
        for (PatternEdge pEdge : this.pShape.outEdgeSet(pNode)) {
            EdgeEquivClass eEc = this.edgeToCellMap.get(pEdge);
            assert eEc != null;
            if (!nInfo.containsKey(eEc)) {
                nInfo.add(eEc, pNode);
            }
        }
        return nInfo;
    }

    private EdgeInfo computeEdgeInfo(PatternEdge pEdge) {
        EdgeInfo eInfo = new EdgeInfo(pEdge);
        return eInfo;
    }

    /** Basic getter. */
    public Set<NodeEquivClass> getNodeEquivRel() {
        return this.nodeRel;
    }

    /** Basic getter. */
    public Set<EdgeEquivClass> getEdgeEquivRel() {
        return this.edgeRel;
    }

    /** Equivalence class of pattern nodes. */
    public static class NodeEquivClass extends MyHashSet<PatternNode> {
        // Empty by design.
    }

    /** Equivalence class of pattern edges. */
    public static class EdgeEquivClass extends MyHashSet<PatternEdge> {
        // Empty by design.
    }

    private class NodeInfo extends MyHashMap<EdgeEquivClass,Multiplicity> {

        final TypeNode typeNode;
        int hashCode;

        NodeInfo(PatternNode pNode) {
            this.typeNode = pNode.getType();
        }

        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                final int prime = 31;
                int result = 1;
                result = prime * result + super.hashCode();
                result = prime * result + this.typeNode.hashCode();
                this.hashCode = result;
            }
            return this.hashCode;
        }

        @Override
        public boolean equals(Object other) {
            return this.hashCode() == other.hashCode();
        }

        void add(EdgeEquivClass eec, PatternNode pNode) {
            assert get(eec) == null;
            Multiplicity mult = Multiplicity.ZERO_EDGE_MULT;
            PatternShape pShape = PatternEquivRel.this.pShape;
            for (PatternEdge pEdge : eec) {
                if (pShape.outEdgeSet(pNode).contains(pEdge)) {
                    mult = mult.add(pShape.getMult(pEdge));
                }
            }
            put(eec, mult);
        }

        @Override
        public String toString() {
            return String.format("Node info: (%d, %s, %s)", this.hashCode,
                this.typeNode, super.toString());
        }

    }

    private class EdgeInfo {

        final TypeEdge typeEdge;
        final NodeEquivClass targetEc;
        int hashCode;

        EdgeInfo(PatternEdge pEdge) {
            this.typeEdge = pEdge.getType();
            this.targetEc =
                PatternEquivRel.this.nodeToCellMap.get(pEdge.target());
        }

        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                final int prime = 31;
                int result = 1;
                result = prime * result + this.typeEdge.hashCode();
                result = prime * result + this.targetEc.hashCode();
                this.hashCode = result;
            }
            return this.hashCode;
        }

        @Override
        public boolean equals(Object other) {
            return this.hashCode() == other.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Edge info: (%d, %s, %s)", this.hashCode,
                this.typeEdge, this.targetEc);
        }

    }
}
