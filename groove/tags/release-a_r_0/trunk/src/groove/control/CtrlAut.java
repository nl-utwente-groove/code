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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
        addState(startState);
        addState(finalState);
        this.startState = startState;
        this.finalState = finalState;
    }

    /** Adds a control transition to this automaton. */
    boolean addTransition(CtrlTransition edge) {
        boolean result = this.transitions.add(edge);
        if (result && edge.label().getCall().isOmega()) {
            this.omegaTransitions.add(edge);
        }
        return result;
    }

    /** Removes a control transition from this automaton. */
    boolean removeTransition(CtrlTransition edge) {
        boolean result = this.transitions.remove(edge);
        if (result) {
            this.omegaTransitions.remove(edge);
        }
        return result;
    }

    /** 
     * Convenience method for adding a control transition between given states and with a given label.
     */
    CtrlTransition addTransition(CtrlState source, CtrlLabel label,
            CtrlState target) {
        CtrlTransition result = createTransition(source, label, target);
        addTransition(result);
        return result;
    }

    /** Adds a control state to this automaton. */
    boolean addState(CtrlState node) {
        if (node.getNumber() == this.maxStateNr + 1) {
            this.maxStateNr++;
        }
        return this.states.add(node);
    }

    /** Adds a fresh state to this control automaton. */
    CtrlState addState() {
        CtrlState result = createState();
        addState(result);
        return result;
    }

    @Override
    public Set<CtrlTransition> edgeSet() {
        return this.transitions;
    }

    public Set<CtrlState> nodeSet() {
        return this.states;
    }

    /** Returns the set of omega transitions. */
    public Set<CtrlTransition> getOmegaTransitions() {
        return this.omegaTransitions;
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
        int stateNr = this.maxStateNr + 1;
        boolean fresh = false;
        while (!fresh) {
            for (CtrlState state : nodeSet()) {
                if (stateNr == state.getNumber()) {
                    fresh = false;
                    stateNr++;
                    break;
                }
            }
        }
        return new CtrlState(stateNr);
    }

    /** Factory method for control transitions. */
    private CtrlTransition createTransition(CtrlState source, CtrlLabel label,
            CtrlState target) {
        return new CtrlTransition(source, label, target);
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

    /** The set of omega transitions in this control automaton. */
    private final Set<CtrlTransition> omegaTransitions =
        new HashSet<CtrlTransition>();

    /** 
     * Assigns a list formal parameters to this automaton.
     * @param pars the list of formal parameters; non-{@code null} 
     */
    public final void setPars(List<CtrlPar.Var> pars) {
        this.pars = new ArrayList<CtrlPar.Var>(pars);
    }

    /**
     * Returns the set of parameters of this control automaton.
     * @return the set of parameters; may be {@code null}
     */
    public final List<CtrlPar.Var> getPars() {
        return this.pars;
    }

    /** List of formal parameters of this automaton; may be {@code null}. */
    private List<CtrlPar.Var> pars;

    /** Upper bound of the range of known consecutive state numbers. */
    private int maxStateNr;

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
            if (o instanceof CtrlTransition) {
                CtrlTransition trans = (CtrlTransition) o;
                return trans.source().removeTransition(trans);
            } else {
                return false;
            }
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
