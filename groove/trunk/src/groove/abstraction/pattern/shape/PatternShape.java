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
import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.shape.PatternEquivRel.EdgeEquivClass;
import groove.abstraction.pattern.shape.PatternEquivRel.NodeEquivClass;
import groove.abstraction.pattern.trans.CombinatorialIterator;
import groove.graph.GraphRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public GraphRole getRole() {
        return GraphRole.SHAPE;
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

        // EZ says: sometimes we can make the shape more precise.
        result.improvePrecision();

        return result;
    }

    private void improvePrecision() {
        for (int layer = 1; layer <= depth(); layer++) {
            for (PatternNode node : getLayerNodes(layer)) {
                if (getMult(node).isCollector() && isUniquelyCovered(node)) {
                    // If any of the ancestors is concrete then this node
                    // must also be concrete.
                    for (PatternEdge inEdge : inEdgeSet(node)) {
                        if (getMult(inEdge).isOne()
                            && getMult(inEdge.source()).isOne()) {
                            setMult(node, Multiplicity.ONE_NODE_MULT);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the pattern edge outgoing from the given node, with the proper
     * given type, or <code>null</code> if no such edge exists. 
     */
    public PatternEdge getOutEdgeWithType(PatternNode node, TypeEdge edgeType) {
        for (PatternEdge outEdge : outEdgeSet(node)) {
            // We assume the same pattern type graph is always used so object
            // equality is sufficient.
            if (outEdge.getType() == edgeType) {
                return outEdge;
            }
        }
        return null;
    }

    /**
     * Returns true if there exists an pattern edge outgoing from the given 
     * node, with the proper given type. 
     */
    public boolean hasOutEdgeWithType(PatternNode node, TypeEdge edgeType) {
        return getOutEdgeWithType(node, edgeType) != null;
    }

    /**
     * Returns a list of pattern edges incoming into the given node, with the
     * proper given type.
     */
    public List<PatternEdge> getInEdgesWithType(PatternNode node,
            TypeEdge edgeType) {
        Set<PatternEdge> inEdgeSet = inEdgeSet(node);
        ArrayList<PatternEdge> result =
            new ArrayList<PatternEdge>(inEdgeSet.size());
        for (PatternEdge inEdge : inEdgeSet) {
            if (inEdge.getType() == edgeType) {
                result.add(inEdge);
            }
        }
        return result;
    }

    /**
     * Returns true if the given node is uniquely covered by the incoming edge
     * morphisms. Uniqueness corresponds to the absence of distinct incoming
     * edges of the same type. While this is condition is always satisfied in
     * pattern graphs, in pattern shapes it may be falsified due to pattern
     * collapsing.  
     */
    public boolean isUniquelyCovered(PatternNode node) {
        // Check the type graph for the incoming types.
        for (TypeEdge edgeType : getTypeGraph().inEdgeSet(node.getType())) {
            if (getInEdgesWithType(node, edgeType).size() != 1) {
                return false;
            }
        }
        return true;
    }

    /** Returns all possible coverages for the given node. */
    public Set<Set<PatternEdge>> getCoveragePossibilities(PatternNode node,
            TypeEdge excludeType) {
        Map<TypeEdge,Set<PatternEdge>> typeMap =
            new MyHashMap<TypeEdge,Set<PatternEdge>>();
        for (PatternEdge inEdge : inEdgeSet(node)) {
            TypeEdge type = inEdge.getType();
            if (type != excludeType) {
                Set<PatternEdge> typeSet = typeMap.get(type);
                if (typeSet == null) {
                    typeSet = new MyHashSet<PatternEdge>();
                    typeMap.put(type, typeSet);
                }
                typeSet.add(inEdge);
            }
        }
        Set<Set<PatternEdge>> result = new MyHashSet<Set<PatternEdge>>();
        result.addAll(typeMap.values());
        CombinatorialIterator<PatternEdge> iter =
            new CombinatorialIterator<PatternEdge>(result);
        result.clear();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

}
