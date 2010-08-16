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

import groove.graph.AbstractGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphCache;
import groove.graph.Node;
import groove.trans.RuleSystem;
import groove.util.NestedIterator;
import groove.util.TransformIterator;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements a control automaton graph.
 * The graph has CtrlStates for nodes and CtrlTransitions for edges.
 * The class offers various operations to compose automata.
 * @author Arend Rensink
 */
public class CtrlAut extends AbstractGraph<GraphCache> {
    /** Constructs a new control automaton.
     * The start state and final state are automatically initialised.
     */
    public CtrlAut() {
        this.startState = createState();
        this.finalState = createState();
    }

    @Override
    public Graph clone() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addEdgeWithoutCheck(Edge edge) {
        return addEdge(edge);
    }

    @Override
    public boolean removeNodeWithoutCheck(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdge(Edge edge) {
        return this.transitions.add((CtrlTransition) edge);
    }

    @Override
    public boolean addNode(Node node) {
        return this.states.add((CtrlState) node);
    }

    @Override
    public CtrlAut newGraph() {
        return new CtrlAut();
    }

    @Override
    public boolean removeEdge(Edge edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<? extends Edge> edgeSet() {
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
     */
    public CtrlAut instantiate(RuleSystem rules) {
        CtrlAut result = new CtrlAut();
        for (CtrlState state : nodeSet()) {

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
