/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: ExistsCondition.java,v 1.5 2007-11-29 12:52:02 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.LabelStore;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExistsCondition extends PositiveCondition<ExistsMatch> {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    public ExistsCondition(RuleName name, Graph target, NodeEdgeMap patternMap,
            LabelStore labelStore, SystemProperties properties) {
        super(name, target, patternMap, labelStore, properties);
    }

    /**
     * Callback factory method to create a match on the basis of a mapping of
     * this condition's target.
     * @param matchMap the mapping, presumably of the elements of
     *        {@link #getTarget()} into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    @Override
    ExistsMatch createMatch(VarNodeEdgeMap matchMap) {
        return new ExistsMatch(matchMap);
    }

    @Override
    Iterator<ExistsMatch> computeMatchIter(final GraphShape host,
            Iterator<VarNodeEdgeMap> matchMapIter) {
        Iterator<ExistsMatch> result = null;
        while (result == null && matchMapIter.hasNext()) {
            ExistsMatch match = getMatch(host, matchMapIter.next());
            if (match != null) {
                result = Collections.singleton(match).iterator();
            }
        }
        if (result == null) {
            result = Collections.<ExistsMatch>emptySet().iterator();
        }
        return result;
    }

    /**
     * Returns a match on the basis of a mapping of this condition's target to a
     * given graph. The mapping is checked for matches of the sub-conditions; if
     * this fails, the method returns <code>null</code>. TODO this is not
     * correct if a sub-condition has more than one match
     * @param host the graph that is being matched
     * @param matchMap the mapping, which should go from the elements of
     *        {@link #getTarget()} into <code>host</code>
     * @return a match constructed on the basis of <code>matchMap</code>, or
     *         <code>null</code> if no match exists
     */
    private ExistsMatch getMatch(GraphShape host, VarNodeEdgeMap matchMap) {
        ExistsMatch result = createMatch(matchMap);
        for (AbstractCondition<?> condition : getComplexSubConditions()) {
            Iterator<? extends Match> subMatchIter =
                condition.getMatchIter(host, matchMap);
            if (subMatchIter.hasNext()) {
                result.addSubMatch(subMatchIter.next());
                // TODO remove check below as soon as method is generalised to
                // sub-conditions with > 1 match
                assert !subMatchIter.hasNext();
            } else {
                result = null;
                break;
            }
        }
        return result;
    }
}