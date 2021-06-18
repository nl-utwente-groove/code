/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.util.collect;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jdt.annotation.Nullable;

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
        this.singleton = obj;
    }

    @Override
    public boolean add(E obj) {
        if (this.inner != null) {
            return this.inner.add(obj);
        } else if (this.singleton == null) {
            this.singleton = obj;
            return true;
        } else {
            this.inner = createCollection();
            assert this.singleton != null; // just tested
            E singleton = this.singleton;
            this.inner.add(singleton);
            this.singleton = null;
            return this.inner.add(obj);
        }
    }

    @Override
    public void clear() {
        this.inner = null;
        this.singleton = null;
    }

    @Override
    public boolean contains(Object obj) {
        if (this.inner != null) {
            return this.inner.contains(obj);
        } else {
            return this.singleton != null && this.singleton.equals(obj);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.singleton == null && (this.inner == null || this.inner.isEmpty());
    }

    @Override
    public Iterator<E> iterator() {
        if (this.inner != null) {
            return this.inner.iterator();
        } else if (this.singleton == null) {
            return Collections.<E>emptyList()
                .iterator();
        } else {
            assert this.singleton != null; // tested just above
            E singleton = this.singleton;
            return Collections.singleton(singleton)
                .iterator();
        }
    }

    @Override
    public boolean remove(Object obj) {
        boolean result;
        if (this.inner == null) {
            result = this.singleton != null && this.singleton.equals(obj);
            if (result) {
                this.singleton = null;
            }
        } else {
            result = this.inner.remove(obj);
            if (result && this.inner.size() == 1) {
                this.singleton = this.inner.iterator()
                    .next();
                this.inner = null;
            }
        }
        return result;
    }

    @Override
    public int size() {
        if (this.inner == null) {
            return this.singleton == null ? 0 : 1;
        } else {
            return this.inner.size();
        }
    }

    /** Indicates is there is precisely one element in this collection. */
    public boolean isSingleton() {
        return this.singleton != null || (this.inner != null && this.inner.size() == 1);
    }

    /**
     * Returns the unique element in this collection, or <code>null</code> if
     * the collection is not a singleton.
     * @return the unique element in this collection, or <code>null</code>
     * @see #isSingleton()
     */
    public @Nullable E getSingleton() {
        @Nullable E result = this.singleton;
        if (result == null && this.inner != null && this.inner.size() == 1) {
            result = this.inner.iterator()
                .next();
        }
        return result;
    }

    /** Factory method to create the inner (non-singular) collection. */
    protected Collection<E> createCollection() {
        return new ArrayList<>();
    }

    /** The singleton element, if the collection is a singleton. */
    private @Nullable E singleton;
    /** The inner (non-singular) collection. */
    private Collection<E> inner;
}
