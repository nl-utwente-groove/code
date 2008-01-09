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
 * $Id: SmallCollection.java,v 1.1 2007-09-19 09:01:07 rensink Exp $
 */
package groove.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Collection built either on a singleton element or on an inner collection.
 * Saves space with respect to an ordinary collection implementation if the
 * content is typically a singleton.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SmallCollection<E> extends AbstractCollection<E> {
    /** Constructs an empty collection. */
    public SmallCollection() {
        // empty
    }
    
    /** Constructs a singleton collection. */
    public SmallCollection(E obj) {
        singleton = obj;
    }

    @Override
    public boolean add(E obj) {
        if (inner != null) {
            return inner.add(obj);
        } else if (singleton == null) {
            singleton = obj;
            return true;
        } else {
            inner = createCollection();
            inner.add(singleton);
            singleton = null;
            return inner.add(obj);
        }
    }

    @Override
    public void clear() {
        inner = null;
        singleton = null;
    }

    @Override
    public boolean contains(Object obj) {
        if (inner != null) {
            return inner.contains(obj);
        } else {
            return singleton != null && singleton.equals(obj);
        }
    }

    @Override
    public boolean isEmpty() {
        return inner == null && singleton == null || inner.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        if (inner != null) {
            return inner.iterator();
        } else if (singleton == null) {
            return Collections.<E>emptyList().iterator();
        } else {
            return Collections.singleton(singleton).iterator();
        }
    }

    @Override
    public boolean remove(Object obj) {
        boolean result;
        if (inner == null) {
            result = singleton != null && singleton.equals(obj);
            if (result) {
                singleton = null;
            }
        } else {
            result = inner.remove(obj);
            if (result && inner.size() == 1) {
                singleton = inner.iterator().next();
                inner = null;
            }
        }
        return result;
    }

    @Override
    public int size() {
        if (inner == null) {
            return singleton == null ? 0 : 1;
        } else {
            return inner.size();
        }
    }
    
    /** Indicates is there is precisely one element in this collection. */
    public boolean isSingleton() {
        return singleton != null || (inner != null && inner.size() == 1);
    }

    /** 
     * Returns the unique element in this collection,
     * or <code>null</code> if the collection is not a singleton.
     * @return the unique element in this collection, of <code>null</code>
     * @see #isSingleton()
     */
    public E getSingleton() {
        E result = singleton;
        if (result == null && inner != null && inner.size() == 1) {
            result = inner.iterator().next();
        }
        return result;
    }
    
    /** Factory method to create the inner (non-singular) collection. */
    protected Collection<E> createCollection() {
        return new ArrayList<E>();
    }
    
    /** The singleton element, if the collection is a singleton. */
    private E singleton;
    /** The inner (non-singular) collection. */
    private Collection<E> inner;
}
