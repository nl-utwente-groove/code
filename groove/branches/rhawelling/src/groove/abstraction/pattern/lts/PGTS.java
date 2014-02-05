/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.lts;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.explore.util.PatternGraphFrameMatchCollector;
import groove.abstraction.pattern.explore.util.PatternGraphMatchApplier;
import groove.abstraction.pattern.explore.util.PatternGraphMatchSetCollector;
import groove.abstraction.pattern.explore.util.PatternRuleEventApplier;
import groove.abstraction.pattern.shape.PatternFactory;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.trans.PatternGraphGrammar;
import groove.control.CtrlFrame;
import groove.graph.AGraph;
import groove.graph.ElementFactory;
import groove.graph.GGraph;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.IsoChecker;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.lts.GTS;
import groove.lts.LTSFactory;
import groove.util.collect.NestedIterator;
import groove.util.collect.TransformIterator;
import groove.util.collect.TreeHashSet;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Pattern Graph Transition System.
 * 
 * Complete re-implementation (AKA copy-paste :P) of the functionality in {@link GTS}. 
 */
public class PGTS extends AGraph<PatternState,PatternTransition> {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /**
     * Tree resolution of the state set (which is a {@link TreeHashSet}). A
     * smaller value means memory savings; a larger value means speedup.
     */
    private static final int STATE_SET_RESOLUTION = 2;

    /**
     * Tree root resolution of the state set (which is a {@link TreeHashSet}).
     * A larger number means speedup, but the memory initially reserved for the
     * set grows exponentially with this number.
     */
    private static final int STATE_SET_ROOT_RESOLUTION = 10;

    /**
     * Number of states for which the state set should have room initially.
     */
    private static final int INITIAL_STATE_SET_SIZE = 10000;

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The rule system generating this PGTS. */
    private final PatternGraphGrammar grammar;
    /** The start state of this PGTS. */
    protected PatternState startState;
    /** Unique factory for host elements, associated with this PGTS. */
    private PatternFactory hostFactory;
    /** The set of states of the PGTS. */
    private StateSet stateSet;
    /** The set of transitions of the PGTS. */
    private TransitionSet transitionSet;
    /** The number of transitions in the PGTS. */
    private int transitionCount = 0;
    /** The number of closed states in the PGTS. */
    private int closedStateCount = 0;

    /**
     * Set of {@link PGTSListener} s to be notified of changes in this graph.
     * Set to <tt>null</tt> when the graph is fixed.
     */
    private Set<PGTSListener> listeners = new MyHashSet<PGTSListener>();

    /** Constructs a PGTS for the given grammar. */
    public PGTS(PatternGraphGrammar grammar) {
        super(grammar.getName() + "-pgts");
        this.grammar = grammar;
    }

    /** Initialises the start state and corresponding host factory. */
    protected void initialise() {
        assert this.hostFactory == null && this.startState == null;
        PatternGraph startGraph = createStartGraph(this.grammar.getStartGraph());
        this.hostFactory = startGraph.getFactory();
        this.startState = createStartState(startGraph);
        addState(this.startState);
    }

    /** 
     * Returns a copy of the given graph with a fresh element factory.
     * The resulting graph will be used as start graph state.
     */
    protected PatternGraph createStartGraph(PatternGraph startGraph) {
        return startGraph.clone();
    }

    /** 
     * Creates the start state for this GTS.
     * Makes sure that the start state graph has a fresh factory.
     */
    protected PatternState createStartState(PatternGraph startGraph) {
        return new PatternGraphState(startGraph, CtrlFrame.NEW_CONTROL
                ? this.grammar.getControl().getStart() : this.grammar.getCtrlAut().getStart(), 0,
            this);
    }

    /**
     * Returns the start state of this LTS.
     */
    public PatternState startState() {
        if (this.startState == null) {
            initialise();
        }
        return this.startState;
    }

    /**
     * Returns the rule system underlying this GTS.
     */
    public PatternGraphGrammar getGrammar() {
        return this.grammar;
    }

    /** 
     * Returns the host element factory associated with this GTS.
     * This is taken from the start state graph. 
     */
    public PatternFactory getHostFactory() {
        if (this.hostFactory == null) {
            initialise();
        }
        return this.hostFactory;
    }

    @Override
    public int nodeCount() {
        return getStateSet().size();
    }

    /**
     * @return Returns the nodeCount
     */
    public final int getStateCount() {
        return nodeCount();
    }

    @Override
    public int edgeCount() {
        return this.transitionCount;
    }

    /**
     * @return Returns the transitionCount.
     */
    public final int getTransitionCount() {
        return this.transitionCount;
    }

    @Override
    public Set<? extends PatternTransition> outEdgeSet(Node node) {
        return ((PatternState) node).getTransitionSet();
    }

    @Override
    public Set<? extends PatternState> nodeSet() {
        return getStateSet();
    }

    @Override
    public Set<? extends PatternTransition> edgeSet() {
        if (this.transitionSet == null) {
            this.transitionSet = new TransitionSet();
        }
        return this.transitionSet;
    }

    @Override
    public boolean addNode(PatternState node) {
        return addState(node) == null;
    }

    @Override
    public boolean addEdge(PatternTransition edge) {
        addTransition(edge);
        return true;
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.LTS;
    }

    /** Basic getter. */
    public int getNextStateNr() {
        return nodeCount();
    }

    /** Get method for the state set. Lazily creates the set first. */
    protected TreeHashSet<PatternState> getStateSet() {
        if (this.stateSet == null) {
            this.stateSet = createStateSet();
        }
        return this.stateSet;
    }

    /** Callback factory method for a state set. */
    protected StateSet createStateSet() {
        return new StateSet(null);
    }

    /**
     * Adds a transition to the GTS, under the assumption that the source and
     * target states are already present.
     * @param transition the source state of the transition to be added
     */
    public void addTransition(PatternTransition transition) {
        // add (possibly isomorphically modified) edge to LTS
        if (transition.source().addTransition(transition)) {
            this.transitionCount++;
            fireAddEdge(transition);
        }
    }

    /**
     * Adds a state to the GTS, if it is not isomorphic to an existing state.
     * Returns the isomorphic state if one was found, or <tt>null</tt> if the
     * state was actually added.
     * @param newState the state to be added
     * @return a state isomorphic to <tt>state</tt>; or <tt>null</tt> if
     *         there was no existing isomorphic state (in which case, and only
     *         then, <tt>state</tt> was added and the listeners notified).
     */
    public PatternState addState(PatternState newState) {
        // see if isomorphic graph is already in the LTS
        PatternState result = getStateSet().put(newState);
        // if not ... 
        if (result == null) {
            // and then add it to the GTS 
            fireAddNode(newState);
        }
        return result;
    }

    /**
     * Returns the set of listeners of this GTS.
     * @return an iterator over the graph listeners of this graph
     */
    public Set<PGTSListener> getGraphListeners() {
        if (isFixed()) {
            return Collections.<PGTSListener>emptySet();
        } else {
            return this.listeners;
        }
    }

    /**
     * Adds a graph listener to this graph.
     */
    public void addLTSListener(PGTSListener listener) {
        if (this.listeners != null) {
            this.listeners.add(listener);
        }
    }

    /**
     * Removes a graph listener from this graph.
     */
    public void removeLTSListener(PGTSListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Notifies the {@link PGTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddNode(PatternState node) {
        super.fireAddNode(node);
        for (PGTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, node);
        }
    }

    /**
     * Notifies the {@link PGTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddEdge(PatternTransition edge) {
        super.fireAddEdge(edge);
        for (PGTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, edge);
        }
    }

    /** The default is not to create any graph elements. */
    @Override
    public ElementFactory<PatternState,PatternTransition> getFactory() {
        return new LTSFactory<PatternState,PatternTransition>(this);
    }

    /** Exports the GTS to a plain graph representation. */
    public PlainGraph toPlainGraph() {
        PlainGraph result = new PlainGraph(getName(), GraphRole.LTS);
        Map<PatternState,PlainNode> nodeMap = new HashMap<PatternState,PlainNode>();
        for (PatternState state : nodeSet()) {
            PlainNode image = result.addNode(state.getNumber());
            nodeMap.put(state, image);
        }
        for (PatternTransition transition : edgeSet()) {
            result.addEdge(nodeMap.get(transition.source()), transition.label().text(),
                nodeMap.get(transition.target()));
        }
        return result;
    }

    /** Callback factory method for the match applier. */
    public PatternRuleEventApplier createMatchApplier() {
        return new PatternGraphMatchApplier(this);
    }

    /** Returns a fresh match collector for the given state. */
    public PatternGraphMatchSetCollector createMatchCollector(PatternState state) {
        return CtrlFrame.NEW_CONTROL ? new PatternGraphFrameMatchCollector(state)
                : new PatternGraphMatchSetCollector(state);
    }

    /** Returns the number of not fully explored states. */
    public int openStateCount() {
        return nodeCount() - this.closedStateCount;
    }

    /** Notify the GTS that the given state was closed. */
    public void notifyClosure(PatternState state) {
        assert state.isClosed();
        this.closedStateCount++;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public AGraph<PatternState,PatternTransition> clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GGraph<PatternState,PatternTransition> newGraph(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(PatternTransition edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(PatternState node) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    // --------
    // StateSet
    // --------

    /** Specialised set implementation for storing states. */
    public static class StateSet extends TreeHashSet<PatternState> {

        /** The isomorphism checker of the state set. */
        protected final IsoChecker checker;

        /** Constructs a new, empty state set. */
        public StateSet(IsoChecker checker) {
            super(INITIAL_STATE_SET_SIZE, STATE_SET_RESOLUTION, STATE_SET_ROOT_RESOLUTION);
            if (checker == null) {
                this.checker = IsoChecker.getInstance(true);
            } else {
                this.checker = checker;
            }
        }

        /**
         * First compares the control locations, then calls
         * {@link IsoChecker#areIsomorphic(Graph, Graph)}.
         */
        @Override
        protected boolean areEqual(PatternState myState, PatternState otherState) {
            if (myState.getFrame() != otherState.getFrame()) {
                return false;
            }
            return this.checker.areIsomorphic(myState.getGraph(), otherState.getGraph());
        }

        /**
         * Returns the hash code of the state, modified by the control location
         * (if any).
         */
        @Override
        protected int getCode(PatternState stateKey) {
            int result;
            CertificateStrategy certifier = this.checker.getCertifier(stateKey.getGraph(), true);
            Object certificate = certifier.getGraphCertificate();
            result = certificate.hashCode();
            CtrlFrame frame = stateKey.getFrame();
            if (frame != null) {
                result += frame.hashCode();
            }
            return result;
        }

    }

    // -------------
    // TransitionSet
    // -------------

    /**
     * An unmodifiable view on the transitions of this GTS. The transitions are
     * (re)constructed from the outgoing transitions as stored in the states.
     */
    private class TransitionSet extends AbstractSet<PatternTransition> {

        /** Empty constructor with the correct visibility. */
        TransitionSet() {
            // empty
        }

        /**
         * To determine whether a transition is in the set, we look if the
         * source state is known and if the transition is registered as outgoing
         * transition with the source state.
         */
        @Override
        public boolean contains(Object o) {
            boolean result = false;
            if (o instanceof PatternTransition) {
                PatternTransition transition = (PatternTransition) o;
                PatternState source = transition.source();
                result = (containsNode(source) && outEdgeSet(source).contains(transition));
            }
            return result;
        }

        /**
         * Iterates over the state and for each state over that state's outgoing
         * transitions.
         */
        @Override
        public Iterator<PatternTransition> iterator() {
            Iterator<Iterator<? extends PatternTransition>> stateOutTransitionIter =
                new TransformIterator<PatternState,Iterator<? extends PatternTransition>>(
                    nodeSet().iterator()) {
                    @Override
                    public Iterator<? extends PatternTransition> toOuter(PatternState state) {
                        return outEdgeSet(state).iterator();
                    }
                };
            return new NestedIterator<PatternTransition>(stateOutTransitionIter);
        }

        @Override
        public int size() {
            return getTransitionCount();
        }

    }

}
