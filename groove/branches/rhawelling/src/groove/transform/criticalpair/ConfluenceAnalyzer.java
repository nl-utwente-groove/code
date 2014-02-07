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
package groove.transform.criticalpair;

import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.graph.iso.IsoChecker.IsoCheckerState;
import groove.transform.Proof;
import groove.transform.Record;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Ruud
 * @version $Revision $
 */
class ConfluenceAnalyzer {

    private static int DEFAULTSEARCHDEPTH = 100;

    private static IsoChecker isoChecker = IsoChecker.getInstance(true);

    /**
     * Checks if the given CriticalPair is strictly locally confluent
     * Strict local confluence means that the pair of direct transformations is locally
     * confluent such that the transformation morphisms commute.
     * @param pair
     * @param rules
     * @return {@link ConfluenceStatus.CONFLUENT} only if the pair is confluent
     */
    static ConfluenceStatus getStrictlyConfluent(CriticalPair pair, Grammar grammar) {
        return getStrictlyConfluent(pair, grammar, DEFAULTSEARCHDEPTH);
    }

    /**
     * Checks if the given CriticalPair is strictly locally confluent
     * Strict local confluence means that the pair of direct transformations is locally
     * confluent such that the transformation morphisms commute.
     * @param pair
     * @param rules
     * @param searchDepth
     * @return {@link ConfluenceStatus.CONFLUENT} only if the pair is confluent
     */
    static ConfluenceStatus getStrictlyConfluent(CriticalPair pair, Grammar grammar, int searchDepth) {
        //analyse if the pair is strictly confluent
        getConfluentPair(pair, grammar, searchDepth);
        //the result is saved in the critical pair, return this result
        return pair.getStrictlyConfluent();
    }

    private static ConfluentPair getConfluentPair(CriticalPair pair, Grammar grammar,
            int searchDepth) {
        Set<HostGraphWithMorphism> oldStates1 = new HashSet<HostGraphWithMorphism>();
        Set<HostGraphWithMorphism> oldStates2 = new HashSet<HostGraphWithMorphism>();
        Set<HostGraphWithMorphism> newStates1 = new HashSet<HostGraphWithMorphism>();
        Set<HostGraphWithMorphism> newStates2 = new HashSet<HostGraphWithMorphism>();

        RuleApplication app1 = pair.getRuleApplication1();
        RuleApplication app2 = pair.getRuleApplication2();
        HostGraphWithMorphism hwm1 =
            new HostGraphWithMorphism(app1.getTarget(), app1.getMorphism());
        HostGraphWithMorphism hwm2 =
            new HostGraphWithMorphism(app2.getTarget(), app2.getMorphism());

        if (isConfluent(hwm1, hwm2)) {
            //the pair was already strictly confluent
            //System.out.println("Directly Confluent");
            pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
            return new ConfluentPair(pair, hwm1);
        }
        newStates1.add(hwm1);
        newStates2.add(hwm2);

        //loop as long as either newStates1 or newStates2 is nonempty
        while (!newStates1.isEmpty() || !newStates2.isEmpty()) {
            //System.out.print("*");
            //add the new states to the old states
            oldStates1.addAll(newStates1);
            oldStates2.addAll(newStates2);

            //create the sets of next states
            Set<HostGraphWithMorphism> nextStates1 = computeNewStates(newStates1, grammar);
            HostGraphWithMorphism confluentState = getConfluentState(nextStates1, oldStates2);
            if (confluentState != null) {
                pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
                return new ConfluentPair(pair, confluentState);
            }
            Set<HostGraphWithMorphism> nextStates2 = computeNewStates(newStates2, grammar);
            confluentState = getConfluentState(nextStates1, nextStates2);
            if (confluentState != null) {
                pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
                return new ConfluentPair(pair, confluentState);
            } else {
                confluentState = getConfluentState(oldStates1, nextStates2);
                if (confluentState != null) {
                    pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
                    return new ConfluentPair(pair, confluentState);
                }
            }
            //no evidence for confluence has been found, we continue the search

            //It is possible that nextStates1 or nextStates2 contains a state that is similar to one of the states
            //we have already visited, check this
            Iterator<HostGraphWithMorphism> stateIt = nextStates1.iterator();
            while (stateIt.hasNext()) {
                HostGraphWithMorphism current = stateIt.next();
                for (HostGraphWithMorphism oldState : oldStates1) {
                    //if the state is confluent with a state we have already discovered, then the states are isomorphic
                    //this means we can remove it from nextStates because it is not actually a new state
                    if (isConfluent(current, oldState)) {
                        stateIt.remove();
                        break;
                    }
                }
            }
            //repeat for nextStates2
            stateIt = nextStates2.iterator();
            while (stateIt.hasNext()) {
                HostGraphWithMorphism current = stateIt.next();
                for (HostGraphWithMorphism oldState : oldStates2) {
                    if (isConfluent(current, oldState)) {
                        stateIt.remove();
                        break;
                    }
                }
            }

            newStates1 = nextStates1;
            newStates2 = nextStates2;

            if (oldStates1.size() + oldStates2.size() > searchDepth) {
                pair.setStrictlyConfluent(ConfluenceStatus.UNDECIDED, grammar);
                return null;
            }
        }
        //all states have been analyzed however no proof for strict local confluence has been found
        pair.setStrictlyConfluent(ConfluenceStatus.NOT_STICTLY_CONFLUENT, grammar);
        return null;
    }

    private static Set<HostGraphWithMorphism> computeNewStates(Set<HostGraphWithMorphism> states,
            Grammar grammar) {
        Set<Rule> rules = grammar.getAllRules();
        Set<HostGraphWithMorphism> result = new HashSet<HostGraphWithMorphism>();
        for (HostGraphWithMorphism state : states) {
            Record record = new Record(grammar, state.getHostGraph().getFactory());
            for (Rule rule : rules) {
                Collection<Proof> matches = rule.getAllMatches(state.getHostGraph(), null);
                if (matches.isEmpty()) {
                    //System.out.println("No matches found");
                } else {
                    //                    System.out.print(matches.size() + " matches found: ");
                    //                    for (Proof proof : matches) {
                    //                        System.out.print(" " + proof.getRule().getFullName());
                    //                    }
                    //                    System.out.println();
                }
                for (Proof proof : matches) {
                    RuleEvent event = proof.newEvent(record);
                    RuleApplication app = new RuleApplication(event, state.getHostGraph());
                    result.add(new HostGraphWithMorphism(app.getTarget(), state.getMorphism().then(
                        app.getMorphism())));
                }
            }
        }
        return result;
    }

    private static HostGraphWithMorphism getConfluentState(Set<HostGraphWithMorphism> first,
            Set<HostGraphWithMorphism> second) {
        for (HostGraphWithMorphism hwm1 : first) {
            for (HostGraphWithMorphism hwm2 : second) {
                if (isConfluent(hwm1, hwm2)) {
                    return hwm1;
                }
            }
        }
        return null;
    }

    private static boolean isConfluent(HostGraphWithMorphism hwm1, HostGraphWithMorphism hwm2) {
        if (hwm1.getMorphism().nodeMap().size() != hwm1.getMorphism().nodeMap().size()
            || hwm1.getMorphism().nodeMap().size() != hwm1.getMorphism().nodeMap().size()) {
            //if the sizes of the node or edge mappings are different, then the morphisms do not commute;
            //we do not need to check for isomorphisms
            return false;
        }

        boolean result = false;

        IsoCheckerState isoState = isoChecker.new IsoCheckerState();
        Morphism<HostNode,HostEdge> isoMorphism =
            isoChecker.getIsomorphism(hwm1.getHostGraph(), hwm2.getHostGraph(), isoState);
        int isoCount = 0;
        while (isoMorphism != null && !result) {
            isoCount++;
            //The transformations are confluent, check strictness
            HostGraphMorphism transformation1 = hwm1.getMorphism().then(isoMorphism);
            HostGraphMorphism transformation2 = hwm2.getMorphism();
            result = transformation1.equals(transformation2);
            if (!result) {
                isoMorphism =
                    isoChecker.getIsomorphism(hwm1.getHostGraph(), hwm2.getHostGraph(), isoState);
            }

            if (isoCount > 10000) {
                //an extreme amount of isomorphisms has been found
                //however none of these commute, stop searching

                //System.out.println("iso check terminated");
                return false;
            }
        }

        return result;
    }

    /**
     * Calculates how many pairs are essential critical pairs
     * @param pairs
     * @param grammar
     * @return
     */
    static int analyzeEssential(Set<CriticalPair> pairs, Grammar grammar) {

        int result = 0;

        for (CriticalPair smallPair : pairs) {
            boolean foundParent = false;
            for (CriticalPair largePair : pairs) {
                if (largePair.getHostGraph().nodeCount() <= smallPair.getHostGraph().nodeCount()) {
                    continue;
                }
                HostGraphMorphism transMorphism1 = largePair.getRuleApplication1().getMorphism();
                HostGraphMorphism transMorphism2 = largePair.getRuleApplication2().getMorphism();
                if (!transMorphism1.isInjective() || !transMorphism2.isInjective()) {
                    //allows us to assume that transformation morphisms are injective
                    throw new RuntimeException("not injective");
                }
                //largePair is now definitely bigger than smallpair
                HostGraphMorphism inclusion = getInclusion(largePair, smallPair);

                if (inclusion != null && checkEssential(inclusion, transMorphism1, transMorphism2)) {
                    foundParent = true;
                    break;
                }

            }
            if (!foundParent) {
                //no essential pair has been found for this critical pair
                result++;
            }
        }

        return result;
    }

    private static boolean checkEssential(HostGraphMorphism inclusion, HostGraphMorphism trans1,
            HostGraphMorphism trans2) {
        ArrayList<HostNode> nodeList = new ArrayList<HostNode>(inclusion.nodeMap().keySet());
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                HostNode iNode = nodeList.get(i);
                HostNode jNode = nodeList.get(j);
                if (inclusion.getNode(iNode).equals(inclusion.getNode(jNode))) {
                    //check if iNode and jNode are equal or both in the domain of rule
                    if (iNode.equals(jNode)
                        || (trans1.getNode(iNode) == null && trans1.getNode(jNode) == null
                            && trans2.getNode(iNode) == null && trans2.getNode(jNode) == null)) {
                        //everything is okay
                    } else {
                        //not essential
                        return false;
                    }
                }
            }
        }
        //repeat the same for the edges
        ArrayList<HostEdge> edgeList = new ArrayList<HostEdge>(inclusion.edgeMap().keySet());
        for (int i = 0; i < edgeList.size(); i++) {
            for (int j = i + 1; j < edgeList.size(); j++) {
                HostEdge iEdge = edgeList.get(i);
                HostEdge jEdge = edgeList.get(j);
                //                System.out.println("iEdge=" + iEdge + "  jEdge=" + jEdge + "  equalUnderMatch="
                //                    + match.getEdge(iEdge).equals(match.getEdge(jEdge)) + "  "
                //                    + match.getEdge(iEdge) + "  " + match.getEdge(jEdge));
                if (inclusion.getEdge(iEdge).equals(inclusion.getEdge(jEdge))) {
                    //                    System.out.println("iEdgeAndjEdgeEqual=" + iEdge.equals(jEdge));
                    //                    System.out.println(transformation.getEdge(iEdge));
                    //                    System.out.println(transformation.getEdge(jEdge));
                    //check if iNode and jNode are equal or both in the domain of rule
                    if (iEdge.equals(jEdge)
                        || (trans1.getEdge(iEdge) == null && trans1.getEdge(jEdge) == null
                            && trans2.getEdge(iEdge) == null && trans2.getEdge(jEdge) == null)) {
                        //everything is okay
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //static int subsumed = 0;

    /**
     * 
     * @param pairs a set of critical pairs, 
     * @return
     */
    static ConfluenceStatus analyzePairSet(Set<CriticalPair> pairs, Grammar grammar) {
        if (pairs.isEmpty()) {
            //if the set is empty, then all pairs in the set are strictly confluent
            return ConfluenceStatus.STRICTLY_CONFLUENT;
        }
        ConfluenceStatus result = ConfluenceStatus.UNTESTED;
        assert checkPairSet(pairs);
        OrderedCriticalPairSet orderedSet = new OrderedCriticalPairSet(pairs);
        LinkedHashSet<ConfluentPair> confluentPairs = new LinkedHashSet<ConfluentPair>();

        System.out.println("orderedSet size :" + orderedSet.size());
        for (CriticalPair pair : orderedSet) {

            ConfluenceStatus status = ConfluenceStatus.UNTESTED;

            confluentPair: for (ConfluentPair confluentPair : confluentPairs) {
                if (confluentPair.getCriticalpair().getHostGraph().nodeCount() == pair.getHostGraph().nodeCount()) {
                    //since the morphism from the confluentpair must be surjective, it will also be injective (because the sizes are the same)
                    //therefore the morphism will be an isomorphism which means that the critical pairs are the same
                    continue;
                }
                assert confluentPair.getCriticalpair().getHostGraph().nodeCount() > pair.getHostGraph().nodeCount();

                HostGraphMorphism match = getInclusion(confluentPair.getCriticalpair(), pair);

                if (match == null) {
                    continue;
                }

                HostGraphMorphism transformation = confluentPair.getConfluentState().getMorphism();

                //check if the matches for the rules commute
                if (isDInjective(match, transformation)) {
                    status = ConfluenceStatus.STRICTLY_CONFLUENT;
                    pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
                    //remark: there is no need to add this pair to the set of confluent pairs

                    //subsumed++;
                    //System.out.println(subsumed);
                    //                    System.out.println("Inclusion found");
                    //                    System.out.println(confluentPair.getCriticalpair().getRule1().getFullName()
                    //                        + " - " + confluentPair.getCriticalpair().getRule2().getFullName());
                    //                    System.out.println(confluentPair.getCriticalpair().getHostGraph());
                    //                    System.out.println(pair.getHostGraph());
                    //                    System.out.println(match);
                    //                    System.out.println();
                    //                    System.out.println(transformation);
                    //                    System.out.println();

                    //                    ConfluenceStatus doubleCheck = getStrictlyConfluent(pair, grammar);
                    //                    if (doubleCheck != ConfluenceStatus.STRICTLY_CONFLUENT) {
                    //                        System.out.println(doubleCheck);
                    //                        saveGraphs(confluentPair.getCriticalpair().getHostGraph(),
                    //                            pair.getHostGraph());
                    //                        throw new RuntimeException();
                    //
                    //                    }
                    //assert getStrictlyConfluent(pair, grammar) == ConfluenceStatus.STRICTLY_CONFLUENT;
                    break confluentPair;
                } else {
                    //System.out.println("not d-injective");
                }
            }

            if (status == ConfluenceStatus.UNTESTED) {
                ConfluentPair confPair = getConfluentPair(pair, grammar, DEFAULTSEARCHDEPTH);
                if (confPair != null) {
                    confluentPairs.add(confPair);
                }
                status = pair.getStrictlyConfluent();
            }
            result = ConfluenceStatus.getWorstStatus(status, result);
        }
        int confluent = 0;
        for (CriticalPair pair : orderedSet) {
            if (pair.getStrictlyConfluent() == ConfluenceStatus.STRICTLY_CONFLUENT) {
                confluent++;
            }
        }
        //System.out.println("confluentPairs size :" + confluentPairs.size());
        //System.out.println("Total num confluent pairs :" + confluent);

        return result;
    }

    //    private static void saveGraphs(HostGraph g1, HostGraph g2) {
    //        try {
    //            Groove.saveGraph(g1, "host1");
    //            System.out.println(Groove.saveGraph(g2, "host2").getAbsolutePath());
    //        } catch (IOException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //    }

    private static HostGraphMorphism getInclusion(CriticalPair confluent, CriticalPair target) {
        HostGraphMorphism result = new HostGraphMorphism(target.getHostGraph().getFactory());

        assert confluent.getRule1().equals(target.getRule1());
        assert confluent.getRule2().equals(target.getRule2());

        if (buildHostGraphMorphism(result, confluent.getMatch1(), target.getMatch1())
            && buildHostGraphMorphism(result, confluent.getMatch2(), target.getMatch2())) {
            return result;
        } else {
            return null;
        }
    }

    private static boolean buildHostGraphMorphism(HostGraphMorphism morphism,
            RuleToHostMap confluentMatch, RuleToHostMap targetMatch) {
        for (RuleNode rn : confluentMatch.nodeMap().keySet()) {
            HostNode cHostNode = confluentMatch.getNode(rn);
            HostNode tHostNode = targetMatch.getNode(rn);

            //            if (rn instanceof VariableNode) {
            //                assert cHostNode instanceof ValueNode;
            //                assert tHostNode instanceof ValueNode;
            //                ValueNode cVal = (ValueNode) cHostNode;
            //                ValueNode tval = (ValueNode) tHostNode;
            //                if (!cVal.getTerm().equals(tval.getTerm())) {
            //                    //if the expressions are not the same, then we can not
            //                    //build an inclusion
            //                    return false;
            //                }
            //            }

            if (!morphism.nodeMap().containsKey(cHostNode)) {
                assert cHostNode != null;
                assert tHostNode != null;
                morphism.putNode(cHostNode, tHostNode);
            } else {
                //if the morphism already has a different target for cHostNode
                if (!morphism.nodeMap().get(cHostNode).equals(tHostNode)) {
                    return false;
                }
            }
        }

        assert morphism.nodeMap().values().containsAll(targetMatch.nodeMap().values());
        assert morphism.nodeMap().keySet().containsAll(confluentMatch.nodeMap().values());

        //repeat for edges
        for (RuleEdge re : confluentMatch.edgeMap().keySet()) {
            HostEdge cHostEdge = confluentMatch.getEdge(re);
            HostEdge tHostEdge = targetMatch.getEdge(re);
            if (!morphism.nodeMap().containsKey(cHostEdge)) {
                assert cHostEdge != null;
                assert tHostEdge != null;
                morphism.putEdge(cHostEdge, tHostEdge);
            } else {
                //if the morphism already has a different target for cHostNode
                if (!morphism.nodeMap().get(cHostEdge).equals(tHostEdge)) {
                    return false;
                }
            }
        }

        return true;
    }

    //    private static HostGraphMorphism compose(
    //            AElementMap<HostNode,HostEdge,RuleNode,RuleEdge> first,
    //            RuleToHostMap second) {
    //        HostGraphMorphism result = new HostGraphMorphism(second.getFactory());
    //
    //        for (HostNode hn : first.nodeMap().keySet()) {
    //            RuleNode rn = first.getNode(hn);
    //            if (rn != null && second.getNode(rn) != null) {
    //                result.putNode(hn, second.getNode(rn));
    //            }
    //        }
    //        for (HostEdge he : first.edgeMap().keySet()) {
    //            RuleEdge re = first.getEdge(he);
    //            if (re != null && second.getEdge(re) != null) {
    //                result.putEdge(he, second.getEdge(re));
    //            }
    //        }
    //
    //        return result;
    //    }

    private static boolean isDInjective(HostGraphMorphism match, HostGraphMorphism transformation) {
        ArrayList<HostNode> nodeList = new ArrayList<HostNode>(match.nodeMap().keySet());
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                HostNode iNode = nodeList.get(i);
                HostNode jNode = nodeList.get(j);
                if (match.getNode(iNode).equals(match.getNode(jNode))) {
                    //check if iNode and jNode are equal or both in the domain of rule
                    if (iNode.equals(jNode)
                        || (transformation.getNode(iNode) != null && transformation.getNode(jNode) != null)) {
                        //everything is okay
                    } else {
                        return false;
                    }
                }
            }
        }
        //repeat the same for the edges
        ArrayList<HostEdge> edgeList = new ArrayList<HostEdge>(match.edgeMap().keySet());
        for (int i = 0; i < edgeList.size(); i++) {
            for (int j = i + 1; j < edgeList.size(); j++) {
                HostEdge iEdge = edgeList.get(i);
                HostEdge jEdge = edgeList.get(j);
                //                System.out.println("iEdge=" + iEdge + "  jEdge=" + jEdge + "  equalUnderMatch="
                //                    + match.getEdge(iEdge).equals(match.getEdge(jEdge)) + "  "
                //                    + match.getEdge(iEdge) + "  " + match.getEdge(jEdge));
                if (match.getEdge(iEdge).equals(match.getEdge(jEdge))) {
                    //                    System.out.println("iEdgeAndjEdgeEqual=" + iEdge.equals(jEdge));
                    //                    System.out.println(transformation.getEdge(iEdge));
                    //                    System.out.println(transformation.getEdge(jEdge));
                    //check if iNode and jNode are equal or both in the domain of rule
                    if (iEdge.equals(jEdge)
                        || (transformation.getEdge(iEdge) != null && transformation.getEdge(jEdge) != null)) {
                        //everything is okay
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean checkPairSet(Set<CriticalPair> pairs) {
        if (pairs.isEmpty()) {
            return true;
        }
        Iterator<CriticalPair> it = pairs.iterator();
        CriticalPair pair = it.next();
        Rule rule1 = pair.getRule1();
        Rule rule2 = pair.getRule2();
        while (it.hasNext()) {
            pair = it.next();
            if (!pair.getRule1().equals(rule1) || !pair.getRule2().equals(rule2)) {
                return false;
            }
        }
        return true;

    }

}

class ConfluentPair {
    private final CriticalPair criticalpair;
    private final HostGraphWithMorphism confluentState;

    ConfluentPair(CriticalPair criticalpair, HostGraphWithMorphism confluentState) {
        this.criticalpair = criticalpair;
        this.confluentState = confluentState;
    }

    public CriticalPair getCriticalpair() {
        return this.criticalpair;
    }

    public HostGraphWithMorphism getConfluentState() {
        return this.confluentState;
    }

    //    private RuleGraph ruleGraph;
    //
    //    private AElementMap<HostNode,HostEdge,RuleNode,RuleEdge> hostToRuleMorphism;
    //
    //    RuleGraph getHostAsRuleGraph() {
    //        if (this.ruleGraph == null) {
    //            HostGraph hostGraph = this.criticalpair.getHostGraph();
    //            RuleFactory factory =
    //                RuleFactory.newInstance(hostGraph.getTypeGraph().getFactory());
    //            this.ruleGraph = new RuleGraph("hostToRule", factory);
    //            this.hostToRuleMorphism =
    //                new AElementMap<HostNode,HostEdge,RuleNode,RuleEdge>(factory) {
    //                    //default implementation
    //                };
    //
    //            for (HostNode hn : hostGraph.nodeSet()) {
    //                RuleNode newNode;
    //                if (hn instanceof DefaultHostNode) {
    //                    newNode =
    //                        factory.nodes(hn.getType(), false, null).createNode();
    //                } else if (hn instanceof ValueNode) {
    //                    ValueNode valNode = (ValueNode) hn;
    //                    newNode =
    //                        factory.createVariableNode(factory.getMaxNodeNr() + 1,
    //                            valNode.getTerm());
    //                } else {
    //                    //this wil not happen since HostNode only has the two above (direct) subtypes
    //                    assert false;
    //                    newNode = null;
    //                }
    //                this.ruleGraph.addNode(newNode);
    //                this.hostToRuleMorphism.putNode(hn, newNode);
    //            }
    //
    //            for (HostEdge he : hostGraph.edgeSet()) {
    //                RuleNode source = this.hostToRuleMorphism.getNode(he.source());
    //                RuleNode target = this.hostToRuleMorphism.getNode(he.target());
    //                RuleEdge newEdge =
    //                    new RuleEdge(source, new RuleLabel(he.label()),
    //                        he.getType(), target);
    //                this.ruleGraph.addEdge(newEdge);
    //                this.hostToRuleMorphism.putEdge(he, newEdge);
    //            }
    //        }
    //        return this.ruleGraph;
    //    }
    //
    //    AElementMap<HostNode,HostEdge,RuleNode,RuleEdge> getHostToRuleMorphism() {
    //        if (this.hostToRuleMorphism == null) {
    //            //compute the rulegraph with the morphism
    //            getHostAsRuleGraph();
    //        }
    //        return this.hostToRuleMorphism;
    //    }
}
