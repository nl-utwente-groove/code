/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.verify;

import groove.grammar.Action;
import groove.lts.GraphTransition;

/**
 * Models a transition in a product automaton consisting of a graph-transition
 * and a buchi-transition.
 *
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-22 13:02:44 $
 */
public class ProductTransition {
    private final GraphTransition graphTransition;
    private final ProductState source;
    private final ProductState target;

    /**
     * Constructor.
     * @param source the source buchi graph-state
     * @param transition the underlying graph-transition
     * @param target the target buchi graph-state
     */
    public ProductTransition(ProductState source, GraphTransition transition, ProductState target) {
        this.source = source;
        this.graphTransition = transition;
        this.target = target;
        transitionCount++;
    }

    /** returns the graphtransition of this producttransition */
    public GraphTransition graphTransition() {
        return this.graphTransition;
    }

    /** returns the source state of this product transition */
    public ProductState source() {
        return this.source;
    }

    /** returns the target state of this product transition */
    public ProductState target() {
        return this.target;
    }

    /** returnsz the rule of this buchi transition */
    public Action rule() {
        return graphTransition().getAction();
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    @Override
    public int hashCode() {
        int result = 0;
        result += source().hashCode() + target().hashCode();
        if (graphTransition() != null) {
            result += graphTransition().hashCode();
        }
        return result;
    }

    /**
     * This implementation delegates to
     * <tt>{@link #equalsSource(ProductTransition)}</tt>.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return false;
        }
        if (!(obj instanceof ProductTransition)) {
            return false;
        }
        ProductTransition other = (ProductTransition) obj;
        return equalsSource(other) && equalsTransition(other);
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsSource(ProductTransition other) {
        return source() == other.source();
    }

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsTransition(ProductTransition other) {
        return graphTransition().equals(other.graphTransition());
    }

    @Override
    public String toString() {
        return source().toString() + "-->" + this.target.toString();
    }

    /** Returns the total number of objects created. */
    public static int getTransitionCount() {
        return transitionCount;
    }

    private static int transitionCount;
}