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
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.transform.BasicEvent;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent.Reuse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ParallelPair {
    private Rule rule1;
    private Rule rule2;
    private Map<Long,Set<RuleNode>> nodeMatch1 =
        new HashMap<Long,Set<RuleNode>>();
    private Map<Long,Set<RuleNode>> nodeMatch2 =
        new HashMap<Long,Set<RuleNode>>();
    private Map<Long,Set<RuleEdge>> edgeMatch1 =
        new HashMap<Long,Set<RuleEdge>>();
    private Map<Long,Set<RuleEdge>> edgeMatch2 =
        new HashMap<Long,Set<RuleEdge>>();
    private boolean criticalPairComputed = false;
    private CriticalPair critPair = null;

    //ensures that the targets of matches are unique when this is desired
    private static long matchTargetCounter = 0;
    //counter to ensure that created variables are unique
    private static int variableCounter = 0;

    static Long getNextMatchTargetNumer() {
        return matchTargetCounter++;
    }

    public Set<Set<RuleNode>> getNodeCombinations() {
        Set<Set<RuleNode>> result = new HashSet<Set<RuleNode>>();
        for (Entry<Long,Set<RuleNode>> entry : this.nodeMatch1.entrySet()) {
            Set<RuleNode> combination = new HashSet<RuleNode>();
            combination.addAll(entry.getValue());
            if (this.nodeMatch2.containsKey(entry.getKey())) {
                combination.addAll(this.nodeMatch2.get(entry.getKey()));
            }
            result.add(combination);
        }
        for (Entry<Long,Set<RuleNode>> entry : this.nodeMatch2.entrySet()) {
            if (this.nodeMatch1.containsKey(entry.getKey())) {
                //combination has already been handled
            } else {
                Set<RuleNode> combination = new HashSet<RuleNode>();
                combination.addAll(entry.getValue());
                result.add(combination);
            }
        }
        return result;
    }

    public Map<Long,Set<RuleNode>> getNodeMatch1() {
        return this.nodeMatch1;
    }

    public Map<Long,Set<RuleNode>> getNodeMatch(int matchnum) {
        Map<Long,Set<RuleNode>> nodeMatch;
        if (matchnum == 1) {
            nodeMatch = this.nodeMatch1;
        } else if (matchnum == 2) {
            nodeMatch = this.nodeMatch2;
        } else {
            throw new IllegalArgumentException("matchnum must be 1 or 2");
        }
        return nodeMatch;
    }

    public boolean isContainedInMatch(RuleNode rn, int matchnum) {
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

    public Map<Long,Set<RuleNode>> getNodeMatch2() {
        return this.nodeMatch2;
    }

    public Map<Long,Set<RuleEdge>> getEdgeMatch1() {
        return this.edgeMatch1;
    }

    public Map<Long,Set<RuleEdge>> getEdgeMatch2() {
        return this.edgeMatch2;
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
        this.edgeMatch1 = copyMatch(other.edgeMatch1);
        this.edgeMatch2 = copyMatch(other.edgeMatch2);
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

    /**
     * Checks if this instance of ParallelPair is a parallel dependent pair
     * If this is the case, then a CriticalPair will be created
     * Otherwise this method will return {@code null}
     * @return A CriticalPair (non-{@code null}) if the pair is parallel dependent, {@code null} if it is parallel independent
     */
    CriticalPair getCriticalPair() {
        if (!this.criticalPairComputed) {
            DefaultHostGraph host = new DefaultHostGraph("target");
            RuleToHostMap match1 =
                createRuleToHostMap(this.nodeMatch1, this.edgeMatch1, host);
            RuleToHostMap match2 =
                createRuleToHostMap(this.nodeMatch2, this.edgeMatch2, host);

            if (isWeaklyParallelDependent(this.rule1, match1, match2, host)
                || isWeaklyParallelDependent(this.rule2, match2, match1, host)) {
                //the pair is a critical pair
                this.critPair =
                    new CriticalPair(host, this.rule1, this.rule2, match1,
                        match2);
            } else {
                //the pair is not a critical pair
                this.critPair = null;
            }

            this.criticalPairComputed = true;
        }
        return this.critPair;

    }

    private RuleToHostMap createRuleToHostMap(
            Map<Long,Set<RuleNode>> nodeMatch,
            Map<Long,Set<RuleEdge>> edgeMatch, DefaultHostGraph host) {
        RuleToHostMap result = new RuleToHostMap(host.getFactory());

        for (Set<RuleNode> rns : nodeMatch.values()) {
            RuleNode firstNode = rns.iterator().next();
            HostNode target;
            if (firstNode instanceof DefaultRuleNode) {
                //TODO use TypeLabel for Typed graphs
                target = host.getFactory().createNode();
            } else if (firstNode instanceof VariableNode) {
                VariableNode varNode = (VariableNode) firstNode;
                Algebra<?> alg =
                    AlgebraFamily.TERM.getAlgebra(varNode.getSignature());
                Constant constant = getFirstConstant(rns);
                if (constant == null) {
                    target =
                        host.getFactory().createValueNode(
                            alg,
                            new Variable("x" + variableCounter++,
                                varNode.getSignature()));
                } else {
                    target = host.getFactory().createValueNode(alg, constant);
                }
            } else {
                throw new UnsupportedOperationException(
                    "Unknown type for RuleNode " + firstNode);
            }
            //Add the target node to the hostgraph and add the node mappings to the result
            host.addNode(target);
            for (RuleNode rn : rns) {
                result.putNode(rn, target);
            }
        }

        //Similar process for edges
        for (Set<RuleEdge> redges : edgeMatch.values()) {
            //The images of the source/target of the edge in the hostgraph
            //are the same for every edge in redges
            RuleEdge firstEdge = redges.iterator().next();
            HostNode edgeSource = result.getNode(firstEdge.source());
            HostNode edgeTarget = result.getNode(firstEdge.target());
            //TODO use the TypeEdge for typed graphs
            HostEdge newEdge =
                host.getFactory().createEdge(edgeSource, firstEdge.label(),
                    edgeTarget);
            host.addEdge(newEdge);
            for (RuleEdge re : redges) {
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

    private boolean isWeaklyParallelDependent(Rule rule, RuleToHostMap match,
            RuleToHostMap otherMatch, HostGraph host) {
        BasicEvent ruleEvent = new BasicEvent(rule, match, Reuse.NONE);
        RuleApplication app = new RuleApplication(ruleEvent, host);
        HostGraphMorphism transformationMorphism = app.getMorphism();
        //check if transformationMorphism1 is defined for all target elements of this.match1
        for (HostNode hn : otherMatch.nodeMap().values()) {
            //valueNodes may be not be defined under the transformation morphism
            //however all values exist universally, values can not actually be deleted
            if (!(hn instanceof ValueNode)
                && transformationMorphism.nodeMap().get(hn) == null) {
                System.out.println("Node dependency found: " + hn);
                return true;
            }
        }
        //same process for edges
        for (HostEdge he : otherMatch.edgeMap().values()) {
            if (transformationMorphism.edgeMap().get(he) == null) {
                if (he.target() instanceof ValueNode) {
                    System.out.println("targetIsValue");
                    ValueNode val = (ValueNode) he.target();
                    System.out.println("Kind = " + val.getTerm().getKind());
                    if (otherMatch.nodeMap().keySet().contains(val)) {
                        System.out.println("target is defined");
                    } else {
                        System.out.println("target is NOT defined");
                    }
                }
                System.out.println("Edge dependency found: " + he);
                return true;
            }
        }
        //all checks complete, no dependencies found
        System.out.println("No dependencies found");
        return false;
    }
}
