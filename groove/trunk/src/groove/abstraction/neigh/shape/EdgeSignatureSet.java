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
import groove.graph.TypeLabel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collection of edge signatures with their multiplicities.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeSignatureSet extends MyHashMap<EdgeSignature,Multiplicity> {
    /**
     * Creates a new set, with a given shape factory.
     * The factory is used to create the edges involved in an edge signature.
     */
    public EdgeSignatureSet(ShapeFactory factory) {
        this.factory = factory;
    }

    @Override
    public Multiplicity put(EdgeSignature key, Multiplicity value) {
        Multiplicity result = super.put(key, value);
        if (result == null) {
            // it's a new signature; update the auxiliary data structures
            if (this.nodeMap != null) {
                addToNodeMap(this.nodeMap, key);
            }
            if (this.edgeMap != null) {
                addToEdgeMap(this.edgeMap, key);
            }
        }
        return result;
    }

    @Override
    public Multiplicity remove(Object key) {
        Multiplicity result = super.remove(key);
        if (result != null) {
            EdgeSignature sig = (EdgeSignature) key;
            ShapeNode incident = sig.getNode();
            if (this.nodeMap != null) {
                this.nodeMap.get(incident).remove(incident);
            }
            if (this.edgeMap != null) {
                TypeLabel label = sig.getLabel();
                boolean outgoing = sig.getDirection() == EdgeMultDir.OUTGOING;
                for (ShapeNode opposite : sig.getEquivClass()) {
                    ShapeEdge edge =
                        this.factory.createEdge(outgoing ? incident : opposite,
                            label, outgoing ? opposite : incident);
                    this.edgeMap.remove(edge);
                }
            }
        }
        return result;
    }

    /** Returns the edge signature corresponding to a given edge. */
    public EdgeSignature getSignature(ShapeEdge edge) {
        if (this.edgeMap == null) {
            this.edgeMap = new HashMap<ShapeEdge,EdgeSignature>();
            for (EdgeSignature sig : keySet()) {
                addToEdgeMap(this.edgeMap, sig);
            }
        }
        return this.edgeMap.get(edge);
    }

    /** Returns the multiplicity of a given edge. */
    public Multiplicity getEdgeMult(ShapeEdge edge) {
        Multiplicity result = null;
        EdgeSignature sig = getSignature(edge);
        if (sig != null) {
            result = get(sig);
        }
        return result;
    }

    /**
     * Adds mappings from all edges corresponding to a given edge signature
     * to the signature.
     */
    private void addToEdgeMap(Map<ShapeEdge,EdgeSignature> result,
            EdgeSignature sig) {
        ShapeFactory factory = this.factory;
        ShapeNode incident = sig.getNode();
        TypeLabel label = sig.getLabel();
        boolean outgoing = sig.getDirection() == EdgeMultDir.OUTGOING;
        for (ShapeNode opposite : sig.getEquivClass()) {
            ShapeEdge edge =
                factory.createEdge(outgoing ? incident : opposite, label,
                    outgoing ? opposite : incident);
            result.put(edge, sig);
        }
    }

    /** Returns the edge signature corresponding to a given edge. */
    public Set<EdgeSignature> getSignatures(ShapeNode node) {
        if (this.nodeMap == null) {
            this.nodeMap = new HashMap<ShapeNode,Set<EdgeSignature>>();
            for (EdgeSignature sig : keySet()) {
                addToNodeMap(this.nodeMap, sig);
            }
        }
        return this.nodeMap.get(node);
    }

    private void addToNodeMap(Map<ShapeNode,Set<EdgeSignature>> result,
            EdgeSignature key) {
        ShapeNode incident = key.getNode();
        Set<EdgeSignature> nodeSigs = result.get(incident);
        if (nodeSigs == null) {
            result.put(incident, nodeSigs = new HashSet<EdgeSignature>());
        }
        nodeSigs.add(key);
    }

    @Override
    public EdgeSignatureSet clone() {
        EdgeSignatureSet result = (EdgeSignatureSet) super.clone();
        result.edgeMap = null;
        result.nodeMap = null;
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        this.edgeMap = null;
        this.nodeMap = null;
    }

    /** Mapping from edges to the corresponding edge signatures. */
    private Map<ShapeEdge,EdgeSignature> edgeMap;
    /** Mapping from nodes to the corresponding sets of edge signatures. */
    private Map<ShapeNode,Set<EdgeSignature>> nodeMap;
    private final ShapeFactory factory;
}
