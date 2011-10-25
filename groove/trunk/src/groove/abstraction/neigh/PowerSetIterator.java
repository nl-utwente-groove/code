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
package groove.abstraction.neigh;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Iterator that produces the power set of a given set.
 * @author Eduardo Zambon
 */
public class PowerSetIterator<T> implements Iterator<Set<T>> {

    final T elems[];
    final int masks[];
    final int total;
    final Set<T> resultSet;
    int curr;

    /**
     * Constructs the iterator. Second argument is a flag to indicate if
     * the empty set should be returned by the iterator. */
    @SuppressWarnings("unchecked")
    public PowerSetIterator(Set<T> elemSet, boolean skipEmpty) {
        this.elems = (T[]) new Object[elemSet.size()];
        this.masks = new int[elemSet.size()];
        int i = 0;
        for (T elem : elemSet) {
            this.elems[i] = elem;
            this.masks[i] = 1 << i;
            i++;
        }
        this.total = (int) Math.pow(2, this.elems.length);
        this.resultSet = new MyHashSet<T>();
        if (skipEmpty) {
            this.curr = 1;
        } else {
            this.curr = 0;
        }
    }

    @Override
    public boolean hasNext() {
        return this.curr < this.total;
    }

    @Override
    public Set<T> next() {
        this.resultSet.clear();
        for (int i = 0; i < this.elems.length; i++) {
            if ((this.curr & this.masks[i]) == this.masks[i]) {
                this.resultSet.add(this.elems[i]);
            }
        }
        this.curr++;
        return Collections.unmodifiableSet(this.resultSet);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
