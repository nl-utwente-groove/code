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

import static groove.abstraction.Multiplicity.ONE_EDGE_MULT;
import static groove.abstraction.Multiplicity.ONE_NODE_MULT;
import static groove.abstraction.Multiplicity.ZERO_EDGE_MULT;
import static groove.abstraction.Multiplicity.ZERO_NODE_MULT;
import groove.abstraction.Multiplicity;
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.shape.PatternEquivRel.EdgeEquivClass;
import groove.abstraction.pattern.shape.PatternEquivRel.NodeEquivClass;
import groove.util.Duo;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Pattern shape.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShape extends PatternGraph {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Multiplicity maps. */
    private final Map<PatternNode,Multiplicity> nodeMultMap;
    private final Map<PatternEdge,Multiplicity> edgeMultMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. Creates an empty pattern shape. */
    public PatternShape(String name, TypeGraph type) {
        super(name, type);
        this.nodeMultMap = new MyHashMap<PatternNode,Multiplicity>();
        this.edgeMultMap = new MyHashMap<PatternEdge,Multiplicity>();
    }

    /**
     * Constructs a pattern shape from the given pattern graph.
     * Used only when computing the start state of a PSTS.
     */
    public PatternShape(PatternGraph pGraph) {
        this(pGraph.getName(), pGraph.getTypeGraph());
        for (PatternNode pNode : pGraph.nodeSet()) {
            addNode(pNode);
        }
        for (PatternEdge pEdge : pGraph.edgeSet()) {
            addEdgeWithoutCheck(pEdge);
        }
    }

    /** Copying constructor. */
    private PatternShape(PatternShape pShape) {
        this((PatternGraph) pShape);
        this.nodeMultMap.putAll(pShape.nodeMultMap);
        this.edgeMultMap.putAll(pShape.edgeMultMap);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pattern shape: " + getName() + "\n");
        sb.append("Nodes: [");
        for (PatternNode node : nodeSet()) {
            sb.append(node.toString() + "(" + getMult(node) + "), ");
        }
        if (nodeSet().isEmpty()) {
            sb.append("]\n");
        } else {
            sb.replace(sb.length() - 2, sb.length(), "]\n");
        }
        sb.append("Edges: [");
        for (PatternEdge edge : edgeSet()) {
            sb.append(edge.toString() + "(" + getMult(edge) + "), ");
        }
        if (edgeSet().isEmpty()) {
            sb.append("]\n");
        } else {
            sb.replace(sb.length() - 2, sb.length(), "]\n");
        }
        return sb.toString();
    }

    @Override
    public boolean addNode(PatternNode node) {
        boolean result = super.addNode(node);
        if (result) {
            this.nodeMultMap.put(node, ONE_NODE_MULT);
        }
        return result;
    }

    @Override
    public boolean addEdgeWithoutCheck(PatternEdge edge) {
        boolean result = super.addEdgeWithoutCheck(edge);
        if (result) {
            this.edgeMultMap.put(edge, ONE_EDGE_MULT);
        }
        return result;
    }

    @Override
    public boolean removeNodeWithoutCheck(PatternNode node) {
        boolean result = super.removeNodeWithoutCheck(node);
        if (result) {
            this.nodeMultMap.remove(node);
        }
        return result;
    }

    @Override
    public boolean removeEdge(PatternEdge edge) {
        boolean result = super.removeEdge(edge);
        if (result) {
            this.edgeMultMap.remove(edge);
        }
        return result;
    }

    @Override
    protected void fireRemoveEdge(PatternEdge edge) {
        super.fireRemoveEdge(edge);
        this.edgeMultMap.remove(edge);
    }

    @Override
    public PatternShape clone() {
        return new PatternShape(this);
    }

    @Override
    public boolean isWellDefined() {
        if (!super.isWellDefined()) {
            return false;
        }
        for (PatternNode pNode : nodeSet()) {
            Multiplicity pMult = getMult(pNode);
            for (TypeEdge tEdge : getIncomingEdgeTypes(pNode)) {
                Multiplicity acc =
                    Multiplicity.getMultiplicity(0, 0, MultKind.EQSYS_MULT);
                for (PatternEdge dEdge : getInEdgesWithType(pNode, tEdge)) {
                    Multiplicity srcMult = getMult(dEdge.source());
                    Multiplicity dMult = getMult(dEdge);
                    acc = acc.add(srcMult.times(dMult));
                }
                // EZ says: equality is too strong! The definition is wrong.
                // if (!pMult.equals(acc.toNodeKind())) {
                if (!pMult.subsumes(acc.toNodeKind())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Duo<PatternEdge> getIncomingEdges(PatternNode node) {
        assert isUniquelyCovered(node);
        return super.getIncomingEdges(node);
    }

    @Override
    public void prepareClosure(Match match) {
        throw new UnsupportedOperationException(
            "Cannot close a pattern shape directly.");
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns an unmodifiable view of the node multiplicity map. */
    public Map<PatternNode,Multiplicity> getNodeMultMap() {
        return Collections.unmodifiableMap(this.nodeMultMap);
    }

    /** Returns an unmodifiable view of the edge multiplicity map. */
    public Map<PatternEdge,Multiplicity> getEdgeMultMap() {
        return Collections.unmodifiableMap(this.edgeMultMap);
    }

    /** Returns the multiplicity of the given node. */
    public Multiplicity getMult(PatternNode node) {
        Multiplicity result = this.nodeMultMap.get(node);
        return result == null ? ZERO_NODE_MULT : result;
    }

    /** Returns the multiplicity of the given edge. */
    public Multiplicity getMult(PatternEdge edge) {
        Multiplicity result = this.edgeMultMap.get(edge);
        return result == null ? ZERO_EDGE_MULT : result;
    }

    /** Returns the bounded sum of the node multiplicities of the given set. */
    Multiplicity getNodeSetMultSum(Set<PatternNode> nodes) {
        Multiplicity accumulator = ZERO_NODE_MULT;
        for (PatternNode node : nodes) {
            Multiplicity nodeMult = this.nodeMultMap.get(node);
            accumulator = accumulator.add(nodeMult);
        }
        return accumulator;
    }

    /** Returns the bounded sum of the edge multiplicities of the given set. */
    Multiplicity getEdgeSetMultSum(Set<PatternEdge> edges) {
        Multiplicity accumulator = ZERO_EDGE_MULT;
        for (PatternEdge edge : edges) {
            Multiplicity edgeMult = this.edgeMultMap.get(edge);
            accumulator = accumulator.add(edgeMult);
        }
        return accumulator;
    }

    /**
     * Sets the node multiplicity. If the multiplicity given is zero, then
     * the node is removed from the shape.
     */
    public void setMult(PatternNode node, Multiplicity mult) {
        assert !isFixed();
        assert mult.isNodeKind();
        assert containsNode(node) : "Node " + node + " is not in the shape!";
        if (!mult.isZero()) {
            this.nodeMultMap.put(node, mult);
        } else {
            // Setting a node multiplicity to zero is equivalent to removing
            // the node from the shape.
            removeNode(node);
        }
    }

    /**
     * Sets the edge multiplicity. If the multiplicity given is zero, then
     * the edge is removed from the shape.
     */
    public void setMult(PatternEdge edge, Multiplicity mult) {
        assert !isFixed();
        assert mult.isEdgeKind();
        assert containsEdge(edge) : "Edge " + edge + " is not in the shape!";
        if (!mult.isZero()) {
            this.edgeMultMap.put(edge, mult);
        } else {
            // Setting an edge multiplicity to zero is equivalent to removing
            // the edge from the shape.
            removeEdge(edge);
        }
    }

    /**
     * Computes the canonical shape of this object.
     * A new shape is returned and this object remains unchanged.
     */
    public PatternShape normalise() {
        PatternEquivRel peq = new PatternEquivRel(this);
        PatternShape result = new PatternShape(getName(), getTypeGraph());
        Map<NodeEquivClass,PatternNode> ecToNodeMap =
            new MyHashMap<NodeEquivClass,PatternNode>();

        for (NodeEquivClass nEc : peq.getNodeEquivRel()) {
            TypeNode nType = nEc.iterator().next().getType();
            PatternNode newNode = result.createNode(nType);
            result.addNode(newNode);
            Multiplicity nMult = nEc.getMult();
            if (!nMult.isOne()) {
                result.setMult(newNode, nMult);
            } // else nothing to do, the multiplicity was already set when
              // the node was added.
            ecToNodeMap.put(nEc, newNode);
        }

        for (EdgeEquivClass eEc : peq.getEdgeEquivRel()) {
            // We already have the fine grained equivalence class.
            PatternNode source = ecToNodeMap.get(eEc.sourceEc);
            PatternNode target = ecToNodeMap.get(eEc.targetEc);
            assert source != null && target != null;
            PatternEdge newEdge =
                getFactory().createEdge(source, eEc.typeEdge, target);
            result.addEdge(newEdge);
            Multiplicity eMult = eEc.mult;
            if (!eMult.isOne()) {
                result.setMult(newEdge, eMult);
            } // else nothing to do, the multiplicity was already set when
              // the edge was added.
        }

        assert result.isWellDefined();

        return result;
    }

    /** Compute the multiplicities from level 1 on.  */
    public void propagateMults() {
        for (int layer = 1; layer <= depth(); layer++) {
            for (PatternNode pNode : getLayerNodes(layer)) {

                Multiplicity acc[] = new Multiplicity[2];
                int i = 0;
                for (TypeEdge tEdge : getIncomingEdgeTypes(pNode)) {
                    acc[i] =
                        Multiplicity.getMultiplicity(0, 0, MultKind.EQSYS_MULT);
                    for (PatternEdge dEdge : getInEdgesWithType(pNode, tEdge)) {
                        Multiplicity srcMult = getMult(dEdge.source());
                        Multiplicity dMult = getMult(dEdge);
                        acc[i] = acc[i].add(srcMult.times(dMult));
                    }
                    i++;
                }

                Multiplicity finalMult;
                if (acc[0].subsumes(acc[1])) {
                    finalMult = acc[0].toNodeKind();
                } else {
                    assert acc[1].subsumes(acc[0]);
                    finalMult = acc[1];
                }
                setMult(pNode, finalMult);
            }
        }
    }
}
