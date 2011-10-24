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

import java.util.LinkedHashMap;

/**
 * Used for debugging. Uncomment the proper lines to switch between a normal
 * hash map and a linked hash map, which provides deterministic iteration.
 */
//public class MyHashMap<K,V> extends THashMap<K,V> {
//public class MyHashMap<K,V> extends HashMap<K,V> {
public class MyHashMap<K,V> extends LinkedHashMap<K,V> {

    @SuppressWarnings("unchecked")
    @Override
    public MyHashMap<K,V> clone() {
        return (MyHashMap<K,V>) super.clone();
    }

    /** Default constructor. Delegates to super class. */
    public MyHashMap() {
        super();
    }

    /** Creates a set with proper initial size. */
    public MyHashMap(int size) {
        super(size);
    }

}
