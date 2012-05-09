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

import groove.abstraction.Multiplicity;
import groove.abstraction.neigh.EdgeMultDir;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.graph.EdgeRole;
import groove.graph.TypeLabel;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collection of edge signatures with their multiplicities.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeSignatureStore {
    /**
     * Creates a new store, for a given shape.
     */
    public EdgeSignatureStore(ShapeGraph shape) {
        this.shape = shape;
        this.sig2MultMap = new HashMap<EdgeSignature,Multiplicity>();
        this.sig2EdgeMap = new HashMap<EdgeSignature,Set<ShapeEdge>>();
        this.sigCount = new int[EdgeMultDir.values().length];
    }

    /** Inserts a new edge signature or changes the multiplicity of an existing signature. */
    public Multiplicity setEdgeMult(EdgeSignature sig, Multiplicity value) {
        Multiplicity result = this.sig2MultMap.put(sig, value);
        if (result == null) {
            // a new signature; update the other maps
            this.sig2EdgeMap.put(sig, sig.findEdges(this.shape));
            addToEdge2SigMap(sig);
            addToNode2SigMap(sig);
            this.sigCount[sig.getDirection().ordinal()]++;
        }
        assert isConsistent();
        return result;
    }

    /** Removes a given signature from the store. */
    public Multiplicity removeSig(EdgeSignature sig) {
        Multiplicity result = this.sig2MultMap.remove(sig);
        if (result != null) {
            removeFromNode2SigMap(sig);
            removeFromEdge2SigMap(sig);
            Set<ShapeEdge> edges = this.sig2EdgeMap.remove(sig);
            this.sigCount[sig.getDirection().ordinal()]--;
            assert edges != null;
        }
        assert isConsistent() : "Inconsistent " + this;
        return result;
    }

    /**
     * Adds an edge, for a given direction.
     * If no corresponding edge signature exists, adds that as well.
     * Never changes the multiplicity of an existing edge signature.
     */
    public void addEdge(ShapeEdge edge) {
        assert edge.getRole() == EdgeRole.BINARY;
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            EdgeSignature sig = this.shape.createEdgeSignature(dir, edge);
            boolean result = !this.sig2MultMap.containsKey(sig);
            addToEdge2SigMap(edge, sig);
            if (result) {
                // it's a new signature; update all other auxiliary structures
                this.sig2MultMap.put(sig, Multiplicity.ONE_EDGE_MULT);
                Set<ShapeEdge> edges = new HashSet<ShapeEdge>();
                edges.add(edge);
                this.sig2EdgeMap.put(sig, edges);
                addToNode2SigMap(sig);
                this.sigCount[dir.ordinal()]++;
            } else {
                this.sig2EdgeMap.get(sig).add(edge);
            }
        }
        assert isConsistent() : "Inconsistent " + this;
    }

    /**
     * Removes an edge from the signature containing it.
     * If the signature contained only a single edge, removes the signature as well.
     */
    public void removeEdge(ShapeEdge edge) {
        assert edge.getRole() == EdgeRole.BINARY;
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            EdgeSignature sig = getEdge2SigMap(dir).remove(edge);
            if (sig != null) {
                Set<ShapeEdge> sigEdges = this.sig2EdgeMap.get(sig);
                boolean removed = sigEdges.remove(edge);
                assert removed;
                if (sigEdges.isEmpty()) {
                    removeSig(sig);
                }
            }
        }
        assert isConsistent() : "Inconsistent " + this;
    }

    /** Clears all data contained in this store. */
    public void clear() {
        this.sig2MultMap.clear();
        this.sig2EdgeMap.clear();
        this.edge2SigMaps = null;
        this.node2SigMaps = null;
        Arrays.fill(this.sigCount, 0);
    }

    /** Returns the actual shape edges contained in a given signature. */
    public Set<ShapeEdge> getEdges(EdgeSignature sig) {
        Set<ShapeEdge> result = this.sig2EdgeMap.get(sig);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /** Returns the edge signature corresponding to a given edge. */
    public EdgeSignature getSig(ShapeEdge edge, EdgeMultDir dir) {
        assert edge.getRole() == EdgeRole.BINARY;
        EdgeSignature result = getEdge2SigMap(dir).get(edge);
        return result;
    }

    /**
     * Returns the edge signature with a given set of fields, if it
     * exists in the store.
     */
    public EdgeSignature getSig(EdgeMultDir direction, ShapeNode node,
            TypeLabel label, EquivClass<ShapeNode> ec) {
        EdgeSignature result = null;
        Set<EdgeSignature> nodeSigs = getSigs(node, direction);
        if (nodeSigs != null) {
            for (EdgeSignature es : nodeSigs) {
                if (es.getLabel().equals(label) && es.hasSameEquivClass(ec)) {
                    result = es;
                    break;
                }
            }
        }
        return result;
    }

    /** Returns the multiplicity of a given edge. */
    public Multiplicity getMult(EdgeSignature sig) {
        return this.sig2MultMap.get(sig);
    }

    /** Returns the multiplicity of a given edge. */
    public Multiplicity getMult(ShapeEdge edge, EdgeMultDir dir) {
        assert edge.getRole() == EdgeRole.BINARY;
        Multiplicity result = null;
        EdgeSignature sig = getSig(edge, dir);
        if (sig != null) {
            result = getMult(sig);
            assert result != null;
        }
        return result;
    }

    /** Returns the mapping from edge signatures to multiplicities. */
    public Map<EdgeSignature,Multiplicity> getMultMap() {
        return this.sig2MultMap;
    }

    /** Returns the edge signature corresponding to a given edge. */
    public Set<EdgeSignature> getSigs(ShapeNode node, EdgeMultDir dir) {
        Set<EdgeSignature> result = getNode2SigMap(dir).get(node);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /** Returns the number of signatures of a specific direction. */
    public int getSigCount(EdgeMultDir dir) {
        return this.sigCount[dir.ordinal()];
    }

    @Override
    public String toString() {
        return "EdgeSignatureStore:\n  edge2SigMap=" + this.edge2SigMaps
            + "\n  node2SigMap=" + this.node2SigMaps + "\n  sig2MultMap="
            + this.sig2MultMap + "\n  sig2EdgeMap=" + this.sig2EdgeMap;
    }

    /** Copies all data structures from another store. */
    public void copyFrom(EdgeSignatureStore other) {
        this.sig2EdgeMap.clear();
        for (Map.Entry<EdgeSignature,Set<ShapeEdge>> entry : other.sig2EdgeMap.entrySet()) {
            this.sig2EdgeMap.put(entry.getKey(),
                new HashSet<ShapeEdge>(entry.getValue()));
        }
        this.sig2MultMap.clear();
        this.sig2MultMap.putAll(other.sig2MultMap);
        if (other.edge2SigMaps == null) {
            this.edge2SigMaps = null;
        } else {
            this.edge2SigMaps = createEdge2SigMaps();
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                this.edge2SigMaps.put(dir,
                    new Edge2SigMap(other.edge2SigMaps.get(dir)));
            }
        }
        if (other.node2SigMaps == null) {
            this.node2SigMaps = null;
        } else {
            this.node2SigMaps = createNode2SigMaps();
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                this.node2SigMaps.put(dir,
                    new Node2SigMap(other.node2SigMaps.get(dir)));
            }
        }
        System.arraycopy(other.sigCount, 0, this.sigCount, 0,
            other.sigCount.length);
    }

    /** Tests if the edge-to-signature map has been initialised. */
    private boolean hasEdge2SigMap() {
        return this.edge2SigMaps != null;
    }

    /** Lazily creates and returns the mapping from edges to corresponding signatures. */
    private Edge2SigMap getEdge2SigMap(EdgeMultDir dir) {
        if (this.edge2SigMaps == null) {
            Map<EdgeMultDir,Edge2SigMap> result =
                this.edge2SigMaps = createEdge2SigMaps();
            for (EdgeMultDir any : EdgeMultDir.values()) {
                result.put(any, new Edge2SigMap());
            }
            for (EdgeSignature sig : this.sig2MultMap.keySet()) {
                Edge2SigMap map = result.get(sig.getDirection());
                for (ShapeEdge edge : getEdges(sig)) {
                    map.put(edge, sig);
                }
            }
        }
        return this.edge2SigMaps.get(dir);
    }

    /**
     * Adds mappings from all edges corresponding to a given edge signature,
     * if the map has been initialised.
     */
    private void addToEdge2SigMap(EdgeSignature sig) {
        Map<EdgeMultDir,Edge2SigMap> maps = this.edge2SigMaps;
        if (maps != null) {
            Edge2SigMap map = maps.get(sig.getDirection());
            for (ShapeEdge edge : getEdges(sig)) {
                map.put(edge, sig);
            }
        }
    }

    /**
     * Adds mappings from one edge to a given edge signature,
     * if the map has been initialised.
     */
    private void addToEdge2SigMap(ShapeEdge edge, EdgeSignature sig) {
        Map<EdgeMultDir,Edge2SigMap> map = this.edge2SigMaps;
        if (map != null) {
            map.get(sig.getDirection()).put(edge, sig);
        }
    }

    /**
     * Removes the mappings from edges to a given signature,
     * if the map has been initialised.
     */
    private void removeFromEdge2SigMap(EdgeSignature sig) {
        Map<EdgeMultDir,Edge2SigMap> maps = this.edge2SigMaps;
        if (maps != null) {
            maps.get(sig.getDirection()).keySet().removeAll(getEdges(sig));
        }
    }

    private Map<EdgeMultDir,Edge2SigMap> createEdge2SigMaps() {
        return new EnumMap<EdgeMultDir,Edge2SigMap>(EdgeMultDir.class);
    }

    /** Tests the various maps are consistent. */
    private boolean isConsistent() {
        boolean result = true;
        if (!this.sig2EdgeMap.keySet().equals(this.sig2MultMap.keySet())) {
            return false;
        }
        if (ASSERT_EXTRA && hasEdge2SigMap()) {
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                for (Map.Entry<ShapeEdge,EdgeSignature> entry : getEdge2SigMap(
                    dir).entrySet()) {
                    EdgeSignature sig = entry.getValue();
                    if (!this.sig2MultMap.containsKey(sig)) {
                        result = false;
                        break;
                    }
                    Set<ShapeEdge> sigEdges = this.sig2EdgeMap.get(sig);
                    if (!sigEdges.contains(entry.getKey())) {
                        result = false;
                        break;
                    }
                    if (!sigEdges.equals(sig.findEdges(this.shape))) {
                        result = false;
                        break;
                    }
                }
            }
            for (Map.Entry<EdgeSignature,Set<ShapeEdge>> entry : this.sig2EdgeMap.entrySet()) {
                EdgeSignature sig = entry.getKey();
                if (!getEdge2SigMap(sig.getDirection()).keySet().containsAll(
                    entry.getValue())) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /** Tests if this store contains signatures for all edges. */
    boolean isComplete() {
        boolean result = true;
        if (hasEdge2SigMap()) {
            for (EdgeMultDir dir : EdgeMultDir.values()) {
                if (!getEdge2SigMap(dir).keySet().equals(
                    Util.getBinaryEdges(this.shape))) {
                    result = false;
                }
            }
        }
        return result;
    }

    /** Returns the edge signature corresponding to a given edge. */
    private Node2SigMap getNode2SigMap(EdgeMultDir dir) {
        if (this.node2SigMaps == null) {
            Map<EdgeMultDir,Node2SigMap> maps =
                this.node2SigMaps = createNode2SigMaps();
            for (EdgeMultDir any : EdgeMultDir.values()) {
                maps.put(any, new Node2SigMap());
            }
            for (EdgeSignature sig : this.sig2MultMap.keySet()) {
                maps.get(sig.getDirection()).add(sig);
            }
        }
        return this.node2SigMaps.get(dir);
    }

    private void addToNode2SigMap(EdgeSignature key) {
        Map<EdgeMultDir,Node2SigMap> maps = this.node2SigMaps;
        if (maps != null) {
            maps.get(key.getDirection()).add(key);
        }
    }

    /**
     * Removes the mappings from edges to a given signature,
     * if the map has been initialised.
     */
    private void removeFromNode2SigMap(EdgeSignature sig) {
        Map<EdgeMultDir,Node2SigMap> map = this.node2SigMaps;
        if (map != null) {
            map.get(sig.getDirection()).get(sig.getNode()).remove(sig);
        }
    }

    private Map<EdgeMultDir,Node2SigMap> createNode2SigMaps() {
        return new EnumMap<EdgeMultDir,Node2SigMap>(EdgeMultDir.class);
    }

    /** The shape with which this store is associated. */
    private final ShapeGraph shape;
    /** Count of signatures per direction. */
    private final int[] sigCount;
    /** Mapping from edges to the corresponding edge signatures. */
    private Map<EdgeMultDir,Edge2SigMap> edge2SigMaps;
    /** Mapping from nodes to the corresponding sets of edge signatures. */
    private Map<EdgeMultDir,Node2SigMap> node2SigMaps;
    /** Mapping from edge signatures to the corresponding multiplicity. */
    private final Map<EdgeSignature,Multiplicity> sig2MultMap;
    /** Mapping from edge signatures to the corresponding shape edges. */
    private final Map<EdgeSignature,Set<ShapeEdge>> sig2EdgeMap;

    /** Flag controlling if heavy assertions are used. */
    static private final boolean ASSERT_EXTRA = false;

    /** Mapping from nodes to sets of signatures. */
    private static class Node2SigMap extends
            HashMap<ShapeNode,Set<EdgeSignature>> {
        /** Constructs an empty map. */
        public Node2SigMap() {
            super();
        }

        /** Constructs a clone of a give map. */
        public Node2SigMap(Node2SigMap m) {
            for (Map.Entry<ShapeNode,Set<EdgeSignature>> entry : m.entrySet()) {
                put(entry.getKey(),
                    new HashSet<EdgeSignature>(entry.getValue()));
            }
        }

        public void add(EdgeSignature sig) {
            ShapeNode incident = sig.getNode();
            Set<EdgeSignature> nodeSigs = get(incident);
            if (nodeSigs == null) {
                put(incident, nodeSigs = new HashSet<EdgeSignature>());
            }
            nodeSigs.add(sig);
        }
    }

    /** Mapping from edges to signatures. */
    private static class Edge2SigMap extends HashMap<ShapeEdge,EdgeSignature> {
        /** Constructs an empty map. */
        public Edge2SigMap() {
            super();
        }

        /** Constructs a clone of a give map. */
        public Edge2SigMap(Edge2SigMap m) {
            super(m);
        }
    }
}
