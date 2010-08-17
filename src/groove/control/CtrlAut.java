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
 * $Id: ControlAutomaton.java,v 1.10 2008-01-30 11:13:57 fladder Exp $
 */
package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.GraphCache;
import groove.trans.RuleSystem;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements a control automaton graph.
 * The graph has CtrlStates for nodes and CtrlTransitions for edges.
 * The class offers various operations to compose automata.
 * @author Arend Rensink
 */
public class CtrlAut extends AbstractGraphShape<GraphCache> {
    /**
     * Constructs a new control automaton.
     * The start state and final state are automatically initialised.
     */
    public CtrlAut() {
        this.startState = createState();
        this.finalState = createState();
    }

    /**
     * Constructs a new control automaton, with a given
     * start and final state.
     */
    public CtrlAut(CtrlState startState, CtrlState finalState) {
        this.startState = startState;
        this.finalState = finalState;
    }

    /** Adds a control transition to this automaton. */
    public boolean addTransition(CtrlTransition edge) {
        return this.transitions.add(edge);
    }

    /** Adds a control state to this automaton. */
    public boolean addState(CtrlState node) {
        return this.states.add(node);
    }

    @Override
    public Set<CtrlTransition> edgeSet() {
        return this.transitions;
    }

    public Set<CtrlState> nodeSet() {
        return this.states;
    }

    /** Returns the start state of the automaton. */
    public CtrlState getStart() {
        return this.startState;
    }

    /** The start state of the automaton. */
    private final CtrlState startState;

    /** Returns the final  state of the automaton. */
    public CtrlState getFinal() {
        return this.finalState;
    }

    /** The final state of the automaton. */
    private final CtrlState finalState;

    /** Factory method to create a control state for this automaton. */
    private CtrlState createState() {
        return new CtrlState();
    }

    /** 
     * Returns a copy of this control automaton in which all 
     * rule names have been instantiated with actual rules.
     * @param rules the rule system from which the actual rules are
     * taken
     * @throws FormatException if the rule system is not compatible with the
     * rule calls in this automaton
     */
    public CtrlAut instantiate(RuleSystem rules) throws FormatException {
        Set<FormatError> errors = new TreeSet<FormatError>();
        Map<CtrlState,CtrlState> oldToNewStateMap =
            new HashMap<CtrlState,CtrlState>();
        CtrlState newStart = getStart().instantiate(oldToNewStateMap, rules);
        CtrlState newFinal = getFinal().instantiate(oldToNewStateMap, rules);
        CtrlAut result = new CtrlAut(newStart, newFinal);
        for (CtrlState oldState : nodeSet()) {
            try {
                result.addState(oldState.instantiate(oldToNewStateMap, rules));
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        for (CtrlTransition oldTrans : edgeSet()) {
            try {
                result.addTransition(oldTrans.instantiate(oldToNewStateMap,
                    rules));
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        return result;
    }

    /** The set of states of this control automaton. */
    private final Set<CtrlState> states = new HashSet<CtrlState>();

    /** The set of transitions of this control automaton. */
    private final Set<CtrlTransition> transitions = new TransitionSet();

    /** 
     * Offers a modifiable view on the transitions stored in the states 
     * of this automaton.
     */
    private class TransitionSet extends AbstractSet<CtrlTransition> {
        @Override
        public boolean add(CtrlTransition e) {
            return e.source().addTransition(e);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof CtrlTransition) {
                return ((CtrlTransition) o).source().getTransitions().contains(
                    o);
            } else {
                return false;
            }
        }

        @Override
        public Iterator<CtrlTransition> iterator() {
            return new NestedIterator<CtrlTransition>(
                new TransformIterator<CtrlState,Iterator<CtrlTransition>>(
                    nodeSet().iterator()) {
                    @Override
                    protected Iterator<CtrlTransition> toOuter(CtrlState from) {
                        return from.getTransitions().iterator();
                    }
                });
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            int result = 0;
            for (CtrlState state : nodeSet()) {
                result += state.getTransitions().size();
            }
            return result;
        }
    }
}
