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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Ruud
 * Ordered Set of CriticalPairs, the iterator of this set will iterate over the pairs in descending order (the largest pairs first)
 * The size of a criticalPair is determined by the number of vertices in the host graph
 */
class OrderedCriticalPairSet implements Set<CriticalPair> {

    private HashMap<Integer,LinkedHashSet<CriticalPair>> pairMap =
        new HashMap<>();

    /**
     * Creates a new set of CriticalPairs for rules
     */
    OrderedCriticalPairSet() {
        //default constructor
    }

    OrderedCriticalPairSet(Set<CriticalPair> criticalPairs) {
        addAll(criticalPairs);
    }

    @Override
    public boolean add(CriticalPair e) {
        int size = e.getHostGraph().nodeCount();
        LinkedHashSet<CriticalPair> pairSet = this.pairMap.get(size);
        if (pairSet == null) {
            pairSet = new LinkedHashSet<>();
            this.pairMap.put(size, pairSet);
        }
        return pairSet.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends CriticalPair> c) {
        boolean result = false;
        for (CriticalPair pair : c) {
            result = this.add(pair) || result;
        }
        return result;
    }

    @Override
    public void clear() {
        this.pairMap.clear();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof CriticalPair) {
            CriticalPair pair = (CriticalPair) o;
            int size = pair.getHostGraph().nodeCount();
            return this.pairMap.get(size) != null && this.pairMap.get(size).contains(pair);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (obj instanceof CriticalPair) {
                CriticalPair pair = (CriticalPair) obj;
                int size = pair.getHostGraph().nodeCount();
                if (this.pairMap.get(size) == null || !this.pairMap.get(size).contains(pair)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        if (this.pairMap.isEmpty()) {
            return true;
        } else {
            for (Set<CriticalPair> pairSet : this.pairMap.values()) {
                if (!pairSet.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public Iterator<CriticalPair> iterator() {
        return new Iterator<CriticalPair>() {

            private CriticalPair last = null;

            //true if currentIt has been replaced with a new iterator
            //this is needed to implement remove() correctly
            private boolean currentItReplaced = false;

            Iterator<Integer> keyIt = new TreeSet<>(
                OrderedCriticalPairSet.this.pairMap.keySet()).descendingIterator();

            Iterator<CriticalPair> currentIt = this.keyIt.hasNext()
                    ? OrderedCriticalPairSet.this.pairMap.get(this.keyIt.next()).iterator()
                    //initialize with emptySet iterator if the pairMap was empty
                    : new HashSet<CriticalPair>().iterator();

            @Override
            public boolean hasNext() {
                while (this.keyIt.hasNext() && !this.currentIt.hasNext()) {
                    this.currentIt =
                        OrderedCriticalPairSet.this.pairMap.get(this.keyIt.next()).iterator();
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
                        OrderedCriticalPairSet.this.remove(this.last);
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
            int size = pair.getHostGraph().nodeCount();
            if (this.pairMap.get(size) != null) {
                return this.pairMap.get(size).remove(pair);
            }
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object obj : c) {
            result = this.remove(obj) || result;
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
        int result = 0;
        for (Set<CriticalPair> pairSet : this.pairMap.values()) {
            result += pairSet.size();
        }
        return result;
    }

    @Override
    public Object[] toArray() {
        return this.toSingleSet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.toSingleSet().toArray(a);
    }

    Set<CriticalPair> toSingleSet() {
        Set<CriticalPair> result = new HashSet<>();
        for (Set<CriticalPair> pairSet : this.pairMap.values()) {
            result.addAll(pairSet);
        }
        return result;
    }

}
