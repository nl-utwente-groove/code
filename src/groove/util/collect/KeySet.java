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
package groove.util.collect;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Set that simultaneously behaves as a mapping from a uniquely defining
 * property of the contained elements to the element with that property.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class KeySet<K,E> extends AbstractSet<E> implements Set<E> {
    @Override
    public Iterator<E> iterator() {
        return this.map.values().iterator();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean contains(Object o) {
        boolean result = false;
        K key = getKey(o);
        if (key != null) {
            result = this.map.containsKey(key);
        }
        return result;
    }

    @Override
    public boolean add(E e) {
        boolean result = false;
        K key = getKey(e);
        if (key != null) {
            E element = this.map.get(key);
            result = element == null;
            if (result) {
                this.map.put(key, e);
            }
        }
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = false;
        K key = getKey(o);
        if (key != null) {
            E element = this.map.remove(key);
            result = element != null;
        }
        return result;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    /** Retrieves the set of elements corresponding to a given key. */
    public E get(K key) {
        return this.map.get(key);
    }

    /** 
     * Method to retrieve the key from a given value.
     * @param value the value to construct the key from
     * @return the non-{@code null} key for {@code value}
     */
    abstract protected K getKey(Object value);

    private final Map<K,E> map = new LinkedHashMap<>();
}
