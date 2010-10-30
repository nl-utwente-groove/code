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
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphListener;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.trans.GraphGrammar;
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
public class ProductGTS implements LTS {

    /**
     * Constructs a GTS from a (fixed) graph grammar.
     */
    public ProductGTS(GraphGrammar grammar) {
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
     * Returns the Buechi start-state of the gts.
     * @return the Buechi start-state of the gts
     */
    public BuchiGraphState startBuchiState() {
        return this.startState;
    }

    /**
     * Adds a transition to the product gts. Basically, the transition is only
     * added to the set of outgoing transitions of the source state.
     * 
     * @param transition the transition to be added
     * @return the singleton set containing the transition added.
     */
    public Set<ProductTransition> addTransition(ProductTransition transition) {
        transition.source().addTransition(transition);
        this.transitionCount++;
        Set<ProductTransition> result = new HashSet<ProductTransition>(1);
        result.add(transition);
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
        if (state.setClosed()) {
            // openStates.remove(state);
            this.closedCount++;
            this.openStateCount--;
            notifyListenersOfClose(state);
        }
        // always notify listeners of state-closing
        // even if the state was already closed
        notifyListenersOfClose(state);
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
    public void addListener(GraphListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener from the ProductGTS
     * @param listener the listener to be removed.
     */
    public void removeListener(GraphListener listener) {
        assert (this.listeners.contains(listener)) : "Listener cannot be removed since it is not registered.";
        this.listeners.remove(listener);
    }

    /**
     * Returns an iterator over the current listeners.
     * @return an iterator over the current listeners.
     */
    public Iterator<GraphShapeListener> getListeners() {
        return this.listeners.iterator();
    }

    /**
     * Notifies the listeners of the event of closing a state.
     * @param state the state that has been closed.
     */
    public void notifyListenersOfClose(BuchiGraphState state) {
        for (GraphShapeListener listener : this.listeners) {
            if (listener instanceof Acceptor) {
                ((Acceptor) listener).closeUpdate(this, state);
            }
        }
    }

    /**
     * Calls {@link GraphShapeListener#addUpdate(GraphShape, Node)} on all
     * GraphListeners in listeners.
     * @param node the node being added
     */
    protected void fireAddNode(Node node) {
        Iterator<GraphShapeListener> iter = getListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, node);
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
                return !((State) obj).isClosed();
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
                return !((State) obj).isClosed();
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

    private final Set<GraphShapeListener> listeners =
        new HashSet<GraphShapeListener>();

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
         * @see GraphState#getLocation()
         */
        @Override
        protected boolean areEqual(BuchiGraphState stateKey,
                BuchiGraphState otherStateKey) {
            if ((stateKey.getLocation() == null || stateKey.getLocation().equals(
                otherStateKey.getLocation()))
                && (stateKey.getBuchiLocation() == null || stateKey.getBuchiLocation().equals(
                    otherStateKey.getBuchiLocation()))) {
                Graph one = stateKey.getGraph();
                Graph two = otherStateKey.getGraph();
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
                    stateKey.getGraph().getCertifier(true).getGraphCertificate().hashCode();
            } else {
                Graph graph = stateKey.getGraph();
                result =
                    graph.nodeSet().hashCode() + graph.edgeSet().hashCode();
            }
            BuchiLocation location = stateKey.getBuchiLocation();
            result += System.identityHashCode(location);
            return result;
        }

        /** The isomorphism checker of the state set. */
        private final IsoChecker checker = DefaultIsoChecker.getInstance(true);
    }

    public Set<? extends Transition> edgeSet() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<? extends State> getFinalStates() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasFinalStates() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFinal(State state) {
        return false;
    }

    public boolean isOpen(State state) {
        return !state.isClosed();
    }

    public Set<? extends State> nodeSet() {
        return this.stateSet;
    }

    /**
     * Deprecated. Use {@link ProductGTS#startBuchiState()} instead.
     */
    @Deprecated
    public State startState() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addGraphListener(GraphShapeListener listener) {
        // TODO Auto-generated method stub
    }

    public boolean containsElement(Element elem) {
        if (elem instanceof BuchiGraphState) {
            return containsState((BuchiGraphState) elem);
        } else if (elem instanceof ProductTransition) {
            return containsTransition((ProductTransition) elem);
        }
        return false;
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

    public boolean containsElementSet(Collection<? extends Element> elements) {
        // TODO Auto-generated method stub
        return false;
    }

    public int edgeCount() {
        return this.transitionCount;
    }

    public Set<? extends Edge> edgeSet(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public Set<? extends Edge> edgeSet(Node node, int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public GraphInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFixed() {
        // TODO Auto-generated method stub
        return false;
    }

    public Set<? extends Edge> labelEdgeSet(Label label) {
        return null;
    }

    @Deprecated
    public Set<? extends Edge> labelEdgeSet(int arity, Label label) {
        return labelEdgeSet(label);
    }

    public int nodeCount() {
        // TODO Auto-generated method stub
        return this.stateSet.size();
    }

    public Set<? extends GraphTransition> inEdgeSet(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<? extends GraphTransition> outEdgeSet(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeGraphListener(GraphShapeListener listener) {
        // TODO Auto-generated method stub
    }

    public void setFixed() {
        // TODO Auto-generated method stub
    }

    public GraphInfo setInfo(GraphInfo info) {
        // TODO Auto-generated method stub
        return null;
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }
}
