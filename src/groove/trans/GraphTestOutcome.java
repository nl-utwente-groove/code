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
 * $Id: GraphTestOutcome.java,v 1.5 2008-01-30 09:32:39 iovka Exp $
 */
package groove.trans;

import java.util.Map;
import java.util.Set;

import groove.graph.Morphism;

/**
 * Auxiliary class indicating the outcome of a graph test applied to a subject morphism.
 * The outcome is a map: if the test in question was a {@link GraphPredicate},
 * the outcome maps the {@link GraphCondition}s of the predicate to 
 * {@link GraphTestOutcome}s for those conditions,
 * whereas if the test was a {@link GraphCondition}, the outcome maps {@link Matching}s 
 * for the condition pattern to {@link GraphTestOutcome}s for the negative predicate. 
 * The {@link GraphTestOutcome} also identifies a subset of the keys that stand for
 * <i>success</i> of the test.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
@Deprecated
public interface GraphTestOutcome<Mine,Nested> extends Map<Mine,GraphTestOutcome<Nested,Mine>> {
    /** 
     * Returns the morphism that has been tested, and of which this
     * outcome reports success or failure.
     */
    Morphism getSubject();
    
    /**
     * Returns the graph test of which this is the outcome.
     */
    @Deprecated
    GraphTest getTest();
    
    /**
     * Returns the set of keys for which this outcome reports success.
     * The keys of the map that are not in this set only report failure
     * of the test.
     */
    Set<Mine> getSuccessKeys();
    
    /**
     * Flag to indicate if the test outcome includes success.
     * Convenience methods for <code>!successKeys().isEmpty()</code>.
     */
    boolean isSuccess();
    
    /**
     * Indicates if the test upon which this outcome reports is a condition.
     * @return <code>true</code> if the test is a {@link GraphCondition};
     * <code>false</code> if it is a {@link GraphPredicate}.
     */
    boolean isCondition();
}