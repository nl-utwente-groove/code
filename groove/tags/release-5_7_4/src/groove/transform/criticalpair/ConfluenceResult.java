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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import groove.grammar.Grammar;
import groove.grammar.Rule;

/**
 * Utility class which allows checking whether a graph transformation system (grammar without hostgraph)
 * is stricty locally confluent.
 */
public class ConfluenceResult {

    //the grammar being analysed
    private Grammar grammar;

    private ConfluenceStatus status = ConfluenceStatus.UNTESTED;

    //true if the alternate "subsumption" method is being used
    private final boolean alternateMethod;

    /*
     * These untestedPairs should not be visible outside of this class, because the confluenceResult
     * Needs to know when pairs are tested
     */
    private LazyCriticalPairSet untestedPairs;
    private Set<CriticalPair> undecidedPairs = new LinkedHashSet<>();
    private Set<CriticalPair> nonConfluentPairs = new LinkedHashSet<>();

    /**
     * @return the number of pairs for which confluence has not yet been analysed
     */
    public int getSizeOfUntestedPairs() {
        return this.untestedPairs.size();
    }

    /**
     * Creates a new ConfluenceResult for the given grammar, the actual analysis is not yet started
     */
    public ConfluenceResult(Grammar grammar) {
        this(grammar, false);
    }

    /**
     * Creates a new ConfluenceResult for the given grammar, the actual analysis is not yet started
     * @param alternateMethod if true, then a slightly more efficient method for confluence analysis will be used.
     * Unfortunately this method may give false positives in a rare case
     * (when a pushout does not exist for some intermediate tranformation)
     */
    public ConfluenceResult(Grammar grammar, boolean alternateMethod) {
        this.grammar = grammar;
        Set<Rule> rules = grammar.getAllRules();
        for (Rule rule : rules) {
            if (!CriticalPair.canComputePairs(rule)) {
                throw new IllegalArgumentException("Cannot compute critical pairs for rule '"
                    + rule.getQualName()
                    + "', because the algorithm can not compute Critical pairs for this type of rule");
            }
        }
        this.untestedPairs = new LazyCriticalPairSet(rules);
        this.alternateMethod = alternateMethod;
    }

    /**
     * @return this.status
     */
    public ConfluenceStatus getStatus() {
        return this.status;
    }

    /**
     * @return this.grammar
     */
    public Grammar getGrammar() {
        return this.grammar;
    }

    /**
     * Returns the critical pairs for which confluence could not be decided.
     *
     * Untested pairs are not included
     */
    public Set<CriticalPair> getUndecidedPairs() {
        return this.undecidedPairs;
    }

    /**
     * Returns the critical pairs which are not strictly locally confluent.
     *
     * Untested pairs are not included
     */
    public Set<CriticalPair> getNonConfluentPairs() {
        return this.nonConfluentPairs;
    }

    /**
     * Creates a new ConfluenceResult for grammar, and starts analysis until the first evidence
     * for a non-strictly locally confluent pair has been found
     */
    public static ConfluenceResult checkStrictlyConfluent(Grammar grammar) {
        return checkStrictlyConfluent(grammar, false);
    }

    /**
     * Creates a new ConfluenceResult for grammar, and starts analysis until the first evidence
     * for a non-strictly locally confluent pair has been found
     * @param alternateMethod if true, then a slightly more efficient method for confluence analysis will be used.
     * Unfortunately this method may give false positives in a rare case
     * (when a pushout does not exist for some intermediate tranformation)
     */
    public static ConfluenceResult checkStrictlyConfluent(Grammar grammar,
        boolean alternateMethod) {
        return checkStrictlyConfluent(grammar,
            ConfluenceStatus.NOT_STICTLY_CONFLUENT,
            alternateMethod);
    }

    /**
     * Creates a new ConfluenceResult for grammar, and starts analysis until the first evidence
     * for critical pair with the status "target" (if not UNTESTED or STRICTLY_CONFLUENT) has been found
     * @param alternateMethod if true, then a slightly more efficient method for confluence analysis will be used.
     * Unfortunately this method may give false positives in a rare case
     * (when a pushout does not exist for some intermediate tranformation)
     * @param target if(target == STRICTLY_CONFLUENT) then a full analysis is started.
     * If (target == UNTESTED) then no analysis is started.
     * Otherwise analysis is started until the first pair with the ConfluenceStatus target has been found
     */
    public static ConfluenceResult checkStrictlyConfluent(Grammar grammar, ConfluenceStatus target,
        boolean alternateMethod) {
        ConfluenceResult result = new ConfluenceResult(grammar, alternateMethod);
        result.analyzeUntil(target);
        return result;
    }

    /**
     * target if(target == STRICTLY_CONFLUENT) then a full analysis is started.
     * If (target == UNTESTED) then no analysis is started.
     * Otherwise analysis is started until the first pair with the ConfluenceStatus target has been found
     */
    public void analyzeUntil(ConfluenceStatus target) {
        if (target == ConfluenceStatus.STRICTLY_CONFLUENT) {
            analyzeAll();
        } else if (target == ConfluenceStatus.UNTESTED) {
            //nothing needs to be done
        } else if (target == this.status || (target == ConfluenceStatus.UNDECIDED
            && this.status == ConfluenceStatus.NOT_STICTLY_CONFLUENT)) {
            //nothing needs to be done
        } else {
            Iterator<CriticalPair> it = this.untestedPairs.iterator();
            boolean done = false;
            while (it.hasNext() && !done) {
                CriticalPair pair = it.next();
                done = updateStatus(pair, target);
                //remove the pair from the untested set
                it.remove();
            }
            if (this.status == ConfluenceStatus.UNTESTED && this.undecidedPairs.isEmpty()) {
                //everything has been analyzed but all pairs are confluent
                this.status = ConfluenceStatus.STRICTLY_CONFLUENT;
            }
        }

    }

    /**
     * Analyse all critical pairs in the grammar
     */
    public void analyzeAll() {
        Iterator<Set<CriticalPair>> setIt = this.untestedPairs.setIterator();
        while (setIt.hasNext()) {
            Set<CriticalPair> pairSet = setIt.next();
            if (this.alternateMethod) {
                ConfluenceAnalyzer.analysePairSet(pairSet, this.grammar);
                for (CriticalPair pair : pairSet) {
                    //the confluenceStatus will not be computed again
                    updateStatus(pair);
                }
            } else {
                Iterator<CriticalPair> pairIt = pairSet.iterator();
                while (pairIt.hasNext()) {
                    CriticalPair pair = pairIt.next();
                    updateStatus(pair);
                    //remove the pair from the untested set
                    pairIt.remove();
                }
            }
            //remove all pairs in the set from the setIterator
            setIt.remove();
        }
        assert this.untestedPairs.isEmpty();
        if (this.status == ConfluenceStatus.UNTESTED) {
            //everything has been analyzed but all pairs are confluent
            this.status = ConfluenceStatus.STRICTLY_CONFLUENT;
        }
    }

    /**
     * Updates the set of undecidedPairs and nonConfluentPairs
     */
    private void updateStatus(CriticalPair pair) {
        updateStatus(pair, null);
    }

    /**
     * Updates the set of undecidedPairs and nonConfluentPairs
     * @param target may be null, if NOT_STICTLY_CONFLUENT or UNDECIDED then the result will
     * be true if the critical pair has this ConfluenceStatus
     * @return true if and only if target is equal to either NOT_STICTLY_CONFLUENT or UNDECIDED
     * and pair.getStrictlyConfluent(this.grammar) is equal to target
     */
    private boolean updateStatus(CriticalPair pair, ConfluenceStatus target) {
        boolean result = false;
        ConfluenceStatus pairStatus = pair.getStrictlyConfluent(this.grammar);
        switch (pairStatus) {
        case STRICTLY_CONFLUENT:
            //do nothing
            break;
        case UNDECIDED:
            //            System.out.println("UNDECIDEDFOUND!!!! "
            //                + getUndecidedPairs().size());
            if (this.status == ConfluenceStatus.STRICTLY_CONFLUENT
                || this.status == ConfluenceStatus.UNTESTED) {
                this.status = ConfluenceStatus.UNDECIDED;
            }
            this.undecidedPairs.add(pair);
            if (target == ConfluenceStatus.UNDECIDED) {
                result = true;
            }
            break;
        case NOT_STICTLY_CONFLUENT:
            this.status = ConfluenceStatus.NOT_STICTLY_CONFLUENT;
            this.nonConfluentPairs.add(pair);
            if (target == ConfluenceStatus.UNDECIDED
                || target == ConfluenceStatus.NOT_STICTLY_CONFLUENT) {
                result = true;
            }
            break;
        case UNTESTED:
            throw new RuntimeException("Test for confluence failed: " + pairStatus);
        default:
            //can not happen unless the pairStatus enum is modified
            throw new RuntimeException("Unknown ConfluenceStatus: " + pairStatus);
        }
        return result;
    }
}