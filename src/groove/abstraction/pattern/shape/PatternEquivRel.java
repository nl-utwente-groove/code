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
import java.util.Map.Entry;
import java.util.Set;

/**
 * Pattern equivalence relation computed over pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public final class PatternEquivRel {

    private final PatternShape pShape;
    private final Map<PatternNode,NodeEquivClass> nodeToCellMap;
    private final Map<PatternEdge,AuxEdgeEquivClass> edgeToCellMap;
    private final Set<NodeEquivClass> nodeRel;
    private final Set<EdgeEquivClass> edgeRel; // Finer relation.

    /** Default constructor. */
    public PatternEquivRel(PatternShape pShape) {
        this.pShape = pShape;
        this.nodeToCellMap = new MyHashMap<PatternNode,NodeEquivClass>();
        this.edgeToCellMap = new MyHashMap<PatternEdge,AuxEdgeEquivClass>();
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
                // Compute the fine grained edge partition.
                this.edgeRel.addAll(computeFinerEdgeRel(nInfo, nEc, pNode));
            }
            nEc.add(pNode);
            this.nodeToCellMap.put(pNode, nEc);
        }
        this.nodeRel.addAll(partition.values());
    }

    private void computeEdgeEquiv(int layer) {
        Map<EdgeInfo,AuxEdgeEquivClass> partition =
            new MyHashMap<EdgeInfo,AuxEdgeEquivClass>();
        for (PatternEdge pEdge : this.pShape.getLayerEdges(layer)) {
            EdgeInfo eInfo = computeEdgeInfo(pEdge);
            AuxEdgeEquivClass eEc = partition.get(eInfo);
            if (eEc == null) {
                eEc = new AuxEdgeEquivClass();
                partition.put(eInfo, eEc);
            }
            eEc.add(pEdge);
            this.edgeToCellMap.put(pEdge, eEc);
        }
    }

    private NodeInfo computeNodeInfo(PatternNode pNode) {
        NodeInfo nInfo = new NodeInfo(pNode);
        for (PatternEdge pEdge : this.pShape.outEdgeSet(pNode)) {
            AuxEdgeEquivClass eEc = this.edgeToCellMap.get(pEdge);
            assert eEc != null;
            if (!nInfo.containsKey(eEc)) {
                nInfo.add(eEc, pNode);
            }
        }
        return nInfo;
    }

    private Set<EdgeEquivClass> computeFinerEdgeRel(NodeInfo nInfo,
            NodeEquivClass sourceEc, PatternNode pNode) {
        Set<EdgeEquivClass> result = new MyHashSet<EdgeEquivClass>();
        for (Entry<AuxEdgeEquivClass,Multiplicity> entry : nInfo.entrySet()) {
            PatternEdge pEdge = entry.getKey().iterator().next();
            NodeEquivClass targetEc = this.nodeToCellMap.get(pEdge.target());
            Multiplicity mult = entry.getValue();
            EdgeEquivClass eEc =
                new EdgeEquivClass(sourceEc, pEdge.getType(), targetEc, mult);
            result.add(eEc);
        }
        return result;
    }

    private EdgeInfo computeEdgeInfo(PatternEdge pEdge) {
        EdgeInfo eInfo = new EdgeInfo(pEdge);
        return eInfo;
    }

    /** Returns the computed node equivalence relation. */
    public Set<NodeEquivClass> getNodeEquivRel() {
        return this.nodeRel;
    }

    /**
     * Returns the fine grained edge equivalence relation. This is the relation
     * that we use to build pattern edges when constructing a canonical pattern
     * shape.
     * Note that the set objects do not actually have the edges that are part
     * of the equivalence class, we just need the multiplicity.  
     */
    public Set<EdgeEquivClass> getEdgeEquivRel() {
        return this.edgeRel;
    }

    /** Equivalence class of pattern nodes. */
    public static class NodeEquivClass extends MyHashSet<PatternNode> {

        /** Returns the bounded multiplicity of this class. */
        public Multiplicity getMult() {
            return Multiplicity.getNodeSetMult(this);
        }

    }

    /** Equivalence class of pattern edges. This is the coarser relation. */
    private static class AuxEdgeEquivClass extends MyHashSet<PatternEdge> {
        // Empty by design.
    }

    /** Equivalence class of pattern edges. This is the finer relation. */
    public static class EdgeEquivClass {

        final NodeEquivClass sourceEc;
        final NodeEquivClass targetEc;
        final TypeEdge typeEdge;
        final Multiplicity mult;

        EdgeEquivClass(NodeEquivClass sourceEc, TypeEdge typeEdge,
                NodeEquivClass targetEc, Multiplicity mult) {
            this.sourceEc = sourceEc;
            this.targetEc = targetEc;
            this.typeEdge = typeEdge;
            this.mult = mult;
        }

        @Override
        public String toString() {
            return String.format("%s--%s-->%s", this.sourceEc, this.mult,
                this.targetEc);
        }
    }

    /** Information used to partition nodes. */
    private class NodeInfo extends MyHashMap<AuxEdgeEquivClass,Multiplicity> {

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

        void add(AuxEdgeEquivClass eEc, PatternNode pNode) {
            assert get(eEc) == null;
            Multiplicity mult = Multiplicity.ZERO_EDGE_MULT;
            PatternShape pShape = PatternEquivRel.this.pShape;
            for (PatternEdge pEdge : eEc) {
                if (pShape.outEdgeSet(pNode).contains(pEdge)) {
                    mult = mult.add(pShape.getMult(pEdge));
                }
            }
            put(eEc, mult);
        }

        @Override
        public String toString() {
            return String.format("Node info: (%d, %s, %s)", this.hashCode,
                this.typeNode, super.toString());
        }

    }

    /** Information used to partition edges. */
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
