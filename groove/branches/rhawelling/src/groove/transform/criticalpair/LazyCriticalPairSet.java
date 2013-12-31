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

import groove.grammar.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Ruud
 * @version $Revision $
 */
class LazyCriticalPairSet implements Set<CriticalPair> {

    /**
     * The set of critical pair which have already been computed
     */
    private Set<CriticalPair> pairs = new HashSet<CriticalPair>();
    /**
     * Set of ruleTuples for which the critical pairs still need to be computed
     */
    private final Set<RuleTuple> ruleTuples;

    /**
     * Creates a new set of CriticalPairs for rules
     * @param rules the rules for which critical pairs should be computed
     */
    LazyCriticalPairSet(Set<Rule> rules) {
        List<Rule> ruleList = new ArrayList<Rule>(rules);
        this.ruleTuples = new HashSet<RuleTuple>();
        for (int i = 0; i < ruleList.size(); i++) {
            for (int j = i; j < ruleList.size(); j++) {
                this.ruleTuples.add(new RuleTuple(ruleList.get(i),
                    ruleList.get(j)));
            }
        }
    }

    /**
     * Computes more critical pairs for this set
     * @return a nonempty set of Critical pairs if there were more critical pairs.
     * Otherwise an empty set (this means that this.ruleTuples.isEmpty after this call)
     */
    private Set<CriticalPair> computeMorePairs() {
        Set<CriticalPair> result = Collections.emptySet();
        if (!this.ruleTuples.isEmpty()) {
            Iterator<RuleTuple> it = this.ruleTuples.iterator();
            while (result.isEmpty() && it.hasNext()) {
                RuleTuple nextTuple = it.next();
                result =
                    CriticalPair.computeCriticalPairs(nextTuple.rule1,
                        nextTuple.rule2);
                //Add the new pairs to the internal set
                this.pairs.addAll(result);
                //remove the tuple
                it.remove();
            }
        }
        //return the new pairs
        return result;
    }

    private Set<CriticalPair> computePairs(RuleTuple tuple) {
        Set<CriticalPair> result;
        if (!this.ruleTuples.remove(tuple)) {
            result = Collections.emptySet();
        } else {
            result =
                CriticalPair.computeCriticalPairs(tuple.rule1, tuple.rule2);
        }
        return result;
    }

    private void computeAllPairs() {
        Iterator<RuleTuple> it = this.ruleTuples.iterator();
        while (it.hasNext()) {
            RuleTuple nextTuple = it.next();
            this.pairs.addAll(CriticalPair.computeCriticalPairs(
                nextTuple.rule1, nextTuple.rule2));
            it.remove();
        }
        //make sure that there are no remaining ruleTuples
        this.ruleTuples.clear();
    }

    @Override
    public boolean add(CriticalPair e) {
        throw new UnsupportedOperationException(
            "Not supported for LazyCriticalPairSet");
    }

    @Override
    public boolean addAll(Collection<? extends CriticalPair> c) {
        throw new UnsupportedOperationException(
            "Not supported for LazyCriticalPairSet");
    }

    @Override
    public void clear() {
        this.pairs.clear();
        this.ruleTuples.clear();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof CriticalPair) {
            CriticalPair pair = (CriticalPair) o;
            RuleTuple tuple = new RuleTuple(pair.getRule1(), pair.getRule2());
            if (this.ruleTuples.contains(tuple)) {
                return computePairs(tuple).contains(pair);
            } else {
                return this.pairs.contains(pair);
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
        while (this.pairs.isEmpty() && !this.ruleTuples.isEmpty()) {
            computeMorePairs();
        }
        return this.pairs.isEmpty();
    }

    @Override
    public Iterator<CriticalPair> iterator() {
        return new Iterator<CriticalPair>() {

            CriticalPair last = null;
            Iterator<CriticalPair> currentIt =
                LazyCriticalPairSet.this.pairs.iterator();

            @Override
            public boolean hasNext() {
                if (!this.currentIt.hasNext()) {
                    this.currentIt = computeMorePairs().iterator();
                }
                return this.currentIt.hasNext();
            }

            @Override
            public CriticalPair next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    this.last = this.currentIt.next();
                    return this.last;
                }
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                } else {
                    LazyCriticalPairSet.this.remove(this.last);
                    this.last = null;
                }
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        if (this.contains(o)) {
            return this.pairs.remove(o);
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object obj : c) {
            result |= remove(obj);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        computeAllPairs();
        return this.pairs.retainAll(c);
    }

    @Override
    public int size() {
        computeAllPairs();
        return this.pairs.size();
    }

    @Override
    public Object[] toArray() {
        computeAllPairs();
        return this.pairs.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        computeAllPairs();
        return this.pairs.toArray(a);
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
        if (this.rule1 == null && this.rule2 == null) {
            return other.rule1 == null && other.rule2 == null;
        } else if (this.rule1 != null && this.rule2 == null) {
            return (this.rule1.equals(other.rule1) && other.rule2 == null)
                || (this.rule1.equals(other.rule2) && other.rule1 == null);
        } else if (this.rule1 == null && this.rule2 != null) {
            return (this.rule2.equals(other.rule1) && other.rule2 == null)
                || (this.rule2.equals(other.rule2) && other.rule1 == null);
        } else {
            return (this.rule1.equals(other.rule1) && this.rule2.equals(other.rule2))
                || (this.rule2.equals(other.rule1) && this.rule1.equals(other.rule2));
        }
    }

}
