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

import static groove.lts.GTS.CollapseMode.COLLAPSE_EQUAL;
import static groove.lts.GTS.CollapseMode.COLLAPSE_ISO_STRONG;
import static groove.lts.GTS.CollapseMode.COLLAPSE_NONE;
import groove.algebra.AlgebraFamily;
import groove.control.CtrlState;
import groove.explore.result.Result;
import groove.graph.AbstractGraph;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.EdgeMultiplicityVerifier;
import groove.graph.ElementFactory;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.graph.iso.IsoChecker;
import groove.lts.GraphState.Flag;
import groove.trans.Grammar;
import groove.trans.GrammarRecord;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.util.TreeHashSet;
import groove.view.FormatException;
import groove.view.PostApplicationError;

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
 * transitions {@link RuleTransition}s. A GTS stores a fixed rule system.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GTS extends AbstractGraph<GraphState,GraphTransition> implements
        Cloneable {
    /** Debug flag controlling whether states are compared for control location equality. */
    protected final static boolean CHECK_CONTROL_LOCATION = true;
    /**
     * The number of transitions generated but not added (due to overlapping
     * existing transitions)
     */
    private static int spuriousTransitionCount;

    /** Post application errors of states in the GTS. */
    private final Map<GraphState,Set<PostApplicationError>> postErrors;

    /** The edge verifiers associated with the (constant) type graph. */
    private final EdgeMultiplicityVerifier verifier;

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
    public GTS(Grammar grammar) {
        super(grammar.getName() + "-gts");
        grammar.testFixed(true);
        this.grammar = grammar;
        this.postErrors = new HashMap<GraphState,Set<PostApplicationError>>();
        this.verifier = new EdgeMultiplicityVerifier(grammar.getTypeGraph());
    }

    /**
     * Returns the start state of this LTS.
     */
    public GraphState startState() {
        if (this.startState == null) {
            HostGraph startGraph =
                createStartGraph(this.grammar.getStartGraph());
            this.startState = createStartState(startGraph);
            addState(this.startState);
        }
        return this.startState;
    }

    /** 
     * Factory method to create a start graph for this GTS, by
     * cloning a given host graph.
     */
    protected HostGraph createStartGraph(HostGraph startGraph) {
        HostGraph result = startGraph.clone(getAlgebraFamily());
        result.setFixed();
        return result;
    }

    /** 
     * Factory method to create the start state for this GTS, for a given start graph.
     */
    protected GraphState createStartState(HostGraph startGraph) {
        return new StartGraphState(this, startGraph);
    }

    /**
     * Returns the rule system underlying this GTS.
     */
    public Grammar getGrammar() {
        return this.grammar;
    }

    /** 
     * Returns the host element factory associated with this GTS.
     * This is taken from the start state graph. 
     */
    public HostFactory getHostFactory() {
        if (this.hostFactory == null) {
            this.hostFactory = this.grammar.getStartGraph().getFactory();
        }
        return this.hostFactory;
    }

    /** Returns the algebra family of the GTS. */
    public AlgebraFamily getAlgebraFamily() {
        return getGrammar().getProperties().getAlgebraFamily();
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
    protected void setFinal(GraphState state) {
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
     */
    public boolean hasOpenStates() {
        return openStateCount() > 0;
    }

    /** Returns the number of not fully explored states. */
    public int openStateCount() {
        return nodeCount() - this.closedStateCount;
    }

    @Override
    public int nodeCount() {
        return getStateSet().size();
    }

    @Override
    public int edgeCount() {
        return this.transitionCount;
    }

    @Override
    public Set<? extends GraphTransition> outEdgeSet(Node node) {
        GraphState state = (GraphState) node;
        return state.getTransitions(GraphTransition.Class.ANY);
    }

    /** Checks if this GTS has a state with a post application error. */
    public boolean hasPostError() {
        return !this.postErrors.isEmpty();
    }

    /** Adds a {@link PostApplicationError} of a {@link GraphState}. */
    public void addPostError(GraphState state, PostApplicationError error) {
        Set<PostApplicationError> errors = this.postErrors.get(state);
        if (errors == null) {
            errors = new HashSet<PostApplicationError>();
        }
        errors.add(error);
        this.postErrors.put(state, errors);
    }

    /** Adds a set of {@link PostApplicationError}s of a {@link GraphState}. */
    public void addPostErrors(GraphState state, Set<PostApplicationError> errors) {
        Set<PostApplicationError> old = this.postErrors.get(state);
        if (old == null) {
            this.postErrors.put(state, errors);
        } else {
            errors.addAll(old);
        }
    }

    /** 
     * Gets the {@link PostApplicationError}s that are associated with a
     * {@link GraphState}. May return <code>null</code> if the state has no
     * errors.
     */
    public Set<? extends PostApplicationError> getPostErrors(GraphState state) {
        return this.postErrors.get(state);
    }

    // ----------------------- OBJECT OVERRIDES ------------------------

    /** The default is not to create any graph elements. */
    @Override
    public ElementFactory<GraphState,GraphTransition> getFactory() {
        return new LTSFactory<GraphState,GraphTransition>(this);
    }

    public Set<? extends GraphState> nodeSet() {
        if (this.nodeSet == null) {
            this.nodeSet = getStateSet();
        }
        return this.nodeSet;
    }

    public Set<? extends GraphTransition> edgeSet() {
        if (this.transitionSet == null) {
            this.transitionSet = new TransitionSet();
        }
        return this.transitionSet;
    }

    /** Get method for the state set. Lazily creates the set first. */
    protected TreeHashSet<GraphState> getStateSet() {
        if (this.stateSet == null) {
            this.stateSet = createStateSet();
        }
        return this.stateSet;
    }

    /** Callback factory method for a state set. */
    protected StateSet createStateSet() {
        return new StateSet(getCollapse(), null);
    }

    /**
     * Method to determine the collapse strategy of the state set. This is
     * determined by {@link GrammarRecord#isCollapse()} and
     * {@link GrammarRecord#isCheckIso()}.
     */
    protected CollapseMode getCollapse() {
        CollapseMode result;
        if (!getRecord().isCollapse()) {
            result = COLLAPSE_NONE;
        } else if (!getRecord().isCheckIso()) {
            result = COLLAPSE_EQUAL;
        } else {
            result = COLLAPSE_ISO_STRONG;
        }
        return result;
    }

    /**
     * Returns the (fixed) derivation record for this GTS.
     */
    public final GrammarRecord getRecord() {
        if (this.record == null) {
            this.record = new GrammarRecord(this.grammar, getHostFactory());
        }
        return this.record;
    }

    /**
     * Adds a transition to the GTS, under the assumption that the source and
     * target states are already present.
     * @param trans the source state of the transition to be added
     */
    public void addTransition(GraphTransition trans) {
        // add (possibly isomorphically modified) edge to LTS
        if (trans.source().addTransition(trans)) {
            fireAddEdge(trans);
        } else {
            spuriousTransitionCount++;
        }
        if (trans instanceof RuleTransition) {
            try {
                String outputString =
                    ((RuleTransition) trans).getOutputString();
                if (outputString != null) {
                    System.out.print(outputString);
                }
            } catch (FormatException e) {
                System.err.println(e.getMessage());
            }
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

        // if not ... 
        if (result == null) {

            // first check the validity of edge multiplicities ...
            this.verifier.reset();
            this.verifier.count(newState.getGraph());
            if (!this.verifier.check(newState)) {
                addPostErrors(newState, this.verifier.getErrors());
                newState.setClosed(false);
                newState.setError();
            }

            // and then add it to the GTS 
            fireAddNode(newState);

        }

        return result;
    }

    /**
     * Returns the set of listeners of this GTS.
     * @return an iterator over the graph listeners of this graph
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
        this.transitionCount++;
        super.fireAddEdge(edge);
        for (GTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, edge);
        }
    }

    /**
     * Notifies all listeners of a change in status of a given state.
     * @param state the status of which the status has changed
     * @param flag the flag that has changed in the state
     */
    protected void fireUpdateState(GraphState state, Flag flag) {
        switch (flag) {
        case CLOSED:
            if (state.getSchedule().isSuccess() || hasFinalProperties(state)) {
                setFinal(state);
            }
            this.closedStateCount++;
        }
        for (GTSListener listener : getGraphListeners()) {
            listener.statusUpdate(this, state, flag);
        }
    }

    /**
     * Tests if a state is present and has no modifying outgoing transitions to
     * a present state.
     */
    private boolean hasFinalProperties(GraphState state) {
        boolean result = state.isPresent();
        if (result) {
            for (RuleTransition trans : state.getRuleTransitions()) {
                if (!trans.target().isAbsent()) {
                    if (trans.getCtrlTransition().getRule().isModifying()
                        || !trans.target().equals(state)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return Returns the transitionCount.
     */
    public final int getTransitionCount() {
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
    public boolean addEdge(GraphTransition edge) {
        if (edge instanceof RuleTransition) {
            addTransition(edge);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeNode(GraphState node) {
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

    /** Returns the match applier associated with this GTS. */
    public final MatchApplier getMatchApplier() {
        if (this.matchApplier == null) {
            this.matchApplier = createMatchApplier();
        }
        return this.matchApplier;
    }

    /** Factory method for the match applier. */
    protected MatchApplier createMatchApplier() {
        return new MatchApplier(this);
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
    private final Grammar grammar;
    /** Unique factory for host elements, associated with this GTS. */
    private HostFactory hostFactory;
    /** The set of nodes of the GTS. */
    private Set<? extends GraphState> nodeSet;
    /** The set of states of the GTS. */
    private StateSet stateSet;

    /** The set of transitions of the GTS. */
    private TransitionSet transitionSet;

    /**
     * Set of states that have not yet been extended.
     * @invariant <tt>freshStates \subseteq nodes</tt>
     */
    private final Set<GraphState> finalStates = new HashSet<GraphState>();

    private final Set<GraphState> resultStates = new HashSet<GraphState>();

    /** The system record for this GTS. */
    private GrammarRecord record;
    /** The match applier associated with this GTS. */
    private MatchApplier matchApplier;
    /**
     * The number of closed states in the GTS.
     */
    private int closedStateCount = 0;
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

    /** Specialised set implementation for storing states. */
    public static class StateSet extends TreeHashSet<GraphState> {
        /** Constructs a new, empty state set. */
        public StateSet(CollapseMode collapse,
                IsoChecker<HostNode,HostEdge> checker) {
            super(INITIAL_STATE_SET_SIZE, STATE_SET_RESOLUTION,
                STATE_SET_ROOT_RESOLUTION);
            this.collapse = collapse;
            if (checker == null) {
                this.checker =
                    IsoChecker.getInstance(collapse == COLLAPSE_ISO_STRONG);
            } else {
                this.checker = checker;
            }
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
            if (CHECK_CONTROL_LOCATION
                && myState.getCtrlState() != otherState.getCtrlState()) {
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
            } else {
                return this.checker.areIsomorphic(myGraph, otherGraph,
                    myBoundNodes, otherBoundNodes);
            }
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
                            Certificate parCert =
                                certifier.getCertificateMap().get(node);
                            hashCode = parCert.hashCode();
                        }
                        result += hashCode;
                        // shift left to ensure the parameters' order matters
                        result = result << 1 | (result < 0 ? 1 : 0);
                    }
                }
            }
            if (CHECK_CONTROL_LOCATION) {
                result += System.identityHashCode(stateKey.getCtrlState());
            }
            return result;
        }

        /** The isomorphism checker of the state set. */
        private final IsoChecker<HostNode,HostEdge> checker;
        /** The value of the collapse property. */
        protected final CollapseMode collapse;
    }

    /** Mode type for isomorphism collapsing. */
    static protected enum CollapseMode {
        /**
         * No states should be collapsed.
         */
        COLLAPSE_NONE,
        /**
         * Only states with equal graphs should be collapsed.
         */
        COLLAPSE_EQUAL,
        /**
         * Isomorphic graphs should be collapsed, where isomorphism is only
         * weakly tested. A weak isomorphism test could yield false negatives.
         * @see IsoChecker#isStrong()
         */
        COLLAPSE_ISO_WEAK,
        /**
         * Isomorphic graphs should be collapsed, where isomorphism is strongly
         * tested. A strong isomorphism test is more costly than a weak one but
         * will never yield false negatives.
         * @see IsoChecker#isStrong()
         */
        COLLAPSE_ISO_STRONG;
    }

    /** Set of states that only tests for state number as equality. */
    public static class NormalisedStateSet extends TreeHashSet<GraphState> {
        @Override
        protected boolean areEqual(GraphState newKey, GraphState oldKey) {
            return true;
        }

        @Override
        protected int getCode(GraphState key) {
            return key.getNumber();
        }

        @Override
        protected boolean allEqual() {
            return true;
        }
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
            boolean result = false;
            if (o instanceof GraphTransition) {
                GraphTransition transition = (GraphTransition) o;
                GraphState source = transition.source();
                result =
                    (containsNode(source) && outEdgeSet(source).contains(
                        transition));
            }
            return result;
        }

        /**
         * Iterates over the state and for each state over that state's outgoing
         * transitions.
         */
        @Override
        public Iterator<GraphTransition> iterator() {
            Iterator<Iterator<? extends GraphTransition>> stateOutTransitionIter =
                new TransformIterator<GraphState,Iterator<? extends GraphTransition>>(
                    nodeSet().iterator()) {
                    @Override
                    public Iterator<? extends GraphTransition> toOuter(
                            GraphState state) {
                        return outEdgeSet(state).iterator();
                    }
                };
            return new NestedIterator<GraphTransition>(stateOutTransitionIter);
        }

        @Override
        public int size() {
            return getTransitionCount();
        }
    }
}
