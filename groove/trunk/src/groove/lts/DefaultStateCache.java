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
 * $Id: DefaultStateCache.java,v 1.2 2007-03-30 15:50:41 rensink Exp $
 */
package groove.lts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import groove.graph.DeltaGraphCache;


/**
 * Extends the cache with the outgoing transitions, as a set.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class DefaultStateCache extends DeltaGraphCache {
    /**
     * Constructs a cache for a given state.
     */
    protected DefaultStateCache(DefaultGraphState state) {
        super(state);
    }
   
    /**
     * Returns the cached set out {@link GraphOutTransition}s.
     * The set is constructed lazily if the state is closed,
     * using {@link #computeOutTransitionSet()}; if the state s not closed,
     * an empty set is initialized.
     */
    Collection<GraphOutTransition> getOutTransitionSet() {
        if (outTransitionSet == null) {
        	outTransitionSet = computeOutTransitionSet();
        }
        return outTransitionSet;
    }
    
    /**
     * Clears the cached set, so it does not occupy memory.
     * This is typically done at the moment the state is closed.
     */
    void clearOutTransitionSet() {
    	outTransitionSet = null;
    }
    
    /**
     * Reconstructs the set of {@link groove.lts.GraphOutTransition}s from the corresponding
     * {@link groove.util.ListEntry} in the underlying graph state.
     * It is assumed that <code>getState().isClosed()</code>.
     */
    protected Collection<GraphOutTransition> computeOutTransitionSet() {
        Collection<GraphOutTransition> result = createOutTransitionSet();
        if (getGraph().storesOutTransition()) {
			Iterator<GraphOutTransition> outTransitionIter = getGraph().getStoredOutTransitionIter();
			while (outTransitionIter.hasNext()) {
				GraphOutTransition outTransition = outTransitionIter.next();
				result.add(outTransition);
			}
		}
        return result;
    }
    
    /**
     * Factory method for the outgoing transition set.
     */
    protected Collection<GraphOutTransition> createOutTransitionSet() {
    	return new ArrayList<GraphOutTransition>();
    }
    
    /** Specialises the returnt ype of the super methods. */
    @Override
    public DefaultGraphState getGraph() {
    	return (DefaultGraphState) super.getGraph();
    }
    
    /**
     * The set of outgoing transitions computed for the underlying graph.
     */
    private Collection<GraphOutTransition> outTransitionSet;
}
