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

import static groove.lts.GraphState.Flag.ABSENT;
import static groove.lts.GraphState.Flag.CLOSED;
import static groove.lts.GraphState.Flag.COOKED;
import static groove.lts.GraphState.Flag.ERROR;
import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.graph.Element;
import groove.graph.Graph;
import groove.trans.HostElement;
import groove.trans.HostNode;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;
import groove.util.TransformIterator;
import groove.util.TransformSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Combination of graph and node functionality, used to store the state of a
 * graph transition system.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-20 09:25:29 $
 */
abstract public class AbstractGraphState extends
        AbstractCacheHolder<StateCache> implements GraphState {
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
        return getRecord().getGTS();
    }

    public Iterator<RuleTransition> getTransitionIter() {
        // the iterator is created as a transformation of the iterator on the
        // stored OutGraphTransitions.
        return new TransformIterator<RuleTransitionStub,RuleTransition>(
            getTransitionStubIter()) {
            @Override
            public RuleTransition toOuter(RuleTransitionStub obj) {
                return obj.toTransition(AbstractGraphState.this);
            }
        };
    }

    public Set<RuleTransition> getTransitionSet() {
        return new TransformSet<RuleTransitionStub,RuleTransition>(
            getCachedTransitionStubs()) {
            @Override
            protected RuleTransition toOuter(RuleTransitionStub stub) {
                return stub.toTransition(AbstractGraphState.this);
            }

            @Override
            protected RuleTransitionStub toInner(Object key) {
                if (key instanceof RuleTransition) {
                    RuleEvent keyEvent = ((RuleTransition) key).getEvent();
                    HostNode[] keyAddedNodes =
                        ((RuleTransition) key).getAddedNodes();
                    GraphState keyTarget = ((RuleTransition) key).target();
                    return createTransitionStub(keyEvent, keyAddedNodes,
                        keyTarget);
                } else {
                    return null;
                }
            }
        };
    }

    public boolean containsTransition(RuleTransition transition) {
        return transition.source().equals(this)
            && getCachedTransitionStubs().contains(
                createTransitionStub(transition.getEvent(),
                    transition.getAddedNodes(), transition.target()));
    }

    @Override
    public Map<RuleEvent,RuleTransition> getTransitionMap() {
        return getCache().getTransitionMap();
    }

    /**
     * Add an outgoing transition to this state, if it is not yet there. Returns
     * the {@link RuleTransition} that was added, or <code>null</code> if no
     * new transition was added.
     */
    public boolean addTransition(RuleTransition transition) {
        return getCache().addTransition(transition);
    }

    public Collection<? extends GraphState> getNextStateSet() {
        return new TransformSet<RuleTransitionStub,GraphState>(
            getCachedTransitionStubs()) {
            @Override
            public GraphState toOuter(RuleTransitionStub stub) {
                return stub.getTarget(AbstractGraphState.this);
            }
        };
    }

    public RuleTransitionStub getOutStub(RuleEvent event) {
        assert event != null;
        RuleTransitionStub result = null;
        if (isClosed()) {
            RuleTransitionStub[] outTransitions = this.transitionStubs;
            for (int i = 0; result == null && i < outTransitions.length; i++) {
                RuleTransitionStub trans = outTransitions[i];
                if (trans.getEvent(this) == event) {
                    result = trans;
                }
            }
        } else {
            Iterator<RuleTransitionStub> outTransIter = getTransitionStubIter();
            while (result == null && outTransIter.hasNext()) {
                RuleTransitionStub trans = outTransIter.next();
                if (trans.getEvent(this) == event) {
                    result = trans;
                }
            }
        }
        return result;
    }

    /**
     * Callback factory method for creating an outgoing transition (from this
     * state) for the given derivation and target state. This implementation
     * invokes {@link #createInTransitionStub(GraphState, RuleEvent, HostNode[])} if
     * the target is a {@link AbstractGraphState}, otherwise it creates a
     * {@link IdentityTransitionStub}.
     */
    protected RuleTransitionStub createTransitionStub(RuleEvent event,
            HostNode[] addedNodes, GraphState target) {
        if (target instanceof AbstractGraphState) {
            return ((AbstractGraphState) target).createInTransitionStub(this,
                event, addedNodes);
        } else {
            return new IdentityTransitionStub(event, addedNodes, target);
        }
    }

    /**
     * Callback factory method for creating a transition stub to this state,
     * from a given graph and with a given rule event.
     */
    protected RuleTransitionStub createInTransitionStub(GraphState source,
            RuleEvent event, HostNode[] addedNodes) {
        return new IdentityTransitionStub(event, addedNodes, this);
    }

    /**
     * Returns an iterator over the outgoing transitions as stored, i.e.,
     * without encodings taken into account.
     */
    final protected Iterator<RuleTransitionStub> getTransitionStubIter() {
        if (isClosed()) {
            return getStoredTransitionStubs().iterator();
        } else {
            return getCachedTransitionStubs().iterator();
        }
    }

    /**
     * Returns a list view upon the current outgoing transitions.
     */
    private Set<RuleTransitionStub> getCachedTransitionStubs() {
        return getCache().getStubSet();
    }

    /**
     * Returns the collection of currently stored outgoing transition stubs.
     * Note that this is only guaranteed to be synchronised with the cached stub
     * set if the state is closed.
     */
    public final Collection<RuleTransitionStub> getStoredTransitionStubs() {
        return Arrays.asList(this.transitionStubs);
    }

    /**
     * Stores a set of outgoing transition stubs in a memory efficient way.
     */
    private void setStoredTransitionStubs(
            Collection<RuleTransitionStub> outTransitionSet) {
        if (outTransitionSet.isEmpty()) {
            this.transitionStubs = EMPTY_TRANSITION_STUBS;
        } else {
            this.transitionStubs =
                new RuleTransitionStub[outTransitionSet.size()];
            outTransitionSet.toArray(this.transitionStubs);
        }
    }

    public boolean isClosed() {
        return testStatus(CLOSED);
    }

    public boolean setClosed(boolean complete) {
        boolean result = setStatus(CLOSED);
        if (result) {
            setStoredTransitionStubs(getCachedTransitionStubs());
            updateClosed();
            // reset the schedule to the beginning if the state was not 
            // completely explored
            if (!complete) {
                setSchedule(getCtrlState().getSchedule());
            }
            getCache().fireClosed();
            fireStatus(CLOSED);
        }
        return result;
    }

    /** Callback method to notify that the state was closed. */
    abstract protected void updateClosed();

    @Override
    public boolean setError() {
        boolean result = setStatus(ERROR);
        if (result) {
            fireStatus(ERROR);
        }
        return result;
    }

    @Override
    public boolean isError() {
        return testStatus(ERROR);
    }

    @Override
    final public boolean isTransient() {
        return getSchedule().isTransient();
    }

    @Override
    public boolean setAbsent() {
        boolean result = setStatus(ABSENT);
        if (result) {
            fireStatus(ABSENT);
        }
        return result;
    }

    @Override
    public boolean isAbsent() {
        return testStatus(ABSENT);
    }

    @Override
    public boolean isPresent() {
        return isCooked() && !isAbsent();
    }

    @Override
    public boolean setCooked() {
        boolean result = setStatus(COOKED);
        if (result) {
            getCache().fireCooked();
            setCacheCollectable();
            fireStatus(COOKED);
        }
        return result;
    }

    @Override
    public boolean isCooked() {
        return testStatus(COOKED);
    }

    /** Tests if a given flag is set in the status mask. */
    private boolean testStatus(Flag flag) {
        return flag.test(this.status);
    }

    /** Sets a given flag in this state's status. */
    private boolean setStatus(Flag flag) {
        boolean result = !testStatus(flag);
        if (result) {
            this.status = flag.set(this.status);
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
     * compared with the other, if that is a {@link AbstractGraphState}, or
     * with its source state if it is a {@link DefaultRuleTransition}.
     * Otherwise, the method throws an {@link UnsupportedOperationException}.
     */
    public int compareTo(Element obj) {
        if (obj instanceof AbstractGraphState) {
            return getNumber() - ((AbstractGraphState) obj).getNumber();
        } else if (obj instanceof DefaultRuleTransition) {
            return getNumber()
                - ((AbstractGraphState) ((DefaultRuleTransition) obj).source()).getNumber();
        } else {
            throw new UnsupportedOperationException(String.format(
                "Classes %s and %s cannot be compared", getClass(),
                obj.getClass()));
        }
    }

    /**
     * Returns a name for this state, rather than a full description. To get the
     * full description, use <tt>DefaultGraph.toString(Graph)</tt>.
     * 
     * @see groove.graph.AbstractGraph#toString(Graph)
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
        if (getBoundNodes().length > 0) {
            result.append(Arrays.toString(getBoundNodes()));
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
    public int getNumber() {
        return this.nr;
    }

    /** Returns the system record associated with this state. */
    protected SystemRecord getRecord() {
        return ((StateReference) getCacheReference()).getRecord();
    }

    /**
     * Returns the map of parameters to nodes for this state
     * @return a Map<String,Node> of parameters
     */
    public HostNode[] getBoundNodes() {
        return EMPTY_NODE_LIST;
    }

    /** 
     * Sets the control schedule.
     * This should occur at initialisation.
     */
    public final void setCtrlState(CtrlState ctrlState) {
        this.schedule = ctrlState.getSchedule();
    }

    public CtrlState getCtrlState() {
        return this.schedule.getState();
    }

    @Override
    public void setSchedule(CtrlSchedule schedule) {
        assert schedule.getState() == getCtrlState();
        boolean wasTransient = isTransient();
        this.schedule = schedule;
        if (wasTransient && !schedule.isTransient()) {
            getCache().fireCooked();
        }
    }

    public final CtrlSchedule getSchedule() {
        return this.schedule;
    }

    /**
     * The number of this Node.
     * 
     * @invariant nr < nrNodes
     */
    private final int nr;

    /** The underlying control state, if any. */
    private CtrlSchedule schedule;

    /** Global constant empty stub array. */
    private RuleTransitionStub[] transitionStubs = EMPTY_TRANSITION_STUBS;

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
    private static final RuleTransitionStub[] EMPTY_TRANSITION_STUBS =
        new RuleTransitionStub[0];
    /** Fixed empty array of (created) nodes. */
    private static final HostNode[] EMPTY_NODE_LIST = new HostNode[0];
}