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
 * $Id: ProductGTS.java,v 1.1 2008-02-20 07:54:15 kastenberg Exp $
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
 * @version $Revision: 1.1 $ $Date: 2008-02-20 07:54:15 $
 */
public class ProductGTS implements LTS {

	/**
     * Constructs a GTS from a (fixed) graph grammar.
     */
    public ProductGTS(GraphGrammar grammar) {
    	this.graphGrammar = grammar;
    }

    public void setStartState(BuchiGraphState startState) {
    	addState(startState);
    	this.startState = startState;
    }

    public BuchiGraphState startBuchiState() {
    	return startState;
    }

    public Set<ProductTransition> addTransition(ProductTransition transition) {
    	transition.source().addTransition(transition);
    	Set<ProductTransition> result = new HashSet<ProductTransition>(1);
    	result.add(transition);
    	return result;
    }

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

    public void setClosed(BuchiGraphState state) {
    	if (state.setClosed()) {
    		openStates.remove(state);
        	notifyListenersOfClose(state);
    	}
    }

    public boolean isCheckIsomorphism() {
    	return true;
    }

    public SystemRecord getRecord() {
    	if (record == null) {
    		record = createRecord();
    	}
    	return record;
    }

    protected SystemRecord createRecord() {
    	return new SystemRecord(getGrammar());
    }

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

	@Override
	public Set<? extends Transition> edgeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends State> getFinalStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasFinalStates() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFinal(State state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen(State state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<? extends State> nodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State startState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGraphListener(GraphShapeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsElement(Element elem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsElementSet(Collection<? extends Element> elements) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int edgeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<? extends Edge> edgeSet(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends Edge> edgeSet(Node node, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphInfo getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFixed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<? extends Edge> labelEdgeSet(int arity, Label label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int nodeCount() {
		// TODO Auto-generated method stub
		return stateSet.size();
	}

	@Override
	public Set<? extends GraphTransition> outEdgeSet(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeGraphListener(GraphShapeListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFixed() {
		// TODO Auto-generated method stub
	}

	@Override
	public GraphInfo setInfo(GraphInfo info) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
}
