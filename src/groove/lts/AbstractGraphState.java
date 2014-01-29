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

import static groove.lts.GraphState.Flag.CLOSED;
import static groove.lts.GraphState.Flag.DONE;
import static groove.lts.GraphState.Flag.ERROR;
import groove.control.CtrlFrame;
import groove.control.CtrlState;
import groove.control.Valuator;
import groove.grammar.host.HostElement;
import groove.grammar.host.HostNode;
import groove.graph.Element;
import groove.graph.Graph;
import groove.transform.Record;
import groove.util.cache.AbstractCacheHolder;
import groove.util.cache.CacheReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
        return getTransitions(GraphTransition.Class.COMPLETE);
    }

    @Override
    @SuppressWarnings("unchecked")
    final public Set<RuleTransition> getRuleTransitions() {
        return (Set<RuleTransition>) getTransitions(GraphTransition.Class.RULE);
    }

    @Override
    public Set<? extends GraphTransition> getTransitions(GraphTransition.Class claz) {
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
        return hasFlag(CLOSED);
    }

    @Override
    public boolean setClosed(boolean complete) {
        boolean result = setStatus(CLOSED, true);
        if (result) {
            setStoredTransitionStubs(getCachedTransitionStubs());
            updateClosed();
            // reset the schedule to the beginning if the state was not 
            // completely explored
            if (!complete) {
                setFrame(getPrimeFrame());
            }
            getCache().notifyClosed();
            fireStatus(CLOSED);
        }
        return result;
    }

    /** Callback method to notify that the state was closed. */
    abstract protected void updateClosed();

    @Override
    public boolean setError() {
        boolean result = setStatus(ERROR, true);
        if (result) {
            fireStatus(ERROR);
        }
        return result;
    }

    @Override
    public boolean isError() {
        return hasFlag(ERROR);
    }

    @Override
    final public boolean isTransient() {
        return getActualFrame().isTransient();
    }

    @Override
    public boolean isAbsent() {
        return isDone() && getPresence() > 0;
    }

    @Override
    public int getPresence() {
        if (isError()) {
            return Integer.MAX_VALUE;
        } else if (isDone()) {
            return Flag.getPresence(this.status);
        } else {
            return getCache().getPresence();
        }
    }

    @Override
    public boolean isPresent() {
        return getPresence() == 0;
    }

    @Override
    public boolean setDone(int presence) {
        boolean result = setStatus(DONE, true);
        if (result) {
            this.status = Flag.setPresence(this.status, presence);
            getCache().notifyDone();
            setCacheCollectable();
            fireStatus(DONE);
        }
        return result;
    }

    @Override
    public boolean isDone() {
        return hasFlag(DONE);
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
     */
    private void fireStatus(Flag flag) {
        getGTS().fireUpdateState(this, flag);
    }

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
        if (getFrameValues().length > 0) {
            result.append(Valuator.toString(getFrameValues()));
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

    /** Returns the system record associated with this state. */
    protected Record getRecord() {
        return getGTS().getRecord();
    }

    /**
     * Returns the map of parameters to nodes for this state
     * @return a Map<String,Node> of parameters
     */
    @Override
    public Object[] getFrameValues() {
        return EMPTY_NODE_LIST;
    }

    @Override
    public CtrlFrame getPrimeFrame() {
        return getActualFrame().getPrime();
    }

    @Override
    public void setFrame(CtrlFrame frame) {
        assert frame != null;
        // AR: the assertion below fails to hold in one of the tests because
        // the frame is artificially set again for a start state
        // assert this.currentFrame == null || frame.getPrime() == getPrimeFrame();
        if (frame instanceof CtrlState) {
            this.currentFrame = ((CtrlState) frame).getSchedule();
        } else {
            this.currentFrame = frame;
        }
    }

    @Override
    public final CtrlFrame getActualFrame() {
        return this.currentFrame;
    }

    private CtrlFrame currentFrame;

    /**
     * The number of this Node.
     * 
     * @invariant nr < nrNodes
     */
    private final int nr;

    /** Global constant empty stub array. */
    private GraphTransitionStub[] transitionStubs = EMPTY_TRANSITION_STUBS;

    /**
     * Slot to store a frozen graph representation. When filled, this provides a
     * faster way to reconstruct the graph of this state.
     */
    private HostElement[] frozenGraph;

    /** Field holding status flags of the state. */
    private int status;

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