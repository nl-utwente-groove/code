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

import groove.grammar.Grammar;
import groove.grammar.Rule;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConfluenceResult {

    private Grammar grammar;

    private ConfluenceStatus status = ConfluenceStatus.UNTESTED;

    /*
     * These untestedPairs should not be visible outside of this class, because the confluenceResult
     * Needs to know when pairs are tested
     */
    private Set<CriticalPair> untestedPairs;
    private Set<CriticalPair> undecidedPairs = new HashSet<CriticalPair>();
    private Set<CriticalPair> nonConfluentPairs = new HashSet<CriticalPair>();

    public int getSizeOfUntestedPairs() {
        return this.untestedPairs.size();
    }

    public ConfluenceResult(Grammar grammar) {
        this.grammar = grammar;
        Set<Rule> rules = grammar.getAllRules();
        for (Rule rule : rules) {
            if (!CriticalPair.canComputePairs(rule)) {
                throw new IllegalArgumentException(
                    "Cannot compute critical pairs for rule '"
                        + rule.getFullName()
                        + "', because the algorithm can not compute Critical pairs for this type of rule");
            }
        }
        this.untestedPairs = new LazyCriticalPairSet(rules);
    }

    public ConfluenceStatus getStatus() {
        return this.status;
    }

    public Grammar getGrammar() {
        return this.grammar;
    }

    public Set<CriticalPair> getUndecidedPairs() {
        return this.undecidedPairs;
    }

    public Set<CriticalPair> getNonConfluentPairs() {
        return this.nonConfluentPairs;
    }

    public static ConfluenceResult checkStrictlyConfluent(Grammar grammar) {
        return checkStrictlyConfluent(grammar, ConfluenceStatus.NOTCONFLUENT);
    }

    public static ConfluenceResult checkStrictlyConfluent(Grammar grammar,
            ConfluenceStatus target) {
        ConfluenceResult result = new ConfluenceResult(grammar);
        result.analyzeUntil(target);
        return result;
    }

    public void analyzeUntil(ConfluenceStatus target) {
        if (target == ConfluenceStatus.CONFLUENT) {
            analyzeAll();
        } else if (target == ConfluenceStatus.UNTESTED) {
            //nothing needs to be done
        } else if (target == this.status
            || (target == ConfluenceStatus.UNDECIDED && this.status == ConfluenceStatus.NOTCONFLUENT)) {
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
            if (this.status == ConfluenceStatus.UNTESTED
                && this.undecidedPairs.isEmpty()) {
                //everything has been analyzed but all pairs are confluent
                this.status = ConfluenceStatus.CONFLUENT;
            }
        }

    }

    public void analyzeAll() {
        Iterator<CriticalPair> it = this.untestedPairs.iterator();
        while (it.hasNext()) {
            CriticalPair pair = it.next();
            updateStatus(pair);
            //remove the pair from the untested set
            it.remove();
        }
        if (!this.undecidedPairs.isEmpty()) {
            System.out.println(this.untestedPairs.size());
            assert this.untestedPairs.isEmpty();
        }
        if (this.status == ConfluenceStatus.UNTESTED) {
            //everything has been analyzed but all pairs are confluent
            this.status = ConfluenceStatus.CONFLUENT;
        }
    }

    /**
     * 
     * @param pair
     */
    private void updateStatus(CriticalPair pair) {
        updateStatus(pair, null);
    }

    private boolean updateStatus(CriticalPair pair, ConfluenceStatus target) {
        boolean result = false;
        ConfluenceStatus pairStatus = pair.getStrictlyConfluent(this.grammar);
        switch (pairStatus) {
        case CONFLUENT:
            //do nothing
            break;
        case UNDECIDED:
            //            System.out.println("UNDECIDEDFOUND!!!! "
            //                + getUndecidedPairs().size());
            if (this.status == ConfluenceStatus.CONFLUENT
                || this.status == ConfluenceStatus.UNTESTED) {
                this.status = ConfluenceStatus.UNDECIDED;
            }
            this.undecidedPairs.add(pair);
            if (target == ConfluenceStatus.UNDECIDED) {
                result = true;
            }
            break;
        case NOTCONFLUENT:
            this.status = ConfluenceStatus.NOTCONFLUENT;
            this.nonConfluentPairs.add(pair);
            if (target == ConfluenceStatus.UNDECIDED
                || target == ConfluenceStatus.NOTCONFLUENT) {
                result = true;
            }
            break;
        case UNTESTED:
            throw new RuntimeException("Test for confluence failed: "
                + pairStatus);
        default:
            //can not happen unless the pairStatus enum is modified
            throw new RuntimeException("Unknown ConfluenceStatus: "
                + pairStatus);
        }
        return result;
    }
}