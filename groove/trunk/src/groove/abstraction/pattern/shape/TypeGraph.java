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

import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.Util;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraph extends AbstractPatternGraph<TypeNode,TypeEdge> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Prototype simple graph used to create new patterns. */
    private static final HostGraph protSimpleGraph = new DefaultHostGraph(
        "protSGraph");

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final PatternFactory factory;
    private boolean fixed;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public TypeGraph(String name) {
        super(name);
        this.factory = new PatternFactory(this);
        this.fixed = false;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pattern type graph: " + getName() + "\n");
        sb.append("Nodes: " + nodeSet() + "\n");
        sb.append("Edges: " + edgeSet() + "\n");
        sb.append("\nPatterns (depth = " + depth() + "):\n");
        for (TypeNode node : nodeSet()) {
            sb.append("  " + node.getPattern().getName() + ": "
                + node.getPattern().toString() + "\n");
        }
        sb.append("\nMorphisms:\n");
        for (TypeEdge edge : edgeSet()) {
            sb.append("  " + edge.getMorphism().toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void setFixed() {
        assert !isFixed();
        for (TypeNode node : this.nodeSet()) {
            node.setFixed();
        }
        for (TypeEdge edge : this.edgeSet()) {
            edge.setFixed();
        }
        computeLayers();
        this.fixed = true;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    @Override
    public TypeNode addNode(int nr) {
        HostGraph pattern = newSimpleGraph(TypeNode.PREFIX + nr);
        TypeNode node = new TypeNode(nr, pattern);
        addNode(node);
        return node;
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.TYPE;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof TypeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof TypeEdge;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Creates a new empty simple graph with the given name. */
    public HostGraph newSimpleGraph(String name) {
        HostGraph result = new DefaultHostGraph(protSimpleGraph);
        result.setName(name);
        return result;
    }

    /** Creates and returns an empty simple graph morphism. */
    public SimpleMorphism newSimpleMorphism(String name, TypeNode source,
            TypeNode target) {
        return new SimpleMorphism(name, source, target);
    }

    /** Creates and returns a new edge with an empty morphism. */
    public TypeEdge addEdge(int nr, TypeNode source, TypeNode target) {
        SimpleMorphism morph =
            newSimpleMorphism(TypeEdge.PREFIX + nr, source, target);
        TypeEdge edge = new TypeEdge(nr, source, target, morph);
        addEdge(edge);
        return edge;
    }

    private void computeLayers() {
        for (TypeNode node : nodeSet()) {
            addToLayer(node);
        }
    }

    private PatternNode createPatternNode(TypeNode type, PatternShape pShape) {
        if (pShape != null) {
            return this.factory.createNode(type, pShape.nodeSet());
        } else {
            return this.factory.createNode(type);
        }
    }

    private PatternEdge createPatternEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        return this.factory.createEdge(source, type, target);
    }

    /** Lifts the given simple graph to a pattern graph. */
    public PatternShape lift(HostGraph graph) {
        PatternShape result = this.factory.newPatternShape();
        Map<HostNode,PatternNode> nodeMap =
            new MyHashMap<HostNode,PatternNode>();

        // First lift the nodes of layer 0.
        for (TypeNode tNode : getLayerNodes(0)) {
            // For each node pattern.
            Set<TypeLabel> nodeLabels = tNode.getNodeLabels();
            // For each matched node in the simple graph.
            for (HostNode sNode : match(graph, nodeLabels)) {
                // Create a new pattern node.
                PatternNode pNode = createPatternNode(tNode, result);
                nodeMap.put(sNode, pNode);
                result.addNode(pNode);
            }
        }

        // Now lift the nodes of layer 1.
        for (TypeNode tTgt : getLayerNodes(1)) {
            // For each edge pattern.
            HostGraph pType = tTgt.getPattern();
            TypeLabel edgeLabel = Util.getBinaryLabels(pType).iterator().next();
            // For each matched edge in the simple graph.
            for (HostEdge sEdge : match(graph, edgeLabel)) {
                // Create a new pattern node.
                PatternNode pTgt = createPatternNode(tTgt, result);
                result.addNode(pTgt);
                // Find source pattern node and pattern edge type.
                PatternNode pSrc = nodeMap.get(sEdge.source());
                TypeEdge tEdge = getCoveringEdge(tTgt, pTgt.getSource());
                PatternEdge pEdge = createPatternEdge(pSrc, tEdge, pTgt);
                result.addEdge(pEdge);
                // Now repeat for the target of the simple edge.
                if (!sEdge.isLoop()) {
                    pSrc = nodeMap.get(sEdge.target());
                    tEdge = getCoveringEdge(tTgt, pTgt.getTarget());
                    pEdge = createPatternEdge(pSrc, tEdge, pTgt);
                    result.addEdge(pEdge);
                }
            }
        }

        // Compute the closure for the pattern shape we have so far.
        close(result);
        assert result.isWellFormed();
        assert result.isCommuting();
        assert result.isConcrete();
        return result;
    }

    /** Computes the closure for the given pattern shape w.r.t. this type graph. */
    public void close(PatternShape pShape) {
        // EDUARDO: Implement this...
    }

    /** Returns the factory associated with this type graph. */
    public PatternFactory getPatternFactory() {
        return this.factory;
    }

    private List<HostNode> match(HostGraph graph, Set<TypeLabel> nodeLabels) {
        List<HostNode> result = new ArrayList<HostNode>(graph.nodeSet().size());
        result.addAll(graph.nodeSet());
        List<HostNode> aux = new ArrayList<HostNode>(graph.nodeSet().size());
        for (TypeLabel label : nodeLabels) {
            for (HostEdge sEdge : graph.labelEdgeSet(label)) {
                aux.add(sEdge.source());
            }
            result.retainAll(aux);
            aux.clear();
        }
        Collections.sort(result);
        return result;
    }

    private List<HostEdge> match(HostGraph graph, TypeLabel edgeLabel) {
        List<HostEdge> result = new ArrayList<HostEdge>();
        result.addAll(graph.labelEdgeSet(edgeLabel));
        Collections.sort(result);
        return result;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public boolean removeNode(TypeNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(TypeEdge edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeSet(Collection<? extends TypeNode> nodeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeSetWithoutCheck(Collection<? extends TypeEdge> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeWithoutCheck(TypeNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeSetWithoutCheck(
            Collection<? extends TypeNode> nodeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends TypeEdge> labelEdgeSet(Label label) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected GraphInfo<TypeNode,TypeEdge> createInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphInfo<TypeNode,TypeEdge> setInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected TypeEdge createEdge(TypeNode source, Label label, TypeNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeNode addNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeEdge addEdge(TypeNode source, String label, TypeNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeEdge addEdge(TypeNode source, Label label, TypeNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addNodeSet(Collection<? extends TypeNode> nodeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeSet(Collection<? extends TypeEdge> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdgeSet(Collection<? extends TypeEdge> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mergeNodes(TypeNode from, TypeNode to) {
        throw new UnsupportedOperationException();
    }

}
