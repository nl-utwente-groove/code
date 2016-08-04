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
package groove.graph;

import groove.util.Dispenser;

/**
 * Factory class for graph elements.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class ElementFactory<N extends Node,E extends Edge> extends NodeFactory<N> {
    /** Constructor for subclassing. */
    protected ElementFactory() {
        this.nodeNrs = createNodeNrDispenser();
        this.maxNodeNr = -1;
    }

    /** Returns the fresh node number dispenser of this factory. */
    @Override
    protected final Dispenser getNodeNrDispenser() {
        return this.nodeNrs;
    }

    /** Callback factory method to create the node number dispenser. */
    protected Dispenser createNodeNrDispenser() {
        return Dispenser.counter();
    }

    private final Dispenser nodeNrs;

    /** Returns the maximum node number known to this factory. */
    public int getMaxNodeNr() {
        return this.maxNodeNr;
    }

    /**
     * Callback method from {@link #createNode()} to register a given node
     * as having been created by this factory.
     * It is an error to register the same node number more than once.
     */
    @Override
    protected void registerNode(N node) {
        int nr = node.getNumber();
        this.maxNodeNr = Math.max(this.maxNodeNr, nr);
        getNodeNrDispenser().notifyUsed(nr);
    }

    private int maxNodeNr;

    @Override
    protected boolean isAllowed(N node) {
        return true;
    }

    @Override
    protected N getNode(int nr) {
        return null;
    }

    /** Creates a label with the given text. */
    public abstract Label createLabel(String text);

    /** Creates an edge with the given source, label text and target. */
    public E createEdge(N source, String text, N target) {
        return createEdge(source, createLabel(text), target);
    }

    /** Creates an edge with the given source, label and target. */
    public abstract E createEdge(N source, Label label, N target);

    /** Creates a fresh morphism between the elements of this factory. */
    public abstract Morphism<N,E> createMorphism();

    /**
     * Node factory that delegates its globally implemented methods to the
     * embedding element factory.
     * @author rensink
     * @version $Revision $
     */
    abstract protected class DependentNodeFactory extends NodeFactory<N> {
        @Override
        protected N getNode(int nr) {
            return ElementFactory.this.getNode(nr);
        }

        @Override
        protected void registerNode(N node) {
            ElementFactory.this.registerNode(node);
        }

        @Override
        protected Dispenser getNodeNrDispenser() {
            return ElementFactory.this.getNodeNrDispenser();
        }
    }
}
