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
 * $Id: UniversalCondition.java,v 1.1 2007-10-03 16:08:40 rensink Exp $
 */
package groove.trans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class UniversalCondition extends AbstractCondition {
    /**
     * @param pattern
     * @param name
     * @param properties
     */
    public UniversalCondition(Morphism pattern, NameLabel name, SystemProperties properties) {
        super(pattern, name, properties);
    }

    /**
     * @param target
     * @param name
     * @param properties
     */
    public UniversalCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target, name, properties);
    }

    public Iterable< ? extends Match> getMatches(Graph host, NodeEdgeMap patternMap) {
        Collection<Match> result = new ArrayList<Match>();
        List<Iterable<? extends Match>> subMatches = new ArrayList<Iterable<? extends Match>>();
        int subConditionCount = getSubConditions().size();
        List<Match> matchSet = new ArrayList<Match>();
        for (int i = 0; i < subConditionCount; i++) {
            subMatches.add(new ArrayList<Match>());
            matchSet.add(null);
        }
        Iterator<VarNodeEdgeMap> matchMapIter = getMatchMapIter(host, patternMap);
        while (matchMapIter.hasNext()) {
            VarNodeEdgeMap matchMap = matchMapIter.next();
            for (AbstractCondition condition: getSubConditions()) {
                subMatches.add(condition.getMatches(host, matchMap));
            }
            Stack<Iterator<? extends Match>> subMatchIters = new Stack<Iterator<? extends Match>>();
            int i = 0;
            do {
                while (i >= 0 && i < subConditionCount) {
                    Iterator< ? extends Match> subMatchIter;
                    if (subMatchIters.size() <= i) {
                        subMatchIters.push(subMatchIter = subMatches.get(i).iterator());
                    } else {
                        subMatchIter = subMatchIters.peek();
                    }
                    if (subMatchIter.hasNext()) {
                        matchSet.set(i, subMatchIters.get(i).next());
                        i++;
                    } else {
                        subMatchIters.pop();
                        i--;
                    }
                }
                result.add(createMatch(matchSet));
            } while (i >= 0);
        }
        return result;
    }

    /** Creates a composite match on the basis if a set of matches of sub-conditions. */
    protected CompositeMatch createMatch(Collection<Match> subMatches) {
        CompositeMatch result = new CompositeMatch();
        for (Match match: subMatches) {
            result.addMatch(match);
        }
        return result;
    }
}
