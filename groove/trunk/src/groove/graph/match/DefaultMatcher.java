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
 * $Id: DefaultMatcher.java,v 1.3 2007-03-27 14:18:35 rensink Exp $
 */
package groove.graph.match;

import groove.graph.DefaultSimulation;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This matcher walks through a search tree built up according to
 * a search plan, in which the matching order of the domain elements
 * is determined.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class DefaultMatcher implements Matcher {
	/** 
	 * The search plan factory for default matchers, statically created
	 * for efficiency.
	 * @see #getSearchPlanFactory() 
	 */
	static private final SearchPlanFactory searchPlanFactory = new DefaultSearchPlanFactory();

	/**
     * Constructs a matcher as an extension of a given morphism.
     * The images of the morphism are not checked for consistency.
     * @param morphism the intended basis of the simulation
     * @ensure <tt>getMorphism() == morph</tt>
     */
    public DefaultMatcher(Morphism morphism) {
        this.morphism = morphism;
    }

    @Deprecated
    public boolean isConsistent() {
        return true;
    }

    @Deprecated
    public boolean isRefined() {
        return getSingularMap().size() == morphism.dom().size();
    }
    
    /**
     * Returns the currently built map between the domain and codomain
     * elements. 
     */
    public NodeEdgeMap getSingularMap() {
    	if (singularMap == null) {
    		singularMap = computeSingularMap();
    	}
    	return singularMap;
    }

    /**
     * Internally clones the element map,
     * so future changes to the map will not affect aliases of the current map.
     */
    protected void cloneSingularMap() {
    	singularMap = getSingularMap().clone();
    }

    /**
     * Computes a fresh element map on the basis of the underlying morphism.
     */
    protected NodeEdgeMap computeSingularMap() {
    	NodeEdgeMap result = createSingularMap();
    	result.putAll(getMorphism().elementMap());
    	return result;
    }

    /**
     * Callback factory method to create the node-edge map to store the
     * final result of the simulation.
     */
    protected NodeEdgeMap createSingularMap() {
    	return new NodeEdgeHashMap();
    }
    
	public Morphism getMorphism() {
		return morphism;
	}

	public Graph dom() {
        return morphism.dom();
    }

    public Graph cod() {
        return morphism.cod();
    }

    public boolean hasRefinement() {
    	boolean result;
		reporter.start(GET_REFINEMENT);
        result = find();
        reporter.stop();
        return result;
    }

    public NodeEdgeMap getRefinement() {
    	NodeEdgeMap result;
		reporter.start(GET_REFINEMENT);
		if (find()) {
			result = this.getSingularMap();
		} else {
			result = null;
		}
		reporter.stop();
		return result;
    }

    public Iterator<? extends NodeEdgeMap> getRefinementIter() {
    	Iterator<NodeEdgeMap> result;
    	reporter.start(GET_REFINEMENT_ITER);
    	result = new Iterator<NodeEdgeMap>() {
    		public boolean hasNext() {
    			// test if there is an unreturned next or if we are done
    			if (next == null && !atEnd) {
    				// search for the next solution
    				if (find()) {
    					next = DefaultMatcher.this.getSingularMap();
    				} else {
    					// there is none and will be none; give up
    					atEnd = true;
    				}
    			}
    			return !atEnd;
    		}
    		
    		public NodeEdgeMap next() {
    			if (hasNext()) {
    				NodeEdgeMap result = next;
    				next = null;
    				return result;
    			} else {
    				throw new NoSuchElementException();
    			}
    		}
    		
    		
    		public void remove() {
				throw new UnsupportedOperationException();
			}

    		/** The next refinement to be returned. */
    		private NodeEdgeMap next;
    		/**
    		 *  Flag to indicate that the last refinement has been returned,
    		 * so {@link #next()} henceforth will return <code>false</code>.
    		 */
			private boolean atEnd = false;
    	};
    	reporter.stop();
    	return result;
    }
    
    public Collection<NodeEdgeMap> getRefinementSet() {
//    	Collection<NodeEdgeMap> result = new TreeHashSet3<NodeEdgeMap>();
    	// these maps have complicated equality so better not use a hash map
    	Collection<NodeEdgeMap> result = new ArrayList<NodeEdgeMap>();
		reporter.start(GET_REFINEMENT_SET);
		while (find()) {
			result.add(this.getSingularMap());
		}
		reporter.stop();
		return result;
	}
    
    /**
     * We choose object identity as the notion of equality. 
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
    
    /**
     * Changed in correspondence with {@link #equals(Object)} to
     * {@link System#identityHashCode(java.lang.Object)}.
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Computes the next refinement of this simulation.
     */
    protected boolean find() {
    	reporter.start(FIND);
		Iterator<SearchItem> searchPlan = getSearchPlan();
    	assert keyIndex == 0 || !searchPlan.hasNext();
		if (found) {
			// we already found a solution;
			// to prevent sharing errors, clone the current singular map
			cloneSingularMap();
			keyIndex--;
		}
		while (keyIndex >= 0 && (keyIndex < images.size() || searchPlan.hasNext())) {
			// retrieve the current search record
			SearchItem.Record record;
			if (keyIndex < images.size()) {
				// take it from the existing records
				record = images.get(keyIndex);
			} else {
				// make a new one
				record = searchPlan.next().get(this);
				images.add(record);
			}
			// find a new image 
			keyIndex += record.find() ? +1 : -1;
		}
		assert keyIndex >= 0 || getSingularMap().size() == morphism.size() : String.format("Element map is %s is non-empty", getSingularMap());
		reporter.stop();
		found = keyIndex >= 0;
		return found;
	}

    /**
     * Retrieves the search plan for this matcher
     */
    protected Iterator<SearchItem> getSearchPlan() {
    	// create the search plan lazily
    	if (plan == null) {
    		plan = computeSearchPlan().iterator();
    	}
    	return plan;
    }

    /**
     * Computes the key schedule for this simulation.
     * This implementation calls {@link #getSearchPlanFactory()}.
     */
    protected Iterable<SearchItem> computeSearchPlan() {
    	return getSearchPlanFactory().createSearchPlan(dom());
    }
    
    /**
     * Retrieves the search plan factory.
     * This implementation returns the statically stored factory of this class.
     */
    protected SearchPlanFactory getSearchPlanFactory() {
    	return searchPlanFactory;
    }
    /**
     * The underlying morphism of this matcher.
     */
    private final Morphism morphism;
    /**
	 * The element map built up during the matching process.
	 */
	private NodeEdgeMap singularMap;
	/** Search stack. */
	private final List<SearchItem.Record> images = new ArrayList<SearchItem.Record>();
    /**
	 * A list of domain elements, in the order in which they are to be matched.
	 */
	private Iterator<SearchItem> plan;
	/** 
	 * The index in {@link #plan} of the first currently unmatched element.
	 */
	private int keyIndex;
	/** Flag indicating that the last call of #find() yielded a solution. */
	private boolean found;
    
    static protected final Reporter reporter = DefaultSimulation.reporter;
    static protected final int GET_REFINEMENT = DefaultSimulation.GET_REFINEMENT;
    static protected final int GET_REFINEMENT_SET = DefaultSimulation.GET_REFINEMENT_SET;
    static protected final int GET_REFINEMENT_ITER = DefaultSimulation.GET_REFINEMENT_ITER;
    static protected final int FIND = reporter.newMethod("find()");
}