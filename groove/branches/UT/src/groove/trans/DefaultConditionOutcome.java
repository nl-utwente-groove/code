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
 * $Id: DefaultConditionOutcome.java,v 1.1.1.2 2007-03-20 10:42:55 kastenberg Exp $
 */
package groove.trans;

import java.util.Map;

import groove.rel.VarMorphism;

/**
 * Default implementation of a {@link GraphTestOutcome} for {@link GraphCondition}s.
 * Specialises {@link AbstractTestOutcome}.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class DefaultConditionOutcome extends AbstractTestOutcome<Matching,GraphCondition> implements GraphConditionOutcome {
    /**
     * Constructs a graph condition outcome from a graph condition, subject,
     * and mapping from {@link Matching}s to {@link GraphTestOutcome}s.
     */
    public DefaultConditionOutcome(GraphCondition test, VarMorphism subject, Map<Matching,GraphPredicateOutcome> outcome) {
        super(test, subject, outcome);
    }

    /**
     * A graph condition is successful if the negated predicate is <i>not</i>
     * successful.
     */
    protected boolean isSuccessKey(GraphTestOutcome<GraphCondition,Matching> image) {
        return ! image.isSuccess();
    }
}