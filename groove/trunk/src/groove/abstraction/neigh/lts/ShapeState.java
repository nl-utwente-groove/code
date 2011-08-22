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
package groove.abstraction.neigh.lts;

import gnu.trove.THashMap;
import gnu.trove.THashSet;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.control.CtrlState;
import groove.lts.AbstractGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.lts.StateCache;
import groove.trans.HostElement;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A state of the abstract GTS that stores a shape as the state configuration.
 * 
 * @author Eduardo Zambon
 */
public class ShapeState extends AbstractGraphState {

    private static final ShapeNode[] EMPTY_NODE_ARRAY = new ShapeNode[0];

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final Shape shape;
    private boolean closed;
    /** The outgoing transitions from this state. */
    final Set<GraphTransition> transitions;
    /** The outgoing transitions from this state. */
    final Map<RuleEvent,GraphTransition> transitionMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a numbered state with the given shape and with an empty set of
     * transitions.
     * @param number the number of the state; required to be non-negative
     */
    public ShapeState(Shape shape, CtrlState ctrlState, int number) {
        // We don't have a cache reference, so just pass null to the
        // super constructor.
        super(null, number);
        setCtrlState(ctrlState);
        this.shape = shape;
        this.shape.setName(toString());
        //this.shape.setFixed();
        this.closed = false;
        this.transitions = new THashSet<GraphTransition>();
        this.transitionMap = new THashMap<RuleEvent,GraphTransition>();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public Shape getGraph() {
        return this.shape;
    }

    @Override
    public int getTransitionCount() {
        return this.transitions.size();
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
    public Map<RuleEvent,GraphTransition> getTransitionMap() {
        return Collections.unmodifiableMap(this.transitionMap);
    }

    @Override
    public boolean containsTransition(GraphTransition transition) {
        return this.transitions.contains(transition);
    }

    @Override
    public boolean addTransition(GraphTransition transition) {
        assert transition instanceof ShapeTransition
            || transition instanceof ShapeState : "Invalid transition type.";
        this.transitionMap.put(transition.getEvent(), transition);
        return this.transitions.add(transition);
    }

    @Override
    public Collection<ShapeState> getNextStateSet() {
        // EDUARDO: Make this efficient?
        THashSet<ShapeState> result = new THashSet<ShapeState>();
        for (GraphTransition transition : this.transitions) {
            result.add((ShapeState) transition.target());
        }
        return Collections.unmodifiableSet(result);
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

    /** Returns the system record associated with this state. */
    @Override
    protected SystemRecord getRecord() {
        return this.getGTS().getRecord();
    }

    @Override
    public ShapeNode[] getBoundNodes() {
        return EMPTY_NODE_ARRAY;
    }

    // ------------------------------------------------------------------------
    // Unimplemented methods
    // ------------------------------------------------------------------------

    @Override
    public GraphTransitionStub getOutStub(RuleEvent prime) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected HostElement[] getFrozenGraph() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void setFrozenGraph(HostElement[] frozenGraph) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected StateCache createCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearCache() {
        throw new UnsupportedOperationException();
    }

}
