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
 * $Id $
 */

package groove.match;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Abstract implementation of a search item, offering some basic search functionality.
 * @author Arend Rensink
 * @version $Revision: 1.11 $
 */
abstract public class AbstractSearchItem implements SearchItem {
    /**
     * This implementation returns the empty set.
     */
    public Collection<? extends Node> bindsNodes() {
        return Collections.emptySet();
    }

    /**
     * This implementation returns the empty set.
     */
    public Collection<String> bindsVars() {
        return Collections.emptySet();
    }

    /**
     * This implementation returns the empty set.
     */
    public Collection<? extends Edge> bindsEdges() {
        return Collections.emptySet();
    }

    /**
     * This implementation returns the empty set.
     */
    public Collection<Node> needsNodes() {
        return Collections.emptySet();
    }

    /**
     * This implementation returns the empty set.
     */
    public Collection<String> needsVars() {
        return Collections.emptySet();
    }

    /**
     * This implementation compares items on the basis of their class names,
     * and after that, on the basis of their ratings.
     * A lower rating means a "smaller" search item.
     */
    public int compareTo(SearchItem other) {
        int result = getClass().getName().compareTo(other.getClass().getName());
        if (result == 0) {
            result = getRating() - getRating(other);
        }
        return result;
    }
    
    /** 
     * Returns the rating of a search item, for the purpose of {@link #compareTo(SearchItem)}.
     * This is obtained by {@link #getRating()} if the item is an {@link AbstractSearchItem};
     * otherwise, it is derived from the item's class name.
     */
    private int getRating(SearchItem other) {
        if (other instanceof AbstractSearchItem) {
            return ((AbstractSearchItem) other).getRating();
        } else {
            return 0;
        }
    }
    
    /** 
     * Returns a rating for this search item, for the purpose of its natural ordering. 
     */
    abstract int getRating();
    
    /** Creates a new dummy search record. */
    final Record createDummyRecord() {
    	return new DummyRecord();
    }

    /** 
     * Dummy search record, which does nothing upon {@link #find()}
     * except alternatingly return <code>true</code> and <code>false</code>.
     */
    final class DummyRecord implements Record {
    	/** 
    	 * This record alternates between <code>true</code> and <code>false</code>,
    	 * and resets to <code>true</code> upon invocation of {@link #reset()}.
    	 */
		public boolean find() {
			found = !found;
			return found;
		}

		/** This implementation returns <code>true</code>. */
		public boolean isSingular() {
			return true;
		}

		public void reset() {
			found = false;
		}
    	
		@Override
		public String toString() {
			return String.format("%s: %s", AbstractSearchItem.this.toString(), found);
		}

		/** 
		 * Flag indicating if the last call of #find returned <code>true</code>,
		 * and hence the next should return <code>false</code>.
		 */
    	private boolean found;
    }
    
    /** 
     * Search item record offering basic functionality 
     * for querying the underlying search and target.
     */
    abstract class BasicRecord implements Record {
        /** Constructs a record for a given search. */
        BasicRecord(Search search) {
            this.search = search;
            this.host = search.getHost();
        }
//
//        /** Returns the search associated with this record. */
//        final Search getSearch() {
//            return search;
//        }
//        
//        /** 
//         * Returns the (partial) result of the search associated with this record.
//         * Convenience method for <code>getSearch().getResult()</code>.
//         */
//        final Graph getTarget() {
//            return target;
//        }
        
        /** The underlying search for this record. */
        final Search search;
        /** The underlying search for this record. */
        final Graph host;
    }

    /**
     * Record type for a search item known to yield at most one solution.
     * @author Arend Rensink
     * @version $Revision: 1.11 $
     */
    abstract class SingularRecord extends BasicRecord {
        /** Constructs an instance for a given search. */
        SingularRecord(Search search) {
            super(search);
        }
        
        /** 
         * Calls {@link #reset()} and returns <code>false</code> if {@link #find()} was
         * successful at the last call; otherwise, delegates to {@link #set()}.
         */
        final public boolean find() {
            SearchPlanStrategy.reporter.start(SearchPlanStrategy.RECORD_FIND_SINGULAR);
            if (found) {
                reset();
            } else {
                found = set();
            }
            SearchPlanStrategy.reporter.stop();
            return found;
        }

        /**
         * Always returns <code>true</code>.
         */
        final public boolean isSingular() {
            return true;
        }

        /**
         * Sets {@link #found} to <code>false</code>.
         */
        public void reset() {
            found = false;
        }
        
        /**
         * Tries to set the unique solution in the target map.
         * @return <code>true</code> if setting the solution was successful.
         */
        abstract boolean set();
        
        /** Returns the return value of the last invocation of {@link #find()}. */
        final boolean isFound() {
            return found;
        }
        
        /** Flag storing the last return value of {@link #find()}. */
        private boolean found;
    }
    /**
     * Abstract implementation of a search item record expected to
     * have more than one solution.
     * @author Arend Rensink
     * @version $Revision: 1.11 $
     */
    abstract class MultipleRecord<E extends Element> extends BasicRecord {
        /** Constructs a record for a given search. */
        MultipleRecord(Search search) {
            super(search);
        }
        
        /** This implementation returns <code>false</code>. */
		public boolean isSingular() {
			return false;
		}

		/**
         * If {@link #imageIter} is not initialised, first invokes {@link #init()}.
         * Then iterates over the images of {@link #imageIter} until one is
         * found for which {@link #setImage(Element)} is satisfied. Calls {@link #reset()}
         * if no such image is found.
         */
        final public boolean find() {
            SearchPlanStrategy.reporter.start(SearchPlanStrategy.RECORD_FIND_MULTIPLE);
            if (imageIter == null) {
                init();
            }
            boolean result = false;
            while (!result && imageIter.hasNext()) {
            	E image = imageIter.next();
            	result = setImage(image);
            }
            if (!result) {
            	reset();
            }
            SearchPlanStrategy.reporter.stop();
            return result;
        }
        
        public void reset() {
        	imageIter = null;
        }
        
		/**
		 * Callback method from {@link #find()} to initialise the variables
		 * necessary for searching; in any case {@link #imageIter}.
		 */
		abstract void init();

		/** 
		 * Callback method from {@link #find()} to install an image.
		 * This method is expected to call other methods of the underlying search
		 * to store images of nodes and edges.
		 * The return value indicates is this has been successful.
		 */
		abstract boolean setImage(E image);
		
		/**
		 * An iterator over the images for the item's edge.
		 */
        Iterator<? extends E> imageIter;
    }    
}
