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
 * $Id: ProductGTS.java,v 1.5 2008/03/19 20:46:48 kastenberg Exp $
 */
package groove.lts;

import groove.explore.result.Acceptor;
import groove.graph.AbstractGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.iso.IsoChecker;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.SystemRecord;
import groove.util.CollectionView;
import groove.util.FilterIterator;
import groove.util.TreeHashSet;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements LTS and represents GTSs in which states are products of
 * graph-states and Buchi-locations.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ProductGTS extends AbstractGraph<GraphState,GraphTransition>
        implements LTS {

    /**
     * Constructs a GTS from a (fixed) graph grammar.
     */
    public ProductGTS(GraphGrammar grammar) {
        super();
        this.graphGrammar = grammar;
    }

    @Override
    public void testFixed(boolean fixed) throws IllegalStateException {
        if (isFixed() != fixed) {
            throw new IllegalStateException(String.format(
                "Expected LTS to be %s", fixed ? "fixed" : "unfixed"));
        }
    }

    /**
     * Sets the Buechi start-state of the gts.
     * @param startState the Buechi start-state
     */
    public void setStartState(BuchiGraphState startState) {
        addState(startState);
        this.startState = startState;
    }

    /**
     * Adds a transition to the product gts. Basically, the transition is only
     * added to the set of outgoing transitions of the source state.
     * 
     * @param transition the transition to be added
     * @return the singleton set containing the transition added.
     */
    public boolean addTransition(ProductTransition transition) {
        boolean result = transition.source().addTransition(transition);
        this.transitionCount++;
        return result;
    }

    /**
     * Adds a Buechi graph-state to the gts. If there exists an isomorphic state
     * in the gts, nothing is done, and this isomorphic state is returned. If it
     * is a new state, this method returns <code>null</code>.
     * @param newState the state to be added
     * @return the isomorphic state if such a state is already in the gts,
     *         <code>null</code> otherwise
     */
    public BuchiGraphState addState(BuchiGraphState newState) {
        // reporter.start(ADD_STATE);
        // see if isomorphic graph is already in the GTS
        ((AbstractGraphState) newState).setNumber(nodeCount());
        BuchiGraphState result = this.stateSet.put(newState);
        // new states are first considered open
        if (result == null) {
            // openStates.put(newState);
            this.stateCount++;
            this.openStateCount++;
            fireAddNode(newState);
        }
        // reporter.stop();
        return result;
    }

    /**
     * Closes a Buechi graph-state. Currently, listeners are always notified,
     * even when the state was already closed.
     * @param state the state to be closed.
     */
    public void setClosed(BuchiGraphState state) {
        if (state.setClosed(true)) {
            // openStates.remove(state);
            this.closedCount++;
            this.openStateCount--;
            fireCloseState(state);
        }
        // always notify listeners of state-closing
        // even if the state was already closed
        fireCloseState(state);
    }

    /**
     * Returns whether a check for isomorphic states should be performed.
     * @return always returns <tt>true</tt>
     */
    public boolean isCheckIsomorphism() {
        return true;
    }

    /**
     * Returns the {@link groove.trans.SystemRecord} of this gts.
     * @return the system-record of this gts
     */
    public SystemRecord getRecord() {
        if (this.record == null) {
            this.record = createRecord();
        }
        return this.record;
    }

    /**
     * Creates a {@link groove.trans.SystemRecord} for this gts.
     * @return the freshly created system-record for this gts.
     */
    protected SystemRecord createRecord() {
        return new SystemRecord(getGrammar());
    }

    /**
     * Returns the grammar of this gts.
     * @return the grammar of this gts
     */
    public GraphGrammar getGrammar() {
        return this.graphGrammar;
    }

    /**
     * Adds a listener to the ProductGTS.
     * @param listener the listener to be added.
     */
    public void addListener(LTSListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener from the ProductGTS
     * @param listener the listener to be removed.
     */
    public void removeListener(LTSListener listener) {
        assert (this.listeners.contains(listener)) : "Listener cannot be removed since it is not registered.";
        this.listeners.remove(listener);
    }

    /**
     * Returns an iterator over the current listeners.
     * @return an iterator over the current listeners.
     */
    public Iterator<LTSListener> getListeners() {
        return this.listeners.iterator();
    }

    /**
     * Notifies the listeners of the event of closing a state.
     * @param state the state that has been closed.
     */
    public void fireCloseState(BuchiGraphState state) {
        for (LTSListener listener : this.listeners) {
            if (listener instanceof Acceptor) {
                ((Acceptor) listener).closeUpdate(this, state);
            }
        }
    }

    /**
     * Calls {@link LTSListener#addUpdate(LTS, GraphState)} on all
     * GraphListeners in listeners.
     * @param state the node being added
     */
    @Override
    protected void fireAddNode(GraphState state) {
        super.fireAddNode(state);
        Iterator<LTSListener> iter = getListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, state);
        }
    }

    /**
     * Return the set of outgoing transitions of a Buchi graph-state.
     * @param state the Buchi graph state
     * @return the set of outgoing transitions of <code>state</code>
     */
    public Set<ProductTransition> outEdgeSet(BuchiGraphState state) {
        return state.outTransitions();
    }

    /**
     * Indicates if the ProductGTS currently has open states. Equivalent to (but
     * more efficient than) <code>getOpenStateIter().hasNext()</code> or
     * <code>!getOpenStates().isEmpty()</code>.
     * @return <code>true</code> if the ProductGTS currently has open states
     * @see #getOpenStateIter()
     * @see #getOpenStates()
     */
    public boolean hasOpenStates() {
        int openStateCount = openStateCount();
        return openStateCount > 0;
    }

    /**
     * Returns a view on the set of currently open states.
     * @see #hasOpenStates()
     * @see #getOpenStateIter()
     */
    public Collection<BuchiGraphState> getOpenStates() {
        return new CollectionView<BuchiGraphState>(this.stateSet) {
            @Override
            public boolean approves(Object obj) {
                return !((GraphState) obj).isClosed();
            }
        };
    }

    /**
     * Returns a view on the set of currently open states.
     * @see #hasOpenStates()
     * @see #getOpenStateIter()
     */
    public Collection<BuchiGraphState> getPocketStates() {
        return new CollectionView<BuchiGraphState>(this.stateSet) {
            @Override
            public boolean approves(Object obj) {
                return ((BuchiGraphState) obj).isPocket();
            }
        };
    }

    /**
     * Returns an iterator over the set of currently open states. Equivalent to
     * <code>getOpenStates().iterator()</code>.
     * @see #hasOpenStates()
     * @see #getOpenStates()
     */
    public Iterator<BuchiGraphState> getOpenStateIter() {
        return new FilterIterator<BuchiGraphState>(nodeSet().iterator()) {
            @Override
            protected boolean approves(Object obj) {
                return !((GraphState) obj).isClosed();
            }
        };
    }

    /** Returns the number of not fully expored states. */
    public int openStateCount() {
        return nodeCount() - this.closedCount;
    }

    private final GraphGrammar graphGrammar;
    private BuchiGraphState startState;
    private final TreeHashSet<BuchiGraphState> stateSet =
        new TreeHashStateSet();
    private int stateCount = 0;
    // private TreeHashSet<BuchiGraphState> openStates = new TreeHashStateSet();
    private int openStateCount = 0;
    private int closedCount = 0;
    private int transitionCount = 0;
    private SystemRecord record;

    private final Set<LTSListener> listeners = new HashSet<LTSListener>();

    /** Specialised set implementation for storing states. */
    private class TreeHashStateSet extends TreeHashSet<BuchiGraphState> {
        /** Constructs a new, empty state set. */
        TreeHashStateSet() {
            super(GTS.INITIAL_STATE_SET_SIZE, GTS.STATE_SET_RESOLUTION,
                GTS.STATE_SET_ROOT_RESOLUTION);
        }

        /**
         * First compares the control locations, then calls
         * {@link IsoChecker#areIsomorphic(Graph, Graph)}.
         */
        @Override
        protected boolean areEqual(BuchiGraphState stateKey,
                BuchiGraphState otherStateKey) {
            if ((stateKey.getCtrlState() == otherStateKey.getCtrlState())
                && (stateKey.getBuchiLocation() == null || stateKey.getBuchiLocation().equals(
                    otherStateKey.getBuchiLocation()))) {
                HostGraph one = stateKey.getGraph();
                HostGraph two = otherStateKey.getGraph();
                if (isCheckIsomorphism()) {
                    return this.checker.areIsomorphic(one, two);
                } else {
                    return one.nodeSet().equals(two.nodeSet())
                        && one.edgeSet().equals(two.edgeSet());
                }
            } else {
                return false;
            }
        }

        /**
         * Returns the hash code of the isomorphism certificate, modified by the
         * control location (if any).
         */
        @Override
        protected int getCode(BuchiGraphState stateKey) {
            int result;
            if (isCheckIsomorphism()) {
                result =
                    this.checker.getCertifier(stateKey.getGraph(), true).getGraphCertificate().hashCode();
            } else {
                HostGraph graph = stateKey.getGraph();
                result =
                    graph.nodeSet().hashCode() + graph.edgeSet().hashCode();
            }
            BuchiLocation location = stateKey.getBuchiLocation();
            result += System.identityHashCode(location);
            return result;
        }

        /** The isomorphism checker of the state set. */
        private final IsoChecker<HostNode,HostEdge> checker =
            IsoChecker.getInstance(true);
    }

    public Set<? extends GraphTransition> edgeSet() {
        return null;
    }

    public Collection<? extends BuchiGraphState> getFinalStates() {
        return null;
    }

    public boolean hasFinalStates() {
        return false;
    }

    public boolean isFinal(GraphState state) {
        return false;
    }

    public boolean isOpen(GraphState state) {
        return !state.isClosed();
    }

    public Set<? extends BuchiGraphState> nodeSet() {
        return this.stateSet;
    }

    public BuchiGraphState startState() {
        return this.startState;
    }

    public void addLTSListener(LTSListener listener) {
        // Empty.
    }

    @Override
    public boolean containsNode(Node elem) {
        assert elem instanceof BuchiGraphState;
        return containsState((BuchiGraphState) elem);
    }

    @Override
    public boolean containsEdge(Edge<?> elem) {
        assert elem instanceof ProductTransition;
        return containsTransition((ProductTransition) elem);
    }

    /**
     * Checks whether a given state is contained in the current ProductGTS.
     * @param state the state to check containment for
     * @return <tt>true</tt> if the state is in the state-set, <tt>false</tt>
     *         otherwise
     * @see TreeHashSet#contains(Object)
     */
    public boolean containsState(BuchiGraphState state) {
        return this.stateSet.contains(state);
    }

    /**
     * Checks whether a given transition is in the set of outgoing transitions
     * of the source state as contained in the current ProductGTS.
     * @param transition the transition to check containment for
     * @return <tt>true</tt> if the transition is in the set of outgoing
     *         transitions of its source-state, <tt>false</tt> otherwise
     */
    public boolean containsTransition(ProductTransition transition) {
        BuchiGraphState source = transition.source();
        return containsState(source)
            && source.outTransitions().contains(transition);
    }

    @Override
    public int edgeCount() {
        return this.transitionCount;
    }

    @Override
    public int nodeCount() {
        return this.stateSet.size();
    }

    public void removeLTSListener(LTSListener listener) {
        // Empty.
    }

    @Override
    public ProductGTS newGraph() {
        return new ProductGTS(this.graphGrammar);
    }

    @Override
    public boolean addNode(GraphState node) {
        return addState((BuchiGraphState) node) == null;
    }

    @Override
    public boolean removeEdge(GraphTransition edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeWithoutCheck(GraphTransition edge) {
        assert edge instanceof ProductTransition;
        return addTransition((ProductTransition) edge);
    }

    @Override
    public boolean removeNodeWithoutCheck(GraphState node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProductGTS clone() {
        throw new UnsupportedOperationException();
    }

}
