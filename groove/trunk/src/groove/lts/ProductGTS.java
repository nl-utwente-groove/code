/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: ProductGTS.java,v 1.5 2008-03-19 20:46:48 kastenberg Exp $
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
 * @version $Revision: 1.5 $
 */
public class ProductGTS implements LTS {

	/**
     * Constructs a GTS from a (fixed) graph grammar.
     */
    public ProductGTS(GraphGrammar grammar) {
    	this.graphGrammar = grammar;
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
    	return startState;
    }

    /**
     * Adds a transition to the product gts. Basically, the transition is
     * only added to the set of outgoing transitions of the source state.
     * 
     * @param transition the transition to be added
     * @return the singleton set containing the transition added.
     */
    public Set<ProductTransition> addTransition(ProductTransition transition) {
    	transition.source().addTransition(transition);
    	Set<ProductTransition> result = new HashSet<ProductTransition>(1);
    	result.add(transition);
    	return result;
    }

    /**
     * Adds a Buechi graph-state to the gts. If there exists an isomorphic
     * state in the gts, nothing is done, and this isomorphic state is returned.
     * If it is a new state, this method returns <code>null</code>.
     * @param newState the state to be added
     * @return the isomorphic state if such a state is already in the gts,
     * <code>null</code> otherwise
     */
    public BuchiGraphState addState(BuchiGraphState newState) {
//        reporter.start(ADD_STATE);
        // see if isomorphic graph is already in the GTS
        ((AbstractGraphState) newState).setStateNumber(nodeCount());
        BuchiGraphState result = stateSet.put(newState);
        // new states are first considered open
        if (result == null) {
            openStates.put(newState);
            fireAddNode(newState);
        }
//        reporter.stop();
        return result;
    }

    /**
     * Closes a Buechi graph-state. Currently, listeners are
     * always notified, even when the state was already closed.
     * @param state the state to be closed.
     */
    public void setClosed(BuchiGraphState state) {
    	if (state.setClosed()) {
    		openStates.remove(state);
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
    	if (record == null) {
    		record = createRecord();
    	}
    	return record;
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
    	return graphGrammar;
    }

    public void addListener(GraphListener listener) {
    	listeners.add(listener);
    }

    public void removeListener(GraphListener listener) {
    	assert (listeners.contains(listener)) : "Listener cannot be removed since it is not registered.";
    	listeners.remove(listener);
    }

    public Iterator<GraphShapeListener> getListeners() {
    	return listeners.iterator();
    }

    public void notifyListenersOfClose(BuchiGraphState state) {
    	for (GraphShapeListener listener: listeners) {
    		if (listener instanceof Acceptor) {
    			((Acceptor) listener).closeUpdate(this, state);
    		}
    	}
    }

    /**
     * Calls {@link GraphShapeListener#addUpdate(GraphShape, Node)} on all GraphListeners in listeners.
     * @param node the node being added
     */
    protected void fireAddNode(Node node) {
        Iterator<GraphShapeListener> iter = getListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, node);
        }
    }

    public Set<ProductTransition> outEdgeSet(BuchiGraphState state) {
    	return state.outTransitions();
    }

    public Collection<BuchiGraphState> getOpenStates() {
        return new CollectionView<BuchiGraphState>(stateSet) {
        	@Override
            public boolean approves(Object obj) {
                return !((State) obj).isClosed();
            }
        };
    }

    private GraphGrammar graphGrammar;
    private BuchiGraphState startState;
    private TreeHashSet<BuchiGraphState> stateSet = new TreeHashStateSet();
    private TreeHashSet<BuchiGraphState> openStates = new TreeHashStateSet();
    private SystemRecord record;

    private Set<GraphShapeListener> listeners = new HashSet<GraphShapeListener>();

    /** Specialised set implementation for storing states. */
    private class TreeHashStateSet extends TreeHashSet<BuchiGraphState> {
    	/** Constructs a new, empty state set. */
        TreeHashStateSet() {
            super(GTS.INITIAL_STATE_SET_SIZE, GTS.STATE_SET_RESOLUTION, GTS.STATE_SET_ROOT_RESOLUTION);
        }
        
        /**
         * First compares the control locations, then calls {@link IsoChecker#areIsomorphic(Graph, Graph)}.
         * @see GraphState#getControl()
         */
    	@Override
        protected boolean areEqual(BuchiGraphState stateKey, BuchiGraphState otherStateKey) {
			if (!getRecord().isReuse()) {
			    return stateKey == otherStateKey;
			} else if (
					(stateKey.getLocation() == null || stateKey.getLocation().equals(otherStateKey.getLocation())) &&
					(stateKey.getBuchiLocation() == null || stateKey.getBuchiLocation().equals(otherStateKey.getBuchiLocation()))) {
				Graph one = stateKey.getGraph();
				Graph two = otherStateKey.getGraph();
				if (isCheckIsomorphism()) {
				    return checker.areIsomorphic(one, two);
				} else {
				    return one.nodeSet().equals(two.nodeSet()) && one.edgeSet().equals(two.edgeSet());
				}
			}
			else {
				return false;
			}
		}

        /**
		 * Returns the hash code of the isomorphism certificate, modified by the control
		 * location (if any).
		 */
    	@Override
        protected int getCode(BuchiGraphState stateKey) {
    	    int result;
    		if (!getRecord().isReuse()) { 
    		    result = System.identityHashCode(stateKey);
    		} else if (isCheckIsomorphism()) {
    		    result = stateKey.getGraph().getCertifier().getGraphCertificate().hashCode();
    		} else {
    			Graph graph = stateKey.getGraph();
    		    result = graph.nodeSet().hashCode() + graph.edgeSet().hashCode();
    		}
    		BuchiLocation location = stateKey.getBuchiLocation();
    		result += System.identityHashCode(location);
    		return result;
        }
        
        /** The isomorphism checker of the state set. */
        private IsoChecker checker = DefaultIsoChecker.getInstance();
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
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen(State state) {
		// TODO Auto-generated method stub
		return !state.isClosed();
//		return false;
	}

	public Set<? extends State> nodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

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

	public boolean containsState(BuchiGraphState state) {
		return stateSet.contains(state);
	}

	public boolean containsTransition(ProductTransition transition) {
		BuchiGraphState source = transition.source();
		return containsState(source) && source.outTransitions().contains(transition);
	}

	public boolean containsElementSet(Collection<? extends Element> elements) {
		// TODO Auto-generated method stub
		return false;
	}

	public int edgeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<? extends Edge> edgeSet(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

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

	public Set<? extends Edge> labelEdgeSet(int arity, Label label) {
		// TODO Auto-generated method stub
		return null;
	}

	public int nodeCount() {
		// TODO Auto-generated method stub
		return stateSet.size();
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
