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

import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.control.CtrlState;
import groove.lts.AbstractGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.lts.StateCache;
import groove.trans.HostElement;
import groove.trans.HostNode;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;

import java.util.ArrayList;
import java.util.Collection;
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
    /** Set of outgoing transitions from this state. */
    final ArrayList<GraphTransition> transitions;

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
        this.closed = false;
        this.transitions = new ArrayList<GraphTransition>();
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
    public boolean addTransition(GraphTransition transition) {
        assert transition instanceof ShapeTransition
            || transition instanceof ShapeNextState : "Invalid transition type.";
        int index = this.transitions.size();
        if (transition instanceof ShapeNextState) {
            ((ShapeNextState) transition).setIndex(index);
        }
        if (transition instanceof ShapeTransition) {
            ((ShapeTransition) transition).setIndex(index);
        }
        this.transitions.add(transition);

        return true;
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

    @Override
    protected GraphTransitionStub createTransitionStub(RuleEvent event,
            HostNode[] addedNodes, GraphState target) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected GraphTransitionStub createInTransitionStub(GraphState source,
            RuleEvent event, HostNode[] addedNodes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<GraphTransition> getTransitionIter() {
        /*return this.transitionMap.values().iterator();*/
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<GraphTransition> getTransitionSet() {
        /*Set<GraphTransition> result = new THashSet<GraphTransition>();
        result.addAll(this.transitionMap.values());
        return Collections.unmodifiableSet(result);*/
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<RuleEvent,GraphTransition> getTransitionMap() {
        /*return Collections.unmodifiableMap(this.transitionMap);*/
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsTransition(GraphTransition transition) {
        /*return this.transitionMap.containsValue(transition);*/
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ShapeState> getNextStateSet() {
        /*THashSet<ShapeState> result = new THashSet<ShapeState>();
        for (GraphTransition transition : this.transitionMap.values()) {
            result.add((ShapeState) transition.target());
        }
        return Collections.unmodifiableSet(result);*/
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<GraphState> getNextStateIter() {
        /*return new Iterator<GraphState>() {
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

        };*/
        throw new UnsupportedOperationException();
    }

}