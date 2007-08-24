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

import groove.graph.Graph;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.VarNodeEdgeMap;

/**
 * Abstract implementation of a searh item, offering some basic search functionality.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
abstract public class AbstractSearchItem implements SearchItem {
    /**
     * Abstract implementation of a search item record, offering basic search functionality.
     * At any point, the record has a state which is {@link #EMPTY} (if the search
     * has not yielded a solution), #READY or {@link #FOUND} (if the search has yielded a solution).
     * @author Arend Rensink
     * @version $Revision: 1.1 $
     */
    abstract public class AbstractRecord implements Record {
        /** Constructs a record for a given search. */
        protected AbstractRecord(Search search) {
            this.search = search;
        }
        
        /**
         * The state is required to be {@link #EMPTY} or {@link #FOUND} upon invocation.
         * The method first invokes {@link #init()} or {@link #undo()}, depending on the state,
         * followed by {@link #next()} to actually search for a solution.
         * If no solution is found, the state of the record is returned to {@link #EMPTY},
         * meaning that the next invocation of {@link #find()} will restart the search.
         * If a solution is found, the state will be set to {@link #FOUND}.
         */
        final public boolean find() {
            boolean result;
            assert isEmpty() || isFound();
            if (isEmpty()) {
                init();
            } else {
                undo();
            }
            setState(READY);
            result = next();
            if (result) {
                setState(FOUND);
            } else {
                exit();
                setState(EMPTY);
            }
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
                exit();
            } else if (isReady()) {
                exit();
            }
            setState(EMPTY);
        }

        /**
         * Sets the state to a given value.
         * The value is required to be one of {@link #EMPTY}, {@link #READY} or {@link #FOUND}.
         */
        final void setState(int value) {
            state = value;
        }
        
        /** 
         * Returns the current state of the record, which is one of
         * {@link #EMPTY} (the initial state, to which the record returns upon an
         * unsuccessful search), {@link #READY} (if all preparations have been made
         * for the next search, but no selection is active) or {@link #FOUND} 
         * (if a search has just been successful). 
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
         * Indicates that the state of the record is {@link #READY}.
         * Convenience method for <code>getState() == READY</code>.
         * @return <code>true</code> if {@link #getState()} returns {@link #READY}.
         */
        final boolean isReady() {
            return getState() == READY;
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
         * It is guaranteed that the state is {@link #READY} upon invocation;
         * after return, the state is changed to {@link #EMPTY}.
         */ 
        abstract void exit();

        /** 
         * Callback method from {@link #find()} to prepare the search.
         * It is guaranteed that the state is {@link #EMPTY} upon invocation;
         * after return, the state is changed to {@link #READY}.
         */ 
        abstract void init();
        
        /** 
         * Callback method from {@link #find()} to undo the previously found solution.
         * It is guaranteed that the state is {@link #FOUND} upon invocation;
         * after return, the state is changed to {@link #READY}.
         */ 
        abstract void undo();
        
        /** 
         * Callback method from {@link #find()} to search for and select a next solution.
         * It is guaranteed that the state is {@link #READY} upon invocation;
         * after return, the state is changed to {@link #EMPTY} or {@link #FOUND},
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
        
        /** The state of the record: one of {@link #EMPTY}, {@link #READY} or {@link #FOUND}. */
        private int state;
        /** The underlying search for this record. */
        private final Search search;
        
        /** 
         * Initial state of the record, which is revisited every time {@link #find()}
         * returns <code>false</code>.
         */
        static final int EMPTY = 0;
        /**
         * State of the record after an invocation of #init() or #undo().
         */
        static final int READY = 1;
        /** 
         * State of the recordreached when {@link #find()} returns <code>true</code>,
         * signifying that the search item has been satisfied.
         */
        static final int FOUND = 2;
    }

    abstract public AbstractRecord getRecord(Search search);
}
