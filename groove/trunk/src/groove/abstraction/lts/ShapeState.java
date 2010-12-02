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
 * $Id: AbstrGraphStateImpl.java,v 1.3 2008-01-31 08:22:52 rensink Exp $
 */
package groove.abstraction.lts;

import groove.abstraction.Shape;
import groove.control.CtrlCall;
import groove.control.CtrlState;
import groove.graph.Element;
import groove.lts.AbstractGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.Rule;
import groove.trans.RuleEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A state of the abstract GTS that stores a shape as the state configuration.
 * 
 * @author Eduardo Zambon
 */
public class ShapeState extends AbstractGraphState {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final Shape shape;
    private boolean closed;
    /** The outgoing transitions from this state. */
    protected final Set<GraphTransition> transitions =
        new HashSet<GraphTransition>();
    /** The outgoing transitions from this state. */
    protected final Map<CtrlCall,Collection<GraphTransition>> transitionMap =
        new HashMap<CtrlCall,Collection<GraphTransition>>();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs an state with the given shape and with an empty set of
     * transitions.
     */
    public ShapeState(Shape shape, CtrlState ctrlState) {
        // We don't have a cache reference, so just pass null to the
        // super constructor.
        super(null);
        setCtrlState(ctrlState);
        this.shape = shape;
        this.closed = false;
        this.nr = -1;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public Shape getGraph() {
        return this.shape;
    }

    @Override
    protected void updateClosed() {
        // Nothing to do.
    }

    @Override
    public Iterator<GraphTransition> getTransitionIter() {
        return this.transitions.iterator();
    }

    @Override
    public Set<GraphTransition> getTransitionSet() {
        return Collections.unmodifiableSet(this.transitions);
    }

    @Override
    public Map<CtrlCall,Collection<GraphTransition>> getTransitionMap() {
        return Collections.unmodifiableMap(this.transitionMap);
    }

    @Override
    public boolean addTransition(GraphTransition transition) {
        assert transition instanceof ShapeTransition
            || transition instanceof ShapeNextState : "Invalid transition type.";
        Rule rule = transition.getEvent().getRule();
        Collection<GraphTransition> ruleTrans = this.transitionMap.get(rule);
        if (ruleTrans == null) {
            this.transitionMap.put(transition.getCtrlTransition().getCall(),
                ruleTrans = new ArrayList<GraphTransition>());
        }
        ruleTrans.add(transition);
        return this.transitions.add(transition);
    }

    @Override
    public boolean setClosed(boolean finished) {
        boolean result = !this.closed;
        this.closed = true;
        return result;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * This implementation compares state numbers. The current state is either
     * compared with the other, if that is a {@link ShapeState}, or
     * with its source state if it is a {@link ShapeTransition}.
     * Otherwise, the method throws an {@link UnsupportedOperationException}.
     */
    @Override
    public int compareTo(Element obj) {
        if (obj instanceof ShapeState) {
            return this.getNumber() - ((ShapeState) obj).getNumber();
        } else if (obj instanceof ShapeTransition) {
            return this.getNumber()
                - ((ShapeTransition) obj).source().getNumber();
        } else {
            throw new UnsupportedOperationException(String.format(
                "Classes %s and %s cannot be compared", getClass(),
                obj.getClass()));
        }
    }

    @Override
    public int hashCode() {
        return getGraph().hashCode();
    }

    /**
     * This implementation returns true if the underlying shapes have
     * isomorphic structure with compatible multiplicities.
     */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof ShapeState) || o instanceof ShapeNextState) {
            result = false;
        } else {
            ShapeState other = (ShapeState) o;
            if (this.hasNumber() && other.hasNumber()
                && this.getNumber() == other.getNumber()) {
                result = true;
            } else {
                result = getGraph().equals(((ShapeState) o).getGraph());
            }
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public Iterator<GraphState> getNextStateIter() {
        return new Iterator<GraphState>() {
            Iterator<GraphTransition> it = ShapeState.this.getTransitionIter();

            public boolean hasNext() {
                return this.it.hasNext();
            }

            public GraphState next() {
                return this.it.next().target();
            }

            public void remove() {
                this.it.remove();
            }

        };
    }

    // Unimplemented methods.

    @Override
    public GraphTransitionStub getOutStub(RuleEvent prime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<GraphState> getNextStateSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsTransition(GraphTransition transition) {
        throw new UnsupportedOperationException();
    }
}
