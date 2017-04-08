/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2014 University of Twente
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
 *
 */
package groove.transform.criticalpair;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeNode;
import groove.transform.BasicEvent;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent.Reuse;

/**
 * CriticalPairs consist of two dependent transformations (RuleApplications)
 * which are applied to the same hostGraph
 *
 * @author Ruud Welling
 */
public class CriticalPair {

    //the hostGraph to which the rules are applied
    private DefaultHostGraph hostGraph;

    //first rule
    private Rule rule1;

    //second rule
    private Rule rule2;

    //match for rule1
    private RuleToHostMap match1;

    //match for rule2
    private RuleToHostMap match2;

    //The ruleApplications for this critical pair
    private RuleApplication app1;
    private RuleApplication app2;

    //keep track of the result of confluence analysis to avoid recomputation
    private ConfluenceStatus confluent = ConfluenceStatus.UNTESTED;
    //the grammar used to analyse whether the pair is confluent
    private Grammar grammar = null;

    CriticalPair(DefaultHostGraph target, Rule rule1, Rule rule2, RuleToHostMap m1,
        RuleToHostMap m2) {
        this.hostGraph = target;
        this.match1 = m1;
        this.match2 = m2;
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    /**
     * @return this.hostGraph
     */
    public DefaultHostGraph getHostGraph() {
        return this.hostGraph;
    }

    /**
     * @return this.match1
     */
    public RuleToHostMap getMatch1() {
        return this.match1;
    }

    /**
     * @return this.match2
     */
    public RuleToHostMap getMatch2() {
        return this.match2;
    }

    /**
     * @return the match with the given number
     */
    RuleToHostMap getMatch(MatchNumber matchnum) {
        switch (matchnum) {
        case ONE:
            return getMatch1();
        case TWO:
            return getMatch2();
        default:
            throw new IllegalArgumentException("matchnum must be one or two");
        }
    }

    /**
     * @return this.rule1
     */
    public Rule getRule1() {
        return this.rule1;
    }

    /**
     * @return this.rule2
     */
    public Rule getRule2() {
        return this.rule2;
    }

    /**
     * Returns the rule with the given number
     */
    Rule getRule(MatchNumber matchnum) {
        switch (matchnum) {
        case ONE:
            return getRule1();
        case TWO:
            return getRule2();
        default:
            throw new IllegalArgumentException("matchnum must be one or two");
        }
    }

    /**
     * @return this.app1 (computed lazily if null)
     */
    public RuleApplication getRuleApplication1() {
        if (this.app1 == null) {
            this.app1 = createRuleApplication(MatchNumber.ONE);
        }
        return this.app1;
    }

    /**
     * @return this.app2 (computed lazily if null)
     */
    public RuleApplication getRuleApplication2() {
        if (this.app2 == null) {
            this.app2 = createRuleApplication(MatchNumber.TWO);
        }
        return this.app2;
    }

    /**
     * returns the rule application with the given number
     */
    RuleApplication getRuleApplication(MatchNumber matchnum) {
        switch (matchnum) {
        case ONE:
            return getRuleApplication1();
        case TWO:
            return getRuleApplication2();
        default:
            throw new IllegalArgumentException("matchnum must be one or two");
        }
    }

    /**
     * Creates a RuleApplication (with the given number) using the rule, match and hostgraph
     */
    private RuleApplication createRuleApplication(MatchNumber matchnum) {
        BasicEvent ruleEvent = new BasicEvent(getRule(matchnum), getMatch(matchnum), Reuse.NONE);
        return new RuleApplication(ruleEvent, this.hostGraph);
    }

    /**
     * @return result of the last confluence check.
     */
    public ConfluenceStatus getStrictlyConfluent() {
        return this.confluent;
    }

    /**
     * Tests whether this Critical Pair is strictly locally confluent
     * @param grammar the grammar containing the rules which are used
     * for confluence analysis
     * @return true only if the critical pair is strictly locally confluent
     */
    public ConfluenceStatus getStrictlyConfluent(Grammar grammar) {
        if (grammar == null) {
            throw new IllegalArgumentException("grammar may not be null");
        }
        if (this.confluent != ConfluenceStatus.UNTESTED && grammar.equals(this.grammar)) {
            //confluence has already been analyzed for this critical pair
        } else {
            this.confluent = ConfluenceAnalyzer.getStrictlyConfluent(this, grammar);
        }
        return this.confluent;
    }

    /**
     * Sets the result of a confluence check
     * @param confluent the result of a confluence check
     * @param grammar the grammar used for the confluence check
     */
    protected void setStrictlyConfluent(ConfluenceStatus confluent, Grammar grammar) {
        this.confluent = confluent;
        this.grammar = grammar;
    }

    /**
     * Computes all critical pairs for all rules in the grammar
     */
    public static Set<CriticalPair> computeCriticalPairs(Grammar grammar) {
        return computeCriticalPairs(grammar.getAllRules());
    }

    /**
     *  Computes all critical pairs for all rules, the result is a set in which pairs are computed on demand
     */
    public static Set<CriticalPair> computeCriticalPairs(Set<Rule> rules) {
        //check if all the rules are compatible
        for (Rule rule : rules) {
            if (!canComputePairs(rule)) {
                throw new IllegalArgumentException("Cannot compute critical pairs for rule '"
                    + rule.getQualName()
                    + "', because the algorithm can not compute Critical pairs for this type of rule");
            }
        }

        //all rule are compatible, compute the pairs lazily
        return new LazyCriticalPairSet(rules);
    }

    /**
     * Compute all possble overlappings of the given set of nodes
     * Every element (set of combinations) in the result is an overlap
     * @return All possble overlappings of the given set of nodes
     */
    public static LinkedHashSet<CriticalPair> computeCriticalPairs(Rule rule1, Rule rule2) {
        assert canComputePairs(rule1);
        assert canComputePairs(rule2);
        //algebraFamily must be TERM, because the host graph will be constructed in the TERM algebra
        //If the rule has no variables, then the algebra does not matter
        assert(!hasVariableNodes(rule1.lhs()) && !hasVariableNodes(rule2.lhs()))
            || rule1.getGrammarProperties()
                .getAlgebraFamily()
                .equals(AlgebraFamily.TERM);
        if ((rule1.getTypeGraph() == null && rule2.getTypeGraph() != null) || !rule1.getTypeGraph()
            .equals(rule2.getTypeGraph())) {
            throw new IllegalArgumentException("Type graphs must be equal");
        }
        //Special case, both of the two rules are nondeleting, then there are no critical pairs
        if (!(rule1.hasNodeErasers() || rule1.hasEdgeErasers() || rule2.hasNodeErasers()
            || rule2.hasEdgeErasers())) {
            return new LinkedHashSet<>();
        }
        LinkedHashSet<ParallelPair> parrPairs = new LinkedHashSet<>();

        parrPairs = buildCriticalSet(parrPairs, rule1, rule2, MatchNumber.ONE);
        parrPairs = buildCriticalSet(parrPairs, rule1, rule2, MatchNumber.TWO);

        assert parrPairs.size() <= calculateMaxPairs(
            getNodesToProcess(rule1.lhs()).size() + getNodesToProcess(rule2.lhs()).size());

        Iterator<ParallelPair> it;
        /*
         * If rule1 and rule2 are the same, then for every critical pair
         * match1 must not equal match2
         */
        if (rule1.equals(rule2)) {
            it = parrPairs.iterator();
            while (it.hasNext()) {
                ParallelPair p = it.next();
                if (p.getNodeMatch1()
                    .equals(p.getNodeMatch2())) {
                    it.remove();
                }
            }
        }

        //Filter out all critical pairs which are not parallel dependent
        LinkedHashSet<CriticalPair> critPairs = new LinkedHashSet<>();
        for (ParallelPair pair : parrPairs) {
            CriticalPair criticalPair = pair.getCriticalPair();
            if (criticalPair != null) {
                critPairs.add(criticalPair);
            }
        }
        return critPairs;
    }

    /**
     * Calculate the maximal numer of critical pairs that this algorithm could return
     * @param numnodes the number of nodes that can be overlapped in both rules
     */
    private static long calculateMaxPairs(int numnodes) {
        long sum = 0;
        for (int i = 1; i <= numnodes; i++) {
            sum += calculateMaxPairs(numnodes, i);
        }

        return sum;
    }

    /**
     * Help method for calculateMaxPairs
     * Calculates the number of critical pairs for wich the hostGraphs have nodesInPair nodes
     * Where the total number of nodes being overlapped is numnodes
     * @param numnodes the number of nodes in the two rules
     * @param nodesInPair the number of nodes in the hostgraph of the pair
     */
    private static long calculateMaxPairs(int numnodes, int nodesInPair) {
        if (numnodes < nodesInPair) {
            return 0;
        }
        if (nodesInPair == 1) {
            return 1;
        }
        return calculateMaxPairs(numnodes - 1, nodesInPair) * nodesInPair
            + calculateMaxPairs(numnodes - 1, nodesInPair - 1);
    }

    /**
     * Help method for computeCriticalPairs(...)
     * @param parrPairs the exists critical pairs (may be empty)
     * @param matchnum the match number (1 or 2) this number states to which match mappings should be added
     * @return a set of parallel pairs
     */
    private static LinkedHashSet<ParallelPair> buildCriticalSet(
        LinkedHashSet<ParallelPair> parrPairs, Rule rule1, Rule rule2, MatchNumber matchnum) {
        boolean injectiveOnly;
        RuleGraph ruleGraph;
        if (matchnum == MatchNumber.ONE) {
            ruleGraph = rule1.lhs();
            injectiveOnly = rule1.getCondition()
                .isInjective();
        } else if (matchnum == MatchNumber.TWO) {
            ruleGraph = rule2.lhs();
            injectiveOnly = rule2.getCondition()
                .isInjective();
        } else {
            throw new IllegalArgumentException("matchnum must be ONE or TWO");
        }

        //Always use the term algebra, other algebras are not yet supported
        AlgebraFamily algebraFamily = AlgebraFamily.TERM;

        //get the nodes from the rule that need to be in the match
        Set<RuleNode> nodesToProcess = getNodesToProcess(ruleGraph);

        for (RuleNode rnode : nodesToProcess) {
            LinkedHashSet<ParallelPair> newParrPairs = new LinkedHashSet<>();
            //initial case, parrPairs contains no pairs yet, this can only happen if l1.nodeSet().isEmpty()
            if (parrPairs.isEmpty()) {
                ParallelPair pair = new ParallelPair(rule1, rule2);
                addNodeToNewGroup(rnode, pair, matchnum);
                newParrPairs.add(pair);
            } else {
                for (ParallelPair pair : parrPairs) {
                    ParallelPair newPair;
                    //case 1: do not overlap rnode with an existing node of pair.getTarget()
                    //This means we create a copy of pair and add the set containing rnode as a separate element
                    if (rnode instanceof VariableNode
                        && pair.findConstant(((VariableNode) rnode).getConstant(),
                            algebraFamily) != null) {
                        //rnode is a VariableNode with a constant, however this constant already exists in some group
                        //case 1 is not applicable because constants are unique
                    } else {
                        newPair = pair.clone();
                        addNodeToNewGroup(rnode, newPair, matchnum);
                        newParrPairs.add(newPair);
                    }
                }
                //in order to ensure that the result (a LinkedHashSet) is ordered by size of the critical pairs
                //from large to small, we handle case 1 and case 2 in separate loops
                for (ParallelPair pair : parrPairs) {
                    ParallelPair newPair;
                    //case 2:
                    //Repeat the following for every node tnode in pair.getTarget():
                    //Map rnode to tnode in M1 (if the types coincide)
                    for (Long group : pair.getCombinationGroups()) {
                        if (isCompatible(rnode,
                            group,
                            pair,
                            injectiveOnly,
                            matchnum,
                            algebraFamily)) {
                            newPair = pair.clone();
                            addNodeToGroup(rnode, group, newPair, matchnum);
                            newParrPairs.add(newPair);
                        }
                    }
                }
            }
            parrPairs = newParrPairs;
        }
        return parrPairs;
    }

    /**
     * Adds ruleNode to a new match group in Pair
     */
    private static void addNodeToNewGroup(RuleNode ruleNode, ParallelPair pair,
        MatchNumber matchnum) {
        Long targetGroup = ParallelPair.getNextMatchTargetNumber();
        addNodeToGroup(ruleNode, targetGroup, pair, matchnum);
    }

    /**
     * Checks whether ruleNode can be added to "group" in the ParallelPair pair
     */
    private static boolean isCompatible(RuleNode ruleNode, Long group, ParallelPair pair,
        boolean injectiveOnly, MatchNumber matchnum, AlgebraFamily algebraFamily) {
        //combination is always nonempty
        List<RuleNode> combination = pair.getCombination(group);
        RuleNode firstNode = combination.iterator()
            .next();

        if (injectiveOnly) {
            //If we only allow injective matches, then isCompatible will only return true
            //when no group exists yet for this MatchNumber
            //VariablesNodes are an exception, these may always be merged non-injectively

            if (!(ruleNode instanceof VariableNode) && !pair.getCombination(group, matchnum)
                .isEmpty()) {
                return false;
            }
        }

        //If the types are not equal return false in any case
        if (!ruleNode.getType()
            .equals(firstNode.getType())) {
            return false;
        }
        if (ruleNode instanceof DefaultRuleNode) {
            return firstNode instanceof DefaultRuleNode;
        } else if (ruleNode instanceof VariableNode) {
            if (firstNode instanceof VariableNode) {
                VariableNode varRuleNode = (VariableNode) ruleNode;
                if (varRuleNode.hasConstant()) {
                    Constant cons = varRuleNode.getConstant();
                    //check if the constant already exists in some group
                    Long constantGroup = pair.findConstant(cons, algebraFamily);
                    //The constant may be merged if no group contains the constant
                    //i.e. constantGroup == null or if constantGroup is equal to group
                    if (constantGroup == null || constantGroup.equals(group)) {
                        Sort sigKind = varRuleNode.getSort();
                        Algebra<?> alg = algebraFamily.getAlgebra(sigKind);
                        Object consValue = alg.toValueFromConstant(cons);
                        for (RuleNode other : pair.getCombination(group)) {
                            if (other instanceof VariableNode) {
                                VariableNode varOther = (VariableNode) other;
                                if (sigKind.equals(varOther.getSort())) {
                                    if (varOther.hasConstant() && !consValue
                                        .equals(alg.toValueFromConstant(varOther.getConstant()))) {
                                        //The other variable has a constant which has a different value in the algebra
                                        return false;
                                    }
                                }

                            } else {
                                //only variables with the same signature kind may be merged
                                return false;
                            }

                        }
                        //the constant may be merged because all constants in the group have the same value
                        //(or no constants are in the group)
                        return true;
                    } else {
                        //there exists another group containing a constant with the same value
                        return false;
                    }
                } else {
                    //ruleNode is not a constant, it can be merged with any variableNode
                    return true;
                }
            } else {
                //the ruleNode is a variable, but the combination is not for variables
                return false;
            }
        } else if (ruleNode instanceof OperatorNode) {
            throw new IllegalArgumentException("OperatorNodes may not be in matches");
        } else {
            throw new UnsupportedOperationException("Unknown type for RuleNode " + ruleNode);
        }

    }

    /**
     * Adds ruleNode to targetGroup in the ParallelPair pair
     */
    private static void addNodeToGroup(RuleNode ruleNode, Long targetGroup, ParallelPair pair,
        MatchNumber matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch = pair.getNodeMatch(matchnum);
        if (!nodeMatch.containsKey(targetGroup)) {
            nodeMatch.put(targetGroup, new LinkedHashSet<RuleNode>());
        }
        Set<RuleNode> nodeSet = nodeMatch.get(targetGroup);
        //Add ruleNode to the Set
        nodeSet.add(ruleNode);
    }

    /**
     * Checks whether this pair is parallel dependent
     * Used only within this package, all critical pairs visible outside this package
     * should be parallel dependent
     */
    boolean isParallelDependent() {
        return isWeaklyParallelDependent(MatchNumber.ONE)
            || isWeaklyParallelDependent(MatchNumber.TWO);
    }

    /**
     * Checks whether the pair is "weakly" parallel dependent (i.e., parallel dependent in a single direction)
     */
    private boolean isWeaklyParallelDependent(MatchNumber matchnum) {
        RuleApplication app = getRuleApplication(matchnum);
        HostGraphMorphism transformationMorphism = app.getMorphism();
        //check if transformationMorphism1 is defined for all target elements of this.match1
        for (HostNode hn : getMatch(matchnum.getOther()).nodeMap()
            .values()) {
            //valueNodes may be not be defined under the transformation morphism
            //however all values exist universally, values can not actually be deleted
            if (!(hn instanceof ValueNode) && transformationMorphism.nodeMap()
                .get(hn) == null) {
                return true;
            }
        }
        //same process for edges
        for (HostEdge he : getMatch(matchnum.getOther()).edgeMap()
            .values()) {
            if (transformationMorphism.edgeMap()
                .get(he) == null) {
                return true;
            }
        }
        //all checks complete, no dependencies found
        return false;
    }

    /**
     * Computes the set of ruleNodes which are DefaultRuleNodes, non-constant VariableNodes
     * or Constant VariableNodes which are connected to a DefaultRuleNode
     * These are the ruleNodes which are required in a match for the rule
     *
     * In addition, also nodes which are targets of OperatorNodes are not included
     * Since these need to be the result of a call expression, if these nodes
     * any edges, an exception will be thrown
     * @param ruleGraph the ruleGraph containing the nodes
     * @return a subset of ruleGraph.nodeSet()
     */
    private static Set<RuleNode> getNodesToProcess(RuleGraph ruleGraph) {
        Set<RuleNode> result = new LinkedHashSet<>(ruleGraph.nodeSet());
        Set<VariableNode> targetsOfOperatorNodes = new LinkedHashSet<>();
        for (RuleNode curNode : ruleGraph.nodeSet()) {
            if (curNode instanceof OperatorNode) {
                result.remove(curNode);
                //also add the target of this operatorNode to a list of variableNodes
                VariableNode target = ((OperatorNode) curNode).getTarget();
                if (!targetsOfOperatorNodes.add(target)) {
                    throw new RuntimeException(
                        "VariableNode " + target + " is a target of multiple operators");
                }
                result.remove(target);
            } else if (curNode instanceof VariableNode && ((VariableNode) curNode).hasConstant()) {
                Set<? extends RuleEdge> edges = ruleGraph.edgeSet(curNode);
                boolean connectedToLhs = false;
                for (RuleEdge e : edges) {
                    RuleNode source = e.source();
                    RuleNode target = e.target();
                    if (source instanceof DefaultRuleNode || target instanceof DefaultRuleNode) {
                        //curNode is connected to a DefaultRuleNode
                        //it must be included in the match
                        connectedToLhs = true;
                        break;
                    }
                }
                if (!connectedToLhs) {
                    //curNode is only connected to OperatorNodes
                    //we do not need to include it in the match
                    result.remove(curNode);
                }
            }
        }
        return result;
    }

    /**
     * Checks if the rule is can be used with this algorithm
     */
    static boolean canComputePairs(Rule rule) {
        boolean result = true;
        //the rule may not have subconditions
        result &= rule.getCondition()
            .getSubConditions()
            .isEmpty();
        //Matches with dangling edges must be allowed
        result &= !rule.getGrammarProperties()
            .isCheckDangling();
        //RHS as NAC is not allowed
        result &= !rule.getGrammarProperties()
            .isRhsAsNac();
        //Creator edges must not be treated as NACs
        result &= !rule.getGrammarProperties()
            .isCheckCreatorEdges();
        //The RhsAsNac property must be false
        result &= !rule.getGrammarProperties()
            .isRhsAsNac();
        if (result && rule.getTypeGraph() != null) {
            //check if the typegraph has inheritance
            for (Set<TypeNode> set : rule.getTypeGraph()
                .getDirectSubtypeMap()
                .values()) {
                result &= set.isEmpty();
            }
        }
        result &= checkOperationTargets(rule);
        //TODO rule priorities are not allowed (these are NACs?)
        //TODO this check may not be complete
        return result;
    }

    /**
     * Checks if the targets of operations are do not have any other edges
     */
    private static boolean checkOperationTargets(Rule rule) {
        for (RuleNode rn : rule.lhs()
            .nodeSet()) {
            if (rn instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) rn;
                if (opNode.getTarget()
                    .hasConstant()
                    || !rule.lhs()
                        .edgeSet(opNode.getTarget())
                        .isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks whether a ruleGraph contains a VariableNode
     */
    private static boolean hasVariableNodes(RuleGraph graph) {
        for (RuleNode rn : graph.nodeSet()) {
            if (rn instanceof VariableNode) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Criticalpair(" + this.rule1.getQualName() + ", " + this.rule2.getQualName()
            + ", hostGraph: " + this.hostGraph + ")";
    }
}

enum MatchNumber {
    ONE,
    TWO;

    MatchNumber getOther() {
        if (this == ONE) {
            return TWO;
        } else {
            //this == Two
            return ONE;
        }
    }
}
