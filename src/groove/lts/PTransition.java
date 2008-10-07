/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.lts;

import groove.graph.AbstractBinaryEdge;
import groove.graph.Label;
import groove.trans.Rule;
import groove.verify.BuchiGraphState;

/**
 * Transition between Büchi states, essentially
 * consisting of a transition in a Büchi automaton and a transition in a GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PTransition extends AbstractBinaryEdge<BuchiGraphState,Label,BuchiGraphState> {
    /** 
     * Constructs a transition between two Büchi states, 
     * storing the underlying graph transition.
     */
    public PTransition(BuchiGraphState source, GraphTransition transition, BuchiGraphState target) {
        super(source, transition.getEvent().getLabel(), target);
        this.graphTransition = transition;
    }

    /** The graph transition underlying this product transition. */
    public GraphTransition graphTransition() {
        return graphTransition;
    }

    /** Transformation rule underlying this product transition. */
    public Rule rule() {
        return graphTransition().getEvent().getRule();
    }

    /**
     * This implementation compares objects on the basis of the
     * source graph, rule and anchor images.
     */
    protected boolean equalsSource(PTransition other) {
        return source() == other.source();
    }

    /**
     * This implementation compares objects on the basis of the
     * source graph, rule and anchor images.
     */
    protected boolean equalsEvent(PTransition other) {
        return graphTransition().source().equals(other.graphTransition().source()) &&
        graphTransition().getEvent().equals(other.graphTransition().getEvent());
    }

    @Override
    public int computeHashCode() {
        int result = 0;
        result += source().hashCode() + target().hashCode();
        if (graphTransition() != null) {
            result += graphTransition().hashCode();
        }
        return result;
    }
    /**
     * This implementation delegates to <tt>{@link #equalsSource(PTransition)}</tt>.
    */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PTransition && equalsSource((PTransition) obj) && equalsEvent((PTransition) obj);
    }

    private final GraphTransition graphTransition;
}
