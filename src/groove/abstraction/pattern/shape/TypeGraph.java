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
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;

/**
 * Pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraph extends AbstractPatternGraph<TypeNode,TypeEdge> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    private static final HostGraph protSimpleGraph = new DefaultHostGraph(
        "protSGraph");

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    boolean fixed;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public TypeGraph(String name) {
        super(name);
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
        sb.append("\nPatterns:\n");
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

    /** Lifts the given simple graph to a pattern graph. */
    public PatternShape lift(HostGraph graph) {
        return null;
    }

}
