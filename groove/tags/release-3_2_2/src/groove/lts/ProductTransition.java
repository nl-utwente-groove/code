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
 * $Id: ProductTransition.java,v 1.3 2008-02-22 13:02:44 rensink Exp $
 */
package groove.lts;

import groove.trans.Rule;
import groove.verify.BuchiGraphState;

/**
 * Models a transition in a product automaton consisting of a graph-transition
 * and a buchi-transition.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-22 13:02:44 $
 */
public class ProductTransition { // extends DefaultGraphTransition {

    private final GraphTransition graphTransition;
    // private BuchiTransition buchiTransition;
    private final BuchiGraphState source;
    private final BuchiGraphState target;

    /** the rule underlying this transition */
    // private Rule rule;
    /**
     * Constructor.
     * @param source the source buchi graph-state
     * @param transition the underlying graph-transition
     * @param target the target buchi graph-state
     */
    public ProductTransition(BuchiGraphState source,
            GraphTransition transition, BuchiGraphState target) {
        this.source = source;
        this.graphTransition = transition;
        this.target = target;
    }

    /** returns the graphtransition of this producttransition */
    public GraphTransition graphTransition() {
        return this.graphTransition;
    }

    /** returns the source state of this product transition */
    public BuchiGraphState source() {
        return this.source;
    }

    /** returns the target state of this product transition */
    public BuchiGraphState target() {
        return this.target;
    }

    /** returnsz the rule of this buchi transition */
    public Rule rule() {
        return graphTransition().getEvent().getRule();
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
    protected boolean equalsEvent(ProductTransition other) {
        return graphTransition().source().equals(
            other.graphTransition().source())
            && graphTransition().getEvent().equals(
                other.graphTransition().getEvent());
    }

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
        return obj instanceof ProductTransition
            && equalsSource((ProductTransition) obj)
            && equalsEvent((ProductTransition) obj);
    }

    @Override
    public String toString() {
        return source().toString() + "-->" + this.target.toString();
    }
}