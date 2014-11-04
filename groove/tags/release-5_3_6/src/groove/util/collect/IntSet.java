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

/**
 * Interface for a set of <code>int</code> keys.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:10 $
 */
public interface IntSet {
    /**
     * Clears the set. The maximum required capacity can be set. Afterwards
     * {@link #size()} returns <code>0</code>.
     * @param capacity the maximum required capacity
     */
    void clear(int capacity);

    /**
     * Adds an <code>int</code> key to the set, if it was not already there.
     * The return value indicates is the key was actually added.
     * @param key the certificate to be added
     * @return <code>true</code> if the certificate was not in the store
     *         before
     */
    boolean add(int key);

    /**
     * Returns the number of keys currently in the set.
     */
    int size();
}