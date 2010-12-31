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

import groove.control.CtrlState;
import groove.explore.result.Result;
import groove.graph.AbstractGraph;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.graph.iso.IsoChecker;
import groove.trans.DefaultHostGraph;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.SystemRecord;
import groove.util.CollectionView;
import groove.util.FilterIterator;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.util.TreeHashSet;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implements an LTS of which the states are {@link GraphState}s and the
 * transitions {@link GraphTransition}s. A GTS stores a fixed rule system.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GTS extends AbstractGraph<GraphState,GraphTransition> {
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
        super(grammar.getName() + "-gts");
        grammar.testFixed(true);
        this.grammar = grammar;
        this.record = new SystemRecord(this);
    }

    /** Initialises the start state and corresponding host factory. */
    protected void initialise() {
        assert this.hostFactory == null && this.startState == null;
        DefaultHostGraph startGraph =
            createStartGraph(this.grammar.getStartGraph());
        this.hostFactory = startGraph.getFactory();
        this.startState = createStartState(startGraph);
        addState(this.startState);
    }

    /** 
     * Returns a copy of the given graph with a fresh element factory.
     * The resulting graph will be used as start graph state.
     */
    protected DefaultHostGraph createStartGraph(DefaultHostGraph startGraph) {
        HostFactory factory = HostFactory.newInstance();
        DefaultHostGraph result =
            new DefaultHostGraph(startGraph.getName(), factory);
        for (HostNode node : startGraph.nodeSet()) {
            factory.addNode(node);
            result.addNode(node);
        }
        for (HostEdge edge : startGraph.edgeSet()) {
            factory.addEdge(edge);
            result.addEdge(edge);
        }
        result.setInfo(GraphInfo.getInfo(startGraph, true).clone());
        result.setFixed();
        return result;
    }

    /** 
     * Creates the start state for this GTS.
     * Makes sure that the start state graph has a fresh factory.
     */
    protected GraphState createStartState(DefaultHostGraph startGraph) {
        return new StartGraphState(this.record, startGraph);
    }

    /**
     * Returns the start state of this LTS.
     */
    public GraphState startState() {
        if (this.startState == null) {
            initialise();
        }
        return this.startState;
    }

    /**
     * Returns the rule system underlying this GTS.
     */
    public GraphGrammar getGrammar() {
        return this.grammar;
    }

    /** 
     * Returns the host element factory associated with this GTS.
     * This is taken from the start state graph. 
     */
    public HostFactory getHostFactory() {
        if (this.hostFactory == null) {
            initialise();
        }
        return this.hostFactory;
    }

    /**
     * Returns the set of final states explored so far.
     */
    public Collection<GraphState> getFinalStates() {
        return this.finalStates;
    }

    /**
     * @return the set of result states.
     */
    public Collection<GraphState> getResultStates() {
        return this.resultStates;
    }

    /**
     * Indicates whether we have found a final state during exploration.
     * Convenience method for <tt>! getFinalStates().isEmpty()</tt>.
     */
    public boolean hasFinalStates() {
        return !getFinalStates().isEmpty();
    }

    /**
     * Indicates whether a given state is final. Equivalent to
     * <tt>getFinalStates().contains(state)</tt>.
     */
    public boolean isFinal(GraphState state) {
        return getFinalStates().contains(state);
    }

    /**
     * @param state the state to be checked.
     * @return true if the state is a result state.
     */
    public boolean isResult(GraphState state) {
        return getResultStates().contains(state);
    }

    /** Adds a given state to the final states of this GTS. */
    private void setFinal(GraphState state) {
        this.finalStates.add(state);
    }

    /**
     * @param result the set of result states.
     */
    public void setResult(Result result) {
        this.resultStates.addAll(result.getValue());
    }

    /**
     * Indicates whether a given state is open, in the sense of not (completely)
     * explored. Equivalent to <tt>!state.isClosed()</tt>.
     */
    public boolean isOpen(GraphState state) {
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
                return !((GraphState) obj).isClosed();
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
                return !((GraphState) obj).isClosed();
            }
        };
    }

    /**
     * Removes a state from the set of open states, and notifies the graph
     * listeners. Also determines the final status of the state. Only call this
     * after all outgoing transitions of the state have been generated!
     * @param state the state to be removed from the set of open states
     * @param complete indicates whether all outgoing transitions of the state have
     * been explored. If {@code true}, determine the final status of the state
     * @require <tt>state instanceof GraphState</tt>
     */
    public void setClosed(GraphState state, boolean complete) {
        if (state.setClosed(complete)) {
            if (state.getSchedule().isSuccess()) {
                setFinal(state);
            }
            incClosedCount();
            fireCloseState(state);
        }
    }

    /** Increases the count of closed states by one. */
    protected void incClosedCount() {
        this.closedCount++;
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

    /**
     * Returns the (fixed) derivation record for this GTS.
     */
    public final SystemRecord getRecord() {
        return this.record;
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
     * Returns the set of listeners of this GTS.
     * @return an iterator over the graph listeners of this graph
     * @ensure result \subseteq GraphListener
     */
    public Set<GTSListener> getGraphListeners() {
        if (isFixed()) {
            return Collections.<GTSListener>emptySet();
        } else {
            return this.listeners;
        }
    }

    /**
     * Adds a graph listener to this graph.
     */
    public void addLTSListener(GTSListener listener) {
        if (this.listeners != null) {
            this.listeners.add(listener);
        }
    }

    /**
     * Removes a graph listener from this graph.
     */
    public void removeLTSListener(GTSListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Notifies the {@link GTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddNode(GraphState node) {
        super.fireAddNode(node);
        for (GTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, node);
        }
    }

    /**
     * Notifies the {@link GTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddEdge(GraphTransition edge) {
        super.fireAddEdge(edge);
        for (GTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, edge);
        }
    }

    /**
     * Iterates over the graph listeners and notifies those which are also LTS
     * listeners of the fact that a state has been closed.
     */
    protected void fireCloseState(GraphState closed) {
        for (GTSListener listener : getGraphListeners()) {
            listener.closeUpdate(this, closed);
        }
    }

    /**
     * @return Returns the transitionCount.
     */
    final int getTransitionCount() {
        return this.transitionCount;
    }

    /**
     * Indicates if the match collector should check for confluent diamonds
     * in this GTS.
     */
    public boolean checkDiamonds() {
        return true;
    }

    /** 
     * Exports the GTS to a plain graph representation,
     * optionally including special edges to represent start, final and
     * open states, and state identifiers.
     */
    public DefaultGraph toPlainGraph(boolean showFinal, boolean showStart,
            boolean showOpen, boolean showNames) {
        DefaultGraph result = new DefaultGraph(getName());
        Map<GraphState,DefaultNode> nodeMap =
            new HashMap<GraphState,DefaultNode>();
        for (GraphState state : nodeSet()) {
            DefaultNode image = result.addNode(state.getNumber());
            nodeMap.put(state, image);
            if (showFinal && isFinal(state)) {
                result.addEdge(image, GTS.FINAL_LABEL_TEXT, image);
            }
            if (showStart && startState().equals(state)) {
                result.addEdge(image, GTS.START_LABEL_TEXT, image);
            }
            if (showOpen && !state.isClosed()) {
                result.addEdge(image, GTS.OPEN_LABEL_TEXT, image);
            }
            if (showNames) {
                result.addEdge(image, state.toString(), image);
            }
        }
        for (GraphTransition transition : edgeSet()) {
            result.addEdge(nodeMap.get(transition.source()),
                transition.label().text(), nodeMap.get(transition.target()));
        }
        return result;
    }

    @Override
    public GTS newGraph(String name) {
        return new GTS(this.grammar);
    }

    @Override
    public boolean addNode(GraphState node) {
        return addState(node) == null;
    }

    @Override
    public boolean removeEdge(GraphTransition edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeWithoutCheck(GraphTransition edge) {
        assert edgeSet().contains(edge);
        addTransition(edge);
        return true;
    }

    @Override
    public boolean removeNodeWithoutCheck(GraphState node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GTS clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.LTS;
    }

    /**
     * The start state of this LTS.
     * @invariant <tt>nodeSet().contains(startState)</tt>
     */
    protected GraphState startState;

    /**
     * The rule system generating this LTS.
     * @invariant <tt>ruleSystem != null</tt>
     */
    private final GraphGrammar grammar;
    /** Unique factory for host elements, associated with this GTS. */
    private HostFactory hostFactory;
    /** The set of states of the GTS. */
    private TreeHashSet<GraphState> stateSet;

    /**
     * Set of states that have not yet been extended.
     * @invariant <tt>freshStates \subseteq nodes</tt>
     */
    private final Set<GraphState> finalStates = new HashSet<GraphState>();

    private final Set<GraphState> resultStates = new HashSet<GraphState>();

    /** The system record for this GTS. */
    private final SystemRecord record;
    /**
     * The number of closed states in the GTS.
     */
    private int closedCount = 0;
    /**
     * The number of transitions in the GTS.
     */
    private int transitionCount = 0;

    /**
     * Set of {@link GTSListener} s to be identified of changes in this graph.
     * Set to <tt>null</tt> when the graph is fixed.
     */
    private Set<GTSListener> listeners = new HashSet<GTSListener>();

    /**
     * Tree resolution of the state set (which is a {@link TreeHashSet}). A
     * smaller value means memory savings; a larger value means speedup.
     */
    public final static int STATE_SET_RESOLUTION = 2;

    /**
     * Tree root resolution of the state set (which is a {@link TreeHashSet}).
     * A larger number means speedup, but the memory initially reserved for the
     * set grows exponentially with this number.
     */
    public final static int STATE_SET_ROOT_RESOLUTION = 10;

    /**
     * Number of states for which the state set should have room initially.
     */
    public final static int INITIAL_STATE_SET_SIZE = 10000;

    /** The text of the self-edge label that indicates a start state. */
    public static final String START_LABEL_TEXT = "start";

    /** The text of the self-edge label that indicates an open state. */
    public static final String OPEN_LABEL_TEXT = "open";

    /** The text of the self-edge label that indicates a final state. */
    public static final String FINAL_LABEL_TEXT = "final";

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
                IsoChecker.getInstance(collapse == COLLAPSE_ISO_STRONG);
        }

        /**
         * First compares the control locations, then calls
         * {@link IsoChecker#areIsomorphic(Graph, Graph)}.
         */
        @Override
        protected boolean areEqual(GraphState myState, GraphState otherState) {
            if (this.collapse == COLLAPSE_NONE) {
                return myState == otherState;
            }
            if (myState.getCtrlState() != otherState.getCtrlState()) {
                return false;
            }
            HostNode[] myBoundNodes = myState.getBoundNodes();
            HostNode[] otherBoundNodes = otherState.getBoundNodes();
            HostGraph myGraph = myState.getGraph();
            HostGraph otherGraph = otherState.getGraph();
            if (this.collapse == COLLAPSE_EQUAL) {
                // check for equality of the bound nodes
                if (!Arrays.equals(myBoundNodes, otherBoundNodes)) {
                    return false;
                }
                // check for graph equality
                Set<?> myNodeSet = new HashSet<HostNode>(myGraph.nodeSet());
                Set<?> myEdgeSet = new HashSet<HostEdge>(myGraph.edgeSet());
                return myNodeSet.equals(otherGraph.nodeSet())
                    && myEdgeSet.equals(otherGraph.edgeSet());
            }
            return this.checker.areIsomorphic(myGraph, otherGraph,
                myBoundNodes, otherBoundNodes);
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
            } else if (this.collapse == COLLAPSE_EQUAL) {
                HostGraph graph = stateKey.getGraph();
                result =
                    graph.nodeSet().hashCode() + graph.edgeSet().hashCode();
                CtrlState ctrlState = stateKey.getCtrlState();
                if (ctrlState != null) {
                    result += ctrlState.hashCode();
                    for (HostNode node : stateKey.getBoundNodes()) {
                        result += node == null ? 31 : node.hashCode();
                        // shift left to ensure the parameters' order matters
                        result = result << 1 | (result < 0 ? 1 : 0);
                    }
                }
            } else {
                CertificateStrategy<HostNode,HostEdge> certifier =
                    this.checker.getCertifier(stateKey.getGraph(), true);
                Object certificate = certifier.getGraphCertificate();
                result = certificate.hashCode();
                CtrlState ctrlState = stateKey.getCtrlState();
                if (ctrlState != null) {
                    result += ctrlState.hashCode();
                    for (HostNode node : stateKey.getBoundNodes()) {
                        int hashCode;
                        // value nodes may be no longer in the graph
                        if (node == null) {
                            hashCode = 31;
                        } else if (node instanceof ValueNode) {
                            hashCode = node.hashCode();
                        } else {
                            Certificate<?> parCert =
                                certifier.getCertificateMap().get(node);
                            hashCode = parCert.hashCode();
                        }
                        result += hashCode;
                        // shift left to ensure the parameters' order matters
                        result = result << 1 | (result < 0 ? 1 : 0);
                    }
                }
            }
            return result;
        }

        /** The isomorphism checker of the state set. */
        private final IsoChecker<HostNode,HostEdge> checker;
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
                return (containsNode(source) && source.containsTransition(transition));
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
}
