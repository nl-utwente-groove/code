/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction;

import java.util.HashMap;

/**
 * Class that keeps track of the number of times a certain element has
 * been inserted in the set.
 * 
 * @author Eduardo Zambon
 * 
 * EDUARDO: Pimp this.
 * Use a better data structure...
 */
public final class CountingSet<T> extends HashMap<T,Integer> {

    /**
     * Keeps track of the number of times a certain element has been inserted
     * in the set.
     */
    public void add(T key) {
        Integer count = this.get(key);
        int i;
        if (count != null) {
            i = count.intValue() + 1;
        } else {
            i = 1;
        }
        this.put(key, Integer.valueOf(i));
    }

}
