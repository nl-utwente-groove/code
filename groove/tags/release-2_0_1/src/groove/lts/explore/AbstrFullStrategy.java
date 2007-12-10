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
 * $Id: AbstrFullStrategy.java,v 1.1 2007-11-28 16:09:19 iovka Exp $
 */
package groove.lts.explore;

import java.util.ArrayList;
import java.util.Collection;

import groove.abs.lts.AGTS;
import groove.abs.lts.AbstrGraphState;
import groove.abs.lts.AbstrStateGenerator;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.State;


/** */
public class AbstrFullStrategy extends AbstrStateGenerator implements ExploreStrategy {

	public Collection<? extends State> explore() throws InterruptedException {
        while (!this.openStateSet.isEmpty() && !Thread.interrupted()) {
            Collection<GraphState> oss = this.openStateSet;
            this.openStateSet = createOpenStateSet();
            for (GraphState openState: oss) {
                explore(openState);                
            }
        }
        return getGTS().getFinalStates();
	}

	public AbstrGraphState getAtState() { return this.currentState; }

	@Override
	public AGTS getGTS() { return super.getGTS(); }

	public String getName() { return STRATEGY_NAME; }

	public String getShortDescription() { return STRATEGY_DESCRIPTION; }

	/** The argument should be of type {@link AbstrGraphState}. */
	public void setAtState(State atState) { this.currentState = (AbstrGraphState) atState; }

	/** Argument should be of type {@link AGTS}. */
	@Override
	public void setGTS(GTS gts) {
        if (getGTS() != null) {
            getGTS().removeGraphListener(this.graphListener);
        }
        gts.addGraphListener(this.graphListener);
        this.openStateSet = createOpenStateSet();
        this.openStateSet.addAll(gts.getOpenStates());
        super.setGTS(gts);
	}    
	
	@Override
    public String toString() { return getName(); }

    /**
	 * @return Returns the openStateSet.
	 */
	final Collection<GraphState> getOpenStateSet() {
		return this.openStateSet;
	}

	private Collection<GraphState> createOpenStateSet() { return new ArrayList<GraphState>(); }
	
	// ------------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS AND STANDARD METHODS
	// ------------------------------------------------------------------------------
	/** Initialization with no options. */
	public AbstrFullStrategy () {
		super(null);
	}
	
	
	private AbstrGraphState currentState;
    /** The graph lisener permanently associated with this exploration strategy. */
    private final GraphShapeListener graphListener = new GraphAdapter() {
        /** This method adds the element to the open states. */
        @Override
        public void addUpdate(GraphShape graph, Node node) {
        	getOpenStateSet().add((GraphState) node);
        }
    };
    private Collection<GraphState> openStateSet;
	
	private static final String STRATEGY_NAME = "Abstract Full Exploration";
	private static final String STRATEGY_DESCRIPTION = "At each pass, asks the LTS for all remaining open states, and explores them";
}
