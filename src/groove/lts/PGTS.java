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
 * $Id$
 */
package groove.lts;

import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.trans.GraphGrammar;
import groove.util.TreeHashSet;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;

public class PGTS extends GTS {

    public PGTS(GraphGrammar grammar) {
        super(grammar);
        // TODO Auto-generated constructor stub
    }
    
    public void setClosed(State state) {
        if (((GraphState) state).setClosed()) {
            openStates.add((BuchiGraphState) state);
            incClosedCount();
        }
        // always notify listeners of state-closing
        // even if the state was already closed
        notifyLTSListenersOfClose(state);
    }

    @Override
    protected TreeHashSet<GraphState> createStateSet() {
        // TODO Auto-generated method stub
        return super.createStateSet();
    }

    private TreeHashSet<GraphState> openStates = new TreeHashStateSet();
    
    /** Specialised set implementation for storing states. */
    protected class TreeHashStateSet extends GTS.TreeHashStateSet {
        /** Tests if the Büchi locations are different (in addition to the super test). */
        @Override
        protected boolean isDistinctLocations(GraphState stateKey, GraphState otherStateKey) {
            if (super.isDistinctLocations(stateKey, otherStateKey)) {
                return true;
            } else {
                BuchiLocation location = ((BuchiGraphState) stateKey).getBuchiLocation();
                BuchiLocation otherLocation = ((BuchiGraphState) otherStateKey).getBuchiLocation();
                return location != null && !location.equals(otherLocation);
            }
        }

        /**
         * Returns the hash code of the state, modified by the control location (if any).
         */
        @Override
        protected int getCode(GraphState stateKey) {
            return super.getCode(stateKey) + ((BuchiGraphState) stateKey).getLocation().hashCode();
        }
        
        /** The isomorphism checker of the state set. */
        private IsoChecker checker = DefaultIsoChecker.getInstance();
    }
}
