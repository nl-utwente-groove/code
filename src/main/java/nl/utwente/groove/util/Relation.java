/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of relational functionality on top of a one-to-many map.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Relation<K,V> extends HashMap<K,HashSet<V>> {
    /** Adds a key-value pair to the relation, and returns the set of values for that key. */
    public Set<V> add(K key, V value) {
        var result = get(key);
        if (result == null) {
            result = new HashSet<>();
        }
        result.add(value);
        return result;
    }

    /** Adds a key-value set pair to the relation, and returns the set of values for that key. */
    public Set<V> add(K key, Set<? extends V> values) {
        var result = get(key);
        if (result == null) {
            result = new HashSet<>();
        }
        result.addAll(values);
        return result;
    }

    /** Adds all key-value pairs in a map to this relation. */
    public void addAll(Map<? extends K,? extends V> map) {
        map.entrySet().forEach(e -> add(e.getKey(), e.getValue()));
    }

    /** Adds another relation to this one. */
    public void addAll(Relation<? extends K,? extends V> relation) {
        relation.entrySet().forEach(e -> add(e.getKey(), e.getValue()));
    }

    /** Adds all value-key pairs in a map to this relation. */
    public void addInverse(Map<? extends V,? extends K> map) {
        map.entrySet().forEach(e -> add(e.getValue(), e.getKey()));
    }
}
