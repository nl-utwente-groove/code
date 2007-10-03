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
 * $Id: ExistsCondition.java,v 1.1 2007-10-03 23:10:53 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

/**
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class ExistsCondition extends PositiveCondition<ExistsMatch> {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    protected ExistsCondition(Graph target, NodeEdgeMap patternMap, NameLabel name, SystemProperties properties) {
        super(target, patternMap, name, properties);
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given pattern target.
     * and initially empty nested predicate.
     * The name may be <code>null</code>.
     */
    protected ExistsCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target, name, properties);
    }

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably of the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
	@Override
    protected ExistsMatch createMatch(VarNodeEdgeMap matchMap) {
        return new ExistsMatch(matchMap);
    }
}