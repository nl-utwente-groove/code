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
 * $Id: ListEntryIterator.java,v 1.2 2008-01-30 09:32:14 iovka Exp $
 */
package groove.util;

import java.util.Iterator;

/**
 * An iterator over a list of items implementing {@link ListEntry}.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class ListEntryIterator implements Iterator<ListEntry> {
    /** Constructs an iterator over a list starting with a given {@link ListEntry}. */
    public ListEntryIterator(ListEntry current) {
        this.current = current;
    }

    /**
     * Returns <code>true</code> if the current entry is not <code>null</code>.
     */
    public boolean hasNext() {
        return current != null;
    }

    /**
     * Returns the current entry.
     * The result value is the payload of the current {@link ListEntry}.
     */
    public ListEntry next() {
        ListEntry result = current;
        current = current.getNext();
        return result;
    }

    /**
     * Removal cannot be supported for the first element of the list,
     * therefore we disallow it altogether.
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * The next list entry to return by {@link #next()}. 
     */
    private ListEntry current;
}
