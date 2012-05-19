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
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Map;
import java.util.Set;

/**
 * Pattern shape.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShape extends
        AbstractPatternGraph<PatternNode,PatternEdge> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Associated type graph. */
    private final TypeGraph type;
    /** Multiplicity maps. */
    private final Map<PatternNode,Multiplicity> nodeMultMap;
    private final Map<PatternEdge,Multiplicity> edgeMultMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternShape(String name, TypeGraph type) {
        super(name);
        this.type = type;
        this.nodeMultMap = new MyHashMap<PatternNode,Multiplicity>();
        this.edgeMultMap = new MyHashMap<PatternEdge,Multiplicity>();
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
        sb.replace(sb.length() - 2, sb.length(), "]\n");
        sb.append("Edges: [");
        for (PatternEdge edge : edgeSet()) {
            sb.append(edge.toString() + "(" + getMult(edge) + "), ");
        }
        sb.replace(sb.length() - 2, sb.length(), "]\n");
        return sb.toString();
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.SHAPE;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof PatternNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof PatternEdge;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PatternNode> nodeSet() {
        return (Set<PatternNode>) super.nodeSet();
    }

    @Override
    public boolean addNode(PatternNode node) {
        boolean result = super.addNode(node);
        if (result) {
            addToLayer(node);
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
    public PatternFactory getFactory() {
        return this.type.getPatternFactory();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

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

    /** Basic getter method. */
    public TypeGraph getTypeGraph() {
        return this.type;
    }

    /** Returns true if all elements have multiplicity one. */
    public boolean isConcrete() {
        for (Multiplicity mult : this.nodeMultMap.values()) {
            if (!mult.isOne()) {
                return false;
            }
        }
        for (Multiplicity mult : this.edgeMultMap.values()) {
            if (!mult.isOne()) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public Set<? extends PatternEdge> labelEdgeSet(Label label) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected GraphInfo<PatternNode,PatternEdge> createInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphInfo<PatternNode,PatternEdge> setInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PatternEdge createEdge(PatternNode source, Label label,
            PatternNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternNode addNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternNode addNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternEdge addEdge(PatternNode source, String label,
            PatternNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternEdge addEdge(PatternNode source, Label label,
            PatternNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mergeNodes(PatternNode from, PatternNode to) {
        throw new UnsupportedOperationException();
    }

}
