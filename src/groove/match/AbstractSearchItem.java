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

import java.util.Collection;
import java.util.Collections;

import groove.graph.Graph;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.VarNodeEdgeMap;

/**
 * Abstract implementation of a searh item, offering some basic search functionality.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
abstract public class AbstractSearchItem implements SearchItem {
    abstract public AbstractRecord getRecord(Search search);

    /**
     * This implementation returns the empty set.
     */
    public Collection<Node> bindsNodes() {
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

    /**
     * Abstract implementation of a search item record, offering basic search functionality.
     * At any point, the record has a state which is {@link #EMPTY} (if the search
     * has not yielded a solution), #READY or {@link #FOUND} (if the search has yielded a solution).
     * @author Arend Rensink
     * @version $Revision: 1.4 $
     */
    abstract public class AbstractRecord implements Record {
        /** Constructs a record for a given search. */
        protected AbstractRecord(Search search) {
            this.search = search;
        }
        
        /**
         * The method first invokes {@link #init()} or {@link #undo()}, if the state upon
         * invocation is {@link #EMPTY} or {@link #FOUND}, respectively,
         * followed by {@link #next()} to actually search for a solution.
         * If no solution is found, the state of the record is returned to {@link #EMPTY},
         * meaning that the next invocation of {@link #find()} will restart the search.
         * If a solution is found, the state will be set to {@link #FOUND}.
         */
        final public boolean find() {
            SearchPlanStrategy.reporter.start(SearchPlanStrategy.RECORD_FIND);
            boolean result;
            assert isEmpty() || isFound();
            if (isEmpty()) {
                init();
                setState(FIRST);
            } else if (isFound()) {
                undo();
                setState(LATER);
            }
            result = next();
            if (result) {
                setState(FOUND);
            } else {
                exit();
                setState(EMPTY);
            }
            SearchPlanStrategy.reporter.stop();
            return result;
        }
        
        /**
         * This implementation always turns the state to {@link #EMPTY}, irregardless of
         * the state at invocation time, by calling {@link #undo()} and {@link #exit()} if
         * necessary.
         */
        final public void reset() {
            if (isFound()) {
                undo();
                setState(LATER);
                exit();
            } else if (isReady()) {
                exit();
            }
            setState(EMPTY);
        }

        /**
         * Sets the state to a given value.
         * The value is required to be one of {@link #EMPTY}, {@link #FIRST}, {@link #LATER} or {@link #FOUND}.
         */
        final void setState(int value) {
            state = value;
            assert isEmpty() || isReady() || isFound();
        }
        
        /** 
         * Returns the current state of the record, which is one of
         * <ul>
         * <li> {@link #EMPTY}, the initial state, to which the record returns upon an unsuccessful search;
         * <li> {@link #FIRST}, if all preparations have been made for the first search, but 
         * {@link #next()} has not yet been called;
         * <li> {@link #LATER}, if {@link #next()} has returned <code>false</code> or {@link #undo()} has
         * just been invoked; or 
         * <li> {@link #FOUND}, if a search has just been successful. 
         * </ul>
         */
        final int getState() {
            return state;
        }
        
        /** 
         * Indicates that the state of the record is {@link #EMPTY}.
         * Convenience method for <code>getState() == EMPTY</code>.
         * @return <code>true</code> if {@link #getState()} returns {@link #EMPTY}.
         */
        final boolean isEmpty() {
            return getState() == EMPTY;
        }

        /** 
         * Indicates that the state of the record is {@link #FIRST} or {@link #LATER}.
         * Convenience method for <code>isFirst() || isLater()</code>.
         * @return <code>true</code> if {@link #getState()} returns {@link #FIRST} or {@link #LATER}.
         */
        final boolean isReady() {
            return isFirst() || isLater();
        }

        /** 
         * Indicates that the state of the record is {@link #FIRST}.
         * Convenience method for <code>getState() == FIRST</code>.
         * @return <code>true</code> if {@link #getState()} returns {@link #FIRST}.
         */
        final boolean isFirst() {
            return getState() == FIRST;
        }

        /** 
         * Indicates that the state of the record is {@link #LATER}.
         * Convenience method for <code>getState() == LATER</code>.
         * @return <code>true</code> if {@link #getState()} returns {@link #LATER}.
         */
        final boolean isLater() {
            return getState() == LATER;
        }

        /** 
         * Indicates that the state of the record is {@link #FOUND}.
         * Convenience method for <code>getState() == FOUND</code>.
         * @return <code>true</code> if {@link #getState()} returns {@link #FOUND}.
         */
        final boolean isFound() {
            return getState() == FOUND;
        }

        /** 
         * Callback method from {@link #reset()} to undo the effects of {@link #init()}.
         * It is guaranteed that the state is {@link #FIRST} or {@link #LATER} upon invocation;
         * after return, the state is changed to {@link #EMPTY}.
         * This implementation does nothing.
         */ 
        void exit() {
            // empty
        }

        /** 
         * Callback method from {@link #find()} to prepare the search.
         * It is guaranteed that the state is {@link #EMPTY} upon invocation;
         * after return, the state is changed to {@link #FIRST}.
         * This implementation does nothing.
         */ 
        void init() {
            // empty
        }
        
        /** 
         * Callback method from {@link #find()} to undo the previously found solution.
         * It is guaranteed that the state is {@link #FOUND} upon invocation;
         * after return, the state is changed to {@link #LATER}.
         */ 
        abstract void undo();
        
        /** 
         * Callback method from {@link #find()} to search for and select a next solution.
         * It is guaranteed that the state is {@link #FIRST} or {@link #LATER} upon invocation;
         * after return, the state is changed to {@link #LATER} or {@link #FOUND},
         * depending on the return value.
         */ 
        abstract boolean next();

        /** Returns the search associated with this record. */
        final Search getSearch() {
            return search;
        }
        
        /** 
         * Returns the (partial) result of the search associated with this record.
         * Convenience method for <code>getSearch().getResult()</code>.
         */
        final VarNodeEdgeMap getResult() {
            return search.getResult();
        }

        /** 
         * Returns the (partial) result of the search associated with this record.
         * Convenience method for <code>getSearch().getResult()</code>.
         */
        final Graph getTarget() {
            return search.getTarget();
        }

        /** 
         * Indicates if a given node is available as image in the current search.
         * This may fail to be the case due to availability constraints.
         * Convenience method for <code>getSearch().isAvailable(node)</code>.
         */
        final boolean isAvailable(Node node) {
            return search.isAvailable(node);
        }
        
        /** The state of the record: one of {@link #EMPTY}, {@link #FIRST}, {@link #LATER} or {@link #FOUND}. */
        private int state;
        /** The underlying search for this record. */
        private final Search search;
        
        /** 
         * Initial state of the record, which is revisited every time {@link #find()}
         * returns <code>false</code>.
         */
        static final int EMPTY = 0;
        /**
         * State of the record after an invocation of #init().
         */
        static final int FIRST = 1;
        /**
         * State of the record after an invocation of #undo().
         */
        static final int LATER = 2;
        /** 
         * State of the recordreached when {@link #find()} returns <code>true</code>,
         * signifying that the search item has been satisfied.
         */
        static final int FOUND = 3;
    }
}
