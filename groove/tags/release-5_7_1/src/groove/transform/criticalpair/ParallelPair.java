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
 * $Id$
 */
package groove.transform.criticalpair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.algebra.syntax.CallExpr;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Variable;
import groove.grammar.Rule;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.graph.NodeFactory;

/**
 * Class that models combinations of ruleNodes for two rules. Used for generation of critical pairs
 *
 * @author Ruud Welling
 */
class ParallelPair {
    private Rule rule1;
    private Rule rule2;

    //A map wich gives Sets of RuleNodes a number these ruleNodes are combined in the match that will be constructed
    //Every RuleNode in rule1 occurs in at most one of the Sets
    private Map<Long,Set<RuleNode>> nodeMatch1 = new LinkedHashMap<>();

    //Similar to nodeMatch1, if the same Long value is used, then ruleNodes from rule1 and rule2 are combined
    private Map<Long,Set<RuleNode>> nodeMatch2 = new LinkedHashMap<>();

    //prevents recomputing of the critical pair
    private boolean criticalPairComputed = false;
    //if criticalPairComputed then critPair == null implies that this parallelPair is a parallel independent situation
    private CriticalPair critPair = null;

    //ensures that the targets of matches are unique when this is desired
    private static long matchTargetCounter = 0;
    //counter to ensure that created variables are unique
    private static int variableCounter = 0;

    //return an unused number which can be used to group sets of ruleNodes
    static Long getNextMatchTargetNumber() {
        return matchTargetCounter++;
    }

    public Map<Long,Set<RuleNode>> getNodeMatch1() {
        return this.nodeMatch1;
    }

    public Map<Long,Set<RuleNode>> getNodeMatch2() {
        return this.nodeMatch2;
    }

    public Rule getRule1() {
        return this.rule1;
    }

    public Rule getRule2() {
        return this.rule2;
    }

    /**
     * Returns the rule with the given matchNum (ONE or TWO)
     * @param matchnum the number of the requested rule
     * @return either this.rule1 or this.rule2
     */
    public Rule getRule(MatchNumber matchnum) {
        if (matchnum == MatchNumber.ONE) {
            return this.rule1;
        } else if (matchnum == MatchNumber.TWO) {
            return this.rule2;
        } else {
            throw new IllegalArgumentException("matchnum must be ONE or TWO");
        }
    }

    /**
     * Creates an empty ParallelPair
     * @param rule1 the first rule
     * @param rule2 the second rule
     */
    ParallelPair(Rule rule1, Rule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    /**
     * Creates a new ParallelPair which is similar to other in the sense that the rules are the same
     * and the nodeMatches have the same contents (but they are different sets)
     * @param other the ParallelPair that will be copied
     */
    private ParallelPair(ParallelPair other) {
        this.nodeMatch1 = copyMatch(other.nodeMatch1);
        this.nodeMatch2 = copyMatch(other.nodeMatch2);
        this.rule1 = other.getRule1();
        this.rule2 = other.getRule2();
    }

    /**
     * Creates a copy of a Map<Long,Set<T>>
     * @param match the Map that will be copied
     * @return a new Map<Long,Set<T>>, the results can be modified without modifying match
     */
    private static <T> Map<Long,Set<T>> copyMatch(Map<Long,Set<T>> match) {
        Map<Long,Set<T>> result = new LinkedHashMap<>();
        for (Entry<Long,Set<T>> entry : match.entrySet()) {
            LinkedHashSet<T> newSet = new LinkedHashSet<>();
            newSet.addAll(entry.getValue());
            result.put(entry.getKey(), newSet);
        }
        return result;
    }

    @Override
    public ParallelPair clone() {
        return new ParallelPair(this);
    }

    /**
     * Returns the nodeMatch with the given matchNum (ONE or TWO)
     * @param matchnum the number of the requested rule
     * @return either this.nodeMatch1 or this.nodeMatch2
     */
    public Map<Long,Set<RuleNode>> getNodeMatch(MatchNumber matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch;
        if (matchnum == MatchNumber.ONE) {
            nodeMatch = this.nodeMatch1;
        } else if (matchnum == MatchNumber.TWO) {
            nodeMatch = this.nodeMatch2;
        } else {
            throw new IllegalArgumentException("matchnum must be One or Two");
        }
        return nodeMatch;
    }

    /**
     * Returns all Long values which have been used for combinations in this ParallelPair
     * @return a set of all Long values which have been used for combinations in this ParallelPair
     */
    public Set<Long> getCombinationGroups() {
        Set<Long> result = new TreeSet<>();
        result.addAll(this.nodeMatch1.keySet());
        result.addAll(this.nodeMatch2.keySet());
        return result;
    }

    /**
     * Returns a List of all ruleNodes which have been combined under the number "group"
     */
    public List<RuleNode> getCombination(Long group) {
        List<RuleNode> result = new ArrayList<>();
        if (this.nodeMatch1.containsKey(group)) {
            result.addAll(this.nodeMatch1.get(group));
        }
        if (this.nodeMatch2.containsKey(group)) {
            result.addAll(this.nodeMatch2.get(group));
        }
        return result;
    }

    /**
     * Returns a Set of all ruleNodes from the rule "matchnum" which have been combined under the number "group"
     */
    public Set<RuleNode> getCombination(Long group, MatchNumber matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch = getNodeMatch(matchnum);
        Set<RuleNode> result = new LinkedHashSet<>();
        if (nodeMatch.containsKey(group)) {
            result.addAll(nodeMatch.get(group));
        }
        return result;
    }

    /**
     * Checks if this instance of ParallelPair is a parallel dependent pair
     * If this is the case, then a CriticalPair will be created
     * Otherwise this method will return {@code null}
     * @return A CriticalPair (non-{@code null}) if the pair is parallel dependent, {@code null} if it is parallel independent
     */
    CriticalPair getCriticalPair() {
        if (!this.criticalPairComputed) {
            DefaultHostGraph host = new DefaultHostGraph("target",
                HostFactory.newInstance(this.rule1.getTypeGraph().getFactory(), true));
            RuleToHostMap match1 = createRuleToHostMap(this.nodeMatch1, host, this.rule1.lhs());
            RuleToHostMap match2 = createRuleToHostMap(this.nodeMatch2, host, this.rule2.lhs());

            CriticalPair potentialPair =
                new CriticalPair(host, this.rule1, this.rule2, match1, match2);
            if (potentialPair.isParallelDependent()) {
                //the pair is a critical pair
                this.critPair = potentialPair;
            } else {
                //the pair is not a critical pair
                this.critPair = null;
            }

            this.criticalPairComputed = true;
        }
        return this.critPair;

    }

    //keep track of which hostNodes we have already created
    private Map<Long,HostNode> hostNodes;

    /**
     * Creates a createRuleToHostMap (a morphism from ruleGraph to host) using nodeMatch
     * In this process the graph host is constructed as well
     */
    private RuleToHostMap createRuleToHostMap(Map<Long,Set<RuleNode>> nodeMatch,
        DefaultHostGraph host, RuleGraph ruleGraph) {
        if (this.hostNodes == null) {
            this.hostNodes = new LinkedHashMap<>();
        }
        Set<RuleEdge> edges = ruleGraph.edgeSet();
        RuleToHostMap result = new RuleToHostMap(host.getFactory());

        //Every entry in the nodeMach will be added to the result
        for (Entry<Long,Set<RuleNode>> entry : nodeMatch.entrySet()) {

            //the hostNode to which all ruleNodes will be mapped
            HostNode target;
            Set<RuleNode> ruleNodes = entry.getValue();
            if (this.hostNodes.containsKey(entry.getKey())) {
                //if the hostnode was already created, then get it
                target = this.hostNodes.get(entry.getKey());
            } else {
                //else create a hostnode depending on its type
                RuleNode firstNode = ruleNodes.iterator().next();
                if (firstNode instanceof DefaultRuleNode) {
                    //use the typefactory to ensure that the typenode is correct
                    NodeFactory<HostNode> typeFactory =
                        host.getFactory().nodes(firstNode.getType());
                    target = typeFactory.createNode();
                } else if (firstNode instanceof VariableNode) {
                    VariableNode varNode = (VariableNode) firstNode;
                    Algebra<?> alg = AlgebraFamily.TERM.getAlgebra(varNode.getSort());
                    //The set can contain multiple constants, the values of these constants
                    //in the algebra of the rule is the same
                    Constant constant = getFirstConstant(getCombination(entry.getKey()));
                    if (constant == null) {
                        target = host.getFactory().createNode(alg,
                            new Variable("x" + variableCounter++, varNode.getSort()));
                    } else {
                        target = host.getFactory().createNode(alg, constant);
                    }
                } else {
                    throw new UnsupportedOperationException(
                        "Unknown type for RuleNode " + firstNode);
                }

                //Add the target node to the hostgraph (this does nothing if if was already added)
                host.addNode(target);
                //add the created hostNode to the map of created hostNodes
                this.hostNodes.put(entry.getKey(), target);
            }
            // add the node mappings to the result
            for (RuleNode rn : ruleNodes) {
                result.putNode(rn, target);
            }
        }

        //now we add all targets of operations to the match (these are not included in the nodeMatch)
        for (RuleNode rn : ruleGraph.nodeSet()) {
            if (rn instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) rn;
                Sort sig = opNode.getOperator().getResultType();
                Algebra<?> alg = AlgebraFamily.TERM.getAlgebra(sig);
                Expression[] args = new Expression[opNode.getArguments().size()];
                for (int i = 0; i < opNode.getArguments().size(); i++) {
                    VariableNode varNode = opNode.getArguments().get(i);
                    Expression term;
                    if (varNode.hasConstant()) {
                        term = varNode.getConstant();
                    } else {
                        ValueNode valNode = (ValueNode) result.getNode(varNode);
                        term = valNode.getTerm();
                    }
                    args[i] = term;
                }

                HostNode target =
                    host.getFactory().createNode(alg, new CallExpr(opNode.getOperator(), args));
                host.addNode(target);
                result.putNode(opNode.getTarget(), target);
            } else if (rn instanceof VariableNode && !result.nodeMap().containsKey(rn)) {
                VariableNode varNode = (VariableNode) rn;
                //add unconnected constants to the match
                if (varNode.hasConstant()) {
                    Sort sig = varNode.getSort();
                    Algebra<?> alg = AlgebraFamily.TERM.getAlgebra(sig);
                    //Create this node in the host graph
                    //(if a node with this constant already exists, it will be reused)
                    HostNode constant = host.getFactory().createNode(alg, varNode.getConstant());
                    host.addNode(constant);
                    result.putNode(rn, constant);
                }

            }
        }

        //Now add all the edges to the match
        //The mappings for edges are defined implicitly by the mappings of the nodes
        for (RuleEdge re : edges) {
            RuleNode source = re.source();
            RuleNode target = re.target();
            HostNode hostSource = result.getNode(source);
            HostNode hostTarget = result.getNode(target);
            if (hostSource == null || hostTarget == null) {
                //either the host or target is not defined in the match
                //this is because source or target is a ProductNode,
                //or a constant which is only connected to a ProductNode
            } else {
                HostEdge newEdge =
                    host.getFactory().createEdge(hostSource, re.getType(), hostTarget);
                host.addEdge(newEdge);
                result.putEdge(re, newEdge);
            }
        }

        return result;
    }

    /**
     * Searches a set of rulenodes for a VariableNode which has a Constant
     * @param nodes the set of RuleNodes which is traversed in search of a constant
     * @return a Constant Expression
     */
    private Constant getFirstConstant(Collection<RuleNode> nodes) {
        for (RuleNode node : nodes) {
            if (node instanceof VariableNode && ((VariableNode) node).hasConstant()) {
                return ((VariableNode) node).getConstant();
            }
        }
        return null;
    }

    /**
     * Searches all the ruleNodes in both nodematches to find the group which contains
     * a constant with a value object equal to value
     */
    public Long findConstant(Constant cons, AlgebraFamily family) {
        if (cons == null) {
            return null;
        }
        for (Long group : getCombinationGroups()) {
            for (RuleNode rn : getCombination(group)) {
                if (rn instanceof VariableNode && cons.equals(((VariableNode) rn).getConstant())) {
                    return group;
                }
            }
        }
        return null;
    }

    //string representation for ParallelPairs for debug purposes
    @Override
    public String toString() {
        String result = "";
        Map<RuleNode,String> nodeName1 = new LinkedHashMap<>();
        Map<RuleNode,String> nodeName2 = new LinkedHashMap<>();
        int counter = 1;
        for (RuleNode rn : this.rule1.lhs().nodeSet()) {
            if (rn instanceof OperatorNode) {
                nodeName1.put(rn, "o-" + counter++);
            } else if (rn instanceof VariableNode) {
                nodeName1.put(rn, "v:" + rn + "-" + counter++);
            } else if (rn instanceof DefaultRuleNode) {
                nodeName1.put(rn, "d-" + counter++);
            }
        }
        for (RuleNode rn : this.rule2.lhs().nodeSet()) {
            if (rn instanceof OperatorNode) {
                nodeName2.put(rn, "o-" + counter++);
            } else if (rn instanceof VariableNode) {
                nodeName2.put(rn, "v:" + rn + "-" + counter++);
            } else if (rn instanceof DefaultRuleNode) {
                nodeName2.put(rn, "d-" + counter++);
            }
        }
        result += "nodes in rule1: {";
        Iterator<String> it = nodeName1.values().iterator();
        while (it.hasNext()) {
            String name = it.next();
            result += " " + name;
            if (it.hasNext()) {
                result += ",";
            }
        }
        result += " }\nnodes in rule2: {";
        it = nodeName2.values().iterator();
        while (it.hasNext()) {
            String name = it.next();
            result += " " + name;
            if (it.hasNext()) {
                result += ",";
            }
        }
        result += " }\nmatch: {";
        for (Long group : getCombinationGroups()) {
            result += " (";
            Set<RuleNode> r1nodes = getCombination(group, MatchNumber.ONE);
            Set<RuleNode> r2nodes = getCombination(group, MatchNumber.TWO);
            for (RuleNode rn : r1nodes) {
                result += " " + nodeName1.get(rn);
            }
            for (RuleNode rn : r2nodes) {
                result += " " + nodeName2.get(rn);
            }
            result += " )";
        }
        result += " }";

        return result;
    }
}
