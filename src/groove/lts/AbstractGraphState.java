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
 * $Id: AbstractGraphState.java,v 1.17 2008-02-20 09:25:29 kastenberg Exp $
 */
package groove.lts;

import groove.control.Call;
import groove.control.instance.Assignment;
import groove.control.instance.Frame;
import groove.grammar.Action;
import groove.grammar.Action.Role;
import groove.grammar.CheckPolicy;
import groove.grammar.Grammar;
import groove.grammar.host.HostElement;
import groove.grammar.host.HostNode;
import groove.grammar.model.FormatErrorSet;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.lts.Status.Flag;
import groove.transform.Record;
import groove.util.Groove;
import groove.util.cache.AbstractCacheHolder;
import groove.util.cache.CacheReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Combination of graph and node functionality, used to store the state of a
 * graph transition system.
 *
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-20 09:25:29 $
 */
abstract public class AbstractGraphState extends AbstractCacheHolder<StateCache> implements
    GraphState {
    /**
     * Constructs a an abstract graph state.
     * @param number the number of the state; required to be non-negative
     */
    public AbstractGraphState(CacheReference<StateCache> reference, int number) {
        super(reference);
        assert number >= 0;
        this.nr = number;
    }

    @Override
    public GTS getGTS() {
        return ((StateReference) getCacheReference()).getGTS();
    }

    @Override
    final public Set<? extends GraphTransition> getTransitions() {
        return getTransitions(GraphTransition.Claz.REAL);
    }

    @Override
    @SuppressWarnings("unchecked")
    final public Set<RuleTransition> getRuleTransitions() {
        return (Set<RuleTransition>) getTransitions(GraphTransition.Claz.RULE);
    }

    @Override
    public Set<? extends GraphTransition> getTransitions(GraphTransition.Claz claz) {
        return getCache().getTransitions(claz);
    }

    @Override
    public boolean addTransition(GraphTransition transition) {
        return getCache().addTransition(transition);
    }

    @Override
    public RuleTransitionStub getOutStub(MatchResult match) {
        assert match != null;
        RuleTransitionStub result = null;
        Iterator<? extends GraphTransitionStub> outTransIter = getTransitionStubIter();
        while (outTransIter.hasNext()) {
            GraphTransitionStub stub = outTransIter.next();
            if (stub instanceof RuleTransitionStub
                && ((RuleTransitionStub) stub).getKey(this) == match) {
                result = (RuleTransitionStub) stub;
                break;
            }
        }
        return result;
    }

    /**
     * Callback factory method for creating an outgoing transition (from this
     * state) for the given derivation and target state. This implementation
     * invokes {@link #createInTransitionStub(GraphState, MatchResult, HostNode[])} if
     * the target is a {@link AbstractGraphState}, otherwise it creates a
     * {@link IdentityTransitionStub}.
     */
    protected RuleTransitionStub createTransitionStub(MatchResult match, HostNode[] addedNodes,
        GraphState target) {
        if (target instanceof AbstractGraphState) {
            return ((AbstractGraphState) target).createInTransitionStub(this, match, addedNodes);
        } else {
            return new IdentityTransitionStub(match, addedNodes, target);
        }
    }

    /**
     * Callback factory method for creating a transition stub to this state,
     * from a given graph and with a given rule event.
     */
    protected RuleTransitionStub createInTransitionStub(GraphState source, MatchResult match,
        HostNode[] addedNodes) {
        return new IdentityTransitionStub(match, addedNodes, this);
    }

    /**
     * Returns an iterator over the outgoing transitions as stored, i.e.,
     * without encodings taken into account.
     */
    final protected Iterator<? extends GraphTransitionStub> getTransitionStubIter() {
        if (isClosed()) {
            return getStoredTransitionStubs().iterator();
        } else {
            return getCachedTransitionStubs().iterator();
        }
    }

    /**
     * Returns a list view upon the current outgoing transitions.
     */
    private Set<GraphTransitionStub> getCachedTransitionStubs() {
        return getCache().getStubSet();
    }

    /**
     * Returns the collection of currently stored outgoing transition stubs.
     * Note that this is only guaranteed to be synchronised with the cached stub
     * set if the state is closed.
     */
    final Collection<GraphTransitionStub> getStoredTransitionStubs() {
        return Arrays.asList(this.transitionStubs);
    }

    /**
     * Stores a set of outgoing transition stubs in a memory efficient way.
     */
    private void setStoredTransitionStubs(Collection<GraphTransitionStub> outTransitionSet) {
        if (outTransitionSet.isEmpty()) {
            this.transitionStubs = EMPTY_TRANSITION_STUBS;
        } else {
            this.transitionStubs = new GraphTransitionStub[outTransitionSet.size()];
            outTransitionSet.toArray(this.transitionStubs);
        }
    }

    @Override
    public List<MatchResult> getMatches() {
        // copy the match set to prevent sharing errors
        return new ArrayList<MatchResult>(getCache().getMatches().getAll());
    }

    @Override
    public MatchResult getMatch() {
        return getCache().getMatches().getOne();
    }

    @Override
    public RuleTransition applyMatch(MatchResult match) {
        RuleTransition result = null;
        if (match instanceof RuleTransition) {
            RuleTransition trans = (RuleTransition) match;
            if (trans.source() == this) {
                result = trans;
            }
        }
        if (result == null) {
            result = getGTS().getMatchApplier().apply(this, match);
        }
        return result;
    }

    @Override
    public boolean isClosed() {
        return hasFlag(Flag.CLOSED);
    }

    @Override
    public boolean setClosed(boolean complete) {
        int oldStatus = this.status;
        boolean result = setStatus(Flag.CLOSED, true);
        if (result) {
            setStoredTransitionStubs(getCachedTransitionStubs());
            updateClosed();
            // reset the schedule to the beginning if the state was not
            // completely explored
            if (!complete) {
                setFrame(getPrimeFrame());
            } else {
                setStatus(Flag.TRANSIENT, getActualFrame().isTransient());
                setStatus(Flag.INTERNAL, getActualFrame().isInternal());
            }
            if (getActualFrame().isError()) {
                setStatus(Flag.ERROR, true);
            } else if (getActualFrame().isAbsence()) {
                setStatus(Flag.ABSENT, true);
            }
            fireStatus(Flag.CLOSED, oldStatus);
            getCache().notifyClosed();
        }
        return result;
    }

    /** Callback method to notify that the state was closed. */
    abstract protected void updateClosed();

    @Override
    public boolean setResult() {
        int oldStatus = this.status;
        boolean result = setStatus(Flag.RESULT, true);
        if (result) {
            fireStatus(Flag.RESULT, oldStatus);
        }
        return result;
    }

    @Override
    public boolean isResult() {
        return hasFlag(Flag.RESULT);
    }

    @Override
    public boolean setError() {
        int oldStatus = this.status;
        boolean result = setStatus(Flag.ERROR, true);
        if (result) {
            fireStatus(Flag.ERROR, oldStatus);
        }
        return result;
    }

    @Override
    public boolean isError() {
        return hasFlag(Flag.ERROR);
    }

    @Override
    final public boolean isInternalState() {
        return hasFlag(Flag.INTERNAL);
    }

    @Override
    public final boolean isRealState() {
        return Status.isReal(this.status);
    }

    @Override
    final public boolean isTransient() {
        return hasFlag(Flag.TRANSIENT);
    }

    @Override
    public boolean isAbsent() {
        return hasFlag(Flag.ABSENT);
    }

    @Override
    public int getAbsence() {
        if (isError()) {
            return Status.MAX_ABSENCE;
        } else if (isDone()) {
            return Status.getAbsence(this.status);
        } else {
            return getCache().getAbsence();
        }
    }

    @Override
    public boolean isPresent() {
        return getAbsence() == 0 && !isAbsent();
    }

    @Override
    public boolean setDone(int absence) {
        int oldStatus = this.status;
        boolean result = setStatus(Flag.DONE, true);
        if (result) {
            setAbsence(absence);
            setStatus(Flag.ABSENT, isAbsent() || absence > 0);
            if (isError()) {
                checkPropertyViolations();
            } else if (!isAbsent()) {
                setStatus(Flag.FINAL, getActualFrame().isFinal());
            }
            if (getActualFrame().isDead() && getGTS().isCheckDeadlock()) {
                boolean alive = false;
                for (GraphTransition trans : getTransitions()) {
                    if (trans.getAction().getRole() == Role.TRANSFORMER) {
                        alive = true;
                        break;
                    }
                }
                if (!alive) {
                    // TODO this test should be moved to elsewhere
                    // and should also take recipes into account
                    setFrame(getActualFrame().onError());
                    setStatus(Flag.ERROR, true);
                    FormatErrorSet error = new FormatErrorSet();
                    Set<String> actions = new LinkedHashSet<String>();
                    for (Call call : getActualFrame().getPastCalls()) {
                        if (call.getRule().getRole() == Role.TRANSFORMER) {
                            actions.add(call.getRule().getFullName());
                        }
                    }
                    if (actions.isEmpty()) {
                        error.add("No transformer scheduled");
                    } else {
                        error.add("Scheduled transformer%s %s failed to be applicable",
                            actions.size() == 1 ? "" : "s",
                            Groove.toString(actions.toArray(), "'", "'", "', '", "' and '"));
                    }
                    GraphInfo.addErrors(getGraph(), error);
                }
            }
            getCache().notifyDone();
            setCacheCollectable();
            fireStatus(Flag.DONE, oldStatus);
        }
        return result;
    }

    /** Tests if the state (which has to be DONE) satisfies all invariant properties,
     * and fails to satisfy all forbidden properties.
     */
    public void checkPropertyViolations() {
        Grammar grammar = getGTS().getGrammar();
        // collect all property matches
        List<Action> forbidden = new ArrayList<Action>();
        Set<Action> failed = new HashSet<Action>(grammar.getActions(Role.INVARIANT));
        for (GraphTransition trans : getTransitions()) {
            if (trans.isLoop()) {
                Action action = trans.getAction();
                switch (action.getRole()) {
                case FORBIDDEN:
                    forbidden.add(action);
                    break;
                case INVARIANT:
                    failed.remove(action);
                }
            }
        }
        FormatErrorSet result = new FormatErrorSet();
        for (Action action : forbidden) {
            result.add("Graph satisfies forbidden property '%s'", action.getFullName());
        }
        for (Action action : failed) {
            if (action.getPolicy() == CheckPolicy.ERROR) {
                result.add("Graph fails to satisfy invariant property '%s'", action.getFullName());
            }
        }
        if (!result.isEmpty()) {
            GraphInfo.addErrors(getGraph(), result);
        }
    }

    @Override
    public boolean isDone() {
        return hasFlag(Flag.DONE);
    }

    @Override
    public boolean isFinal() {
        return hasFlag(Flag.FINAL);
    }

    @Override
    public boolean hasFlag(Flag flag) {
        return flag.test(this.status);
    }

    @Override
    public boolean setFlag(Flag flag, boolean value) {
        assert flag.isStrategy();
        return setStatus(flag, value);
    }

    private void setAbsence(int absence) {
        this.status = Status.setAbsence(this.status, absence);
    }

    /**
     * Sets a given flag in this state's status.
     * @param value the new value of the flag
     * @return if {@code true}, the status value for the flag was changed
     */
    private boolean setStatus(Flag flag, boolean value) {
        boolean result = value != hasFlag(flag);
        if (result) {
            this.status = value ? flag.set(this.status) : flag.reset(this.status);
        }
        return result;
    }

    /**
     * Notifies the observers of a change in this state's status with respect
     * to a given status flag.
     * @param oldStatus the status of this state before the change
     */
    private void fireStatus(Flag flag, int oldStatus) {
        getGTS().fireUpdateState(this, flag, oldStatus);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    /** Field holding status flags of the state. */
    private int status;

    /**
     * Retrieves a frozen representation of the graph, in the form of all nodes
     * and edges collected in one array. May return <code>null</code> if there
     * is no frozen representation.
     * @return All nodes and edges of the graph, or <code>null</code>
     */
    protected HostElement[] getFrozenGraph() {
        return this.frozenGraph;
    }

    /** Stores a frozen representation of the graph. */
    protected void setFrozenGraph(HostElement[] frozenGraph) {
        this.frozenGraph = frozenGraph;
        frozenGraphCount++;
    }

    /**
     * Slot to store a frozen graph representation. When filled, this provides a
     * faster way to reconstruct the graph of this state.
     */
    private HostElement[] frozenGraph;

    /**
     * This implementation compares state numbers. The current state is either
     * compared with the other, if that is a {@link GraphState}, or
     * with its source state if it is a {@link GraphTransition}.
     * Otherwise, the method throws an {@link UnsupportedOperationException}.
     */
    public int compareTo(Element obj) {
        if (obj instanceof GraphState) {
            return getNumber() - ((GraphState) obj).getNumber();
        } else if (obj instanceof GraphTransition) {
            return getNumber() - ((GraphTransition) obj).source().getNumber();
        } else {
            throw new UnsupportedOperationException(String.format(
                "Classes %s and %s cannot be compared", getClass(), obj.getClass()));
        }
    }

    /**
     * Returns a name for this state, rather than a full description. To get the
     * full description, use <tt>DefaultGraph.toString(Graph)</tt>.
     *
     * @see groove.graph.AGraph#toString(Graph)
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("s");
        if (hasNumber()) {
            result.append(getNumber());
        } else {
            result.append("??");
        }
        return result.toString();
    }

    /**
     * Callback factory method for a new cache based on this state.
     */
    @Override
    protected StateCache createCache() {
        return new StateCache(this);
    }

    /**
     * Stores the transitions from the cache, if the state is not already
     * closed.
     */
    @Override
    public void clearCache() {
        if (!isClosed()) {
            setStoredTransitionStubs(getCachedTransitionStubs());
        }
        super.clearCache();
    }

    /** Indicates whether the state has already been assigned a number. */
    protected boolean hasNumber() {
        return this.nr >= 0;
    }

    /**
     * Returns the number of this state. The number is meant to be unique for
     * each state in a given transition system.
     * @throws IllegalStateException if {@link #hasNumber()} returns
     *         <code>false</code> at the time of calling
     */
    @Override
    public int getNumber() {
        return this.nr;
    }

    /**
     * The number of this Node.
     *
     * @invariant nr < nrNodes
     */
    private final int nr;

    /** Returns the system record associated with this state. */
    protected Record getRecord() {
        return getGTS().getRecord();
    }

    @Override
    public void setFrame(Frame frame) {
        assert frame != null;
        // AR: the assertion below fails to hold in one of the tests because
        // the frame is artificially set again for a start state
        // assert this.currentFrame == null || frame.getPrime() == getPrimeFrame();
        this.actualFrame = frame;
        setStatus(Flag.TRANSIENT, frame.isTransient());
        setStatus(Flag.INTERNAL, frame.isInternal());
    }

    @Override
    public Frame getPrimeFrame() {
        return getActualFrame().getPrime();
    }

    @Override
    public final Frame getActualFrame() {
        return this.actualFrame;
    }

    private Frame actualFrame;

    @Override
    public Object[] getPrimeValues() {
        return EMPTY_NODE_LIST;
    }

    @Override
    public Object[] getActualValues() {
        Object[] result = getPrimeValues();
        for (Assignment pop : getActualFrame().getPops()) {
            result = pop.apply(result);
        }
        return result;
    }

    /** Global constant empty stub array. */
    private GraphTransitionStub[] transitionStubs = EMPTY_TRANSITION_STUBS;

    /** Returns the total number of fixed delta graphs. */
    static public int getFrozenGraphCount() {
        return frozenGraphCount;
    }

    /** The total number of delta graphs frozen. */
    static private int frozenGraphCount;

    /** Constant empty array of out transition, shared for memory efficiency. */
    private static final GraphTransitionStub[] EMPTY_TRANSITION_STUBS = new RuleTransitionStub[0];
    /** Fixed empty array of (created) nodes. */
    private static final HostNode[] EMPTY_NODE_LIST = new HostNode[0];
}