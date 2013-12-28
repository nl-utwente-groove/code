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

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Constant;
import groove.algebra.syntax.Variable;
import groove.grammar.Rule;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.graph.NodeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class ParallelPair {
    private Rule rule1;
    private Rule rule2;
    private Map<Long,Set<RuleNode>> nodeMatch1 =
        new HashMap<Long,Set<RuleNode>>();
    private Map<Long,Set<RuleNode>> nodeMatch2 =
        new HashMap<Long,Set<RuleNode>>();
    private boolean criticalPairComputed = false;
    private CriticalPair critPair = null;

    //ensures that the targets of matches are unique when this is desired
    private static long matchTargetCounter = 0;
    //counter to ensure that created variables are unique
    private static int variableCounter = 0;

    static Long getNextMatchTargetNumer() {
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
     * Creates an empty ParallelPair
     * @param rule1 the first rule
     * @param rule2 the second rule
     */
    ParallelPair(Rule rule1, Rule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    private ParallelPair(ParallelPair other) {
        this.nodeMatch1 = copyMatch(other.nodeMatch1);
        this.nodeMatch2 = copyMatch(other.nodeMatch2);
        this.rule1 = other.getRule1();
        this.rule2 = other.getRule2();
    }

    private static <T> Map<Long,Set<T>> copyMatch(Map<Long,Set<T>> match) {
        Map<Long,Set<T>> result = new HashMap<Long,Set<T>>();
        for (Entry<Long,Set<T>> entry : match.entrySet()) {
            HashSet<T> newSet = new HashSet<T>();
            newSet.addAll(entry.getValue());
            result.put(entry.getKey(), newSet);
        }
        return result;
    }

    @Override
    public ParallelPair clone() {
        return new ParallelPair(this);
    }

    public Map<Long,Set<RuleNode>> getNodeMatch(MatchNumber matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch;
        if (matchnum == MatchNumber.One) {
            nodeMatch = this.nodeMatch1;
        } else if (matchnum == MatchNumber.Two) {
            nodeMatch = this.nodeMatch2;
        } else {
            throw new IllegalArgumentException("matchnum must be One or Two");
        }
        return nodeMatch;
    }

    public boolean isContainedInMatch(RuleNode rn, MatchNumber matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch = getNodeMatch(matchnum);
        for (Set<RuleNode> nodes : nodeMatch.values()) {
            for (RuleNode node : nodes) {
                if (node.equals(rn)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Long> getCombinationGroups() {
        Set<Long> result = new TreeSet<Long>();
        result.addAll(this.nodeMatch1.keySet());
        result.addAll(this.nodeMatch2.keySet());
        return result;
    }

    public List<RuleNode> getCombination(Long group) {
        List<RuleNode> result = new ArrayList<RuleNode>();
        if (this.nodeMatch1.containsKey(group)) {
            result.addAll(this.nodeMatch1.get(group));
        }
        if (this.nodeMatch2.containsKey(group)) {
            result.addAll(this.nodeMatch2.get(group));
        }
        return result;
    }

    public Set<RuleNode> getCombination(Long group, MatchNumber matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch = getNodeMatch(matchnum);
        Set<RuleNode> result = new HashSet<RuleNode>();
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
            DefaultHostGraph host =
                new DefaultHostGraph(
                    "target",
                    HostFactory.newInstance(this.rule1.getTypeGraph().getFactory()));
            RuleToHostMap match1 =
                createRuleToHostMap(this.nodeMatch1, host,
                    this.rule1.lhs().edgeSet());
            RuleToHostMap match2 =
                createRuleToHostMap(this.nodeMatch2, host,
                    this.rule2.lhs().edgeSet());

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

    private Map<Long,HostNode> hostNodes;

    private RuleToHostMap createRuleToHostMap(
            Map<Long,Set<RuleNode>> nodeMatch, DefaultHostGraph host,
            Set<RuleEdge> edges) {
        if (this.hostNodes == null) {
            this.hostNodes = new HashMap<Long,HostNode>();
        }
        RuleToHostMap result = new RuleToHostMap(host.getFactory());

        for (Entry<Long,Set<RuleNode>> entry : nodeMatch.entrySet()) {
            HostNode target;
            Set<RuleNode> ruleNodes = entry.getValue();
            if (this.hostNodes.containsKey(entry.getKey())) {
                //if the hostnode was already create it, then get it
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
                    Algebra<?> alg =
                        AlgebraFamily.TERM.getAlgebra(varNode.getSignature());
                    //TODO set can contain multiple constants
                    Constant constant = getFirstConstant(ruleNodes);
                    if (constant == null) {
                        target =
                            host.getFactory().createNode(
                                alg,
                                new Variable("x" + variableCounter++,
                                    varNode.getSignature()));
                    } else {
                        target = host.getFactory().createNode(alg, constant);
                    }
                } else {
                    throw new UnsupportedOperationException(
                        "Unknown type for RuleNode " + firstNode);
                }
                //Add the target node to the hostgraph
                host.addNode(target);
                //add the created hostNode to the map of created hostNodes
                this.hostNodes.put(entry.getKey(), target);
            }
            // add the node mappings to the result
            for (RuleNode rn : ruleNodes) {
                result.putNode(rn, target);
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
                    host.getFactory().createEdge(hostSource, re.getType(),
                        hostTarget);
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
    private Constant getFirstConstant(Set<RuleNode> nodes) {
        for (RuleNode node : nodes) {
            if (node instanceof VariableNode
                && ((VariableNode) node).hasConstant()) {
                return ((VariableNode) node).getConstant();
            }
        }
        return null;
    }

    /**
     * Searches all the ruleNode in both nodematches to find the group which contains
     * the Constant cons
     */
    public Long findConstant(Constant cons) {
        if (cons == null) {
            return null;
        }
        for (Long group : getCombinationGroups()) {
            for (RuleNode rn : getCombination(group)) {
                if (rn instanceof VariableNode
                    && cons.equals(((VariableNode) rn).getConstant())) {
                    return group;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String result = "";
        Map<RuleNode,String> nodeName1 = new HashMap<RuleNode,String>();
        Map<RuleNode,String> nodeName2 = new HashMap<RuleNode,String>();
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
            Set<RuleNode> r1nodes = getCombination(group, MatchNumber.One);
            Set<RuleNode> r2nodes = getCombination(group, MatchNumber.Two);
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
