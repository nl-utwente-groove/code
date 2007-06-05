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
 * $Id: DefaultListEntry.java,v 1.2 2007-03-28 15:12:28 rensink Exp $
 */
package groove.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Node in a linked list. Used for space optimizations instead of arrays.
 * Use it by subclassing.
 * This saves out the array object itself (20 bytes).
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class DefaultListEntry implements ListEntry {
    /**
     * Converts a collection of {@link ListEntry}s without any particular
     * linking structure into a {@link ListEntry} linked list in the order
     * of the iterator.
     */
    static public <T> ListEntry toListEntryList(Collection<ListEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        } else {
            Iterator<ListEntry> entryIter = entries.iterator();
            ListEntry result = entryIter.next();
            ListEntry last = result;
            while (entryIter.hasNext()) {
                last.setNext(entryIter.next());
                last = last.getNext();
            }
            return result;
        }
    }
    
    /**
     * Constructs a list entry with a given payload and without successor.
     * The payload element is required to be non-<code>null</code>.
     */
    public DefaultListEntry() {
        this(null);
    }
    
    /**
     * Constructs a list entry with a given payload and successor.
     * The payload element is required to be non-<code>null</code>.
     */
    public DefaultListEntry(ListEntry next) {
        this.next = next;
    }

    /* (non-Javadoc)
     * @see groove.util.ListEntry#getNext()
     */
    public ListEntry getNext() {
        return next;
    }

    /* (non-Javadoc)
     * @see groove.util.ListEntry#setNext(groove.util.DefaultListEntry)
     */
    public void setNext(ListEntry next) {
        this.next = next;
    }

    /**
     * Returns <code>true</code> if <code>obj</code> is also a {@link ListEntry},
     * with equal element and successor.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ListEntry) {
            Object otherNext = ((ListEntry) obj).getNext();
            return (next == null ? otherNext == null : next.equals(otherNext));
        } else {
            return false;
        }
    }

    /**
     * Only tests if there is a next entry at all.
     */
    @Override
    public int hashCode() {
        return (next == null ? 0: 1);
    }

    /**
     * The next entry in the list.
     */
    private ListEntry next;
}
