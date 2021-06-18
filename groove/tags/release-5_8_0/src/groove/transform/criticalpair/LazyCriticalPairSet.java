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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.grammar.Rule;

/**
 * @author Ruud Welling
 * A Lazy Set of CriticalPairs, the critical pairs for every pair of rules are computed on demand
 */
class LazyCriticalPairSet implements Set<CriticalPair> {

    /**
     * The set of critical pair which have already been computed
     */
    private Map<RuleTuple,LinkedHashSet<CriticalPair>> pairMap =
        new LinkedHashMap<>();

    /**
     * Set of ruleTuples for which the critical pairs still need to be computed
     */
    private final Set<RuleTuple> ruleTuplesToProcess;

    Set<CriticalPair> getPairs(Rule rule1, Rule rule2) {
        return getPairs(new RuleTuple(rule1, rule2));
    }

    Set<CriticalPair> getPairs(RuleTuple tuple) {
        if (this.ruleTuplesToProcess.contains(tuple)) {
            computePairs(tuple);
        }
        return this.pairMap.get(tuple);
    }

    Set<RuleTuple> getRuleTuples() {
        return this.pairMap.keySet();
    }

    /**
     * Creates a new set of CriticalPairs for rules
     * @param rules the rules for which critical pairs should be computed
     */
    LazyCriticalPairSet(Set<Rule> rules) {
        List<Rule> ruleList = new ArrayList<>(rules);
        this.ruleTuplesToProcess = new LinkedHashSet<>();
        for (int i = 0; i < ruleList.size(); i++) {
            for (int j = i; j < ruleList.size(); j++) {
                RuleTuple tuple = new RuleTuple(ruleList.get(i), ruleList.get(j));
                this.ruleTuplesToProcess.add(tuple);
                this.pairMap.put(tuple, null);
            }
        }
    }

    /**
     * Computes more critical pairs for this set
     * @return a nonempty set of Critical pairs if there were more critical pairs.
     * Otherwise an empty set (this means that this.ruleTuples.isEmpty after this call)
     */
    private Set<CriticalPair> computeMorePairs() {
        LinkedHashSet<CriticalPair> result = new LinkedHashSet<>(0);
        if (!this.ruleTuplesToProcess.isEmpty()) {
            Iterator<RuleTuple> it = this.ruleTuplesToProcess.iterator();
            while (result.isEmpty() && it.hasNext()) {
                RuleTuple nextTuple = it.next();
                result = CriticalPair.computeCriticalPairs(nextTuple.rule1, nextTuple.rule2);
                //Add the new pairs to the internal set
                this.pairMap.put(nextTuple, result);
                //remove the tuple
                it.remove();
            }
        }
        //return the new pairs
        return result;
    }

    private Set<CriticalPair> computePairs(RuleTuple tuple) {
        Set<CriticalPair> result;
        if (!this.ruleTuplesToProcess.remove(tuple)) {
            result = Collections.emptySet();
        } else {
            result = CriticalPair.computeCriticalPairs(tuple.rule1, tuple.rule2);
        }
        return result;
    }

    private void computeAllPairs() {
        Iterator<RuleTuple> it = this.ruleTuplesToProcess.iterator();
        while (it.hasNext()) {
            RuleTuple nextTuple = it.next();
            this.pairMap.put(nextTuple,
                CriticalPair.computeCriticalPairs(nextTuple.rule1, nextTuple.rule2));
            it.remove();
        }
        //make sure that there are no remaining ruleTuples
        this.ruleTuplesToProcess.clear();
    }

    @Override
    public boolean add(CriticalPair e) {
        throw new UnsupportedOperationException("Not supported for LazyCriticalPairSet");
    }

    @Override
    public boolean addAll(Collection<? extends CriticalPair> c) {
        throw new UnsupportedOperationException("Not supported for LazyCriticalPairSet");
    }

    @Override
    public void clear() {
        this.pairMap.clear();
        this.ruleTuplesToProcess.clear();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof CriticalPair) {
            CriticalPair pair = (CriticalPair) o;
            RuleTuple tuple = new RuleTuple(pair.getRule1(), pair.getRule2());
            if (this.ruleTuplesToProcess.contains(tuple)) {
                return computePairs(tuple).contains(pair);
            } else {
                return this.pairMap.containsKey(tuple) && this.pairMap.get(tuple)
                    .contains(pair);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!this.contains(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        for (Set<CriticalPair> pairSet : this.pairMap.values()) {
            if (!pairSet.isEmpty()) {
                return false;
            }
        }

        boolean pairsFound = false;
        while (this.ruleTuplesToProcess.isEmpty() && !pairsFound) {
            pairsFound |= computeMorePairs().isEmpty();
        }
        return pairsFound;
    }

    /**
     * Return an Iterator which iterates over the Sets of critical pairs
     * Every Set of CriticalPairs will consist of CriticalPairs where rule1 and rule2 are the same
     */
    Iterator<Set<CriticalPair>> setIterator() {
        return new Iterator<Set<CriticalPair>>() {

            private RuleTuple current = null;
            private Iterator<RuleTuple> tupleIt = LazyCriticalPairSet.this.pairMap.keySet()
                .iterator();

            @Override
            public boolean hasNext() {
                return this.tupleIt.hasNext();
            }

            @Override
            public Set<CriticalPair> next() {
                this.current = this.tupleIt.next();
                Set<CriticalPair> result = LazyCriticalPairSet.this.getPairs(this.current);
                if (result == null) {
                    //do not return null
                    result = Collections.emptySet();
                }
                return result;
            }

            @Override
            public void remove() {
                this.tupleIt.remove();
            }

        };
    }

    @Override
    public Iterator<CriticalPair> iterator() {
        return new Iterator<CriticalPair>() {

            private CriticalPair last = null;

            //true if currentIt has been replaced with a new iterator
            //this is needed to implement remove() correctly
            private boolean currentItReplaced = false;

            Iterator<RuleTuple> keyIt = LazyCriticalPairSet.this.pairMap.keySet()
                .iterator();

            Iterator<CriticalPair> currentIt =
                //initialize emptySet iterator
                new HashSet<CriticalPair>().iterator();

            @Override
            public boolean hasNext() {
                while (this.keyIt.hasNext() && !this.currentIt.hasNext()) {
                    Set<CriticalPair> pairs =
                        LazyCriticalPairSet.this.pairMap.get(this.keyIt.next());
                    if (pairs == null) {
                        continue;
                    } else {
                        this.currentIt = pairs.iterator();
                    }
                    this.currentItReplaced = true;
                }
                //if the iterator is still empty, then compute more pairs if possible
                while (!this.currentIt.hasNext()
                    && !LazyCriticalPairSet.this.ruleTuplesToProcess.isEmpty()) {
                    this.currentIt = LazyCriticalPairSet.this.computeMorePairs()
                        .iterator();
                    this.currentItReplaced = true;
                }
                return this.currentIt.hasNext();
            }

            @Override
            public CriticalPair next() {
                /* hasNext() ensures that currentIt is replaced
                 * with a new iterator if currentIt.isEmpty()
                 */
                hasNext();
                this.last = this.currentIt.next();
                this.currentItReplaced = false;
                return this.last;
            }

            @Override
            public void remove() {
                if (this.currentItReplaced) {
                    if (this.last == null) {
                        throw new IllegalStateException();
                    } else {
                        LazyCriticalPairSet.this.remove(this.last);
                        this.last = null;
                    }
                } else {
                    this.currentIt.remove();
                }
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof CriticalPair) {
            CriticalPair pair = (CriticalPair) o;
            RuleTuple tuple = new RuleTuple(pair.getRule1(), pair.getRule2());
            if (this.ruleTuplesToProcess.contains(tuple)) {
                computePairs(tuple);
            }
            if (this.pairMap.get(tuple) != null) {
                return this.pairMap.get(tuple)
                    .remove(pair);
            }
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object obj : c) {
            result = remove(obj) || result;
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator<CriticalPair> it = this.iterator();
        boolean result = false;
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                result = true;
            }
        }
        return result;
    }

    @Override
    public int size() {
        computeAllPairs();
        int result = 0;
        for (Set<CriticalPair> pairSet : this.pairMap.values()) {
            result += pairSet.size();
        }
        return result;
    }

    @Override
    public Object[] toArray() {
        return this.toSingleSet()
            .toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.toSingleSet()
            .toArray(a);
    }

    Set<CriticalPair> toSingleSet() {
        this.computeAllPairs();
        Set<CriticalPair> result = new LinkedHashSet<>();
        for (Set<CriticalPair> pairSet : this.pairMap.values()) {
            result.addAll(pairSet);
        }
        return result;
    }

}

class RuleTuple {
    final Rule rule1;
    final Rule rule2;

    RuleTuple(Rule rule1, Rule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * ((this.rule1 == null) ? 0 : this.rule1.hashCode());
        result += prime * ((this.rule2 == null) ? 0 : this.rule2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleTuple other = (RuleTuple) obj;
        if (this.rule1 == null) {
            if (this.rule2 == null) {
                return other.rule1 == null && other.rule2 == null;
            } else {
                return (this.rule2.equals(other.rule1) && other.rule2 == null)
                    || (this.rule2.equals(other.rule2) && other.rule1 == null);
            }
        }
        if (this.rule2 == null) {
            return (this.rule1.equals(other.rule1) && other.rule2 == null)
                || (this.rule1.equals(other.rule2) && other.rule1 == null);
        }
        return (this.rule1.equals(other.rule1) && this.rule2.equals(other.rule2))
            || (this.rule2.equals(other.rule1) && this.rule1.equals(other.rule2));
    }
}
