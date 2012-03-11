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
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.graph.TypeLabel;

import java.util.Collections;
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
    }

    /** Inserts a new edge signature or changes the multiplicity of an existing signature. */
    public Multiplicity setEdgeMult(EdgeSignature key, Multiplicity value) {
        Multiplicity result = this.sig2MultMap.put(key, value);
        // refresh the edges for the signature
        this.sig2EdgeMap.put(key, key.findEdges(this.shape));
        if (hasEdge2SigMap()) {
            addToEdge2SigMap(key);
        }
        if (result == null) {
            // a new signature; update the other maps
            if (hasNode2SigMap()) {
                addToNode2SigMap(key);
            }
        }
        assert isConsistent();
        return result;
    }

    /** Removes a given signature from the store. */
    public Multiplicity removeSig(EdgeSignature sig) {
        Multiplicity result = this.sig2MultMap.remove(sig);
        if (result != null) {
            Set<ShapeEdge> edges = this.sig2EdgeMap.remove(sig);
            assert edges != null;
            if (hasNode2SigMap()) {
                getNode2SigMap().get(sig.getNode()).remove(sig);
            }
            if (hasEdge2SigMap()) {
                getEdge2SigMap().keySet().removeAll(edges);
            }
        }
        assert isConsistent() : "Inconsistent " + this;
        return result;
    }

    /**
     * Removes an edge from the signature containing it.
     * If the signature contained only a single edge, removes the signature as well.
     * @return {@code true} if the signature was removed
     */
    public boolean removeEdge(ShapeEdge edge) {
        EdgeSignature sig = getEdge2SigMap().remove(edge);
        boolean result = sig != null;
        if (result) {
            Set<ShapeEdge> sigEdges = this.sig2EdgeMap.get(sig);
            boolean removed = sigEdges.remove(edge);
            assert removed;
            result = sigEdges.isEmpty();
            if (result) {
                removeSig(sig);
            }
        }
        assert isConsistent() : "Inconsistent " + this;
        return result;
    }

    /** Clears all data contained in this store. */
    public void clear() {
        this.sig2MultMap.clear();
        this.sig2EdgeMap.clear();
        this.edge2SigMap = null;
        this.node2SigMap = null;
    }

    /** Returns the actual shape edges contained in a given signature. */
    public Set<ShapeEdge> getEdges(EdgeSignature sig) {
        Set<ShapeEdge> result = this.sig2EdgeMap.get(sig);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    /** Returns the edge signature corresponding to a given edge. */
    public EdgeSignature getSig(ShapeEdge edge) {
        EdgeSignature result = getEdge2SigMap().get(edge);
        return result;
    }

    /**
     * Returns the edge signature with a given set of fields, if it
     * exists in the store.
     */
    public EdgeSignature getSig(EdgeMultDir direction, ShapeNode node,
            TypeLabel label, EquivClass<ShapeNode> ec) {
        EdgeSignature result = null;
        Set<EdgeSignature> nodeSigs = getSigs(node);
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
    public Multiplicity getMult(ShapeEdge edge) {
        Multiplicity result = null;
        EdgeSignature sig = getSig(edge);
        if (sig != null) {
            result = getMult(sig);
            assert result != null;
        }
        return result;
    }

    /** Returns the mapping from edge signatures to multiplicities. */
    public Map<EdgeSignature,Multiplicity> getMultMap() {
        return Collections.unmodifiableMap(this.sig2MultMap);
    }

    /** Returns the edge signature corresponding to a given edge. */
    public Set<EdgeSignature> getSigs(ShapeNode node) {
        Set<EdgeSignature> result = getNode2SigMap().get(node);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    @Override
    public String toString() {
        return "EdgeSignatureStore:\n  edge2SigMap=" + this.edge2SigMap
            + "\n  node2SigMap=" + this.node2SigMap + "\n  sig2MultMap="
            + this.sig2MultMap + "\n  sig2EdgeMap=" + this.sig2EdgeMap;
    }

    /** Tests if the edge-to-signature map has been initialised. */
    private boolean hasEdge2SigMap() {
        return this.edge2SigMap != null;
    }

    /** Lazily creates and returns the mapping from edges to corresponding signatures. */
    private Map<ShapeEdge,EdgeSignature> getEdge2SigMap() {
        if (this.edge2SigMap == null) {
            this.edge2SigMap = new HashMap<ShapeEdge,EdgeSignature>();
            for (EdgeSignature sig : this.sig2MultMap.keySet()) {
                addToEdge2SigMap(sig);
            }
        }
        return this.edge2SigMap;
    }

    /**
     * Adds mappings from all edges corresponding to a given edge signature
     * to the signature.
     */
    private void addToEdge2SigMap(EdgeSignature sig) {
        Map<ShapeEdge,EdgeSignature> map = this.edge2SigMap;
        for (ShapeEdge edge : getEdges(sig)) {
            map.put(edge, sig);
        }
    }

    /** Tests the various maps are consistent. */
    private boolean isConsistent() {
        boolean result = true;
        if (!this.sig2EdgeMap.keySet().equals(this.sig2MultMap.keySet())) {
            return false;
        }
        if (ASSERT_EXTRA && hasEdge2SigMap()) {
            for (Map.Entry<ShapeEdge,EdgeSignature> entry : this.edge2SigMap.entrySet()) {
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
                if (!getEdge2SigMap().keySet().containsAll(sigEdges)) {
                    result = false;
                    break;
                }
                if (!sigEdges.equals(sig.findEdges(this.shape))) {
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
        if (hasEdge2SigMap()
            && !getEdge2SigMap().keySet().equals(
                Util.getBinaryEdges(this.shape))) {
            result = false;
        }
        return result;
    }

    /** Tests if the node-to-signatures-map has been initialised. */
    private boolean hasNode2SigMap() {
        return this.node2SigMap != null;
    }

    /** Returns the edge signature corresponding to a given edge. */
    private Map<ShapeNode,Set<EdgeSignature>> getNode2SigMap() {
        if (this.node2SigMap == null) {
            this.node2SigMap = new HashMap<ShapeNode,Set<EdgeSignature>>();
            for (EdgeSignature sig : this.sig2MultMap.keySet()) {
                addToNode2SigMap(sig);
            }
        }
        return this.node2SigMap;
    }

    private void addToNode2SigMap(EdgeSignature key) {
        Map<ShapeNode,Set<EdgeSignature>> map = this.node2SigMap;
        ShapeNode incident = key.getNode();
        Set<EdgeSignature> nodeSigs = map.get(incident);
        if (nodeSigs == null) {
            map.put(incident, nodeSigs = new HashSet<EdgeSignature>());
        }
        nodeSigs.add(key);
    }

    /** Mapping from edges to the corresponding edge signatures. */
    private Map<ShapeEdge,EdgeSignature> edge2SigMap;
    /** Mapping from nodes to the corresponding sets of edge signatures. */
    private Map<ShapeNode,Set<EdgeSignature>> node2SigMap;
    /** Mapping from edge signatures to the corresponding multiplicity. */
    private final Map<EdgeSignature,Multiplicity> sig2MultMap;
    /** Mapping from edge signatures to the corresponding shape edges. */
    private final Map<EdgeSignature,Set<ShapeEdge>> sig2EdgeMap;
    /** The shape with which this store is associated. */
    private final ShapeGraph shape;

    /** Flag controlling if heavy assertions are used. */
    static private final boolean ASSERT_EXTRA = false;
}
