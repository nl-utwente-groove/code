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
 * $Id: State.java,v 1.1.1.1 2007-03-20 10:05:25 kastenberg Exp $
 */
package groove.lts;

import groove.graph.Node;

/**
 * Interface of a state of a labelled transition system.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:25 $
 */
public interface State extends Node {
    // ----------------------- OBJECT OVERRIDES ---------------------

    /**
     * Returns a clone of this state.
     * @return a clone of this state
     * @ensure <tt>result != null</tt>
     * @deprecated use clone() instead
     */
    //public State cloneState();
    
    /** Overrides the {@link Object#clone()} method so as to return a {@link State}. */
    //public State clone();
    
    /**
     * Returns a new, empty state prototyped on this one.
     */
	@Deprecated
    public State newState();
    
    /**
     * Tests if this state is fully explored, i.e., all outgoing transitions have been generated.
     */
    public boolean isClosed();
}