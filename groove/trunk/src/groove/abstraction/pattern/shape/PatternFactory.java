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

import groove.grammar.host.DefaultHostNode;
import groove.grammar.type.TypeLabel;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.NodeFactory;
import groove.graph.StoreFactory;

/**
 * Factory for pattern graph elements.
 * 
 * @author Eduardo Zambon
 */
public final class PatternFactory extends
        StoreFactory<PatternNode,PatternEdge,TypeLabel> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternFactory(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    @Override
    public Morphism<PatternNode,PatternEdge> createMorphism() {
        return new Morphism<PatternNode,PatternEdge>(this);
    }

    /** Creates and returns an empty pattern graph associated with this factory. */
    public PatternGraph newPatternGraph() {
        return new PatternGraph(this.typeGraph, this);
    }

    /** Returns a node factory for typed default host nodes. */
    public NodeFactory<PatternNode> nodes(TypeNode type) {
        return new PatternNodeFactory(type);
    }

    /** Puts an edge in the store and returns its canonical representative. */
    public PatternEdge createEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        assert source != null : "Source node of pattern edge should not be null";
        assert target != null : "Target node of pattern edge should not be null";
        PatternEdge edge = newEdge(source, type, target, getEdgeCount());
        return storeEdge(edge);
    }

    private PatternEdge newEdge(PatternNode source, TypeEdge type,
            PatternNode target, int nr) {
        return new PatternEdge(nr, source, target, type);
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    protected PatternNode newNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PatternEdge newEdge(PatternNode source, Label label,
            PatternNode target, int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Label createLabel(String text) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final TypeGraph typeGraph;

    /** Factory for (typed) {@link DefaultHostNode}s. */
    protected class PatternNodeFactory extends DependentNodeFactory {
        /** Constructor for subclassing. */
        protected PatternNodeFactory(TypeNode type) {
            this.type = type;
        }

        @Override
        protected boolean isAllowed(PatternNode node) {
            return node.getType() == this.type;
        }

        @Override
        protected PatternNode newNode(int nr) {
            return new PatternNode(nr, this.type);
        }

        private final TypeNode type;
    }

}
