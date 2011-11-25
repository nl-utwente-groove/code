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

import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.control.CtrlState;
import groove.lts.AbstractGraphState;
import groove.lts.DerivationLabel;
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
    // Static fields
    // ------------------------------------------------------------------------

    private static final ShapeNode[] EMPTY_NODE_ARRAY = new ShapeNode[0];

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The shape associated with this state. */
    private final Shape shape;
    /** Flag to indicate whether this state is closed or not. */
    private boolean closed;
    /** A (possible null) reference to a state that subsumes this one. */
    private ShapeState subsumptor;
    /** Set of outgoing transitions from this state. */
    private final ArrayList<GraphTransition> transitions;
    /**
     * Temporary set of possible subsumed states used when adding the state to
     * the GTS.
     */
    private ArrayList<ShapeState> subsumedStates;
    /** Set of successor states. Computed lazily. */
    private Set<ShapeState> nextStates;

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
        // Fix the shape to avoid modifications.
        // EDUARDO: restore this after debugging is done.
        // this.shape.setFixed();
        this.closed = false;
        this.transitions = new ArrayList<GraphTransition>();
        this.subsumedStates = new ArrayList<ShapeState>();
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
    public Iterator<GraphTransition> getTransitionIter() {
        return this.transitions.iterator();
    }

    @Override
    public Set<GraphTransition> getTransitionSet() {
        return new HashSet<GraphTransition>(this.transitions);
    }

    @Override
    protected void updateClosed() {
        // Nothing to do.
    }

    @Override
    public boolean addTransition(GraphTransition transition) {
        assert transition instanceof ShapeTransition
            || transition instanceof ShapeNextState : "Invalid transition type.";
        this.transitions.add(transition);
        return true;
    }

    @Override
    public boolean containsTransition(GraphTransition transition) {
        return this.transitions.contains(transition);
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
    protected SystemRecord getRecord() {
        return this.getGTS().getRecord();
    }

    @Override
    public ShapeNode[] getBoundNodes() {
        return EMPTY_NODE_ARRAY;
    }

    @Override
    public void clearCache() {
        // We don't have a cache, so just return.
    }

    @Override
    public Collection<ShapeState> getNextStateSet() {
        if (this.nextStates != null) {
            return this.nextStates;
        } else {
            Set<ShapeState> result = new MyHashSet<ShapeState>();
            for (GraphTransition transition : this.transitions) {
                result.add((ShapeState) transition.target());
            }
            if (this.isClosed()) {
                this.nextStates = result;
            }
            return result;
        }
    }

    @Override
    public Iterator<ShapeState> getNextStateIter() {
        return getNextStateSet().iterator();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns true if this state has an outgoing transition to the given
     * target state with given label.
     */
    protected boolean containsTransition(DerivationLabel label,
            ShapeState target) {
        boolean result = false;
        for (GraphTransition trans : this.transitions) {
            if (trans.target().getNumber() == target.getNumber()
                && trans.label().equals(label)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Tries to set the subsumptor to the given state.
     * Returns true is this state didn't already have a subsumptor.
     */
    protected boolean setSubsumptor(ShapeState subsumptor) {
        if (this.getSubsumptor() != null) {
            return false;
        } else {
            this.subsumptor = subsumptor;
            return true;
        }
    }

    /** Basic getter method. Returned state may be null. */
    protected ShapeState getSubsumptor() {
        return this.subsumptor;
    }

    /** Returns true if this state is subsumed by another one in the GTS. */
    protected boolean isSubsumed() {
        return this.subsumptor != null;
    }

    /**
     * Adds the given state to the list of states possibly subsumed by this
     * one.
     */
    protected void addSubsumedState(ShapeState subsumed) {
        this.subsumedStates.add(subsumed);
    }

    /**
     * Goes over the list of possible subsumed states and mark them as such,
     * trying to set this is state as their subsumptor. The list of possible
     * subsumed states of this state is destroyed during this method call.
     * Returns the number of states that were marked as subsumed.
     */
    protected int markSubsumedStates() {
        int markCount = 0;
        for (ShapeState subsumed : this.subsumedStates) {
            if (subsumed.setSubsumptor(this)) {
                markCount++;
            }
        }
        this.subsumedStates = null;
        return markCount;
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
    public Map<RuleEvent,GraphTransition> getTransitionMap() {
        throw new UnsupportedOperationException();
    }

}
