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
 * $Id: CollectionOfCollections.java,v 1.5 2008-01-30 09:32:02 iovka Exp $
 */
package groove.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Implements a collection which flattens a collection of collections.
 * The collection is unmodifiable.
 * Containment test is by iterating over the underlying collections,
 * which is expensive!
 * Equality is deferred to <tt>Object</tt>.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CollectionOfCollections<T> extends AbstractCollection<T> {
    /**
     * Constructs a new collection of collections.
     * @require <tt>collections \subseteq Collection</tt>
     */
    public CollectionOfCollections(Collection<? extends Collection<? extends T>> collections) {
        this.collections = collections;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> res = new Iterator<T>() {
            public boolean hasNext() {
                return forwardCollectionIter();
            }

            public T next() {
                forwardCollectionIter();
                T latest = elemIter.next();
                return latest;
            }

            public void remove() {
                elemIter.remove();
                updateRemove(latest);
            }

            private boolean forwardCollectionIter() {
                while (elemIter == null || !elemIter.hasNext()) {
                    if (collectionIter.hasNext()) {
                        elemIter = collectionIter.next().iterator();
                    } else {
                        return false;
                    }
                }
                return true;
            }

            private Iterator<? extends Collection<? extends T>> collectionIter = getCollections().iterator();
            private Iterator<? extends T> elemIter;
            private T latest;
        };
        return res;
   }

    @Override
    public int size() {
        int size = 0;
        for (Collection<?> collection: collections) {
            size += collection.size();
        }
        return size;
    }

    /** 
	 * Callback method that signals the removal of an element
	 * by an inner iterator.
	 * This implementation does nothing.
	 */
	protected void updateRemove(T elem) {
		// empty
	}

	/**
	 * @return Returns the collections.
	 */
	final Collection<? extends Collection<? extends T>> getCollections() {
		return this.collections;
	}

	/**
     * The collection of collections that is the basis of this class.
     * @invariant <tt>collections \subseteq Collection</tt>
     */
    private final Collection<? extends Collection<? extends T>> collections;
}
