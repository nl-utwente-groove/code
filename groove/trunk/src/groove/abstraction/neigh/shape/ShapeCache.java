/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.neigh.shape;

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.graph.GraphCache;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostNode;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ShapeCache extends GraphCache<HostNode,HostEdge> {
    /**
     * Constructs a cache for a given shape graph.
     * The shape graph may be either fresh (and empty) or filled (if the
     * cache was cleared and is not being reconstructed).
     * @param graph the shape graph for which this is the cache.
     */
    ShapeCache(ShapeGraph graph) {
        super(graph, true);
    }

    void copyFrom(ShapeGraph original) {
        ShapeCache other = original.getCache();
        getNodeSet().addAll(other.getNodeSet());
        getEdgeSet().addAll(other.getEdgeSet());
        // Clone the equivalence relation. A deep copy is used.
        if (other.equivRel != null) {
            this.equivRel = other.equivRel.clone();
        }
        // Clone the multiplicity maps. A shallow copy is sufficient.
        if (other.nodeMultMap != null) {
            this.nodeMultMap = other.nodeMultMap.clone();
        }
        if (other.edgeMultMaps != null) {
            this.edgeMultMaps = createEdgeMultMaps();
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                this.edgeMultMaps.put(dir, other.edgeMultMaps.get(dir).clone());
            }
        }
    }

    @Override
    public ShapeGraph getGraph() {
        return (ShapeGraph) super.getGraph();
    }

    /** Convenience method to retrieve the shape factory of the graph. */
    ShapeFactory getFactory() {
        return getGraph().getFactory();
    }

    /** Convenience method to retrieve the maximum node number plus one. */
    int getNodeStoreSize() {
        return getNodeCounter().getCount();
    }

    /** Lazily creates and returns the node set of the underlying shape. */
    Set<ShapeNode> getNodeSet() {
        if (this.nodeSet == null) {
            ShapeStore store = getGraph().store;
            if (store == null) {
                setNodeSet(this.<ShapeNode>createElementSet());
            } else {
                store.fill(this);
            }
        }
        return this.nodeSet;
    }

    /** 
     * Assigns the node set, presumably from a shape graph store.
     * @see ShapeStore#fill(ShapeCache) 
     */
    void setNodeSet(Set<ShapeNode> nodeSet) {
        this.nodeSet = nodeSet;
    }

    /** Lazily creates and returns the edge set of the underlying shape. */
    Set<ShapeEdge> getEdgeSet() {
        if (this.edgeSet == null) {
            ShapeStore store = getGraph().store;
            if (store == null) {
                setEdgeSet(this.<ShapeEdge>createElementSet());
            } else {
                store.fill(this);
            }
        }
        return this.edgeSet;
    }

    /** 
     * Assigns the edge set, presumably from a shape graph store.
     * @see ShapeStore#fill(ShapeCache) 
     */
    void setEdgeSet(Set<ShapeEdge> edgeSet) {
        this.edgeSet = edgeSet;
    }

    /** Lazily creates and returns the node equivalence relation of the underlying shape. */
    EquivRelation<ShapeNode> getEquivRel() {
        if (this.equivRel == null) {
            ShapeStore store = getGraph().store;
            if (store == null) {
                setEquivRel(createNodeEquiv());
            } else {
                store.fill(this);
            }
        }
        return this.equivRel;
    }

    /** 
     * Assigns the node equivalence relation, presumably from a shape graph store.
     * @see ShapeStore#fill(ShapeCache) 
     */
    void setEquivRel(EquivRelation<ShapeNode> equivRel) {
        this.equivRel = equivRel;
    }

    /** Lazily creates and returns the node multiplicity map of the underlying shape. */
    MyHashMap<ShapeNode,Multiplicity> getNodeMultMap() {
        if (this.nodeMultMap == null) {
            ShapeStore store = getGraph().store;
            if (store == null) {
                setNodeMultMap(createNodeMultMap());
            } else {
                store.fill(this);
            }
        }
        return this.nodeMultMap;
    }

    /** 
     * Assigns the node multiplicity map, presumably from a shape graph store.
     * @see ShapeStore#fill(ShapeCache) 
     */
    void setNodeMultMap(MyHashMap<ShapeNode,Multiplicity> nodeMultMap) {
        this.nodeMultMap = nodeMultMap;
    }

    /** Lazily creates and returns the edge multiplicity map of the underlying shape
     * in a given direction.
     */
    MyHashMap<EdgeSignature,Multiplicity> getEdgeMultMap(EdgeMultDir dir) {
        if (this.edgeMultMaps == null) {
            ShapeStore store = getGraph().store;
            if (store == null) {
                setEdgeMultMaps(createEdgeMultMaps());
            } else {
                store.fill(this);
            }
        }
        return this.edgeMultMaps.get(dir);
    }

    /** 
     * Assigns the edge multiplicity maps.
     * @see ShapeStore#fill(ShapeCache) 
     */
    void setEdgeMultMaps(
            Map<EdgeMultDir,MyHashMap<EdgeSignature,Multiplicity>> edgeMultMaps) {
        this.edgeMultMaps = edgeMultMaps;
    }

    /** Stores the data structures in flattened form in the underlying shape. */
    void flatten() {
        getGraph().store = STORE_PROTOTYPE.flatten(this);
    }

    EquivRelation<ShapeNode> createNodeEquiv() {
        return new EquivRelation<ShapeNode>();
    }

    MyHashMap<ShapeNode,Multiplicity> createNodeMultMap() {
        return new MyHashMap<ShapeNode,Multiplicity>();
    }

    Map<EdgeMultDir,MyHashMap<EdgeSignature,Multiplicity>> createEdgeMultMaps() {
        Map<EdgeMultDir,MyHashMap<EdgeSignature,Multiplicity>> result =
            new EnumMap<EdgeMultDir,MyHashMap<EdgeSignature,Multiplicity>>(
                EdgeMultDir.class);
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            result.put(dir, new MyHashMap<EdgeSignature,Multiplicity>());
        }
        return result;
    }

    /** Factory method for an empty set of host elements. */
    <E extends HostElement> Set<E> createElementSet() {
        return new NotifySet<E>();
    }

    /** Set of nodes of the underlying shape. */
    private Set<ShapeNode> nodeSet;
    /** Set of edges of the underlying shape. */
    private Set<ShapeEdge> edgeSet;
    /**
     * Equivalence relation over nodes of the shape.
     */
    private EquivRelation<ShapeNode> equivRel;
    /**
     * Node multiplicity map.
     */
    private MyHashMap<ShapeNode,Multiplicity> nodeMultMap;
    /**
     * Outgoing edge multiplicity map.
     */
    private Map<EdgeMultDir,MyHashMap<EdgeSignature,Multiplicity>> edgeMultMaps;

    private final static ShapeStore STORE_PROTOTYPE = ShapeStore1.PROTOTYPE;

    /**
     * Extension of <tt>Set</tt> that invokes the notify methods of the graph
     * when elements are added or deleted
     */
    private class NotifySet<EL extends HostElement> extends LinkedHashSet<EL> {
        /** Constructs an empty set. */
        public NotifySet() {
            // we need an explicit empty constructor
        }

        /**
         * Overwrites the method from <tt>Set</tt> to take care of proper
         * notification.
         */
        @Override
        public Iterator<EL> iterator() {
            return new MyIterator();
        }

        /** Returns <code>super.iterator()</code>. */
        Iterator<EL> superIterator() {
            return super.iterator();
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure proper observer
         * notification in all cases.
         */
        @Override
        public final boolean add(EL elem) {
            if (super.add(elem)) {
                if (elem instanceof ShapeNode) {
                    addUpdate((ShapeNode) elem);
                } else {
                    addUpdate((ShapeEdge) elem);
                }
                return true;
            } else {
                return false;
            }
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure proper observer
         * notification in all cases.
         */
        @Override
        public final boolean remove(Object elem) {
            if (super.remove(elem)) {
                if (elem instanceof ShapeNode) {
                    removeUpdate((ShapeNode) elem);
                } else {
                    removeUpdate((ShapeEdge) elem);
                }
                return true;
            } else {
                return false;
            }
        }

        /**
         * An iterator over the underlying hash set that extends
         * <tt>remove()</tt> by invoking the graph listeners.
         */
        class MyIterator implements Iterator<EL> {
            public boolean hasNext() {
                return this.setIterator.hasNext();
            }

            public EL next() {
                this.latest = this.setIterator.next();
                return this.latest;
            }

            public void remove() {
                this.setIterator.remove();
                if (this.latest instanceof ShapeNode) {
                    removeUpdate((ShapeNode) this.latest);
                } else {
                    removeUpdate((ShapeEdge) this.latest);
                }
            }

            private final Iterator<EL> setIterator = superIterator();
            EL latest;
        }
    }
}
