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
import groove.control.Valuator;
import groove.control.instance.Frame;
import groove.explore.util.LTSLabels;
import groove.grammar.Grammar;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNodeSet;
import groove.grammar.model.FormatException;
import groove.grammar.model.PostApplicationError;
import groove.grammar.type.EdgeMultiplicityVerifier;
import groove.graph.AGraph;
import groove.graph.ElementFactory;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.IsoChecker;
import groove.graph.multi.MultiGraph;
import groove.graph.multi.MultiNode;
import groove.lts.Status.Flag;
import groove.transform.Record;
import groove.util.collect.NestedIterator;
import groove.util.collect.SetView;
import groove.util.collect.TransformIterator;
import groove.util.collect.TreeHashSet;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements an LTS of which the states are {@link GraphState}s and the
 * transitions {@link RuleTransition}s. A GTS stores a fixed rule system.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GTS extends AGraph<GraphState,GraphTransition> implements Cloneable {
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
        return allStateSet().getBytesPerElement();
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
            this.startState = createStartState();
            getGrammar().getControl().initialise(this.startState.getGraph().getFactory());
            addState(this.startState);
        }
        return this.startState;
    }

    /**
     * Factory method to create a start graph for this GTS, by
     * cloning a given host graph.
     */
    protected HostGraph createStartGraph() {
        HostGraph result = getGrammar().getStartGraph().clone(getAlgebraFamily());
        result.setFixed();
        return result;
    }

    /**
     * Factory method to create the start state for this GTS, for a given start graph.
     */
    protected GraphState createStartState() {
        return new StartGraphState(this, createStartGraph());
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
        GraphState result = allStateSet().put(newState);

        // if not ...
        if (result == null) {
            if (newState.isRealState()) {
                this.realStateCount++;
            }
            // and then add it to the GTS
            fireAddNode(newState);

            // first check the validity of edge multiplicities ...
            this.verifier.reset();
            this.verifier.count(newState.getGraph());
            if (!this.verifier.check(newState)) {
                addPostErrors(newState, this.verifier.getErrors());
                newState.setError();
            }
        }

        return result;
    }

    @Override
    public Set<? extends GraphState> nodeSet() {
        return allStateSet();
    }

    /** Delegate method for {@link #nodeSet()} with a specialised return type. */
    protected StateSet allStateSet() {
        if (this.allStateSet == null) {
            this.allStateSet = createStateSet();
        }
        return this.allStateSet;
    }

    /** The set of nodes of the GTS. */
    private StateSet allStateSet;

    /** Callback factory method for a state set. */
    protected StateSet createStateSet() {
        return new StateSet(getCollapse(), null);
    }

    /**
     * Method to determine the collapse strategy of the state set. This is
     * determined by {@link Record#isCollapse()} and
     * {@link Record#isCheckIso()}.
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
     * Returns a view on the set of <i>real</i> states in the GTS.
     * A state is real if it is not absent, erroneous or inside a recipe.
     * @see GraphState#isRealState()
     */
    public Set<GraphState> getStates() {
        if (this.realStateSet == null) {
            this.realStateSet = new SetView<GraphState>(nodeSet()) {
                @Override
                public boolean approves(Object obj) {
                    if (!(obj instanceof GraphState)) {
                        return false;
                    }
                    return ((GraphState) obj).isRealState();
                }
            };
        }
        return this.realStateSet;
    }

    /** Set of real states, as a view on {@link #allStateSet}. */
    private Set<GraphState> realStateSet;

    /**
     * Returns the number of real states.
     * Calling this is more efficient than {@code getStates().size()}.
     */
    public int getStateCount() {
        return this.realStateCount;
    }

    /** Number of real states, stored separately for efficiency. */
    private int realStateCount;

    /**
     * Returns the set of error states found so far.
     */
    public Collection<GraphState> getErrorStates() {
        return getStates(Flag.ERROR);
    }

    /**
     * Indicates whether we have found an error state during exploration.
     * Convenience method for <tt>getErrorStateCount() > 0</tt>.
     */
    public boolean hasErrorStates() {
        return hasStates(Flag.ERROR);
    }

    /** Returns the set of error states. */
    public int getErrorStateCount() {
        return getStateCount(Flag.ERROR);
    }

    /**
     * Returns the set of final states explored so far.
     */
    public Collection<GraphState> getFinalStates() {
        return getStates(Flag.FINAL);
    }

    /**
     * Indicates whether we have found a final state during exploration.
     * Convenience method for <tt>getFinalStateCount() > 0</tt>.
     */
    public boolean hasFinalStates() {
        return hasStates(Flag.FINAL);
    }

    /** Returns the set of final states. */
    public int getFinalStateCount() {
        return getStateCount(Flag.FINAL);
    }

    /**
     * Indicates whether we have found a result state during exploration.
     * Convenience method for <tt>getResultStateCount() > 0</tt>.
     */
    public boolean hasResultStates() {
        return hasStates(Flag.RESULT);
    }

    /**
     * Returns the set of result states.
     */
    public Collection<GraphState> getResultStates() {
        return getStates(Flag.RESULT);
    }

    /** Returns the set of result states. */
    public int getResultStateCount() {
        return getStateCount(Flag.RESULT);
    }

    /**
     * Indicates if the GTS currently has open (real) states.
     * @return <code>true</code> if the GTS currently has open states
     */
    public boolean hasOpenStates() {
        return getOpenStateCount() > 0;
    }

    /** Returns the number of not fully explored states. */
    public int getOpenStateCount() {
        return getStateCount() - getStateCount(Flag.CLOSED);
    }

    /**
     * Returns the set of real states with a given flag.
     */
    private Collection<GraphState> getStates(Flag flag) {
        List<GraphState> result = this.statesMap.get(flag);
        if (result == null) {
            this.statesMap.put(flag, result = new ArrayList<GraphState>());
            for (GraphState state : getStates()) {
                if (state.hasFlag(flag)) {
                    result.add(state);
                }
            }
        }
        assert result.size() == getStateCount(flag);
        return result;
    }

    private final Map<Flag,List<GraphState>> statesMap = new EnumMap<Status.Flag,List<GraphState>>(
            Flag.class);

    /**
     * Indicates if there are states with a given flag.
     */
    private boolean hasStates(Flag flag) {
        return getStateCount(flag) > 0;
    }

    /** Returns the number of states with a given flag. */
    private int getStateCount(Flag flag) {
        return this.stateCounts[flag.ordinal()];
    }

    private final int[] stateCounts = new int[Flag.values().length];

    /**
     * Indicates if this GTS has at any point included transient states.
     * Note that the transient nature may have dissipated when the
     * state was done.
     */
    public boolean hasTransientStates() {
        return this.transients;
    }

    private boolean transients;

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
                String outputString = ((RuleTransition) trans).getOutputString();
                if (outputString != null) {
                    System.out.print(outputString);
                }
            } catch (FormatException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public int edgeCount() {
        return this.allTransitionCount;
    }

    /**
     * The number of transitions in the GTS.
     */
    private int allTransitionCount = 0;

    @Override
    public Set<? extends GraphTransition> outEdgeSet(Node node) {
        GraphState state = (GraphState) node;
        return state.getTransitions(GraphTransition.Claz.ANY);
    }

    @Override
    public Set<? extends GraphTransition> edgeSet() {
        if (this.allTransitionSet == null) {
            this.allTransitionSet = new TransitionSet();
        }
        return this.allTransitionSet;
    }

    /** The set of transitions of the GTS. */
    private TransitionSet allTransitionSet;

    /**
     * Returns a view on the set of real transitions in the GTS.
     * A transition is real if it is not inside a recipe, and its source
     * and target states are real.
     * @see GraphTransition#isRealStep()
     */
    public Set<GraphTransition> getTransitions() {
        if (this.realTransitionSet == null) {
            this.realTransitionSet = new SetView<GraphTransition>(edgeSet()) {
                @Override
                public boolean approves(Object obj) {
                    if (!(obj instanceof GraphTransition)) {
                        return false;
                    }
                    return ((GraphTransition) obj).isRealStep();
                }
            };
        }
        return this.realTransitionSet;
    }

    private Set<GraphTransition> realTransitionSet;

    /** Returns the number of real transitions, i.e., those
     * that satisfy {@link GraphTransition#isRealStep()}.
     * More efficient than calling {@code getTransitions().size()}
     */
    public int getTransitionCount() {
        return this.realTransitionCount;
    }

    private int realTransitionCount;

    /**
     * Returns the (fixed) derivation record for this GTS.
     */
    public final Record getRecord() {
        if (this.record == null) {
            this.record = new Record(this.grammar, getHostFactory());
        }
        return this.record;
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
    protected void fireAddNode(GraphState state) {
        if (state.isTransient()) {
            this.transients = true;
        }
        super.fireAddNode(state);
        for (GTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, state);
        }
    }

    /**
     * Notifies the {@link GTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddEdge(GraphTransition edge) {
        this.allTransitionCount++;
        if (edge.isRealStep()) {
            this.realTransitionCount++;
        }
        super.fireAddEdge(edge);
        for (GTSListener listener : getGraphListeners()) {
            listener.addUpdate(this, edge);
        }
    }

    /**
     * Notifies all listeners of a change in status of a given state.
     * @param state the state of which the status has changed
     * @param flag the flag that is indicative of the change
     * @param oldStatus status
     */
    protected void fireUpdateState(GraphState state, Flag flag, int oldStatus) {
        assert flag.isChange();
        if (state.isRealState()) {
            for (Flag recorded : FLAG_ARRAY) {
                boolean had = recorded.test(oldStatus);
                int index = recorded.ordinal();
                if (state.hasFlag(recorded)) {
                    if (!had) {
                        this.stateCounts[index]++;
                        if (this.statesMap.containsKey(recorded)) {
                            this.statesMap.get(recorded).add(state);
                        }
                    }
                } else if (had) {
                    this.stateCounts[index]--;
                }
            }
        } else {
            // real states can't become unreal
            assert !Status.isReal(oldStatus);
        }
        for (GTSListener listener : getGraphListeners()) {
            listener.statusUpdate(this, state, flag, oldStatus);
        }
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
    public MultiGraph toPlainGraph(LTSLabels flags) {
        MultiGraph result = new MultiGraph(getName(), GraphRole.LTS);
        Map<GraphState,MultiNode> nodeMap = new HashMap<GraphState,MultiNode>();
        for (GraphState state : nodeSet()) {
            // don't include transient states unless forced to
            if (state.isInternalState() && !flags.showRecipes()) {
                continue;
            }
            if (state.isAbsent()) {
                continue;
            }
            MultiNode image = result.addNode(state.getNumber());
            nodeMap.put(state, image);
            if (flags.showResult() && state.isResult()) {
                result.addEdge(image, flags.getResultLabel(), image);
            }
            if (flags.showFinal() && state.isFinal()) {
                result.addEdge(image, flags.getFinalLabel(), image);
            }
            if (flags.showStart() && startState().equals(state)) {
                result.addEdge(image, flags.getStartLabel(), image);
            }
            if (flags.showOpen() && !state.isClosed()) {
                result.addEdge(image, flags.getOpenLabel(), image);
            }
            if (flags.showNumber()) {
                String label = flags.getNumberLabel().replaceAll("#", "" + state.getNumber());
                result.addEdge(image, label, image);
            }
            if (flags.showTransience() && state.isTransient()) {
                String label =
                    flags.getTransienceLabel().replaceAll("#",
                        "" + state.getActualFrame().getTransience());
                result.addEdge(image, label, image);
            }
            if (flags.showRecipes() && state.isInternalState()) {
                String label =
                    flags.getRecipeLabel().replaceAll("#",
                        "" + state.getActualFrame().getRecipe().getFullName());
                result.addEdge(image, label, image);
            }
        }
        for (GraphTransition transition : edgeSet()) {
            // don't include partial transitions unless forced to
            if (transition.isInternalStep() && !flags.showRecipes()) {
                continue;
            }
            MultiNode sourceImage = nodeMap.get(transition.source());
            MultiNode targetImage = nodeMap.get(transition.target());
            result.addEdge(sourceImage, transition.label().text(), targetImage);
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

    /** The system record for this GTS. */
    private Record record;
    /** The match applier associated with this GTS. */
    private MatchApplier matchApplier;
    /**
     * Set of {@link GTSListener} s to be identified of changes in this graph.
     * Set to <tt>null</tt> when the graph is fixed.
     */
    private Set<GTSListener> listeners = new HashSet<GTSListener>();

    /** Set of all flags of which state sets are recorded. */
    private static final Set<Flag> FLAG_SET = EnumSet.of(Flag.CLOSED, Flag.FINAL, Flag.RESULT,
        Flag.ERROR);
    /** Array of all flags of which state sets are recorded. */
    private static final Flag[] FLAG_ARRAY = FLAG_SET.toArray(new Flag[FLAG_SET.size()]);

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
        public StateSet(CollapseMode collapse, IsoChecker checker) {
            super(INITIAL_STATE_SET_SIZE, STATE_SET_RESOLUTION, STATE_SET_ROOT_RESOLUTION);
            this.collapse = collapse;
            if (checker == null) {
                this.checker = IsoChecker.getInstance(collapse == COLLAPSE_ISO_STRONG);
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
            if (CHECK_CONTROL_LOCATION && myState.getPrimeFrame() != otherState.getPrimeFrame()) {
                return false;
            }
            Object[] myBoundNodes = myState.getPrimeValues();
            Object[] otherBoundNodes = otherState.getPrimeValues();
            HostGraph myGraph = myState.getGraph();
            HostGraph otherGraph = otherState.getGraph();
            if (this.collapse == COLLAPSE_EQUAL) {
                // check for equality of the bound nodes
                if (!Valuator.areEqual(myBoundNodes, otherBoundNodes)) {
                    return false;
                }
                // check for graph equality
                Set<?> myNodeSet = new HostNodeSet(myGraph.nodeSet());
                Set<?> myEdgeSet = new HostEdgeSet(myGraph.edgeSet());
                return myNodeSet.equals(otherGraph.nodeSet())
                    && myEdgeSet.equals(otherGraph.edgeSet());
            } else {
                return this.checker.areIsomorphic(myGraph, otherGraph, myBoundNodes,
                    otherBoundNodes);
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
                result = graph.nodeSet().hashCode() + graph.edgeSet().hashCode();
                Frame ctrlState = stateKey.getPrimeFrame();
                if (ctrlState != null) {
                    result += ctrlState.hashCode();
                    result += Valuator.hashCode(stateKey.getPrimeValues());
                }
            } else {
                CertificateStrategy certifier =
                    this.checker.getCertifier(stateKey.getGraph(), true);
                Object certificate = certifier.getGraphCertificate();
                result = certificate.hashCode();
                Frame ctrlState = stateKey.getPrimeFrame();
                if (ctrlState != null) {
                    result += ctrlState.hashCode();
                    result +=
                        Valuator.hashCode(stateKey.getPrimeValues(), certifier.getCertificateMap());
                }
            }
            if (CHECK_CONTROL_LOCATION) {
                result += System.identityHashCode(stateKey.getPrimeFrame());
            }
            return result;
        }

        /** The isomorphism checker of the state set. */
        private final IsoChecker checker;
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
                result = (containsNode(source) && outEdgeSet(source).contains(transition));
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
                    public Iterator<? extends GraphTransition> toOuter(GraphState state) {
                        return outEdgeSet(state).iterator();
                    }
                };
            return new NestedIterator<GraphTransition>(stateOutTransitionIter);
        }

        @Override
        public int size() {
            return edgeCount();
        }
    }
}
