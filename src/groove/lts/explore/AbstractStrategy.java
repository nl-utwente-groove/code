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
 * $Id: AbstractStrategy.java,v 1.7 2007-04-27 22:06:58 rensink Exp $
 */
package groove.lts.explore;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.State;
import groove.lts.StateGenerator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract LTS exploration strategy.
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
public abstract class AbstractStrategy extends StateGenerator implements ExploreStrategy {
//	/** 
//	 * Constructs a new (abstract) strategy.
//	 * The GTS and system record are initially <code>null</code>.
//	 */
//	protected AbstractStrategy() {
//		this.generator = new StateGenerator();
//	}
	
    /** Value for the depth parameter of the strategy that means no depth is set. */
    static public final int NO_TO_DEPTH = 0;
    
    /**
     * Returns the state set by the previous call of {@link #setAtState(State)},
     * or the start state of the LTS if no state was set.
     * @see #setAtState(State)
     */
    public GraphState getAtState() {
    	if (atState == null) {
    		atState = getGTS().startState();
    	}
        return atState;
    }

    /**
     * Sets the state at which the next exploration should take place.
     * @param atState the state at which the next exploration should take place
     * @ensure <tt>getAtState() == atState</tt>
     * @see #getAtState()
     */
    public void setAtState(State atState) {
        this.atState = (GraphState) atState;
    }
//
//    /**
//     * Returns the exploration depth set by <tt>setToDepth(int)</tt>.
//     * @return the exploration depth set by <tt>setToDepth(int)</tt>
//     * @see #setToDepth(int)
//     */
//    @Deprecated
//    public int getToDepth() {
//        return toDepth;
//    }
//
//    /**
//     * Sets the maximum depth to which the next exploration should continue.
//     * The depth is counted as the number of transitions from the state at which exploration
//     * starts.
//     * A value of 0 means no maximum depth.
//     * @param toDepth the maximum depth to which the next exploration should continue
//     * @require <tt>toDepth >= 0</tt>
//     * @see #getToDepth()
//     * @see #explore()
//     */
//    @Deprecated
//    public void setToDepth(int toDepth) {
//        this.toDepth = toDepth;
//    }
//    
//    /**
//     * Sets the GTS to which this exploration strategy should be applied.
//     * The current state is set to the GTS's start state.
//     * The state generator is not initialised.
//     * @param gts the new underlying GTS to be explored
//     */
//    public final void setGTS(GTS gts) {
//        setLTS(gts, null);
//    }
//    
//    /**
//     * Sets the state generator for this strategy.
//     * The GTS is also initialised, to the generator's GTS.
//     * @param generator the new state generator to be used for exploration
//     */
//    @Override
//    public final void setSubject(StateGenerator generator) {
//    	setGTS(generator.getGTS(), generator.getRecord());
//    }
        
    /**
     * Apart from the super method, sets the state being explored to the GTS'
     * start state.
     */
    @Override
    public void setGTS(GTS gts) {
        super.setGTS(gts);
        setAtState(gts.startState());
        if (collector != null) {
        	collector.setGTS(gts);
        }
    }
//
//    /**
//     * Returns the underlying lts that is being explored.
//     * @return the underlying lts being explored
//     */
//    public GTS getGTS() {
//        return lts;
//    }
    
    @Override
    public String toString() {
        String result = getName();
//        if (toDepth != NO_TO_DEPTH) {
//            result += "  to depth " + toDepth;
//        }
        if (getGTS() != null && atState != null && atState != getGTS().startState()) {
            result += " (starting at " + atState + ")";
        }
        return result;
    }
//
//    /**
//	 * Lazily creates and returns the state generator for this strategy.
//	 * @return a generator for the current GTS; never <code>null</code>
//	 */
//	protected StateGenerator getGenerator() {
////	    if (generator == null) {
////	    	generator = new StateGenerator(getLTS());
////	    }
//	    return generator;
//	}

	/**
     * Callback method to create the (initially empty) collection of open states 
     * for a given LTS, with an initial capacity.
     */
    protected Collection<GraphState> createStateSet(int initialSize) {
        return new ArrayList<GraphState>(initialSize);
    }
    
    /**
     * Callback method to create the (initially empty) collection of open states 
     * for a given LTS.
     */
    protected Collection<GraphState> createStateSet() {
        return new ArrayList<GraphState>();
    }
    
    /** 
     * Returns a new state collector, which initially listens to the 
     * current GTS.
     */
    protected FreshStateCollector getCollector() {
    	if (collector == null) {
    		collector = new FreshStateCollector();
    		collector.setGTS(getGTS());
    	}
    	return collector;
    }
    
//    /**
//     * The current graph deriver.
//     */
//    private final StateGenerator generator;
//    /**
//     * The currently set LTS.
//     */
//    private GTS lts;
//    /**
//     * The currently set exploration depth.
//     * @invariant <tt>toDepth >= 0</tt>
//     */
//    private int toDepth = 0;
    /**
     * The currently set start state for the exploration.
     * @invariant <tt>lts.containsNode(atState)</tt>
     */
    private GraphState atState;
    /** Collector primed to listen to the current GTS. */
    private FreshStateCollector collector;
    
	/**
	 * Listener that collects the fresh states into a set.
	 */
	static protected class FreshStateCollector extends GTSListener {
		/** Empty constructor declaration to restrict visibility. */
		private FreshStateCollector() {
			// empty
		}
		/**
		 * Sets the result set to an alial of a given set.
		 */
		public void set(Collection<GraphState> result){
			this.result = result;
			transitionsAdded = false;
		}
		
		/**
		 * Sets the result set to the empty set.
		 */
		public void reset() {
			result = null;
			transitionsAdded = false;
		}
		
		/** 
		 * Indicates if any transitions were added since {@link #set(Collection)}
		 * was last called.
		 * @return <code>true</code> if any transitions were added
		 */
		public boolean isTransitionsAdded() {
			return transitionsAdded;
		}
		
		@Override
		public void addUpdate(GraphShape graph, Node node) {
			if (result != null) {
				result.add((GraphState) node);
			}
		}
		
		@Override
		public void addUpdate(GraphShape graph, Edge edge) {
			transitionsAdded = true;
		}
		
		/** The set to collect the fresh states. */
		private Collection<GraphState> result;
		/** 
		 * Variable that records if any transition have been added since the last
		 * {@link #set(Collection)}.
		 */
		private boolean transitionsAdded;
	}
}