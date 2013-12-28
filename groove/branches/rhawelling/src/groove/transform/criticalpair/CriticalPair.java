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
package groove.transform.criticalpair;

import groove.algebra.Constant;
import groove.grammar.Rule;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CriticalPair {

    private DefaultHostGraph hostGraph;
    private Rule rule1;
    private Rule rule2;
    private RuleToHostMap match1;
    private RuleToHostMap match2;

    public DefaultHostGraph getHostGraph() {
        return this.hostGraph;
    }

    public RuleToHostMap getMatch1() {
        return this.match1;
    }

    public RuleToHostMap getMatch2() {
        return this.match2;
    }

    public Rule getRule1() {
        return this.rule1;
    }

    public Rule getRule2() {
        return this.rule2;
    }

    CriticalPair(DefaultHostGraph target, Rule rule1, Rule rule2,
            RuleToHostMap m1, RuleToHostMap m2) {
        this.hostGraph = target;
        this.match1 = m1;
        this.match2 = m2;
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    private CriticalPair(CriticalPair other) {
        this.hostGraph = other.getHostGraph().clone();
        this.match1 = new RuleToHostMap(this.hostGraph.getFactory());
        this.match2 = new RuleToHostMap(this.hostGraph.getFactory());
        this.match1.putAll(other.getMatch1());
        this.match2.putAll(other.getMatch2());
        this.rule1 = other.getRule1();
        this.rule2 = other.getRule2();
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
    public static Set<CriticalPair> computeCriticalPairs(Rule rule1, Rule rule2) {
        RuleGraph l1 = rule1.lhs();
        RuleGraph l2 = rule2.lhs();
        Set<ParallelPair> parrPairs = new HashSet<ParallelPair>();

        parrPairs = buildCriticalSet(l1, parrPairs, rule1, rule2, 1);
        parrPairs = buildCriticalSet(l2, parrPairs, rule1, rule2, 2);

        Iterator<ParallelPair> it;

        /*
         * If rule1 and rule2 are the same, then for every critical pair
         * match1 must not equal match2
         */
        Set<ParallelPair> deletedPairs = new HashSet<ParallelPair>();
        if (rule1.equals(rule2)) {
            it = parrPairs.iterator();
            while (it.hasNext()) {
                ParallelPair p = it.next();
                if (p.getNodeMatch1().equals(p.getNodeMatch2())) {
                    it.remove();
                    deletedPairs.add(p);
                }
            }
        }
        //Filter out all critical pairs which are not parallel dependent
        System.out.println("\nPairs before parallelDep check:"
            + parrPairs.size());
        for (ParallelPair pair : parrPairs) {
            System.out.println(pair);
        }
        System.out.println("\n");

        Set<CriticalPair> critPairs = new HashSet<CriticalPair>();
        for (ParallelPair pair : parrPairs) {
            CriticalPair criticalPair = pair.getCriticalPair();
            if (criticalPair != null) {
                System.out.println(pair);
                critPairs.add(criticalPair);
            }
        }
        //        System.out.println("\nDeleted pairs: " + deletedPairs.size());
        //        for (ParallelPair pair : deletedPairs) {
        //            System.out.println(pair);
        //            System.out.println("WasCritical: " + pair.getCriticalPair() != null);
        //        }
        return critPairs;
    }

    /**
     * Help method for formAllOverlaps(...)
     * @param nodes the nodes which should be added
     * @param parrPairs the exists critical pairs (may be empty)
     * @param matchnum the match number (1 or 2) this number states to which match mappings should be added
     * @return a set of parallel pairs
     */
    private static Set<ParallelPair> buildCriticalSet(RuleGraph ruleGraph,
            Set<ParallelPair> parrPairs, Rule rule1, Rule rule2, int matchnum) {
        if (matchnum != 1 && matchnum != 2) {
            throw new IllegalArgumentException("matchnum may only be 1 or 2");
        }

        Set<RuleNode> nodesToProcess =
            new HashSet<RuleNode>(ruleGraph.nodeSet());
        Iterator<RuleNode> nodeIt = nodesToProcess.iterator();
        while (nodeIt.hasNext()) {
            RuleNode curNode = nodeIt.next();
            if (curNode instanceof OperatorNode) {
                nodeIt.remove();
            } else if (curNode instanceof VariableNode
                && ((VariableNode) curNode).getTerm() instanceof Constant) {
                Set<? extends RuleEdge> edges = ruleGraph.edgeSet(curNode);
                boolean connectedToLhs = false;
                for (RuleEdge e : edges) {
                    RuleNode source = e.source();
                    RuleNode target = e.target();
                    if (source instanceof DefaultRuleNode
                        || target instanceof DefaultRuleNode) {
                        //curNode is only connected to OperatorNodes or variableNodes
                        //it must be included in the match
                        connectedToLhs = true;
                        break;
                    }
                }
                if (!connectedToLhs) {
                    nodeIt.remove();
                }
            }
        }

        for (RuleNode rnode : nodesToProcess) {
            Set<? extends RuleEdge> edges = ruleGraph.edgeSet(rnode);
            HashSet<ParallelPair> newParrPairs = new HashSet<ParallelPair>();
            //initial case, parrPairs contains no pairs yet, this can only happen if l1.nodeSet().isEmpty()
            if (parrPairs.isEmpty()) {
                ParallelPair pair = new ParallelPair(rule1, rule2);
                addNodeToNewGroup(rnode, pair, matchnum, edges);
                newParrPairs.add(pair);
            } else {
                for (ParallelPair pair : parrPairs) {
                    ParallelPair newPair;
                    //case 1: do not overlap rnode with an existing node of pair.getTarget()
                    //This means we create a copy of pair and add the set containing rnode as a separate element
                    if (rnode instanceof VariableNode
                        && pair.findConstant(((VariableNode) rnode).getConstant()) != null) {
                        //rnode is a VariableNode with a constant, however this constant already exists in some group
                        //case 1 is not applicable because constants are unique
                    } else {
                        newPair = pair.clone();
                        addNodeToNewGroup(rnode, newPair, matchnum, edges);
                        newParrPairs.add(newPair);
                    }

                    //case 2: 
                    //Repeat the following for every node tnode in pair.getTarget():
                    //Map rnode to tnode in M1 (if the types coincide)
                    for (Long group : pair.getCombinationGroups()) {
                        if (isCompatible(rnode, group, pair)) {
                            newPair = pair.clone();
                            addNodeToGroup(rnode, group, newPair, matchnum,
                                edges);
                            newParrPairs.add(newPair);
                        }
                    }
                }
            }
            parrPairs = newParrPairs;
            System.out.println("\nEnd of Iteration\n");
            for (ParallelPair pair : parrPairs) {
                System.out.println(pair);
            }
            System.out.println();
        }
        return parrPairs;
    }

    private static void addNodeToNewGroup(RuleNode ruleNode, ParallelPair pair,
            int matchnum, Set<? extends RuleEdge> edges) {
        Long targetGroup = ParallelPair.getNextMatchTargetNumer();
        addNodeToGroup(ruleNode, targetGroup, pair, matchnum, edges);
    }

    private static boolean isCompatible(RuleNode ruleNode, Long group,
            ParallelPair pair) {
        //TODO compare types to support typed graphs
        //combination is nonempty
        List<RuleNode> combination = pair.getCombination(group);
        RuleNode firstNode = combination.iterator().next();
        if (ruleNode instanceof DefaultRuleNode) {
            return firstNode instanceof DefaultRuleNode;
        } else if (ruleNode instanceof VariableNode) {
            if (firstNode instanceof VariableNode) {
                VariableNode varRuleNode = (VariableNode) ruleNode;
                if (varRuleNode.hasConstant()) {
                    Constant cons = varRuleNode.getConstant();
                    //check if the constant already exists in a group
                    Long constantGroup = pair.findConstant(cons);
                    //The constant may be merged if no group contains the constant
                    //i.e. constantGroup == null or if constantGroup is equal to group
                    return constantGroup == null || constantGroup.equals(group);
                    //Note: the line above may put two different constants in the same group,
                    //this can result in critical pairs which are only relevant for when the point algebra is used
                } else {
                    //ruleNode is not a constant, it can be merged with any variableNode
                    return true;
                }
            } else {
                //the ruleNode is a variable, but the combination is not for variables
                return false;
            }
        } else if (ruleNode instanceof OperatorNode) {
            throw new IllegalArgumentException(
                "OperatorNodes may not be in matches");
        } else {
            throw new UnsupportedOperationException(
                "Unknown type for RuleNode " + ruleNode);
        }

    }

    private static <T extends RuleEdge> void addNodeToGroup(RuleNode ruleNode,
            Long targetGroup, ParallelPair pair, int matchnum,
            Set<? extends RuleEdge> edges) {
        Map<Long,Set<RuleNode>> nodeMatch = pair.getNodeMatch(matchnum);
        if (!nodeMatch.containsKey(targetGroup)) {
            nodeMatch.put(targetGroup, new HashSet<RuleNode>());
        }
        Set<RuleNode> nodeSet = nodeMatch.get(targetGroup);
        //Add ruleNode to the Set
        nodeSet.add(ruleNode);
    }
}
