/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.pattern.explore.strategy;

import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PGTSAdapter;
import groove.abstraction.pattern.lts.PatternState;
import groove.explore.strategy.ClosingStrategy;

/**
 * Strategy that closes every state it explores, and adds the newly generated
 * states to a pool, together with information regarding the outgoing
 * transitions of its parent. Subclasses must decide on the order of the pool;
 * e.g., breadth-first or depth-first.
 * 
 * See {@link ClosingStrategy} 
 */
public abstract class ClosingPatternStrategy extends AbstractPatternStrategy {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void prepare(PGTS pgts) {
        super.prepare(pgts);
        getPGTS().addLTSListener(this.exploreListener);
        clearPool();
    }

    @Override
    protected PatternState getNextState() {
        PatternState result = getFromPool();
        if (result == null) {
            getPGTS().removeLTSListener(this.exploreListener);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Callback method to add a pool element to the pool. */
    abstract protected void putInPool(PatternState element);

    /** Returns the next element from the pool of explorable states. */
    abstract protected PatternState getFromPool();

    /** Clears the pool, in order to prepare the strategy for reuse. */
    abstract protected void clearPool();

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends PGTSAdapter {
        @Override
        public void addUpdate(PGTS gts, PatternState state) {
            if (!state.isClosed()) {
                putInPool(state);
            }
        }
    }

}
