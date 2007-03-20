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
 * $Id: StackedMap.java,v 1.1.1.2 2007-03-20 10:42:59 kastenberg Exp $
 */
package groove.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Defines a stacked map, consisting of a lower map (which is not modified by
 * any of the operations) and a delta map.
 * The stacked map does not support <tt>null</tt> values, and
 * currently also does not support removal of elements.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class StackedMap<T,U> extends AbstractMap<T,U> {
    /**
     * Creates a stacked map with an empty lower map.
     */
    public StackedMap() {
        this(Collections.<T,U>emptyMap());
    }

    /**
     * Creates a stacked map on top of a given map,
     * which will serve as the lower map.
     */
    public StackedMap(Map<T,U> lower) {
        this.lower = lower;
        this.delta = createDeltaMap();
    }

    /**
     * Creates a stacked map from a given lower map and delta map.
     */
    public StackedMap(Map<T,U> lower, Map<T,U> delta) {
        this.lower = lower;
        this.delta = delta;
    }

    /**
     * Returns a {@link StackedSet} consisting of a {@link SetView} on
     * the lower map's entry set, where the entries are filtered out that 
     * are redefined in the delta map, with the delta map's entry set added.
     */
    public Set<Map.Entry<T,U>> entrySet() {
    	if (entrySet == null) {
    		entrySet = createEntrySet();
    	}
        return entrySet;
    }
    
    /**
     * Clearing a stacked map is currently not supported.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     */
    public U get(Object key) {
        U result = delta.get(key);
        if (result == null) {
            result = lower.get(key);
            if (result != null) {
            	delta.put((T) key, result);
            }
        }
        return result;
    }

    /**
     * Inserts the <tt>key</tt>-<tt>value</tt> pair into the delta map.
     * @throws IllegalArgumentException if <tt>value</tt> equals <tt>null</tt>
     */
    public U put(T key, U value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        U result = delta.put(key, value);
        if (result == null) {
            return lower.get(key);
        } else {
            return result;
        }
    }

    /**
     * Removal from a stacked map is currently not supported.
     */
    public U remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Factory method for the delta map.
     * This implementation returns a {@link HashMap}.
     */
    protected Map<T,U> createDeltaMap() {
        return new HashMap<T,U>();
    }
    
    protected Set<Map.Entry<T,U>> createEntrySet() {
    	return new StackedSet<Map.Entry<T,U>>(new UnmodifiableSetView<Map.Entry<T,U>>(lower.entrySet()) {
            public boolean approves(Object obj) {
                return !delta.containsKey(((Map.Entry) obj).getKey());
            }
        }) {
            protected Set<Map.Entry<T,U>> createAddedSet() {
                return delta.entrySet();
            }
        };
    }
    
    /**
     * Returns an unmodifiable view on the delta map.
     */
    protected Map<T,U> delta() {
        return Collections.unmodifiableMap(delta);
    }
    
    /**
     * Returns an unmodifiable view on the lower map.
     */
    protected Map<T,U> lower() {
        return Collections.unmodifiableMap(lower);
    }

    /**
     * The delta map, storing the difference between {@link #lower} and
     * the implemented map.
     */
    private final Map<T,U> delta;
    /**
     * The lower map.
     */
    private final Map<T,U> lower; 
    /**
     * Auxiliary variable storing a view on the entry set.
     */
    private Set<Map.Entry<T,U>> entrySet;
}