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

import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.control.CtrlPar;
import groove.control.Location;
import groove.graph.DefaultMorphism;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.trans.RuleEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.util.AbstractCacheHolder;
import groove.util.CacheReference;
import groove.util.TransformIterator;
import groove.util.TransformSet;

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
abstract public class AbstractGraphState extends
        AbstractCacheHolder<StateCache> implements GraphState {
    /**
     * Constructs a an abstract graph state, with a given control location.
     * @param location the control location; may be <code>null</code>.
     */
    public AbstractGraphState(CacheReference<StateCache> reference,
            Location location) {
        super(reference);
        if (location != null) {
            this.setLocation(location);
        }
        stateCount++;
    }

    /**
     * Constructs a an abstract graph state, with <code>null</code> control
     * location.
     */
    public AbstractGraphState(CacheReference<StateCache> reference) {
        this(reference, null);
    }

    abstract public Graph getGraph();

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location l) {
        this.location = l;
        if (this.parameters == null && l != null
            && ((ControlState) l).getInitializedVariables().size() > 0) {
            this.parameters =
                new Node[((ControlState) l).getInitializedVariables().size()];
        }
    }

    /*
     * (non-Javadoc)
     * @see groove.lts.GraphState#getOutTransitionIter()
     */
    public Iterator<GraphTransition> getTransitionIter() {
        // the iterator is created as a transformation of the iterator on the
        // stored OutGraphTransitions.
        return new TransformIterator<GraphTransitionStub,GraphTransition>(
            getTransitionStubIter()) {
            @Override
            public GraphTransition toOuter(GraphTransitionStub obj) {
                return obj.toTransition(AbstractGraphState.this);
            }
        };
    }

    public Set<GraphTransition> getTransitionSet() {
        return new TransformSet<GraphTransitionStub,GraphTransition>(
            getCachedTransitionStubs()) {
            @Override
            protected GraphTransition toOuter(GraphTransitionStub stub) {
                return stub.toTransition(AbstractGraphState.this);
            }

            @Override
            protected GraphTransitionStub toInner(Object key) {
                if (key instanceof GraphTransition) {
                    RuleEvent keyEvent = ((GraphTransition) key).getEvent();
                    Node[] keyAddedNodes =
                        ((GraphTransition) key).getAddedNodes();
                    GraphState keyTarget = ((GraphTransition) key).target();
                    return createTransitionStub(keyEvent, keyAddedNodes,
                        keyTarget);
                } else {
                    return null;
                }
            }
        };
    }

    public boolean containsTransition(GraphTransition transition) {
        return transition.source().equals(this)
            && getCachedTransitionStubs().contains(
                createTransitionStub(transition.getEvent(),
                    transition.getAddedNodes(), transition.target()));
    }

    // ----------------------- commands -----------------------------

    /**
     * Add an outgoing transition to this state, if it is not yet there. Returns
     * the {@link GraphTransition} that was added, or <code>null</code> if no
     * new transition was added.
     */
    public boolean addTransition(GraphTransition transition) {
        return getCache().addTransitionStub(transition.toStub());
    }

    public Collection<? extends GraphState> getNextStateSet() {
        return new TransformSet<GraphTransitionStub,GraphState>(
            getCachedTransitionStubs()) {
            @Override
            public GraphState toOuter(GraphTransitionStub stub) {
                return stub.getTarget(AbstractGraphState.this);
            }
        };
    }

    public Iterator<? extends GraphState> getNextStateIter() {
        return new TransformIterator<GraphTransitionStub,GraphState>(
            getTransitionStubIter()) {
            @Override
            public GraphState toOuter(GraphTransitionStub obj) {
                return obj.getTarget(AbstractGraphState.this);
            }
        };
    }

    public GraphTransitionStub getOutStub(RuleEvent event) {
        assert event != null;
        GraphTransitionStub result = null;
        if (isClosed()) {
            GraphTransitionStub[] outTransitions = this.transitionStubs;
            for (int i = 0; result == null && i < outTransitions.length; i++) {
                GraphTransitionStub trans = outTransitions[i];
                if (trans.getEvent(this) == event) {
                    result = trans;
                }
            }
        } else {
            Iterator<GraphTransitionStub> outTransIter =
                getTransitionStubIter();
            while (result == null && outTransIter.hasNext()) {
                GraphTransitionStub trans = outTransIter.next();
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
     * invokes {@link #createInTransitionStub(GraphState, RuleEvent, Node[])} if
     * the target is a {@link AbstractGraphState}, otherwise it creates a
     * {@link IdentityTransitionStub}.
     */
    protected GraphTransitionStub createTransitionStub(RuleEvent event,
            Node[] addedNodes, GraphState target) {
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
    protected GraphTransitionStub createInTransitionStub(GraphState source,
            RuleEvent event, Node[] addedNodes) {
        return new IdentityTransitionStub(event, addedNodes, this);
    }

    /**
     * Returns an iterator over the outgoing transitions as stored, i.e.,
     * without encodings taken into account.
     */
    final protected Iterator<GraphTransitionStub> getTransitionStubIter() {
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
    public final Collection<GraphTransitionStub> getStoredTransitionStubs() {
        return Arrays.asList(this.transitionStubs);
    }

    /**
     * Stores a set of outgoing transition stubs in a memory efficient way.
     */
    private void setStoredTransitionStubs(
            Collection<GraphTransitionStub> outTransitionSet) {
        if (outTransitionSet.isEmpty()) {
            this.transitionStubs = EMPTY_TRANSITION_STUBS;
        } else {
            this.transitionStubs =
                new GraphTransitionStub[outTransitionSet.size()];
            outTransitionSet.toArray(this.transitionStubs);
        }
    }

    public boolean isClosed() {
        return isCacheCollectable();
    }

    public boolean setClosed() {
        if (!isClosed()) {
            setStoredTransitionStubs(getCachedTransitionStubs());
            setCacheCollectable();
            updateClosed();
            return true;
        } else {
            return false;
        }
    }

    /** Callback method to notify that the state was closed. */
    abstract protected void updateClosed();

    /**
     * Retrieves a frozen representation of the graph, in the form of all nodes
     * and edges collected in one array. May return <code>null</code> if there
     * is no frozen representation.
     * @return All nodes and edges of the graph, or <code>null</code>
     */
    Element[] getFrozenGraph() {
        return this.frozenGraph;
    }

    /** Stores a frozen representation of the graph. */
    void setFrozenGraph(Element[] frozenGraph) {
        this.frozenGraph = frozenGraph;
        frozenGraphCount++;
    }

    /**
     * This implementation compares state numbers. The current state is either
     * compared with the other, if that is a {@link AbstractGraphState}, or
     * with its source state if it is a {@link DefaultGraphTransition}.
     * Otherwise, the method throws an {@link UnsupportedOperationException}.
     */
    public int compareTo(Element obj) {
        if (obj instanceof AbstractGraphState) {
            return getNumber() - ((AbstractGraphState) obj).getNumber();
        } else if (obj instanceof DefaultGraphTransition) {
            return getNumber()
                - ((AbstractGraphState) ((DefaultGraphTransition) obj).source()).getNumber();
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
     * @see groove.graph.AbstractGraph#toString(groove.graph.GraphShape)
     */
    @Override
    public String toString() {
        if (hasNumber()) {
            return "s"
                + getNumber()
                + (this.hasParameters() ? Arrays.toString(this.parameters) : "");
        } else {
            return "s??";
        }
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
        if (!hasNumber()) {
            throw new IllegalStateException("State number not set");
        }
        return this.nr;
    }

    /**
     * Sets the state number. This method should be called only once, with a
     * non-negative number.
     * @throws IllegalStateException if {@link #hasNumber()} returns
     *         <code>true</code>
     * @throws IllegalArgumentException if <code>nr</code> is illegal (i.e.,
     *         negative)
     */
    public void setNumber(int nr) {
        if (hasNumber()) {
            throw new IllegalStateException(String.format(
                "State number already set to %s", this.nr));
        }
        if (nr < 0) {
            throw new IllegalArgumentException(String.format(
                "Illegal state number %s", nr));
        }
        this.nr = nr;
    }

    /** Returns the system record associated with this state. */
    SystemRecord getRecord() {
        return ((StateReference) getCacheReference()).getRecord();
    }

    /**
     * Whether this state has parameters with a value
     * @return true if this state has parameters, false if not
     */
    public boolean hasParameters() {
        return this.parameters != null && this.parameters.length > 0;
    }

    /**
     * Returns the map of parameters to nodes for this state
     * @return a Map<String,Node> of parameters
     */
    public Node[] getParameters() {
        return this.parameters;
    }

    /**
     * Sets the parameter at position index to the given parameter
     * @param index the parameter index to change
     * @param parameter the parameter to set it to
     */
    public void setParameter(int index, Node parameter) {
        this.parameters[index] = parameter;
    }

    /**
     * Returns a partial Morphism for this state, based on the given 
     * ControlTransition's input parameters
     * @param ct the ControlTransition to base the Morphism on
     * @return a partial Morphism which can be used in determining whether rules
     * Match given certain parameters
     */
    public Morphism getPartialMorphism(ControlTransition ct) {
        Morphism result = null;
        if (ct.hasInputParameters()) {
            SPORule rule = (SPORule) ct.getRule();
            String[] inArgs = ct.getInputParameters();
            List<CtrlPar.Var> ruleSig = rule.getSignature();
            result = new DefaultMorphism(rule.getTarget(), this.getGraph());
            for (int i = 0; i < inArgs.length; i++) {
                String arg = inArgs[i];
                if (arg != null && !arg.equals("_")) {
                    Node src = ruleSig.get(i).getRuleNode();
                    int idx =
                        ((ControlState) this.location).getVariablePosition(arg);
                    Node tgt =
                        idx < 0 ? ValueNode.createValueNode(arg)
                                : this.parameters[idx];
                    if (tgt == null) {
                        // we're trying to match a node that has been deleted!
                        result = null;
                        break;
                    }
                    result.putNode(src, tgt);
                }
            }
        }
        return result;
    }

    /** The internally stored (optional) control location. */
    private Location location;

    /** Global constant empty stub array. */
    private GraphTransitionStub[] transitionStubs = EMPTY_TRANSITION_STUBS;

    /**
     * Slot to store a frozen graph representation. When filled, this provides a
     * faster way to reconstruct the graph of this state.
     */
    private Element[] frozenGraph;
    /**
     * The number of this Node.
     * 
     * @invariant nr < nrNodes
     */
    protected int nr = -1;

    /** Returns the total number of fixed delta graphs. */
    static public int getFrozenGraphCount() {
        return frozenGraphCount;
    }

    /** The total number of delta graphs frozen. */
    static private int frozenGraphCount;

    /**
     * The number of DefaultStates constructed.
     * 
     * @invariant nrNodes >= 0
     */
    private static int stateCount;
    /** Constant empty array of out transition, shared for memory efficiency. */
    private static final GraphTransitionStub[] EMPTY_TRANSITION_STUBS =
        new GraphTransitionStub[0];

    /** Keeps track of bound variables */
    private Node[] parameters;
}