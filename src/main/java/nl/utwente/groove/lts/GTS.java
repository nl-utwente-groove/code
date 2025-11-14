// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 * $Id$
 */
package nl.utwente.groove.lts;

import static nl.utwente.groove.lts.GTS.CollapseMode.COLLAPSE_EQUAL;
import static nl.utwente.groove.lts.GTS.CollapseMode.COLLAPSE_ISO_STRONG;
import static nl.utwente.groove.lts.GTS.CollapseMode.COLLAPSE_NONE;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.control.CallStack;
import nl.utwente.groove.control.instance.Frame;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.host.HostEdgeSet;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNodeSet;
import nl.utwente.groove.graph.AGraph;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.iso.CertificateStrategy;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.transform.Record;
import nl.utwente.groove.transform.oracle.NoValueOracle;
import nl.utwente.groove.transform.oracle.ValueOracle;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.collect.NestedIterator;
import nl.utwente.groove.util.collect.SetView;
import nl.utwente.groove.util.collect.TreeHashSet;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Implements an LTS of which the states are {@link GraphState}s and the
 * transitions {@link RuleTransition}s. A GTS stores a fixed rule system.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class GTS extends AGraph<GraphState,GraphTransition> implements Cloneable {
    /** Debug flag controlling whether states are compared for control location equality. */
    protected final static boolean CHECK_CONTROL_LOCATION = true;
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
        return allStateSet().getBytesPerElement();
    }

    /**
     * Constructs a GTS from a (fixed) graph grammar.
     */
    public GTS(Grammar grammar) {
        super(grammar.getName() + "-gts", false);
        grammar.testFixed(true);
        this.grammar = grammar;
        ValueOracle oracle;
        try {
            oracle = grammar.getProperties().getValueOracle();
        } catch (FormatException exc) {
            addErrors(exc.getErrors());
            oracle = NoValueOracle.instance();
        }
        this.oracle = oracle;
    }

    /** Indicates if the grammar works with simple or multi-graphs. */
    public boolean hasSimpleGraphs() {
        return getHostFactory().isSimple();
    }

    /**
     * Returns the start state of this LTS.
     */
    public GraphState startState() {
        var result = this.startState;
        if (result == null) {
            this.startState = result = createStartState();
            addState(result);
        }
        return result;
    }

    /**
     * Factory method to create a start graph for this GTS, by
     * cloning a given host graph.
     */
    protected HostGraph createStartGraph() {
        return getGrammar().getStartGraph().clone(getAlgebraFamily());
    }

    /**
     * Factory method to create the start state for this GTS, for a given start graph.
     */
    protected GraphState createStartState() {
        return new StartGraphState(this, createStartGraph());
    }

    /**
     * The start state of this LTS.
     * @invariant <tt>nodeSet().contains(startState)</tt>
     */
    protected @Nullable GraphState startState;

    /**
     * Returns the rule system underlying this GTS.
     */
    public Grammar getGrammar() {
        return this.grammar;
    }

    /**
     * The rule system generating this LTS.
     * @invariant <tt>ruleSystem != null</tt>
     */
    private final Grammar grammar;

    /**
     * Returns the host element factory associated with this GTS.
     * This is taken from the start state graph.
     */
    public HostFactory getHostFactory() {
        var result = this.hostFactory;
        if (result == null) {
            this.hostFactory = result = this.grammar.getStartGraph().getFactory();
        }
        return result;
    }

    /** Unique factory for host elements, associated with this GTS. */
    private @Nullable HostFactory hostFactory;

    /** Returns the algebra family of the GTS. */
    public AlgebraFamily getAlgebraFamily() {
        return getGrammar().getProperties().getAlgebraFamily();
    }

    // ----------------------- OBJECT OVERRIDES ------------------------

    /**
     * Adds a state to the GTS, if it is not isomorphic to an existing state.
     * Returns the isomorphic state if one was found, or <tt>null</tt> if the
     * state was actually added.
     * @param newState the state to be added
     * @return an existing state isomorphic to <tt>state</tt>; or <tt>null</tt> if
     *         there was no existing isomorphic state (in which case, and only
     *         then, <tt>state</tt> was added and the listeners notified).
     */
    public @Nullable GraphState addState(GraphState newState) {
        // see if isomorphic graph is already in the LTS
        GraphState result = allStateSet().put(newState);
        if (result == null) {
            // otherwise, add it to the GTS
            fireAddNode(newState);
            if (newState instanceof AbstractGraphState s) {
                s.checkInitConstraints();
            }
        }
        return result;
    }

    /** Returns the policy for type checking. */
    public CheckPolicy getTypePolicy() {
        return getGrammar().getProperties().getTypePolicy();
    }

    /** Indicates if deadlock errors should be checked on all graphs. */
    public boolean isCheckDeadlock() {
        return getGrammar().getProperties().getDeadPolicy() == CheckPolicy.ERROR;
    }

    @Override
    public Set<? extends GraphState> nodeSet() {
        return allStateSet();
    }

    /** Delegate method for {@link #nodeSet()} with a specialised return type. */
    protected StateSet allStateSet() {
        var result = this.allStateSet;
        if (result == null) {
            this.allStateSet = result = createStateSet();
        }
        return result;
    }

    /** The set of nodes of the GTS. */
    private @Nullable StateSet allStateSet;

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
     * Returns a view on the set of <i>public</i> states in the GTS.
     * A state is public if it is not absent (including erroneous) or inner.
     * @see GraphState#isPublic()
     */
    public Set<? extends GraphState> getStates() {
        var result = this.publicStateSet;
        if (result == null) {
            this.publicStateSet = result = new SetView<>(nodeSet()) {
                @Override
                public int size() {
                    return GTS.this.publicStateCount;
                }

                @Override
                public boolean approves(@Nullable Object obj) {
                    return obj instanceof GraphState gs && gs.isPublic();
                }
            };
        }
        return result;
    }

    /** Set of public states, as a view on {@link #allStateSet}. */
    private @Nullable Set<? extends GraphState> publicStateSet;

    /**
     * Returns the number of public states.
     * Calling this is more efficient than {@code getStates().size()}.
     */
    public int getStateCount() {
        return this.publicStateCount;
    }

    /** Number of exposed states, stored separately for efficiency. */
    private int publicStateCount;

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
     * Indicates if the GTS currently has open (exposed) states.
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
     * Returns the set of exposed states with a given flag.
     */
    private Collection<GraphState> getStates(Flag flag) {
        List<GraphState> result = this.statesMap.get(flag);
        if (result == null) {
            this.statesMap.put(flag, result = new ArrayList<>());
            for (GraphState state : getStates()) {
                if (state.hasFlag(flag)) {
                    result.add(state);
                }
            }
        }
        assert result.size() == getStateCount(flag);
        return result;
    }

    /** Mapping from status flags to sets of states with that flag. */
    private final Map<Flag,@Nullable List<GraphState>> statesMap = new EnumMap<>(Flag.class);

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
     * state was completed.
     */
    public boolean hasTransientStates() {
        return this.transients;
    }

    private boolean transients;

    /**
     * Indicates if this GTS has at any point included absent states.
     */
    public boolean hasAbsentStates() {
        return this.absents;
    }

    private boolean absents;

    /**
     * Indicates if this GTS has internal steps.
     */
    public boolean hasInternalSteps() {
        return this.inners;
    }

    private boolean inners;

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
        trans
            .getOutputString()
            .ifPresent(o -> o
                .ifPresentOrElse(s -> System.out.print(s),
                                 e -> System.err.println(e.getMessage())));
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
        var result = this.allTransitionSet;
        if (result == null) {
            this.allTransitionSet = result = new TransitionSet();
        }
        return result;
    }

    /** The set of transitions of the GTS. */
    private @Nullable TransitionSet allTransitionSet;

    /**
     * Returns a view on the set of exposed transitions in the GTS.
     * A transition is exposed if it is not inside a recipe, and its source
     * and target states are exposed.
     * @see GraphTransition#isPublicStep()
     */
    public Set<? extends GraphTransition> getTransitions() {
        var result = this.publicTransitionSet;
        if (result == null) {
            this.publicTransitionSet = result = new SetView<>(edgeSet()) {
                @Override
                public boolean approves(@Nullable Object obj) {
                    return obj instanceof GraphTransition gt && gt.isPublicStep();
                }

                @Override
                public int size() {
                    return GTS.this.publicTransitionCount;
                }
            };
        }
        return result;
    }

    /** The set of public transitions (as a view on the set of all transitions). */
    private @Nullable Set<? extends GraphTransition> publicTransitionSet;

    /** The number of public transitions, stored separately for efficiency. */
    private int publicTransitionCount;

    /** Returns the number of public transitions, i.e., those
     * that satisfy {@link GraphTransition#isPublicStep()}.
     * More efficient than calling {@code getTransitions().size()}
     */
    public int getTransitionCount() {
        return this.publicTransitionCount;
    }

    /** Tests if this GTS has a state property with a given name. */
    public boolean hasStateProperty(String name) {
        return this.stateProperties.containsKey(name);
    }

    /** Returns a state property with a given name. */
    public @Nullable StateProperty getStateProperty(String name) {
        return this.stateProperties.get(name);
    }

    /** Adds a named state predicate to this LTS.
     * @throws IllegalArgumentException if a state predicate with this name already exists.
     * @see #hasStateProperty
     */
    public void addStateProperty(String name, Predicate<GraphState> prop) {
        addStateProperty(new StateProperty(name, prop));
    }

    /** Adds a named state properties to this LTS.
     * @throws IllegalArgumentException if a state properties with this name already exists.
     * @see #hasStateProperty
     */
    public void addStateProperty(StateProperty pred) {
        var name = pred.name();
        if (hasStateProperty(name)) {
            throw Exceptions.illegalArg("Predicate '%s' already exists", name);
        }
        this.stateProperties.put(name, pred);
    }

    /** Resets the set of state properties associated with this GTS. */
    public void clearStateProperties() {
        this.stateProperties.clear();
    }

    /** Returns the set of state property names satisfied by a given state. */
    public Set<String> getSatisfiedProps(GraphState state) {
        Set<String> result = new LinkedHashSet<>();
        this.stateProperties
            .entrySet()
            .stream()
            .filter(e -> e.getValue().test(state))
            .map(Map.Entry::getKey)
            .forEach(result::add);
        return result;
    }

    private final Map<String,StateProperty> stateProperties = new TreeMap<>();

    /**
     * Returns the (fixed) derivation record for this GTS.
     */
    public final Record getRecord() {
        var result = this.record;
        if (result == null) {
            this.record = result = new Record(this.grammar, getHostFactory());
        }
        return result;
    }

    /** The system record for this GTS. */
    private @Nullable Record record;

    /**
     * Returns the set of listeners of this GTS.
     * @return an iterator over the graph listeners of this graph
     */
    public Set<GTSListener> getGTSListeners() {
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
        this.listeners.add(listener);
    }

    /**
     * Removes a graph listener from this graph.
     */
    public void removeLTSListener(GTSListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Set of {@link GTSListener} s to be identified of changes in this graph.
     * Set to <tt>null</tt> when the graph is fixed.
     */
    private Set<GTSListener> listeners = new LinkedHashSet<>();

    /**
     * Notifies the {@link GTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddNode(GraphState state) {
        this.transients |= state.isTransient();
        this.absents |= state.isAbsent();
        if (state.isPublic()) {
            this.publicStateCount++;
        }
        super.fireAddNode(state);
        for (GTSListener listener : getGTSListeners()) {
            listener.addUpdate(this, state);
        }
    }

    /**
     * Notifies the {@link GTSListener}s, in addition to
     * calling the super method.
     */
    @Override
    protected void fireAddEdge(GraphTransition edge) {
        this.inners |= edge.isInnerStep();
        this.allTransitionCount++;
        if (edge.isPublicStep()) {
            this.publicTransitionCount++;
        }
        super.fireAddEdge(edge);
        for (GTSListener listener : getGTSListeners()) {
            listener.addUpdate(this, edge);
        }
    }

    /**
     * Notifies all listeners of a change in status of a given state.
     * @param state the state of which the status has changed
     * @param oldStatus status before the reported change
     */
    protected void fireUpdateState(GraphState state, int oldStatus) {
        this.transients |= state.isTransient();
        this.absents |= state.isAbsent();
        boolean wasPublic = Status.isPublic(oldStatus);
        boolean isPublic = state.isPublic();
        if (wasPublic != isPublic) {
            this.publicStateCount += wasPublic
                ? -1
                : +1;
        }
        for (Flag recorded : FLAG_ARRAY) {
            var flaggedStates = this.statesMap.get(recorded);
            boolean had = wasPublic && recorded.test(oldStatus);
            int index = recorded.ordinal();
            if (isPublic && state.hasFlag(recorded)) {
                if (!had) {
                    this.stateCounts[index]++;
                    if (flaggedStates != null) {
                        flaggedStates.add(state);
                    }
                }
            } else if (had) {
                this.stateCounts[index]--;
                if (flaggedStates != null) {
                    flaggedStates.remove(state);
                }
            }
        }
        int change = state.getStatus() ^ oldStatus;
        for (GTSListener listener : getGTSListeners()) {
            listener.statusUpdate(this, state, change);
        }
        if (state.isError()) {
            for (FormatError error : state.getGraph().getErrors()) {
                addError("Error in state %s: %s", state, error);
            }
        }
    }

    /**
     * Creates a GTS fragment, consisting of all states (optionally including internal ones),
     * and either all or just spanning transitions (optionally including internal ones).
     * @param complete if {@code true}, all transitions are included, otherwise only the spanning ones
     * @param internal if {@code true}, internal states and transitions are included
     */
    public GTSFragment toFragment(boolean complete, boolean internal) {
        GTSFragment result;
        var states = internal
            ? nodeSet()
            : getStates();
        var transitions = internal
            ? edgeSet()
            : getTransitions();
        if (complete) {
            result = new GTSFragment(this, states, transitions);
        } else {
            result = new GTSFragment(this, states, Collections.emptySet());
            result.complete(internal);
        }
        return result;
    }

    @Override
    public GTS newGraph(String name) {
        throw new UnsupportedOperationException();
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
        var result = this.matchApplier;
        if (result == null) {
            this.matchApplier = result = createMatchApplier();
        }
        return result;
    }

    /** Factory method for the match applier. */
    protected MatchApplier createMatchApplier() {
        return new MatchApplier(this);
    }

    /** The match applier associated with this GTS. */
    private @Nullable MatchApplier matchApplier;

    /** Returns the oracle associated with this GTS. */
    public ValueOracle getOracle() {
        return this.oracle;
    }

    private final ValueOracle oracle;
    /** Set of all flags of which state sets are recorded. */
    private static final Set<Flag> FLAG_SET = EnumSet.of(Flag.CLOSED, Flag.FINAL, Flag.ERROR);
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
        public StateSet(CollapseMode collapse, @Nullable IsoChecker checker) {
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
            Object[] myCallStack = myState.getPrimeStack();
            Object[] otherCallStack = otherState.getPrimeStack();
            HostGraph myGraph = myState.getGraph();
            HostGraph otherGraph = otherState.getGraph();
            if (this.collapse == COLLAPSE_EQUAL) {
                // check for equality of the bound nodes
                if (!CallStack.areEqual(myCallStack, otherCallStack)) {
                    return false;
                }
                // check for graph equality
                Set<?> myNodeSet = new HostNodeSet(myGraph.nodeSet());
                Set<?> myEdgeSet = new HostEdgeSet(myGraph.edgeSet());
                return myNodeSet.equals(otherGraph.nodeSet())
                    && myEdgeSet.equals(otherGraph.edgeSet());
            } else {
                return this.checker.areIsomorphic(myGraph, otherGraph, myCallStack, otherCallStack);
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
                result += ctrlState.hashCode();
                result += CallStack.hashCode(stateKey.getPrimeStack());
            } else {
                CertificateStrategy certifier
                    = this.checker.getCertifier(stateKey.getGraph(), true);
                Object certificate = certifier.getGraphCertificate();
                result = certificate.hashCode();
                Frame ctrlState = stateKey.getPrimeFrame();
                result += ctrlState.hashCode();
                result
                    += CallStack.hashCode(stateKey.getPrimeStack(), certifier.getCertificateMap());
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
    static public enum CollapseMode {
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
        public boolean contains(@Nullable Object o) {
            boolean result = false;
            if (o instanceof GraphTransition transition) {
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
            return NestedIterator.newInstance(nodeSet().stream().map(GTS.this::outEdgeSet));
        }

        @Override
        public int size() {
            return edgeCount();
        }
    }
}
