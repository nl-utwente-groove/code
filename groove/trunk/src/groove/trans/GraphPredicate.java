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
 * $Id: GraphPredicate.java,v 1.4 2007-08-22 15:04:48 rensink Exp $
 */
package groove.trans;

import groove.rel.VarMorphism;

import java.util.Set;

/**
 * Interface for predicates over graphs.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public interface GraphPredicate extends GraphTest {    
    /**
     * Adds a graph condition to this predicate.
     * The condition is added to the set returned by {@link #getConditions()}.
     * It is required that the condition context equals this predicate's context;
     * an <code>IllegalArgumentException</code> is thrown otherwise.
     * May only be invoked as long as the predicate is not fixed.
     * @param cond the graph condition to be added
     * @throws IllegalArgumentException if <code>cond.getContext() != getContext()</code>
     * @throws IllegalStateException if <code>isFixed()</code>
     */
    public void setOr(GraphTest cond);
    
    /**
     * Returns the set of graph conditions making up this predicate.
     * The predicate is considered to hold (for a given morphism) if at least <i>one</i>
     * of its coonditions holds.
     */
    public Set<? extends GraphCondition> getConditions();
    
    /**
     * Specialises the return type.
     */
    public GraphPredicateOutcome getOutcome(VarMorphism subject);
}