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
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.transform.Proof;
import groove.transform.Record;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
    static ConfluenceStatus getStrictlyConfluent(CriticalPair pair,
            Grammar grammar) {
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
    static ConfluenceStatus getStrictlyConfluent(CriticalPair pair,
            Grammar grammar, int searchDepth) {
        Set<HostGraphWithMorphism> oldStates1 =
            new HashSet<HostGraphWithMorphism>();
        Set<HostGraphWithMorphism> oldStates2 =
            new HashSet<HostGraphWithMorphism>();
        Set<HostGraphWithMorphism> newStates1 =
            new HashSet<HostGraphWithMorphism>();
        Set<HostGraphWithMorphism> newStates2 =
            new HashSet<HostGraphWithMorphism>();

        RuleApplication app1 = pair.getRuleApplication1();
        RuleApplication app2 = pair.getRuleApplication2();
        HostGraphWithMorphism hwm1 =
            new HostGraphWithMorphism(app1.getTarget(), app1.getMorphism());
        HostGraphWithMorphism hwm2 =
            new HostGraphWithMorphism(app2.getTarget(), app2.getMorphism());

        if (isConfluent(hwm1, hwm2)) {
            //the pair was already strictly confluent
            System.out.println("Directly Confluent");
            return ConfluenceStatus.CONFLUENT;
        }
        newStates1.add(hwm1);
        newStates2.add(hwm2);

        //loop as long as either newStates1 or newStates2 is nonempty
        while (!newStates1.isEmpty() || !newStates2.isEmpty()) {
            System.out.print("*");
            //add the new states to the old states
            oldStates1.addAll(newStates1);
            oldStates2.addAll(newStates2);

            //create the sets of next states
            Set<HostGraphWithMorphism> nextStates1 =
                computeNewStates(newStates1, grammar);
            if (containsConfluentStatePair(nextStates1, oldStates2)) {
                pair.setStrictlyConfluent(ConfluenceStatus.CONFLUENT, grammar);
                return ConfluenceStatus.CONFLUENT;
            }
            Set<HostGraphWithMorphism> nextStates2 =
                computeNewStates(newStates2, grammar);
            if (containsConfluentStatePair(nextStates1, nextStates2)
                | containsConfluentStatePair(oldStates1, nextStates2)) {
                pair.setStrictlyConfluent(ConfluenceStatus.CONFLUENT, grammar);
                return ConfluenceStatus.CONFLUENT;
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
                return ConfluenceStatus.UNDECIDED;
            }
        }
        //all states have been analyzed however no proof for strict local confluence has been found
        pair.setStrictlyConfluent(ConfluenceStatus.NOTCONFLUENT, grammar);
        return ConfluenceStatus.NOTCONFLUENT;
    }

    private static Set<HostGraphWithMorphism> computeNewStates(
            Set<HostGraphWithMorphism> states, Grammar grammar) {
        Set<Rule> rules = grammar.getAllRules();
        Set<HostGraphWithMorphism> result =
            new HashSet<HostGraphWithMorphism>();
        for (HostGraphWithMorphism state : states) {
            Record record =
                new Record(grammar, state.getHostGraph().getFactory());
            for (Rule rule : rules) {
                Collection<Proof> matches =
                    rule.getAllMatches(state.getHostGraph(), null);
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
                    //TODO unsure if it matters whether the argument is record or null
                    RuleEvent event = proof.newEvent(record);
                    RuleApplication app =
                        new RuleApplication(event, state.getHostGraph());
                    result.add(new HostGraphWithMorphism(app.getTarget(),
                        state.getMorphism().then(app.getMorphism())));
                }
            }
        }
        return result;
    }

    private static boolean containsConfluentStatePair(
            Set<HostGraphWithMorphism> first, Set<HostGraphWithMorphism> second) {
        System.out.print(".");
        for (HostGraphWithMorphism hwm1 : first) {
            for (HostGraphWithMorphism hwm2 : second) {
                if (isConfluent(hwm1, hwm2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isConfluent(HostGraphWithMorphism hwm1,
            HostGraphWithMorphism hwm2) {
        Morphism<HostNode,HostEdge> isoMorphism =
            isoChecker.getIsomorphism(hwm1.getHostGraph(), hwm2.getHostGraph());
        if (isoMorphism != null) {
            //The transformations are confluent, check strictness
            HostGraphMorphism transformation1 =
                hwm1.getMorphism().then(isoMorphism);
            HostGraphMorphism transformation2 = hwm2.getMorphism();
            return transformation1.equals(transformation2);
        }
        return false;
    }
}
