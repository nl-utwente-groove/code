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

import groove.graph.Edge;
import groove.graph.ElementFactory;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.trans.DefaultHostGraph;

import java.util.Collection;
import java.util.Set;

/**
 * Pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraph implements Graph<TypeNode,TypeEdge> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final TypeNode nodes[];
    private final TypeEdge edges[];
    private int nodeCount;
    private int edgeCount;
    private boolean fixed;
    private String name;

    private SimpleGraph protSimpleGraph;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public TypeGraph(String name, int maxNodeCount, int maxEdgeCount) {
        this.nodes = new TypeNode[maxNodeCount];
        this.edges = new TypeEdge[maxEdgeCount];
        this.nodeCount = 0;
        this.edgeCount = 0;
        this.fixed = false;
        this.name = name;

        this.protSimpleGraph = (SimpleGraph) new DefaultHostGraph("protSGraph");
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pattern type graph: " + this.name + "\n");
        sb.append("Nodes: " + this.nodes + "\n");
        sb.append("Edges: " + this.edges + "\n");
        return sb.toString();
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void setFixed() {
        assert !isFixed();
        this.fixed = true;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    @Override
    public int size() {
        return nodeCount() + edgeCount();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int nodeCount() {
        return this.nodeCount;
    }

    @Override
    public int edgeCount() {
        return this.edgeCount;
    }

    @Override
    public boolean containsNode(Node node) {
        assert node instanceof TypeNode;
        return this.nodes[node.getNumber()] != null;
    }

    @Override
    public boolean containsEdge(Edge edge) {
        assert edge instanceof TypeEdge;
        return this.edges[((TypeEdge) edge).getNumber()] != null;
    }

    @Override
    public TypeNode addNode(int nr) {
        SimpleGraph pattern = newSimpleGraph("p" + nr);
        TypeNode node = new TypeNode(nr, pattern);
        addNode(node);
        return node;
    }

    @Override
    public boolean addNode(TypeNode node) {
        assert !containsNode(node);
        assert node.getNumber() < this.nodes.length;
        this.nodes[node.getNumber()] = node;
        this.nodeCount++;
        return true;
    }

    @Override
    public boolean addEdge(TypeEdge edge) {
        assert !containsEdge(edge);
        assert edge.getNumber() < this.edges.length;
        this.edges[edge.getNumber()] = edge;
        this.edgeCount++;
        return true;
    }

    @Override
    public boolean addEdgeWithoutCheck(TypeEdge edge) {
        return addEdge(edge);
    }

    @Override
    public void setName(String name) {
        assert !isFixed();
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.TYPE;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Creates a new empty simple graph with the given name. */
    public SimpleGraph newSimpleGraph(String name) {
        SimpleGraph result =
            (SimpleGraph) new DefaultHostGraph(this.protSimpleGraph);
        result.setName(name);
        return result;
    }

    /** EDUARDO: Comment this... */
    public SimpleMorphism newSimpleMorphism(String name, TypeNode source,
            TypeNode target) {
        return new SimpleMorphism(name, source, target);
    }

    /** EDUARDO: Comment this... */
    public TypeEdge addEdge(int nr, TypeNode source, TypeNode target) {
        SimpleMorphism morph = newSimpleMorphism("d" + nr, source, target);
        TypeEdge edge = new TypeEdge(nr, source, target, morph);
        addEdge(edge);
        return edge;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public Set<? extends TypeNode> nodeSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends TypeEdge> edgeSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends TypeEdge> edgeSet(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends TypeEdge> inEdgeSet(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends TypeEdge> outEdgeSet(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends TypeEdge> labelEdgeSet(Label label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphInfo<TypeNode,TypeEdge> getInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphInfo<TypeNode,TypeEdge> setInfo(GraphInfo<?,?> info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Graph<TypeNode,TypeEdge> newGraph(String name) {
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
    public boolean removeEdgeSet(Collection<? extends TypeEdge> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mergeNodes(TypeNode from, TypeNode to) {
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
    public ElementFactory<TypeNode,TypeEdge> getFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Graph<TypeNode,TypeEdge> clone() {
        throw new UnsupportedOperationException();
    }

}
