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
 * $Id: AGTS.java,v 1.5 2008-03-18 13:51:38 iovka Exp $
 */
package groove.abs.lts;

import groove.abs.AbstrGraph;
import groove.abs.Abstraction;
import groove.abs.DefaultAbstrGraph;
import groove.abs.ExceptionIncompatibleWithMaxIncidence;
import groove.abs.MyHashSet;
import groove.abs.PatternFamily;
import groove.abs.Util;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphShapeCache;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.GraphGrammar;
import groove.trans.SystemRecord;
import groove.util.CollectionView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * An Abstract graph transition system.
 * @author Iovka Boneva
 * @version $Revision $
 * @invariant (TypeInv) States of the system are always of type
 *            {@link ShapeGraphState}
 * @invariant (TypeInv) Transitions of the system are always of type
 *            {@link ShapeGraphTransition}
 */
public class AGTS extends GTS {

    @Override
    // IOVKA this is almost a copy of the super method, because of the different
    // implementation of the ShapeGraphState
    /** @require newState is of type ShapeGraphState */
    public GraphState addState(GraphState newState) {
        assert newState instanceof ShapeGraphState : "Type error : " + newState
            + " is not of type ShapeGraphState.";
        ShapeGraphState state = (ShapeGraphState) newState;
        state.setNumber(nodeCount());
        ShapeGraphState result = this.stateSet.getAndAdd(state);
        if (result == null) {
            fireAddNode(state);
        }
        return result;
    }

    @Override
    /** @require transition is of type AbstrGraphTransition */
    public void addTransition(GraphTransition transition) {
        assert (transition instanceof ShapeGraphTransition)
            || (transition instanceof ShapeGraphNextState) : "Type error : "
            + transition
            + " is not of type ShapeGraphTransition neither ShapeGraphNextState.";
        super.addTransition(transition);
        if (transition.target().equals(INVALID_STATE)) {
            this.invalidTransitionsCount++;
        }
    }

    @Override
    /** Specialises return type. */
    public ShapeGraphState startState() {
        return (ShapeGraphState) super.startState();
    }

    /**
     * The set of transitions with same source and same event.
     * @param trans
     * @return The set of transitions with same source and same event
     */
    public Collection<Transition> getEquivalentTransitions(
            ShapeGraphTransition trans) {
        Collection<Transition> result = new ArrayList<Transition>();
        Iterator<? extends Edge> transIt = trans.source().getTransitionIter();
        while (transIt.hasNext()) {
            ShapeGraphTransition other = (ShapeGraphTransition) transIt.next();
            if (other.getEvent().equals(trans.getEvent())) {
                result.add(other);
            }
        }
        return result;
    }

    @Override
    public Collection<GraphState> getOpenStates() {
        return new CollectionView<GraphState>(this.stateSet) {
            @Override
            public boolean approves(Object obj) {
                return !((State) obj).isClosed();
            }
        };
    }

    @Override
    public int nodeCount() {
        return this.stateSet.size();
    }

    /** Returns the number of invalid transitions. */
    public int invalidTransitionsCount() {
        return this.invalidTransitionsCount;
    }

    @Override
    public synchronized Set<? extends GraphState> nodeSet() {
        return Collections.unmodifiableSet(this.stateSet);
    }

    @Override
    protected SystemRecord createRecord() {
        SystemRecord record = new SystemRecord(getGrammar(), true);
        return record;
    }

    // ---------------------------------------------------------------
    // NON IMPLEMENTED PUBLIC METHODS
    // ---------------------------------------------------------------
    @Override
    public double getBytesPerState() {
        throw new UnsupportedOperationException();
    }

    // ---------------------------------------------------------------
    // FIELDS, CONSTRUCTORS AND STANDARD METHODS
    // ---------------------------------------------------------------

    /**
     * @param grammar
     */
    public AGTS(GraphGrammar grammar, Abstraction.Parameters options) {
        super(grammar);
        getRecord().setCheckIso(true);
        this.options = options;
        this.family = new PatternFamily(options.radius, options.maxIncidence);
        ((InvalidState) INVALID_STATE).removeStateNumber();
        startState(); // Will add the start state
        ((InvalidState) INVALID_STATE).removeStateNumber();
        addState(INVALID_STATE);
        setClosed(INVALID_STATE);
        checkInvariants();
    }

    /** The pattern family of graphs in the transition system. */
    public PatternFamily getFamily() {
        return this.family;
    }

    /**
     * The parameters of the abstraction used for obtaining the states of the
     * transition system.
     */
    public Abstraction.Parameters getParameters() {
        return this.options;
    }

    private final PatternFamily family;
    // initialised with an hasher checking for isomorphism of graphs
    private final MyHashSet<ShapeGraphState> stateSet =
        new MyHashSet<ShapeGraphState>(new IsoCheckHasher());

    private final Abstraction.Parameters options;

    /**
     * This is a unique state that represents all states which could not be
     * constructed because the maximal incidence constraint failed.
     */
    public final static ShapeGraphState INVALID_STATE = new InvalidState();

    /** Used for counting the number of invalid transitions. */
    private int invalidTransitionsCount = 0;

    /** Used for a singleton invalid state. */
    static class InvalidState extends ShapeGraphState {

        InvalidState() {
            super(DefaultAbstrGraph.INVALID_AG);
        }

        void removeStateNumber() {
            super.nr = -1;
        }

        @Override
        public boolean addTransition(GraphTransition transition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    // ---------------------------------------------------------------
    // NON PUBLIC METHODS
    // ---------------------------------------------------------------

    @Override
    /** For now, control is not possible with abstract transformation. */
    protected ShapeGraphState createStartState(Graph startGraph) {
        AbstrGraph ag = null;
        try {
            ag =
                DefaultAbstrGraph.factory(this.family, this.options.precision).getShapeGraphFor(
                    startGraph);
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            return INVALID_STATE;
        }
        ShapeGraphState result = new ShapeGraphState(ag);
        return result;
    }

    @Override
    protected GraphShapeCache createCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void notifyLTSListenersOfClose(State closed) {
        super.notifyLTSListenersOfClose(closed);
    }

    class IsoCheckHasher implements MyHashSet.Hasher<ShapeGraphState> {

        public int getHashCode(ShapeGraphState o) {
            return o.getGraph().hashCode();
        }

        public boolean areEqual(ShapeGraphState o1, ShapeGraphState o2) {
            return o1.getGraph().equals(o2.getGraph());
        }

    }

    // ---------------------------------------------------------------
    // INVARIANTS
    // ---------------------------------------------------------------
    private void checkInvariants() {
        if (!Util.ea()) {
            return;
        }
        assert isStoreTransitions() : "AGTS should always store transitions";
        checkTypeInv();
    }

    private void checkTypeInv() {
        if (!Util.ea()) {
            return;
        }
        for (GraphState s : this.stateSet) {
            assert s instanceof ShapeGraphState : "Type error : " + s
                + " is not of type ShapeGraphState.";
        }
    }

}
