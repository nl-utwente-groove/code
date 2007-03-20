/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: Marking.java,v 1.1.1.2 2007-03-20 10:43:00 kastenberg Exp $
 */
package groove.verify;

import groove.lts.GraphState;

/**
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:43:00 $
 * 
 * Class description.
 */
public interface Marking {

    /**
     * Mark that the given state satisfies the given predicate.
     * 
     * @param state the state satisfying the given predicate
     * @param property the propery to be set forthe given state.
     * @param value the new value for the given state and the given expression
     * @return <tt>true</tt> if nothing unexpected happens, <tt>false</tt> otherwise
     */
    public boolean set(GraphState state, TemporalFormula property, boolean value);

    /**
     * Check wether the given state satisfies the given predicate.
     * 
     * @param state the state to be checked for satisfying a particular predicate
     * @param property the property to be checked
     * @return <tt>true</tt> if the gives state satisfies the predicate, <tt>false</tt> otherwise
     */
    public boolean satisfies(GraphState state, TemporalFormula property);
}
