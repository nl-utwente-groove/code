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
import groove.grammar.Rule;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.DefaultRuleNode;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.transform.BasicEvent;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent.Reuse;

import java.util.HashSet;
import java.util.Iterator;
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

    private CriticalPair(DefaultHostGraph target, Rule rule1, Rule rule2,
            RuleToHostMap m1, RuleToHostMap m2) {
        this.hostGraph = target;
        this.match2 = m1;
        this.match2 = m2;
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
        Set<CriticalPair> parrPairs = new HashSet<CriticalPair>();

        parrPairs = buildCriticalSet(l1, parrPairs, rule1, rule2, 1);
        parrPairs = buildCriticalSet(l2, parrPairs, rule1, rule2, 2);

        //Filter out all critical pairs which are not parallel dependent
        Iterator<CriticalPair> it = parrPairs.iterator();
        while (it.hasNext()) {
            if (!it.next().isParallelDependent()) {
                it.remove();
            }
        }
        //The resulting critical pairs are all parallel dependent
        //TODO assert that m1 and m2 are jointly surjective
        return parrPairs;
    }

    /**
     * Help method for formAllOverlaps(...)
     * @param nodes the nodes which should be added
     * @param parrPairs the exists critical pairs (may be empty)
     * @param matchnum the match number (1 or 2) this number states to which match mappings should be added
     * @return a set of parallel pairs
     */
    private static Set<CriticalPair> buildCriticalSet(RuleGraph ruleGraph,
            Set<CriticalPair> parrPairs, Rule rule1, Rule rule2, int matchnum) {
        Set<RuleNode> nodes = ruleGraph.nodeSet();
        if (matchnum != 1 || matchnum != 2) {
            throw new IllegalArgumentException("matchnum may only be 1 or 2");
        }
        for (RuleNode rnode : nodes) {
            Set<? extends RuleEdge> edges = ruleGraph.edgeSet(rnode);
            HashSet<CriticalPair> newParrPairs = new HashSet<CriticalPair>();
            //initial case, parrPairs contains no pairs yet, this can only happen if l1.nodeSet().isEmpty()
            if (parrPairs.isEmpty()) {
                DefaultHostGraph target = new DefaultHostGraph("target");

                RuleToHostMap m1 = new RuleToHostMap(target.getFactory());
                RuleToHostMap m2 = new RuleToHostMap(target.getFactory());
                createAndMapNodeWithEdges(rnode, target, m1, m2, matchnum,
                    edges);
                newParrPairs.add(new CriticalPair(target, rule1, rule2, m1, m2));
            } else {
                for (CriticalPair pair : parrPairs) {
                    //case 1: do not overlap rnode with an existing node of pair.getTarget()
                    //This means we create a copy of pair and add the set containing rnode as a separate element

                    CriticalPair newPair = new CriticalPair(pair);
                    createAndMapNodeWithEdges(rnode, newPair.getHostGraph(),
                        newPair.getMatch1(), newPair.getMatch2(), matchnum,
                        edges);
                    newParrPairs.add(newPair);

                    //case 2: 
                    //Repeat the following for every node tnode in pair.getTarget():
                    //Map rnode to tnode in M1 (if the types coincide)
                    for (HostNode tnode : pair.getHostGraph().nodeSet()) {
                        if (tnode.getType().equals(rnode.getType())) {
                            newPair = new CriticalPair(pair);
                            mapNodeAndAddEdges(rnode, tnode,
                                newPair.getHostGraph(), newPair.getMatch1(),
                                newPair.getMatch2(), matchnum, edges);
                            newParrPairs.add(newPair);
                        }
                    }
                }
            }
            parrPairs = newParrPairs;
        }
        return parrPairs;
    }

    private static void createAndMapNodeWithEdges(RuleNode ruleNode,
            DefaultHostGraph hostGraph, RuleToHostMap m1, RuleToHostMap m2,
            int matchnum, Set<? extends RuleEdge> edges) {
        HostNode targetNode;
        if (ruleNode instanceof DefaultRuleNode) {
            targetNode = hostGraph.addNode();
        } else if (ruleNode instanceof OperatorNode) {
            //TODO
            throw new UnsupportedOperationException(
                "OperatorNode not supported");
        } else if (ruleNode instanceof ValueNode) {
            Algebra<?> algebra = ((ValueNode) ruleNode).getAlgebra();
            Object value = ((ValueNode) ruleNode).getValue();
            targetNode = hostGraph.addNode(algebra, value);
            /* TODO if this valuenode is not conected to any other elements other than the productNode
             * then it does not need to be added */
        } else {
            //Not supposed to happen, all supertypes of RuleNode should have been handled above
            throw new UnsupportedOperationException(
                "Unknown type for RuleNode " + ruleNode);
        }
        //TODO zorgen dat een node van het goede type is (DefaultRuleNode, OperatorNode of VariableNode)
        //TODO zorgen dat de TypeNode goed gezet wordt
        mapNodeAndAddEdges(ruleNode, targetNode, hostGraph, m1, m2, matchnum,
            edges);
    }

    private static <T extends RuleEdge> void mapNodeAndAddEdges(
            RuleNode ruleNode, HostNode targetNode, HostGraph hostGraph,
            RuleToHostMap m1, RuleToHostMap m2, int matchnum, Set<T> edges) {
        RuleToHostMap match;
        if (matchnum == 1) {
            match = m1;
        } else if (matchnum == 1) {
            match = m2;
        } else {
            throw new IllegalArgumentException("matchnum must be 1 or 2");
        }
        //Add the mapping (ruleNode -> targetNode) to the match
        match.putNode(ruleNode, targetNode);
        //For all edges for which both the source and target are defined in match
        //Add a similar edge to the hostgraph (if it does not yet exist) and add
        //the mapping (ruleEdge -> hostEdge) the the match
        for (T ruleEdge : edges) {
            if (match.nodeMap().containsKey(ruleEdge.source())
                && match.nodeMap().containsKey(ruleEdge.target())) {
                //since edges was the set of edges adjacent to ruleNode, one of the next two
                //HostNodes will be equal to targetNode
                HostNode edgeSource = match.getNode(ruleEdge.source());
                HostNode edgeTarget = match.getNode(ruleEdge.target());
                //addEdge returns the existing edge, if an edge with these properties already exists
                //this is exactly what we need
                HostEdge hostEdge =
                    hostGraph.addEdge(edgeSource, ruleEdge.label(), edgeTarget);
                //TODO are all possible values for ruleEdge.label() valid for host graphs?
                //TODO use typeEdges?
                match.putEdge(ruleEdge, hostEdge);
            }
        }
    }

    /**
     * Checks if this instance of CriticalPair is a parallel dependent pair
     * This method is used to filter out parallel pairs wich are no real critical pairs
     * @return true if the pair is parallel dependent, false if it is parallel independent
     */
    private boolean isParallelDependent() {
        return isWeaklyParallelDependent(this.rule1, this.match1, this.match2)
            && isWeaklyParallelDependent(this.rule2, this.match2, this.match1);
    }

    private boolean isWeaklyParallelDependent(Rule rule, RuleToHostMap match,
            RuleToHostMap otherMatch) {
        BasicEvent ruleEvent = new BasicEvent(rule, match, Reuse.NONE);
        RuleApplication app = new RuleApplication(ruleEvent, this.hostGraph);
        HostGraphMorphism transformationMorphism = app.getMorphism();
        //check if transformationMorphism1 is defined for all target elements of this.match1
        for (HostNode hn : this.match1.nodeMap().values()) {
            if (!transformationMorphism.nodeMap().containsKey(hn)) {
                return false;
            }
        }
        //same process for edges
        for (HostEdge he : this.match1.edgeMap().values()) {
            if (!transformationMorphism.edgeMap().containsKey(he)) {
                return false;
            }
        }
        //all checks complete
        return true;
    }
}
