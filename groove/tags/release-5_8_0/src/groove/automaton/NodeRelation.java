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
package groove.automaton;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.util.Duo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class NodeRelation implements Cloneable {
    /**
     * Returns the set of all related pairs.
     */
    public Set<Entry> getAllRelated() {
        return this.supportMap.keySet();
    }

    /** 
     * Adds a relation from a given node to itself.
     * The return value indicates if a corresponding entry was already there.
     */
    public boolean addSelfRelated(Node node) {
        return addRelated(createEntry(node));
    }

    /**
     * Adds a pair to the relation, consisting of the source and target
     * of a given edge.
     * The return value indicates if the pair was actually added or was already
     * in the relation.
     * @param edge the source of the pair to be added
     * @return <tt>true</tt> if the pair was actually added, <tt>false</tt> if
     *         it was already in the relation.
     */
    public boolean addRelated(Edge edge) {
        return addRelated(createEntry(edge));
    }

    /**
     * Returns a copy of this node relation.
     */
    @Override
    public NodeRelation clone() {
        NodeRelation result = newInstance();
        for (Entry entry : getAllRelated()) {
            result.addRelated(entry);
        }
        return result;
    }

    /**
     * Returns a fresh, empty node relation over the same universe as this one.
     */
    public NodeRelation newInstance() {
        return new NodeRelation();
    }

    /**
     * Has the effect of <tt>getOr(EdgeBasedRelation)</tt>, but modifies
     * <tt>this</tt>. Returns <tt>true</tt> if this relation was changed as a
     * result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the
     *         operation
     */
    public boolean doOr(NodeRelation other) {
        boolean result = false;
        for (Entry entry : other.getAllRelated()) {
            result |= addRelated(entry);
        }
        return result;
    }

    /**
     * Has the effect of <tt>getThen(EdgeBasedRelation)</tt>, but modifies
     * <tt>this</tt>.
     * @return <tt>this</tt>
     */
    public NodeRelation doThen(NodeRelation other) {
        Set<Entry> oldRelatedSet = new HashSet<>(getAllRelated());
        clear();
        for (Entry oldRel : oldRelatedSet) {
            Set<Entry> otherEntries = other.getEntries(oldRel.two());
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
     * Has the effect of <tt>getTransitiveClosure()</tt>, but modifies
     * <tt>this</tt>. Returns <tt>true</tt> if this relation was changed as a
     * result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the
     *         operation
     */
    public boolean doTransitiveClosure() {
        boolean result = false;
        boolean unstable = true;
        NodeRelation me = clone();
        while (unstable) {
            unstable = doOrThen(me);
            result |= unstable;
        }
        return result;
    }

    /**
     * Returns the relation that is the inverse of this one. The new relation
     * consists of all <code>(pre,post)</code> pairs for which
     * <code>(post,pre)</code> is in this relation.
     */
    public void doInverse() {
        Set<Entry> relatedSet = new HashSet<>(getAllRelated());
        clear();
        for (Entry entry : relatedSet) {
            addRelated(entry.invert());
        }
    }

    /**
     * Indicates if there are no related elements in the relation.
     * @return <tt>true</tt> if there are no related elements in the relation
     */
    public boolean isEmpty() {
        return getAllRelated().isEmpty();
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NodeRelation)
            && getAllRelated().equals(((NodeRelation) obj).getAllRelated());
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

    /**
     * Yields the set of all graph elements supporting this relation.
     */
    public Collection<Element> getSupport() {
        return this.allSupport;
    }

    /** Clears this relation. */
    protected void clear() {
        this.oneToEntryMap = null;
        this.supportMap.clear();
        this.allSupport.clear();
    }

    /** Adds a given entry to this relation. */
    protected boolean addRelated(Entry entry) {
        boolean result;
        addToEntryMap(entry);
        Entry existing = this.supportMap.get(entry);
        if (existing == null) {
            this.supportMap.put(entry, entry);
            result = true;
        } else {
            result = existing.addSupport(entry);
        }
        this.allSupport.addAll(entry.getSupport());
        return result;
    }

    /**
     * Constructs the union of this relation and its concatenation
     * with another.
     */
    protected boolean doOrThen(NodeRelation other) {
        boolean result = false;
        Set<Entry> oldRelatedSet = new HashSet<>(getAllRelated());
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
    protected Entry createEntry(Edge edge) {
        return new Entry(edge);
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
        Map<Node,Set<Entry>> result = new HashMap<>();
        for (Entry entry : getAllRelated()) {
            addToOneToEntryMap(entry, result);
        }
        return result;
    }

    /** Adds a given entry to the one-to-entry-map. */
    private boolean addToOneToEntryMap(Entry entry, Map<Node,Set<Entry>> result) {
        Set<Entry> entries = result.get(entry.one());
        if (entries == null) {
            result.put(entry.one(), entries = new HashSet<>());
        }
        return entries.add(entry);
    }

    /** Mapping from the first element in the duo to the set of entries. */
    private Map<Node,Set<Entry>> oneToEntryMap;

    /**
     * The underlying map containing the data of this relation, stored as a
     * mapping from edges (which encode the related pairs) to collections of
     * elements justifying them.
     */
    private Map<Entry,Entry> supportMap = new HashMap<>();
    /** The set of all support elements. */
    private Set<Element> allSupport = new HashSet<>();

    /** Entry in the relation. */
    static class Entry extends Duo<Node> {
        /** Constructs a self-entry from a given node. */
        public Entry(Node node) {
            super(node, node);
        }

        /** Constructs an entry between two nodes. */
        protected Entry(Node one, Node two) {
            super(one, two);
            this.support.add(one);
            this.support.add(two);
        }

        /** Constructs an entry from a given edge. */
        public Entry(Edge edge) {
            this(edge.source(), edge.target());
            this.support.add(edge);
        }

        /** Constructs the inverse of this entry.
         * This means the two elements of the duo are swapped.
         */
        public Entry invert() {
            Entry result = new Entry(two(), one());
            result.addSupport(this);
            return result;
        }

        /**
         * Appends another entry to this one.
         * @param other the other entry
         */
        public Entry append(Entry other) {
            assert two().equals(other.one());
            Entry result = new Entry(one(), other.two());
            result.addSupport(this);
            result.addSupport(other);
            return result;
        }

        /** Returns the support of this entry. */
        public Set<Element> getSupport() {
            return this.support;
        }

        /** Augments the support of this entry with that of another. */
        public boolean addSupport(Entry other) {
            return this.support.addAll(other.support);
        }

        @Override
        public String toString() {
            return super.toString() + ", support: " + this.support.toString();
        }

        final private Set<Element> support = new HashSet<>();
    }
}
