// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.util.collect;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link groove.util.collect.IntSet} on the basis of an underlying
 * {@link java.util.HashSet}, holding an {@link Integer} representation of the
 * <code>int</code> keys.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:02 $
 */
final public class HashIntSet implements IntSet {
    /**
     * This implementation clears the underlying {@link HashSet}.
     */
    @Override
    public void clear(int capacity) {
        this.store.clear();
    }

    /**
     * This implementation returns the size of the underlying {@link HashSet}.
     */
    @Override
    public int size() {
        return this.store.size();
    }

    /**
     * Adds an {@link Integer} on the basis of <code>key</code> to the
     * underlying set.
     */
    @Override
    public boolean add(int key) {
        return this.store.add(Integer.valueOf(key));
    }

    /**
     * The underlying set.
     */
    private final Set<Integer> store = new HashSet<>();
}
