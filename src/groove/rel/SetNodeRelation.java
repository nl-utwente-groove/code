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
 * $Id: SetNodeRelation.java,v 1.4 2008-01-30 09:32:27 iovka Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class SetNodeRelation implements NodeRelation {
    public Set<Entry> getAllRelated() {
        return Collections.unmodifiableSet(this.relatedSet);
    }

    @Override
    public boolean addSelfRelated(Node node) {
        return addRelated(createEntry(node));
    }

    public boolean addRelated(Edge<?> edge) {
        return addRelated(createEntry(edge));
    }

    @Override
    public SetNodeRelation clone() {
        SetNodeRelation result = newInstance();
        for (Entry entry : getAllRelated()) {
            result.addRelated(entry);
        }
        return result;
    }

    public SetNodeRelation newInstance() {
        return new SetNodeRelation();
    }

    public boolean doOr(NodeRelation other) {
        boolean result = false;
        for (Entry entry : other.getAllRelated()) {
            result |= addRelated(entry);
        }
        return result;
    }

    public NodeRelation doThen(NodeRelation other) {
        assert other instanceof SetNodeRelation;
        Set<Entry> oldRelatedSet = new HashSet<Entry>(getAllRelated());
        clear();
        for (Entry oldRel : oldRelatedSet) {
            Set<Entry> otherEntries =
                ((SetNodeRelation) other).getEntries(oldRel.two());
            if (otherEntries != null) {
                for (Entry otherRel : otherEntries) {
                    assert otherRel.one().equals(oldRel.two());
                    Entry newRel = oldRel.append(otherRel);
                    addRelated(newRel);
                }
            }
        }
        return this;
    }

    /**
     * This implementation repeatedly takes the union of <tt>this</tt> with the
     * concatenation of <tt>this</tt> and it's reflexive closure, until that no
     * longer changes the relation.
     */
    public boolean doTransitiveClosure() {
        boolean result = false;
        boolean unstable = true;
        SetNodeRelation me = clone();
        while (unstable) {
            unstable = doOrThen(me);
            result |= unstable;
        }
        return result;
    }

    /**
     * This implementation iterates over the set of related elements and adds
     * their inverse to a new relation.
     */
    public void doInverse() {
        Set<Entry> relatedSet = new HashSet<Entry>(getAllRelated());
        clear();
        for (Entry entry : relatedSet) {
            addRelated(entry.invert());
        }
    }

    public boolean isEmpty() {
        return getAllRelated().isEmpty();
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SetNodeRelation)
            && getAllRelated().equals(((SetNodeRelation) obj).getAllRelated());
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public int hashCode() {
        return getAllRelated().hashCode();
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public String toString() {
        return getAllRelated().toString();
    }

    /** Clears this relation. */
    protected void clear() {
        this.relatedSet.clear();
        this.oneToEntryMap = null;
    }

    /** Adds a given entry to this relation. */
    protected boolean addRelated(Entry entry) {
        addToEntryMap(entry);
        return this.relatedSet.add(entry);
    }

    /**
     * Constructs the union of this relation and its concatenation
     * with another.
     */
    protected boolean doOrThen(SetNodeRelation other) {
        boolean result = false;
        Set<Entry> oldRelatedSet = new HashSet<Entry>(getAllRelated());
        for (Entry oldRel : oldRelatedSet) {
            Set<Entry> otherEntries = other.getEntries(oldRel.two());
            if (otherEntries != null) {
                for (Entry otherRel : otherEntries) {
                    assert otherRel.one().equals(oldRel.two());
                    Entry newRel = oldRel.append(otherRel);
                    result |= addRelated(newRel);
                }
            }
        }
        return result;
    }

    /**
     * Factory method: constructs a self-entry for a given node.
     */
    protected Entry createEntry(Node node) {
        return new Entry(node);
    }

    /**
     * Factory method: constructs an entry from an edge.
     */
    protected Entry createEntry(Edge<?> edge) {
        return new Entry(edge.source(), edge.target());
    }

    /** Returns the set of entries with a given node as first element. */
    protected Set<Entry> getEntries(Node one) {
        return getOneToEntryMap().get(one);
    }

    /** Adds a given entry to the internally kept entry maps. */
    protected boolean addToEntryMap(Entry entry) {
        boolean result = false;
        if (this.oneToEntryMap != null) {
            result = addToOneToEntryMap(entry, this.oneToEntryMap);
        }
        return result;
    }

    private Map<Node,Set<Entry>> getOneToEntryMap() {
        if (this.oneToEntryMap == null) {
            this.oneToEntryMap = computeOneToEntryMap();
        }
        return this.oneToEntryMap;
    }

    /** Constructs the one-to-entry-map. */
    private Map<Node,Set<Entry>> computeOneToEntryMap() {
        Map<Node,Set<Entry>> result = new HashMap<Node,Set<Entry>>();
        for (Entry entry : getAllRelated()) {
            addToOneToEntryMap(entry, result);
        }
        return result;
    }

    /** Adds a given entry to the one-to-entry-map. */
    private boolean addToOneToEntryMap(Entry entry, Map<Node,Set<Entry>> result) {
        Set<Entry> entries = result.get(entry.one());
        if (entries == null) {
            result.put(entry.one(), entries = new HashSet<Entry>());
        }
        return entries.add(entry);
    }

    /** Mapping from the first element in the duo to the set of entries. */
    private Map<Node,Set<Entry>> oneToEntryMap;
    /** The set of related nodes, stored as edges. */
    private Set<Entry> relatedSet = new HashSet<Entry>();
}
