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
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.abstraction.neigh.shape.ShapeGraph.EdgeRecord;
import groove.graph.GraphCache;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
        this.equivRel = other.equivRel.clone();
        // Clone the multiplicity maps. A shallow copy is sufficient.
        this.nodeMultMap = other.nodeMultMap.clone();
        this.outEdgeMultMap = other.outEdgeMultMap.clone();
        this.inEdgeMultMap = other.inEdgeMultMap.clone();
    }

    @Override
    public ShapeGraph getGraph() {
        return (ShapeGraph) super.getGraph();
    }

    /** Convenience method to retrieve the shape factory of the graph. */
    private ShapeFactory getFactory() {
        return getGraph().getFactory();
    }

    /** Lazily creates and returns the node set of the underlying shape. */
    Set<ShapeNode> getNodeSet() {
        if (this.nodeSet == null) {
            this.nodeSet = new NotifySet<ShapeNode>();
            ShapeNode[] nodes = getGraph().nodes;
            if (nodes != null) {
                for (int i = 0; i < nodes.length; i++) {
                    this.nodeSet.add(nodes[i]);
                }
            }
        }
        return this.nodeSet;
    }

    /** Lazily creates and returns the edge set of the underlying shape. */
    Set<ShapeEdge> getEdgeSet() {
        if (this.edgeSet == null) {
            this.edgeSet = new NotifySet<ShapeEdge>();
            ShapeEdge[] edges = getGraph().edges;
            if (edges != null) {
                for (int i = 0; i < edges.length; i++) {
                    this.edgeSet.add(edges[i]);
                }
            }
        }
        return this.edgeSet;
    }

    /** Lazily creates and returns the node equivalence relation of the underlying shape. */
    EquivRelation<ShapeNode> getEquivRel() {
        if (this.equivRel == null) {
            this.equivRel = new EquivRelation<ShapeNode>();
            byte[] nodeEquiv = getGraph().nodeEquiv;
            if (nodeEquiv != null) {
                List<EquivClass<ShapeNode>> cells =
                    new ArrayList<EquivClass<ShapeNode>>(nodeEquiv.length);
                for (ShapeNode node : getNodeSet()) {
                    int cellIx = nodeEquiv[node.getNumber()];
                    while (cellIx >= cells.size()) {
                        cells.add(new NodeEquivClass<ShapeNode>(getFactory()));
                    }
                    cells.get(cellIx).add(node);
                }
                this.equivRel.addAll(cells);
            }
        }
        return this.equivRel;
    }

    /** Lazily creates and returns the node multiplicity map of the underlying shape. */
    Map<ShapeNode,Multiplicity> getNodeMultMap() {
        if (this.nodeMultMap == null) {
            this.nodeMultMap = new MyHashMap<ShapeNode,Multiplicity>();
            byte[] nodeMult = getGraph().nodeMult;
            if (nodeMult != null) {
                for (ShapeNode node : getNodeSet()) {
                    Multiplicity mult =
                        Multiplicity.getMultiplicity(
                            nodeMult[node.getNumber()], MultKind.NODE_MULT);
                    this.nodeMultMap.put(node, mult);
                }
            }
        }
        return this.nodeMultMap;
    }

    /** Lazily creates and returns the outgoing edge multiplicity map of the underlying shape. */
    Map<EdgeSignature,Multiplicity> getOutEdgeMultMap() {
        if (this.outEdgeMultMap == null) {
            this.outEdgeMultMap =
                computeMultMap(EdgeMultDir.OUTGOING, getGraph().outEdgeMult);
        }
        return this.outEdgeMultMap;
    }

    /** Lazily creates and returns the incoming edge multiplicity map of the underlying shape. */
    Map<EdgeSignature,Multiplicity> getInEdgeMultMap() {
        if (this.inEdgeMultMap == null) {
            this.inEdgeMultMap =
                computeMultMap(EdgeMultDir.INCOMING, getGraph().inEdgeMult);
        }
        return this.inEdgeMultMap;
    }

    private MyHashMap<EdgeSignature,Multiplicity> computeMultMap(
            EdgeMultDir dir, EdgeRecord[] records) {
        MyHashMap<EdgeSignature,Multiplicity> result =
            new MyHashMap<EdgeSignature,Multiplicity>();
        if (records != null) {
            for (int i = 0; i < records.length; i++) {
                EdgeRecord record = records[i];
                result.put(record.getSig(dir, getFactory()), record.getMult());
            }
        }
        return result;
    }

    /** Transfers the data structures in flattened form to the underlying shape. */
    void flatten() {
        assert getGraph().nodes == null;
        getGraph().nodes = getNodeSet().toArray(new ShapeNode[0]);
        getGraph().edges = getEdgeSet().toArray(new ShapeEdge[0]);
        getGraph().nodeEquiv = flattenEquivRel();
        getGraph().nodeMult = flattenNodeMultMap();
        getGraph().inEdgeMult = flattenEdgeMultMap(getInEdgeMultMap());
        getGraph().outEdgeMult = flattenEdgeMultMap(getOutEdgeMultMap());
    }

    /** Computes the flattened representation of an edge multiplicity map. */
    private ShapeGraph.EdgeRecord[] flattenEdgeMultMap(
            Map<EdgeSignature,Multiplicity> multMap) {
        ShapeGraph.EdgeRecord[] result =
            new ShapeGraph.EdgeRecord[multMap.size()];
        int ix = 0;
        for (Map.Entry<EdgeSignature,Multiplicity> multEntry : multMap.entrySet()) {
            result[ix] =
                new EdgeRecord(multEntry.getKey(), multEntry.getValue(),
                    getFactory());
            ix++;
        }
        return result;
    }

    /** Computes the flattened representation of the node multiplicity map. */
    private byte[] flattenNodeMultMap() {
        byte[] result = new byte[getNodeCounter().getCount()];
        for (Map.Entry<ShapeNode,Multiplicity> multEntry : getNodeMultMap().entrySet()) {
            result[multEntry.getKey().getNumber()] =
                multEntry.getValue().getIndex();
        }
        return result;
    }

    /** Computes the flattened representation of the node equivalence relation. */
    private byte[] flattenEquivRel() {
        byte[] result = new byte[getNodeCounter().getCount()];
        byte cellIx = 0;
        for (EquivClass<ShapeNode> cell : getEquivRel()) {
            for (ShapeNode node : cell) {
                result[node.getNumber()] = cellIx;
            }
            cellIx++;
            assert cellIx != 0 : "Too many cells in the node partition";
        }
        return result;
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
    private MyHashMap<EdgeSignature,Multiplicity> outEdgeMultMap;
    /**
     *Incoming edge multiplicity map.
     */
    private MyHashMap<EdgeSignature,Multiplicity> inEdgeMultMap;

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
