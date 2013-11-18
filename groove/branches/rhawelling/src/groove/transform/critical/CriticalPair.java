/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.transform.critical;

import groove.grammar.Rule;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.type.TypeNode;
import groove.transform.RuleApplication;

import java.util.HashSet;
import java.util.Set;

public class CriticalPair {

    private CriticalPair(RuleApplication app1, RuleApplication app2) {
        this.app1 = app1;
        this.app2 = app2;
    }

    public static Set<CriticalPair> computeCriticalPairs(Rule r1, Rule r2) {
        Set<CriticalPair> result = new HashSet<CriticalPair>();
        RuleGraph l1 = r1.lhs();
        RuleGraph l2 = r2.lhs();

        //TODO ensure that l1 and l2 are disjoint

        //Form the set of all Nodes for l1 and l2, we do this by taking the union of these sets
        //(create a copy of l1.nodeSet(), and all all elements of l2.nodeSet())
        Set<RuleNode> allNodes = new HashSet<RuleNode>(l1.nodeSet());
        allNodes.addAll(l2.nodeSet());

        //The set of all possible overlaps, every element is a possible overlap
        //Every overlap is a set of sets, each element of an overlap is a nonempty set (combination) of rulenodes
        Set<Set<Combination>> overlapSet = formAllOverlaps(allNodes);

        //TODO for all combinations in curPairs create host graphs with matches to form a potential critical pairs

        //TODO check if the potential pairs are actually in conflict

        return result;
    }

    /**
     * Compute all possble overlappings of the given set of nodes
     * Every element (set of combinations) in the result is an overlap
     * @param nodes the nodes for which overlappings should be computed
     * @return All possble overlappings of the given set of nodes
     */
    private static Set<Set<Combination>> formAllOverlaps(Set<RuleNode> nodes) {
        Set<Set<Combination>> overlapSet = new HashSet<Set<Combination>>();
        for (RuleNode rnode : nodes) {
            Set<Set<Combination>> newOverlapSet =
                new HashSet<Set<Combination>>();
            //special case, the overlapSet may contain no overlappings
            if (overlapSet.isEmpty()) {
                Combination combination = new Combination(rnode);
                Set<Combination> overlap = new HashSet<Combination>();
                overlap.add(combination);
                newOverlapSet.add(overlap);
            } else {
                for (Set<Combination> overlap : overlapSet) {
                    //case 1: do not overlap rn1 with an existing element of the overlap
                    //This means we create a copy of overlap and add the set containing rn1 as a separate element
                    addToCombination(rnode, null, overlap, newOverlapSet);

                    //case 2: 
                    //Repeat the following for every combination in overlap:
                    //Add rn1 to the combination and add a copy of overlap with rn1 added to newOverlapSet
                    for (Combination combination : overlap) {
                        addToCombination(rnode, combination, overlap,
                            newOverlapSet);
                    }
                }
            }
            overlapSet = newOverlapSet;
        }
        return overlapSet;
    }

    /**
     * Help method for computing overlaps
     * @param rnode the node that will be added to (a copy of) combination
     * @param combination The combination to which rnode should be added (if combination is null, a new Combination will be created) 
     * @param overlap the new combination will be added to a copy of overlap, the old combination will be removed from the copy
     * @param newOverlapSet the new (copy of) overlap will be added to newOverlapset
     */
    private static void addToCombination(RuleNode rnode,
            Combination combination, Set<Combination> overlap,
            Set<Set<Combination>> newOverlapSet) {
        if (combination == null
            || rnode.getType().equals(combination.getType())) {
            Set<Combination> newOverlap = new HashSet<Combination>(overlap);
            Combination newCombination;
            if (combination == null) {
                newCombination = new Combination(rnode);
            } else {
                newCombination = new Combination(combination);
                newCombination.add(rnode);
                //We do not need the old combination in the new overlap
                newOverlap.remove(combination);
            }
            newOverlap.add(newCombination);
            newOverlapSet.add(newOverlap);
        }
    }

    private RuleApplication app1;
    private RuleApplication app2;

    private static class Combination extends HashSet<RuleNode> {

        protected Combination(RuleNode singleElement) {
            this.add(singleElement);
            this.type = singleElement.getType();
        }

        protected Combination(Combination comb) {
            super(comb);
            this.type = comb.getType();
        }

        @Override
        public boolean add(RuleNode e) {
            if (!e.getType().equals(this.type)) {
                throw new IllegalArgumentException(
                    "RuleNode can not be added to the combination because it has a different Type");
            }
            return super.add(e);
        }

        public TypeNode getType() {
            return this.type;
        }

        private TypeNode type;

    }
}
