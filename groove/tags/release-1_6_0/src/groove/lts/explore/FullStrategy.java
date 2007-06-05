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
 * $Id: FullStrategy.java,v 1.4 2007-04-27 22:06:58 rensink Exp $
 */
package groove.lts.explore;

import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;

import java.util.Collection;

/**
 * Recursively explores all open states of the LTS, in a breadth first manner.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class FullStrategy extends AbstractStrategy {
	/** Name of this exploration strategy. */
    static public final String STRATEGY_NAME = "Full";
    /** Short description of this exploration strategy. */
    static public final String STRATEGY_DESCRIPTION = "At each pass, asks the LTS for all remaining open states, and explores them";
//    
//    /**
//     * Constructs a strategy without setting an LTS.
//     */
//    public FullStrategy() {
//    	// empty constructor
//    }
//    
//    /**
//     * Constructs an exploration strategy for a given LTS.
//     * @param generator
//     */
//    public FullStrategy(StateGenerator generator) {
//    	setSubject(generator);
//    }
//    
    /**
     * Initializes the set of open states, then calls the super method.
     */
    @Override
    public void setGTS(GTS gts) {
        if (getGTS() != null) {
            getGTS().removeGraphListener(graphListener);
        }
        gts.addGraphListener(graphListener);
        openStateSet = createOpenStateSet();
        openStateSet.addAll(gts.getOpenStates());
        super.setGTS(gts);
    }

    /** 
     * The result states for this method are the final states of the LTS.
     * @see LTS#getFinalStates()
     */
    public Collection<? extends State> explore() throws InterruptedException {
        while (!openStateSet.isEmpty() && !Thread.interrupted()) {
            Collection<GraphState> openStateSet = this.openStateSet;
            this.openStateSet = createOpenStateSet();
            for (GraphState openState: openStateSet) {
                explore(openState);                
            }
//            // clear some caches, just to outsmart the garbage collector
//            for (GraphState openState: openStateSet) {
//                if (openState instanceof AbstractGraphState) {
//                	((AbstractGraphState) openState).clearCache();
//                }
//            }
        }
        return getGTS().getFinalStates();
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Callback method to create the (initially empty) collection of open states 
     * for a given LTS.
     */
    protected Collection<GraphState> createOpenStateSet() {
        int newSize = predictNewOpenStateCount();
        return createStateSet(newSize);
    }

    /**
     * Returns a prediction of the number of open states that will be generated
     * during the next round of exploration.
     * The prediction is based on the ratio of the current size of the
     * open state set with the previous size.
     * The current size is subsequently stored as the previous size,
     * for use in the next calculation.
     */
    protected int predictNewOpenStateCount() {
        int currentOpenStateCount = (openStateSet == null) ? 0 : openStateSet.size();
        double growth = previousOpenStateCount == 0 ? 1 : (double) currentOpenStateCount / previousOpenStateCount;
        previousOpenStateCount = currentOpenStateCount;
        return Math.max(100,(int) (currentOpenStateCount * Math.min(1,growth+0.1)));
    }

    /**
     * The current set of open states.
     */
    private Collection<GraphState> openStateSet;
    /**
     * The size of the {@link #openStateSet} at the previous invocation of
     * {@link #predictNewOpenStateCount()}.
     */
    private int previousOpenStateCount;
    /** The graph lisener permanently associated with this exploration strategy. */
    private final GraphShapeListener graphListener = new GraphAdapter() {
        /** This method adds the element to the open states. */
        @Override
        public void addUpdate(GraphShape graph, Node node) {
        	openStateSet.add((GraphState) node);
        }
    };
}