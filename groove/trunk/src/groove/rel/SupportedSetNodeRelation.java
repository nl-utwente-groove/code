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
 * $Id: SupportedSetNodeRelation.java,v 1.4 2008-01-30 09:32:27 iovka Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class SupportedSetNodeRelation extends SetNodeRelation implements
        SupportedNodeRelation {
    @Override
    protected void clear() {
        super.clear();
        this.supportMap.clear();
        this.allSupport.clear();
    }

    @Override
    protected boolean addRelated(Entry entry) {
        boolean result;
        assert entry instanceof MyEntry;
        addToEntryMap(entry);
        MyEntry existing = this.supportMap.get(entry);
        if (existing == null) {
            this.supportMap.put(entry, (MyEntry) entry);
            result = true;
        } else {
            result = existing.addSupport((MyEntry) entry);
        }
        this.allSupport.addAll(((MyEntry) entry).getSupport());
        return result;
    }

    @Override
    protected Entry createEntry(Node node) {
        return new MyEntry(node);
    }

    @Override
    protected Entry createEntry(Edge<?> edge) {
        return new MyEntry(edge);
    }

    public Collection<Element> getSupport() {
        return this.allSupport;
    }

    @Override
    public SupportedSetNodeRelation newInstance() {
        return new SupportedSetNodeRelation();
    }

    @Override
    public Set<Entry> getAllRelated() {
        return this.supportMap.keySet();
    }

    /**
     * The underlying map containing the data of this relation, stored as a
     * mapping from edges (which encode the related pairs) to collections of
     * elements justifying them.
     */
    private Map<Entry,MyEntry> supportMap = new HashMap<Entry,MyEntry>();
    /** The set of all support elements. */
    private Set<Element> allSupport = new HashSet<Element>();

    static private class MyEntry extends Entry {
        public MyEntry(Edge<?> edge) {
            super(edge);
            this.support.add(edge);
        }

        protected MyEntry(Node one, Node two) {
            super(one, two);
            this.support.add(one);
            this.support.add(two);
        }

        public MyEntry(Node node) {
            this(node, node);
        }

        @Override
        public Entry invert() {
            MyEntry result = new MyEntry(two(), one());
            result.addSupport(this);
            return result;
        }

        @Override
        public Entry append(Entry other) {
            assert other instanceof MyEntry;
            assert two().equals(other.one());
            MyEntry result = new MyEntry(one(), other.two());
            result.addSupport(this);
            result.addSupport((MyEntry) other);
            return result;
        }

        /** Returns the support of this entry. */
        public Set<Element> getSupport() {
            return this.support;
        }

        /** Augments the support of this entry with that of another. */
        public boolean addSupport(MyEntry other) {
            return this.support.addAll(other.support);
        }

        @Override
        public String toString() {
            return super.toString() + ", support: " + this.support.toString();
        }

        final private Set<Element> support = new HashSet<Element>();
    }
}