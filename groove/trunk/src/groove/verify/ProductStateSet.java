/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.verify;

import groove.explore.result.CycleAcceptor;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.util.collect.TreeHashSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores the set of product states encountered during a model checking exploration.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ProductStateSet {
    /**
     * Adds a product state to this set. If the state is already in the set,
     * the existing object is returned. If it
     * is a new state, this method returns <code>null</code>.
     * @param newState the state to be added
     * @return the existing state if it is already in the gts,
     *         <code>null</code> otherwise
     */
    public ProductState addState(ProductState newState) {
        // test if this is a known state
        ProductState result = this.stateSet.put(newState);
        // new states are first considered open
        if (result == null) {
            // openStates.put(newState);
            fireAddState(newState);
        }
        return result;
    }

    /**
     * Closes a Buchi graph-state. Currently, listeners are always notified,
     * even when the state was already closed.
     * @param state the state to be closed.
     */
    public void setClosed(ProductState state) {
        if (state.setClosed()) {
            this.closedCount++;
        }
        // always notify listeners of state-closing
        // even if the state was already closed
        fireCloseState(state);
    }

    /**
     * Adds a listener to the ProductGTS.
     * @param listener the listener to be added.
     */
    public void addListener(ProductListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener from the ProductGTS
     * @param listener the listener to be removed.
     */
    public void removeListener(ProductListener listener) {
        assert (this.listeners.contains(listener)) : "Listener cannot be removed since it is not registered.";
        this.listeners.remove(listener);
    }

    /**
     * Notifies the listeners of the event of closing a state.
     * @param state the state that has been closed.
     */
    private void fireCloseState(ProductState state) {
        for (ProductListener listener : this.listeners) {
            if (listener instanceof CycleAcceptor) {
                listener.closeUpdate(this, state);
            }
        }
    }

    /**
     * Calls {@link GTSListener#addUpdate(GTS, GraphState)} on all
     * GraphListeners in listeners.
     * @param state the node being added
     */
    private void fireAddState(ProductState state) {
        for (ProductListener listener : this.listeners) {
            listener.addUpdate(this, state);
        }
    }

    /**
     * Indicates if the ProductGTS currently has open states. Equivalent to (but
     * more efficient than) <code>getOpenStateIter().hasNext()</code> or
     * <code>!getOpenStates().isEmpty()</code>.
     * @return <code>true</code> if the ProductGTS currently has open states
     */
    public boolean hasOpenStates() {
        int openStateCount = openStateCount();
        return openStateCount > 0;
    }

    /** Returns the number of not fully expored states. */
    public int openStateCount() {
        return stateCount() - this.closedCount;
    }

    /** Returns the number of product states. */
    public int stateCount() {
        return this.stateSet.size();
    }

    private final TreeHashSet<ProductState> stateSet = new TreeHashStateSet();
    private int closedCount = 0;

    private final Set<ProductListener> listeners =
        new HashSet<>();

    /** Specialised set implementation for storing product states. */
    private class TreeHashStateSet extends TreeHashSet<ProductState> {
        // empty
    }
}
