/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2013 University of Twente
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
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleGraphMorphism;
import groove.grammar.rule.RuleNode;

import java.util.HashSet;
import java.util.Set;

public class CriticalPair {

    private RuleGraph target;
    private RuleGraphMorphism m1;
    private RuleGraphMorphism m2;

    public RuleGraph getTarget() {
        return this.target;
    }

    public RuleGraphMorphism getM1() {
        return this.m1;
    }

    public RuleGraphMorphism getM2() {
        return this.m2;
    }

    private CriticalPair(RuleGraph target, RuleGraphMorphism m1,
            RuleGraphMorphism m2) {
        this.target = target;
        this.m1 = m1;
        this.m2 = m2;
    }

    private CriticalPair(CriticalPair other) {
        this.target = other.target.clone();
        this.m1 = other.m1.clone();
        this.m2 = other.m2.clone();
    }

    @Override
    public CriticalPair clone() {
        return new CriticalPair(this);
    }

    /**
     * Compute all possble overlappings of the given set of nodes
     * Every element (set of combinations) in the result is an overlap
     * @param nodes the nodes for which overlappings should be computed
     * @return All possble overlappings of the given set of nodes
     */
    public static Set<CriticalPair> computeCriticalPairs(Rule r1, Rule r2) {
        RuleGraph l1 = r1.lhs();
        RuleGraph l2 = r2.lhs();
        Set<CriticalPair> parrPairs = new HashSet<CriticalPair>();

        parrPairs = buildCriticalNodeSet(l1.nodeSet(), parrPairs, 1);
        parrPairs = buildCriticalNodeSet(l2.nodeSet(), parrPairs, 2);

        //TODO add edges: buildCriticalEdgeSet
        //TODO check if every critical pair is indeed a critical pair

        DefaultHostGraph hostGraph = new DefaultHostGraph("host");

        return parrPairs;
    }

    private static Set<CriticalPair> buildCriticalEdgeSet(RuleGraph graph,
            Set<CriticalPair> parrPairs, int matchnum) {
        for (RuleEdge edge : graph.edgeSet()) {
            RuleNode source = edge.source();
            RuleNode target = edge.target();
            //TODO
        }
        return parrPairs;
    }

    /**
     * Help method for formAllOverlaps(...)
     * @param nodes the nodes which should be added
     * @param parrPairs the exists critical pairs (may be empty)
     * @param matchnum the match number (1 or 2) this number states to which match mappings should be added
     * @return a set of parallel pairs
     */
    private static Set<CriticalPair> buildCriticalNodeSet(Set<RuleNode> nodes,
            Set<CriticalPair> parrPairs, int matchnum) {
        if (matchnum != 1 || matchnum != 2) {
            throw new IllegalArgumentException("matchnum may only be 1 or 2");
        }
        for (RuleNode rnode : nodes) {
            HashSet<CriticalPair> newParrPairs = new HashSet<CriticalPair>();
            //special case, parrPairs contains no pairs yet, this can only happen if l1.nodeSet().isEmpty()
            if (parrPairs.isEmpty()) {
                RuleGraph target = new RuleGraph("target");
                RuleNode targetNode = createAndAddSimilarNode(rnode, target);
                RuleGraphMorphism m1 = new RuleGraphMorphism();
                RuleGraphMorphism m2 = new RuleGraphMorphism();
                if (matchnum == 1) {
                    m1.nodeMap().put(rnode, targetNode);
                } else { //matchnum == 2
                    m2.nodeMap().put(rnode, targetNode);
                }
                newParrPairs.add(new CriticalPair(target, m1, m2));
            } else {
                for (CriticalPair pair : parrPairs) {
                    //case 1: do not overlap rnode with an existing node of pair.getTarget()
                    //This means we create a copy of pair and add the set containing rnode as a separate element

                    CriticalPair newPair = new CriticalPair(pair);
                    RuleNode targetNode =
                        createAndAddSimilarNode(rnode, newPair.getTarget());
                    if (matchnum == 1) {
                        newPair.getM1().nodeMap().put(rnode, targetNode);
                    } else { //matchnum == 2
                        newPair.getM2().nodeMap().put(rnode, targetNode);
                    }
                    newParrPairs.add(newPair);

                    //case 2: 
                    //Repeat the following for every node tnode in pair.getTarget():
                    //Map rnode to tnode in M1 (if the types coincide)
                    for (RuleNode tnode : pair.getTarget().nodeSet()) {
                        if (tnode.getType().equals(rnode.getType())) {
                            newPair = new CriticalPair(pair);
                            if (matchnum == 1) {
                                newPair.getM1().nodeMap().put(rnode, targetNode);
                            } else { //matchnum == 2
                                newPair.getM2().nodeMap().put(rnode, targetNode);
                            }
                            newParrPairs.add(newPair);
                        }
                    }
                }
            }
            parrPairs = newParrPairs;
        }
        return parrPairs;
    }

    private static RuleNode createAndAddSimilarNode(RuleNode toCopy,
            RuleGraph forGraph) {
        //TODO zorgen dat een node van het goede type is (DefaultRuleNode, OperatorNode of VariableNode)
        //TODO zorgen dat de TypeNode goed gezet wordt
        RuleNode result = forGraph.addNode();
        return result;
    }
}
