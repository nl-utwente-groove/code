// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: GTS.java,v 1.39 2008-03-18 10:58:51 fladder Exp $
 */
package groove.lts;

import groove.control.ControlState;
import groove.control.Location;
import groove.control.LocationAutomatonBuilder;
import groove.explore.result.Result;
import groove.graph.AbstractGraphShape;
import groove.graph.Graph;
import groove.graph.GraphShapeCache;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.SystemRecord;
import groove.util.CollectionView;
import groove.util.FilterIterator;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.util.TreeHashSet;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implements an LTS of which the states are {@link GraphState}s and the
 * transitions {@link GraphTransition}s. A GTS stores a fixed rule system.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GTS extends AbstractGraphShape<GraphShapeCache> implements LTS {
    /**
     * The number of transitions generated but not added (due to overlapping
     * existing transitions)
     */
    private static int spuriousTransitionCount;

    /**
     * Returns the number of confluent diamonds found during generation.
     */
    public static int getSpuriousTransitionCount() {
        return spuriousTransitionCount;
    }

    /**
     * Returns an estimate of the number of bytes used to store each state.
     */
    public double getBytesPerState() {
        return getStateSet().getBytesPerElement();
    }

    /**
     * Constructs a GTS from a (fixed) graph grammar.
     */
    public GTS(GraphGrammar grammar) {
        grammar.testFixed(true);
        this.ruleSystem = grammar;
        // this.storeTransitions = storeTransitions;
    }

    /**
     * Callback factory method to create and initialise the start graph of the
     * GTS, on the basis of a given graph. Creation is done using
     * {@link #createStartState(Graph)}.
     */
    protected GraphState computeStartState(Graph startGraph) {
        GraphState result = createStartState(startGraph);
        // result.getGraph().setFixed();
        return result;
    }

    /**
     * Callback factory method to create the start graph of the GTS, on the
     * basis of a given graph.
     */
    protected GraphState createStartState(Graph startGraph) {
        // initialise the start state with a control location if necessary
        if (this.ruleSystem.getControl() != null) {
            return new StartGraphState(getRecord(),
                startGraph,
                //    this.ruleSystem.getControl().getStart());
                new LocationAutomatonBuilder().getLocation(this.ruleSystem.getControl().getStart()));
        } else {
            return new StartGraphState(getRecord(), startGraph);
        }
    }

    /** This implementation specialises the return type to {@link GraphState}. */
    public GraphState startState() {
        if (this.startState == null) {
            this.startState = computeStartState(getGrammar().getStartGraph());
            addState(this.startState);
        }
        return this.startState;
    }

    /**
     * Returns the rule system underlying this GTS.
     */
    public GraphGrammar getGrammar() {
        return this.ruleSystem;
    }

    public Collection<GraphState> getFinalStates() {
        return this.finalStates;
    }

    /**
     * @return the set of result states.
     */
    public Collection<GraphState> getResultStates() {
        return this.resultStates;
    }

    public boolean hasFinalStates() {
        return !getFinalStates().isEmpty();
    }

    public boolean isFinal(State state) {
        return getFinalStates().contains(state);
    }

    /**
     * @param state the state to be checked.
     * @return true if the state is a result state.
     */
    public boolean isResult(State state) {
        return getResultStates().contains(state);
    }

    /** Adds a given state to the final states of this GTS. */
    private void setFinal(State state) {
        this.finalStates.add((GraphState) state);
    }

    /**
     * @param result the set of result states.
     */
    public void setResult(Result result) {
        this.resultStates.addAll(result.getValue());
    }

    public boolean isOpen(State state) {
        return !state.isClosed();
    }

    /**
     * Indicates if the GTS currently has open states. Equivalent to (but more
     * efficient than) <code>getOpenStateIter().hasNext()</code> or
     * <code>!getOpenStates().isEmpty()</code>.
     * @return <code>true</code> if the GTS currently has open states
     * @see #getOpenStateIter()
     * @see #getOpenStates()
     */
    public boolean hasOpenStates() {
        return openStateCount() > 0;
    }

    /**
     * Returns a view on the set of currently open states.
     * @see #hasOpenStates()
     * @see #getOpenStateIter()
     */
    public Collection<GraphState> getOpenStates() {
        return new CollectionView<GraphState>(getStateSet()) {
            @Override
            public boolean approves(Object obj) {
                return !((State) obj).isClosed();
            }
        };
    }

    /**
     * Returns an iterator over the set of currently open states. Equivalent to
     * <code>getOpenStates().iterator()</code>.
     * @see #hasOpenStates()
     * @see #getOpenStates()
     */
    public Iterator<GraphState> getOpenStateIter() {
        return new FilterIterator<GraphState>(nodeSet().iterator()) {
            @Override
            protected boolean approves(Object obj) {
                return !((State) obj).isClosed();
            }
        };
    }

    /**
     * Removes a state from the set of open states, and notifies the graph
     * listeners. Also determines the final status of the state. Only call this
     * after all outgoing transitions of the state have been generated!
     * @param state the state to be removed from the set of open states
     * @require <tt>state instanceof GraphState</tt>
     */
    public void setClosed(State state) {
        GraphState graphState = (GraphState) state;

        if (graphState.setClosed()) {
            if (determineIsFinal(graphState)) {
                setFinal(graphState);
            }
            incClosedCount();
            notifyLTSListenersOfClose(state);
        }
    }

    /** Increases the count of closed states by one. */
    protected void incClosedCount() {
        this.closedCount++;
    }

    // a state is final if
    // - with location, and then the location is a success state
    // - without location, and then no outgoing transitions
    private boolean determineIsFinal(GraphState state) {
        if (state.getLocation() == null) {

            // only states without applications of unmodifying rules are final
            for (GraphTransition trans : state.getTransitionSet()) {
                if (trans.getEvent().getRule().isModifying()) {
                    return false;
                }
            }
            return true;

        } else {
            Set<Rule> rulesFound = new HashSet<Rule>();
            for (GraphTransition trans : state.getTransitionSet()) {
                rulesFound.add(trans.getEvent().getRule());
            }
            return state.getLocation().isSuccess(rulesFound);
        }
    }

    /** Returns the number of not fully explored states. */
    public int openStateCount() {
        return nodeCount() - this.closedCount;
    }

    @Override
    public int nodeCount() {
        return getStateSet().size();
    }

    @Override
    public int edgeCount() {
        assert this.transitionCount == edgeSet().size();
        return this.transitionCount;
    }

    /**
     * This implementation calls {@link GraphState#getTransitionSet()} on
     * <tt>node</tt>.
     */
    @Override
    public Set<GraphTransition> outEdgeSet(Node node) {
        return ((GraphState) node).getTransitionSet();
    }

    // ----------------------- OBJECT OVERRIDES ------------------------

    public Set<? extends GraphState> nodeSet() {
        return Collections.unmodifiableSet(getStateSet());
    }

    public Set<? extends GraphTransition> edgeSet() {
        if (isStoreTransitions()) {
            return new TransitionSet();
        } else {
            return new NextStateSet();
        }
    }

    /** Get method for the state set. Lazily creates the set first. */
    public TreeHashSet<GraphState> getStateSet() {
        if (this.stateSet == null) {
            this.stateSet = createStateSet();
        }
        return this.stateSet;
    }

    /** Callback factory method for a state set. */
    protected TreeHashSet<GraphState> createStateSet() {
        return new StateSet(getCollapse());
    }

    /**
     * Method to determine the collapse strategy of the state set. This is
     * determined by {@link SystemRecord#isCollapse()} and
     * {@link SystemRecord#isCheckIso()}.
     */
    protected int getCollapse() {
        int collapse;
        if (!getRecord().isCollapse()) {
            collapse = StateSet.COLLAPSE_NONE;
        } else if (!getRecord().isCheckIso()) {
            collapse = StateSet.COLLAPSE_EQUAL;
        } else {
            collapse = StateSet.COLLAPSE_ISO_STRONG;
        }
        return collapse;
    }

    @Override
    protected GraphShapeCache createCache() {
        return new GraphShapeCache(this, false);
    }

    /**
     * Returns the (fixed) derivation record for this GTS.
     */
    public final SystemRecord getRecord() {
        if (this.record == null) {
            this.record = createRecord();
        }
        return this.record;
    }

    /** Callback method to create a derivation data store for this GTS. */
    protected SystemRecord createRecord() {
        return new SystemRecord(getGrammar());
    }

    /**
     * Adds a transition to the GTS, under the assumption that the source and
     * target states are already present.
     * @param transition the source state of the transition to be added
     */
    public void addTransition(GraphTransition transition) {
        if (isStoreTransitions()) {
            // add (possibly isomorphically modified) edge to LTS
            if (transition.source().addTransition(transition)) {
                this.transitionCount++;
                fireAddEdge(transition);
            } else {
                spuriousTransitionCount++;
            }
        } else if (transition instanceof GraphNextState) {
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
    public GraphState addState(GraphState newState) {
        // see if isomorphic graph is already in the LTS
        GraphState result = getStateSet().put(newState);
        if (result == null) {
            ((AbstractGraphState) newState).setNumber(nodeCount() - 1);
            fireAddNode(newState);
        }
        return result;
    }

    /**
     * Indicates if transitions are to be stored in the GTS. If they are not
     * stored, the transition set will only include the spanning tree
     * (consisting of the {@link GraphNextState}s).
     */
    protected final boolean isStoreTransitions() {
        return getRecord().isStoreTransitions();
    }

    /**
     * Iterates over the graph listeners and notifies those which are also LTS
     * listeners of the fact that a state has been closed.
     */
    protected void notifyLTSListenersOfClose(State closed) {
        Iterator<GraphShapeListener> listenerIter = getGraphListeners();
        while (listenerIter.hasNext()) {
            GraphShapeListener listener = listenerIter.next();
            if (listener instanceof LTSListener) {
                ((LTSListener) listener).closeUpdate(this, closed);
            }
        }
    }

    /**
     * @return Returns the transitionCount.
     */
    final int getTransitionCount() {
        return this.transitionCount;
    }

    /**
     * The start state of this LTS.
     * @invariant <tt>nodeSet().contains(startState)</tt>
     */
    private GraphState startState;

    /**
     * The rule system generating this LTS.
     * @invariant <tt>ruleSystem != null</tt>
     */
    private final GraphGrammar ruleSystem;
    /** The set of states of the GTS. */
    private TreeHashSet<GraphState> stateSet;

    /**
     * Set of states that have not yet been extended.
     * @invariant <tt>freshStates \subseteq nodes</tt>
     */
    private final Set<GraphState> finalStates = new HashSet<GraphState>();

    private final Set<GraphState> resultStates = new HashSet<GraphState>();

    /** The system record for this GTS. */
    private SystemRecord record;
    /**
     * The number of closed states in the GTS.
     */
    private int closedCount = 0;
    /**
     * The number of transitions in the GTS.
     */
    private int transitionCount = 0;

    // /** Flag to indicate whether transitions are to be stored in the GTS. */
    // private final boolean storeTransitions;

    /** Specialised set implementation for storing states. */
    public static class StateSet extends TreeHashSet<GraphState> {
        /** Constructs a new, empty state set. */
        public StateSet(int collapse) {
            super(INITIAL_STATE_SET_SIZE, STATE_SET_RESOLUTION,
                STATE_SET_ROOT_RESOLUTION);
            this.collapse = collapse;
            this.checker =
                DefaultIsoChecker.getInstance(collapse == COLLAPSE_ISO_STRONG);
        }

        /**
         * First compares the control locations, then calls
         * {@link IsoChecker#areIsomorphic(Graph, Graph)}.
         * @see GraphState#getLocation()
         */
        @Override
        protected boolean areEqual(GraphState stateKey, GraphState otherStateKey) {
            if (this.collapse == COLLAPSE_NONE) {
                return stateKey == otherStateKey;
            } else {
                // distinct control locations means distinct states
                if (isDistinctLocations(stateKey, otherStateKey)) {
                    return false;
                }
                Graph one = stateKey.getGraph();
                Graph two = otherStateKey.getGraph();
                if (this.collapse == COLLAPSE_EQUAL) {
                    // check for graph equality
                    return one.nodeSet().equals(two.nodeSet())
                        && one.edgeSet().equals(two.edgeSet());
                } else {
                    // check for graph isomorphism
                    if (this.checker.areIsomorphic(one, two)) {
                        // if variables are involved we need to make sure they 
                        // map to isomorphic nodes
                        ControlState cs = (ControlState) stateKey.getLocation();
                        if (cs != null) {
                            List<String> variables =
                                ((ControlState) stateKey.getLocation()).getInitializedVariables();
                            if (variables.size() > 0) {
                                NodeEdgeMap isomorphism =
                                    ((DefaultIsoChecker) this.checker).getIsomorphism(
                                        one, two);
                                if (isomorphism != null) {
                                    Node[] parametersOne =
                                        stateKey.getParameters();
                                    Node[] parametersTwo =
                                        otherStateKey.getParameters();
                                    if (parametersOne != null
                                        && parametersTwo != null) {
                                        for (int i = 0; i < parametersOne.length; i++) {
                                            if (parametersOne[i] != parametersTwo[i]) {
                                                return false;
                                            }
                                        }
                                    } else {
                                        return false;
                                    }
                                }
                            }

                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        /**
         * Callback method to test if two graph states have different associated
         * locations.
         */
        protected boolean isDistinctLocations(GraphState stateKey,
                GraphState otherStateKey) {
            Location control = stateKey.getLocation();
            return control != null
                && !control.equals(otherStateKey.getLocation());
        }

        /**
         * Returns the hash code of the state, modified by the control location
         * (if any).
         */
        @Override
        protected int getCode(GraphState stateKey) {
            int result;
            if (this.collapse == COLLAPSE_NONE) {
                result = System.identityHashCode(stateKey);
            } else {
                if (this.collapse == COLLAPSE_EQUAL) {
                    Graph graph = stateKey.getGraph();
                    result =
                        graph.nodeSet().hashCode() + graph.edgeSet().hashCode();
                } else {
                    CertificateStrategy certifier =
                        stateKey.getGraph().getCertifier(true);
                    Object certificate = certifier.getGraphCertificate();
                    result = certificate.hashCode();
                }
                Object control = stateKey.getLocation();
                result += control == null ? 0 : control.hashCode();
                if (stateKey.getParameters() != null) {
                    CertificateStrategy certifier =
                        stateKey.getGraph().getCertifier(true);
                    for (Node n : stateKey.getParameters()) {
                        if (n != null) {
                            result +=
                                certifier.getCertificateMap().get(n).hashCode();
                            // shift left to ensure the parameters' order matters
                            result = result << 1 | (result < 0 ? 1 : 0);
                        }
                    }
                }
            }
            return result;
        }

        /** The isomorphism checker of the state set. */
        private final IsoChecker checker;
        /** The value of the collapse property. */
        private final int collapse;

        /**
         * Value for the state collapse property indicating that no states
         * should be collapsed.
         */
        static public final int COLLAPSE_NONE = 0;
        /**
         * Value for the state collapse property indicating that only states
         * with equal graphs should be collapsed.
         */
        static public final int COLLAPSE_EQUAL = 1;
        /**
         * Value for the state collapse property indicating that states with
         * isomorphic graphs should be collapsed, where isomorphism is only
         * weakly tested. A weak isomorphism test could yield false negatives.
         * @see IsoChecker#isStrong()
         */
        static public final int COLLAPSE_ISO_WEAK = 2;
        /**
         * Value for the state collapse property indicating that states with
         * isomorphic graphs should be collapsed, where isomorphism is strongly
         * tested. A strong isomorphism test is more costly than a weak one but
         * will never yield false negatives.
         * @see IsoChecker#isStrong()
         */
        static public final int COLLAPSE_ISO_STRONG = 3;
    }

    /**
     * An unmodifiable view on the transitions of this GTS. The transitions are
     * (re)constructed from the outgoing transitions as stored in the states.
     */
    private class TransitionSet extends AbstractSet<GraphTransition> {
        /** Empty constructor with the correct visibility. */
        TransitionSet() {
            // empty
        }

        /**
         * To determine whether a transition is in the set, we look if the
         * source state is known and if the transition is registered as outgoing
         * transition with the source state.
         * @require <tt>o instanceof GraphTransition</tt>
         */
        @Override
        public boolean contains(Object o) {
            if (o instanceof GraphTransition) {
                GraphTransition transition = (GraphTransition) o;
                GraphState source = transition.source();
                return (containsElement(source) && source.containsTransition(transition));
            } else {
                return false;
            }
        }

        /**
         * Iterates over the state and for each state over that state's outgoing
         * transitions.
         */
        @Override
        public Iterator<GraphTransition> iterator() {
            Iterator<Iterator<GraphTransition>> stateOutTransitionIter =
                new TransformIterator<GraphState,Iterator<GraphTransition>>(
                    nodeSet().iterator()) {
                    @Override
                    public Iterator<GraphTransition> toOuter(GraphState state) {
                        return state.getTransitionIter();
                    }
                };
            return new NestedIterator<GraphTransition>(stateOutTransitionIter);
        }

        @Override
        public int size() {
            return getTransitionCount();
        }
    }

    /**
     * An unmodifiable view on the transitions of this GTS. The transitions are
     * (re)constructed from the outgoing transitions as stored in the states.
     */
    private class NextStateSet extends AbstractSet<GraphTransition> {
        /** Empty constructor with the correct visibility. */
        NextStateSet() {
            // empty
        }

        /**
         * To determine whether a transition is in the set, we look if the
         * target state (which is typically a {@link GraphNextState}) is
         * actually this transition.
         */
        @Override
        public boolean contains(Object o) {
            if (o instanceof GraphTransition) {
                GraphTransition transition = (GraphTransition) o;
                GraphState target = transition.target();
                return target.equals(transition);
            } else {
                return false;
            }
        }

        /**
         * Iterates over the states that are {@link GraphNextState}s.
         */
        @Override
        public Iterator<GraphTransition> iterator() {
            Iterator<? extends GraphState> stateIter = nodeSet().iterator();
            return new FilterIterator<GraphTransition>(stateIter) {
                @Override
                protected boolean approves(Object obj) {
                    return obj instanceof GraphNextState;
                }
            };
        }

        @Override
        public int size() {
            return nodeCount() - 1;
        }
    }

    /**
     * Tree resolution of the state set (which is a {@link TreeHashSet}). A
     * smaller value means memory savings; a larger value means speedup.
     */
    protected final static int STATE_SET_RESOLUTION = 2;
    /**
     * Tree root resolution of the state set (which is a {@link TreeHashSet}).
     * A larger number means speedup, but the memory initially reserved for the
     * set grows exponentially with this number.
     */
    protected final static int STATE_SET_ROOT_RESOLUTION = 10;
    /**
     * Number of states for which the state set should have room initially.
     */
    protected final static int INITIAL_STATE_SET_SIZE = 10000;
}