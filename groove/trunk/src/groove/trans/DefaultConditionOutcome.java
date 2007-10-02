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
 * $Id: DefaultConditionOutcome.java,v 1.3 2007-10-02 23:06:20 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.NodeEdgeMap;

import java.util.Map;

/**
 * Default implementation of a {@link GraphTestOutcome} for {@link GraphCondition}s.
 * Specialises {@link AbstractTestOutcome}.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class DefaultConditionOutcome extends AbstractTestOutcome<Match,GraphCondition> implements GraphConditionOutcome {
    /**
     * Constructs a graph condition outcome from a graph condition, subject,
     * and mapping from {@link Matching}s to {@link GraphTestOutcome}s.
     */
    public DefaultConditionOutcome(GraphCondition test, Graph host, NodeEdgeMap elementMap, Map<Match,GraphPredicateOutcome> outcome) {
        super(test, host, elementMap, outcome);
    }

    /**
     * A graph condition is successful if the negated predicate is <i>not</i>
     * successful.
     */
    @Override
    protected boolean isSuccessKey(GraphTestOutcome<GraphCondition,Match> image) {
        return ! image.isSuccess();
    }
}