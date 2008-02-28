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
 * $Id: DefaultMarking.java,v 1.2 2008-02-28 05:59:23 kastenberg Exp $
 */
package groove.verify;

import groove.lts.GraphState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class keeps track of the properties satisfied for a particular state. A state
 * will thus be mapped on a set of properties it satisfies.
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2008-02-28 05:59:23 $
 */
public class DefaultMarking extends HashMap<GraphState, Set<TemporalFormula>> implements Marking {

    /* (non-Javadoc)
     * @see groove.verify.Marking#set(groove.lts.State, groove.trans.GraphPredicate)
     */
    public boolean set(GraphState state, TemporalFormula property, boolean value) {
        if (value) {
            return setTrue(state, property);
        }
        else {
            return setFalse(state, property);
        }
    }

    /* (non-Javadoc)
     * @see groove.verify.Marking#satisfies(groove.lts.State, groove.trans.GraphPredicate)
     */
    public boolean satisfies(GraphState state, TemporalFormula property) {
        if (!containsKey(state)) {
            return false;
        }
        else {
            Set<TemporalFormula> predicates = get(state);
            return predicates.contains(property);
        }
    }

    /**
     * Adds the given CTL-expression to the set of propositions fullfilled
     * of the given state.
     * @param state the state to which the given CTL-expression applies
     * @param property the CTL-expression to be added to the set of
     * propositions met by the given state
     * @return <tt>true</tt> if the given state did not yet fulfill the given
     * CTL-expression, <tt>false</tt> otherwise
     */
    protected boolean setTrue(GraphState state, TemporalFormula property) {
        boolean added = false;
        // if this marking does not yet contain an entry for this state
        // create one and add the give predicate as the only marking so far
        if (!containsKey(state)) {
            Set<TemporalFormula> predicates = new HashSet<TemporalFormula>();
            added = predicates.add(property);
            put(state, predicates);
        }
        // add this predicate to the marking of this state
        else {
            Set<TemporalFormula> currentPredicates = get(state);
            added = currentPredicates.add(property);
            put(state, currentPredicates);
        }
        return added;
    }

    /**
     * Removes the given CTL-expression from the set of propositions
     * fulfilled by the given state.
     * @param state the state which is said not to fulfill the given CTL-expression
     * @param property the CTL-expression to be removed from the set of
     * propositions fulfilled by the given state
     * @return <tt>true</tt> if the CTL-expression could be removed from the
     * set of propositions fulfilled by the given state, <tt>false</tt> otherwise
     */
    protected boolean setFalse(GraphState state, TemporalFormula property) {
        boolean removed = false;
        if (containsKey(state)) {
            Set<TemporalFormula> predicates = get(state);
            if (predicates.contains(property)) {
                removed = predicates.remove(property);
            }
        }
        return removed;
    }
}
