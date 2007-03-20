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
 * $Id: DefaultPredicateOutcome.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

import groove.rel.VarMorphism;

import java.util.Map;

/**
 * Default implementation of a {@link GraphTestOutcome} for {@link GraphPredicate}s.
 * Specialises {@link AbstractTestOutcome}.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class DefaultPredicateOutcome extends AbstractTestOutcome<GraphCondition,Matching> implements GraphPredicateOutcome {
    /**
     * Constructs a predicate outcome from a given predicate, subject,
     * and mapping from {@link GraphCondition}s to {@link GraphTestOutcome}s.
     */
    public DefaultPredicateOutcome(GraphPredicate test, VarMorphism subject, Map<GraphCondition, GraphConditionOutcome> outcome) {
        super(test, subject, outcome);
    }

    /**
     * A predicate is successful if one if its conditions is successful.
     */
    protected boolean isSuccessKey(GraphTestOutcome<Matching,GraphCondition> image) {
        return image.isSuccess();
    }
}