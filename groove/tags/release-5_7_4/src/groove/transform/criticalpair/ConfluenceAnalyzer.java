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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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

/**
 * @author Ruud
 * @version $Revision $
 */
class ConfluenceAnalyzer {

    /**
     * Default search depth for confluence analysis
     */
    private static final int DEFAULTSEARCHDEPTH = 100;

    private static IsoChecker isoChecker = IsoChecker.getInstance(true);

    /**
     * Checks if the given CriticalPair is strictly locally confluent
     * Strict local confluence means that the pair of direct transformations is locally
     * confluent such that the transformation morphisms commute.
     * @return ConfluenceStatus.STRICTLY_CONFLUENT only if the pair is confluent
     */
    static ConfluenceStatus getStrictlyConfluent(CriticalPair pair, Grammar grammar) {
        return getStrictlyConfluent(pair, grammar, DEFAULTSEARCHDEPTH);
    }

    /**
     * Checks if the given CriticalPair is strictly locally confluent
     * Strict local confluence means that the pair of direct transformations is locally
     * confluent such that the transformation morphisms commute.
     * @return ConfluenceStatus.STRICTLY_CONFLUENT only if the pair is confluent
     */
    static ConfluenceStatus getStrictlyConfluent(CriticalPair pair, Grammar grammar,
        int searchDepth) {
        //analyse if the pair is strictly confluent
        getConfluentPair(pair, grammar, searchDepth);
        //the result is saved in the critical pair, return this result
        return pair.getStrictlyConfluent();
    }

    /**
     * Checks if the given CriticalPair is strictly locally confluent, if this is the case,
     * then the ConfluentPair (evidence for strict local confluence) will be returned.
     * If the pair is not strictly locally confluent (or if it cannot be decided) then the result will be null
     *
     * The result of the confluene analysis is also saved in the critical pair
     */
    private static ConfluentPair getConfluentPair(CriticalPair pair, Grammar grammar,
        int searchDepth) {
        Set<HostGraphWithMorphism> oldStates1 = new HashSet<>();
        Set<HostGraphWithMorphism> oldStates2 = new HashSet<>();
        Set<HostGraphWithMorphism> newStates1 = new HashSet<>();
        Set<HostGraphWithMorphism> newStates2 = new HashSet<>();

        RuleApplication app1 = pair.getRuleApplication1();
        RuleApplication app2 = pair.getRuleApplication2();
        HostGraphWithMorphism hwm1 =
            new HostGraphWithMorphism(app1.getTarget(), app1.getMorphism());
        HostGraphWithMorphism hwm2 =
            new HostGraphWithMorphism(app2.getTarget(), app2.getMorphism());

        if (isConfluent(hwm1, hwm2)) {
            //the pair was already strictly confluent
            pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
            return new ConfluentPair(pair, hwm1);
        }
        newStates1.add(hwm1);
        newStates2.add(hwm2);

        //loop as long as either newStates1 or newStates2 is nonempty
        while (!newStates1.isEmpty() || !newStates2.isEmpty()) {
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

    /**
     * For every element of states compute all possible rule applications
     * @param states the states for which the next states will be computed
     * @param grammar the grammar which contains the rules which can be applied
     * @return A set of HostGraphWithMorphism states which can be reached in a single step from an element of "states"
     */
    private static Set<HostGraphWithMorphism> computeNewStates(Set<HostGraphWithMorphism> states,
        Grammar grammar) {
        Set<Rule> rules = grammar.getAllRules();
        Set<HostGraphWithMorphism> result = new HashSet<>();
        for (HostGraphWithMorphism state : states) {
            Record record = new Record(grammar, state.getHostGraph()
                .getFactory());
            for (Rule rule : rules) {
                Collection<Proof> matches = rule.getAllMatches(state.getHostGraph(), null);
                for (Proof proof : matches) {
                    RuleEvent event = proof.newEvent(record);
                    RuleApplication app = new RuleApplication(event, state.getHostGraph());
                    result.add(new HostGraphWithMorphism(app.getTarget(), state.getMorphism()
                        .then(app.getMorphism())));
                }
            }
        }
        return result;
    }

    /**
     * Checks if there exists a pair (a, b) in (first X second) such that isConfluent(a,b)
     * @return a if there exists a pair (a, b) such that isConfluent(a,b)
     * If no such pair exists then null will be returned
     */
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

    /**
     * Checks if the hostGraphWithMorphism objects are isomorphic
     * @return true if and only if there exists an isomorphism iso from hwm1.getHostGraph() to hwm2.getHostGraph()
     * such that hwm1.getMorphism().then(iso) equals hwm2.getMorphism() (i.e., the morphisms commute)
     */
    private static boolean isConfluent(HostGraphWithMorphism hwm1, HostGraphWithMorphism hwm2) {

        if (hwm1.getMorphism()
            .nodeMap()
            .size() != hwm2.getMorphism()
                .nodeMap()
                .size()
            || hwm1.getMorphism()
                .edgeMap()
                .size() != hwm2.getMorphism()
                    .edgeMap()
                    .size()) {
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
            HostGraphMorphism transformation1 = hwm1.getMorphism()
                .then(isoMorphism);
            HostGraphMorphism transformation2 = hwm2.getMorphism();
            result = transformation1.equals(transformation2);
            if (!result) {
                isoMorphism =
                    isoChecker.getIsomorphism(hwm1.getHostGraph(), hwm2.getHostGraph(), isoState);
            }

            if (isoCount > 10000) {
                //an extreme amount of isomorphisms has been found
                //however none of these commute, stop searching

                /* TODO Compute partial isomorphism such that the IsomorphismChecker can continue the search
                 *
                 * Using the two morphisms we can compute a partial isomorphism
                 * (the part from which we require that it is in the isomorphism)
                 * The if the IsomorphismChecker supports expanding an exising morphism
                 * then the IsomorphismChecker will either find isomorphism(s) such that
                 * transformation1.equals(transformation2), otherwise it will not find an isomoprhism at all
                 */
                return false;
            }
        }

        return result;
    }

    /**
     * Analyses a set of critical pairs. The "subsumption" method will be used to try to avoid analysis of all pairs.
     * In some cases the strict local confluence of a (smaller) pair can be implied by the strict local confluence of a (larger) pair.
     * @param pairs a set of critical pairs (for the same rules)
     */
    static ConfluenceStatus analysePairSet(Set<CriticalPair> pairs, Grammar grammar) {
        if (pairs.isEmpty()) {
            //if the set is empty, then all pairs in the set are strictly confluent
            return ConfluenceStatus.STRICTLY_CONFLUENT;
        }
        ConfluenceStatus result = ConfluenceStatus.UNTESTED;
        assert checkPairSet(pairs);
        //order the critical pair by the size of their host graph
        OrderedCriticalPairSet orderedSet = new OrderedCriticalPairSet(pairs);

        //Set containing all pairs which were confluent, together with a transformation morphism that made the pair confluent
        LinkedHashSet<ConfluentPair> confluentPairs = new LinkedHashSet<>();

        for (CriticalPair pair : orderedSet) {

            ConfluenceStatus status = ConfluenceStatus.UNTESTED;

            confluentPair: for (ConfluentPair confluentPair : confluentPairs) {
                if (confluentPair.getCriticalpair()
                    .getHostGraph()
                    .nodeCount() == pair.getHostGraph()
                        .nodeCount()) {
                    //since the morphism from the confluent pair must be surjective, it will also be injective (because the sizes are the same)
                    //therefore the morphism will be an isomorphism which means that the critical pairs are the same
                    continue;
                }
                assert confluentPair.getCriticalpair()
                    .getHostGraph()
                    .nodeCount() > pair.getHostGraph()
                        .nodeCount();

                HostGraphMorphism match =
                    getEmbeddingMorphism(confluentPair.getCriticalpair(), pair);

                if (match == null) {
                    continue;
                }

                HostGraphMorphism transformation = confluentPair.getConfluentState()
                    .getMorphism();

                //check if the matches for the rules commute
                if (isDInjective(match, transformation)) {
                    status = ConfluenceStatus.STRICTLY_CONFLUENT;
                    pair.setStrictlyConfluent(ConfluenceStatus.STRICTLY_CONFLUENT, grammar);
                    //remark: there is no need to add this pair to the set of confluent pairs

                    //this assertion may fail in come cases: this is because the one of the transformations
                    //which needs to be applied to show that "pair" is confluent, is actually not a pushout
                    assert getStrictlyConfluent(pair,
                        grammar) == ConfluenceStatus.STRICTLY_CONFLUENT;
                    break confluentPair;
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

        return result;
    }

    /**
     * Searches for an embedding morphism (from confluent to target) which embeds the CriticalPair target in the
     * CriticalPair confluent. If the result is non-null then (confluent.getMatchX() andThen result) equals target.getMatchX()
     */
    private static HostGraphMorphism getEmbeddingMorphism(CriticalPair confluent,
        CriticalPair target) {
        HostGraphMorphism result = new HostGraphMorphism(target.getHostGraph()
            .getFactory());

        assert confluent.getRule1()
            .equals(target.getRule1());
        assert confluent.getRule2()
            .equals(target.getRule2());

        if (buildHostGraphMorphism(result, confluent.getMatch1(), target.getMatch1())
            && buildHostGraphMorphism(result, confluent.getMatch2(), target.getMatch2())) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * Adds new mappings to morphism such that (confluentMatch andThen morphism) and targetMatch commute
     * @return false if morphism could not be expanded such that (confluentMatch andThen morphism) and targetMatch commute.
     * Return true if morphism was succesfully expanded
     */
    private static boolean buildHostGraphMorphism(HostGraphMorphism morphism,
        RuleToHostMap confluentMatch, RuleToHostMap targetMatch) {

        //first add node mappings
        for (RuleNode rn : confluentMatch.nodeMap()
            .keySet()) {
            HostNode cHostNode = confluentMatch.getNode(rn);
            HostNode tHostNode = targetMatch.getNode(rn);

            if (!morphism.nodeMap()
                .containsKey(cHostNode)) {
                assert cHostNode != null;
                assert tHostNode != null;
                morphism.putNode(cHostNode, tHostNode);
            } else {
                //if the morphism already has a different target for cHostNode
                if (!morphism.nodeMap()
                    .get(cHostNode)
                    .equals(tHostNode)) {
                    return false;
                }
            }
        }

        assert morphism.nodeMap()
            .values()
            .containsAll(targetMatch.nodeMap()
                .values());
        assert morphism.nodeMap()
            .keySet()
            .containsAll(confluentMatch.nodeMap()
                .values());

        //repeat process for edges
        for (RuleEdge re : confluentMatch.edgeMap()
            .keySet()) {
            HostEdge cHostEdge = confluentMatch.getEdge(re);
            HostEdge tHostEdge = targetMatch.getEdge(re);
            if (!morphism.edgeMap()
                .containsKey(cHostEdge)) {
                assert cHostEdge != null;
                assert tHostEdge != null;
                morphism.putEdge(cHostEdge, tHostEdge);
            } else {
                //if the morphism already has a different target for cHostNode
                if (!morphism.edgeMap()
                    .get(cHostEdge)
                    .equals(tHostEdge)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if match is d-injective w.r.t. transformation.
     * @return true if match is injective on all elements which are deleted in transformation
     */
    private static boolean isDInjective(HostGraphMorphism match, HostGraphMorphism transformation) {
        ArrayList<HostNode> nodeList = new ArrayList<>(match.nodeMap()
            .keySet());
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                HostNode iNode = nodeList.get(i);
                HostNode jNode = nodeList.get(j);
                if (match.getNode(iNode)
                    .equals(match.getNode(jNode))) {
                    //check if iNode and jNode are equal or both in the domain of rule
                    if (iNode.equals(jNode) || (transformation.getNode(iNode) != null
                        && transformation.getNode(jNode) != null)) {
                        //everything is okay
                    } else {
                        return false;
                    }
                }
            }
        }
        //repeat the same for the edges
        ArrayList<HostEdge> edgeList = new ArrayList<>(match.edgeMap()
            .keySet());
        for (int i = 0; i < edgeList.size(); i++) {
            for (int j = i + 1; j < edgeList.size(); j++) {
                HostEdge iEdge = edgeList.get(i);
                HostEdge jEdge = edgeList.get(j);
                if (match.getEdge(iEdge)
                    .equals(match.getEdge(jEdge))) {
                    //check if iNode and jNode are equal or both in the domain of rule
                    if (iEdge.equals(jEdge) || (transformation.getEdge(iEdge) != null
                        && transformation.getEdge(jEdge) != null)) {
                        //everything is okay
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Check if every critical pair in the set is a critical pair for the same rules
     */
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
            if (!pair.getRule1()
                .equals(rule1)
                || !pair.getRule2()
                    .equals(rule2)) {
                return false;
            }
        }
        return true;

    }

}

/**
 * Tuple of a (strictly locally confluent) critical pair with the HostGraphWithMorphism that made the pair confluent
 *
 * @author Ruud Welling
 */
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
}
