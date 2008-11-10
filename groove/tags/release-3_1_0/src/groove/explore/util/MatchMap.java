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
package groove.explore.util;

import groove.trans.Rule;
import groove.util.NestedIterator;
import groove.util.TransformIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Extension of a map from rules to collections of a generic type, that
 * additionally keeps track of the combined size of the collections. The total
 * size is only accurate if the collections are not modified after addition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchMap<T> extends LinkedHashMap<Rule,Collection<T>> {
    @Override
    public Collection<T> put(Rule key, Collection<T> value) {
        this.totalSize += value.size();
        Collection<T> result = super.put(key, value);
        if (result != null) {
            this.totalSize -= result.size();
        }
        return result;
    }

    @Override
    public Collection<T> remove(Object key) {
        Collection<T> result = super.remove(key);
        if (result != null) {
            this.totalSize -= result.size();
        }
        return result;
    }

    /** Returns an iterator over all the collected items. */
    public Iterator<T> globalIterator() {
        Iterator<Iterator<T>> iter =
            new TransformIterator<Collection<T>,Iterator<T>>(
                values().iterator()) {
                @Override
                protected Iterator<T> toOuter(Collection<T> from) {
                    return from.iterator();
                }
            };
        return new NestedIterator<T>(iter);
    }

    /** Returns the combined size of the event collections in the map. */
    public int globalSize() {
        return this.totalSize;
    }

    /** The combined size of the event sets. */
    private int totalSize;
}
