/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.neigh.trans;

import gnu.trove.THashMap;
import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeFactory;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.rel.LabelVar;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.Fixable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Mapping from rules to shapes, used in pre-matches and matches. */
public class RuleToShapeMap extends RuleToHostMap implements Fixable {
    /** Creates an empty rule-to-shape match. */
    public RuleToShapeMap(ShapeFactory factory) {
        super(factory);
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<RuleEdge,ShapeEdge> edgeMap() {
        return (Map<RuleEdge,ShapeEdge>) super.edgeMap();
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<RuleNode,ShapeNode> nodeMap() {
        return (Map<RuleNode,ShapeNode>) super.nodeMap();
    }

    @Override
    public TypeLabel putVar(LabelVar var, TypeLabel value) {
        assert !isFixed();
        return super.putVar(var, value);
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeEdge mapEdge(RuleEdge key) {
        assert !isFixed();
        return (ShapeEdge) super.mapEdge(key);
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeNode putNode(RuleNode key, HostNode layout) {
        assert !isFixed();
        return (ShapeNode) super.putNode(key, layout);
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeEdge putEdge(RuleEdge key, HostEdge layout) {
        assert !isFixed();
        return (ShapeEdge) super.putEdge(key, layout);
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeNode removeNode(RuleNode key) {
        assert !isFixed();
        return (ShapeNode) super.removeNode(key);
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeEdge removeEdge(RuleEdge key) {
        assert !isFixed();
        return (ShapeEdge) super.removeEdge(key);
    }

    /** Specialises the return type. */
    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public RuleToShapeMap clone() {
        return (RuleToShapeMap) super.clone();
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public RuleToShapeMap newMap() {
        return new RuleToShapeMap(getFactory());
    }

    @Override
    public void setFixed() {
        // Fixing is the same as computing the inverse map.
        getInverseNodeMap();
        getInverseEdgeMap();
    }

    @Override
    public boolean isFixed() {
        return this.inverseNodeMap != null;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException("Map is not fixed as expected.");
        }
    }

    /** Returns the set of rule nodes mapped to a given shape node. */
    public Set<RuleNode> getPreImages(ShapeNode node) {
        Set<RuleNode> result = getInverseNodeMap().get(node);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    /** Returns the set of rule edges mapped to a given shape edge. */
    public Set<RuleEdge> getPreImages(ShapeEdge edge) {
        Set<RuleEdge> result = getInverseEdgeMap().get(edge);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    /** Returns an self edge from given node with given label. Maybe be null. */
    public RuleEdge getSelfEdge(RuleNode node, TypeLabel label) {
        RuleEdge result = null;
        for (RuleEdge edge : this.edgeMap().keySet()) {
            if (edge.label().compareTo(label) == 0
                && edge.source().equals(node) && edge.target().equals(node)) {
                result = edge;
                break;
            }
        }
        return result;
    }

    /**
     * Returns a set of values for the node map. Contrary to calling
     * nodeMap().values(), the set has no repeated values.
     */
    public Set<ShapeNode> nodeMapValueSet() {
        Set<ShapeNode> result = new THashSet<ShapeNode>();
        result.addAll(this.nodeMap().values());
        return result;
    }

    /** Returns the inverse mapping, from shape nodes to their 
     * sets of pre-images.
     */
    public Map<ShapeNode,Set<RuleNode>> getInverseNodeMap() {
        if (this.inverseNodeMap == null) {
            this.inverseNodeMap = computeInverse(nodeMap());
            this.inverseEdgeMap = computeInverse(edgeMap());
        }
        return this.inverseNodeMap;
    }

    /** Returns the inverse mapping, from shape nodes to their 
     * sets of pre-images.
     */
    public Map<ShapeEdge,Set<RuleEdge>> getInverseEdgeMap() {
        if (this.inverseEdgeMap == null) {
            this.inverseNodeMap = computeInverse(nodeMap());
            this.inverseEdgeMap = computeInverse(edgeMap());
        }
        return this.inverseEdgeMap;
    }

    /**
     * Return the set of inconsistent edges, i.e., with a mapping that does not
     * conform to the node map.
     */
    public Set<ShapeEdge> getInconsistentEdges() {
        Set<ShapeEdge> result = new THashSet<ShapeEdge>();
        for (Entry<RuleEdge,ShapeEdge> entry : this.edgeMap().entrySet()) {
            RuleEdge edgeR = entry.getKey();
            ShapeEdge edgeS = entry.getValue();
            if (this.isSrcInconsistent(edgeR, edgeS)
                || this.isTgtInconsistent(edgeR, edgeS)) {
                result.add(edgeS);
            }
        }
        return result;
    }

    /** Checks the consistency between node and edge maps. */
    public boolean isConsistent() {
        boolean result = true;
        for (Entry<RuleEdge,ShapeEdge> entry : this.edgeMap().entrySet()) {
            RuleEdge edgeR = entry.getKey();
            ShapeEdge edgeS = entry.getValue();
            if (this.isSrcInconsistent(edgeR, edgeS)
                || this.isTgtInconsistent(edgeR, edgeS)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Checks which maps are inconsistent and returns the set of directions
     * for the edge multiplicities that need to be adjusted in the shape.
     */
    public Set<EdgeMultDir> getDirectionsToAdjust(ShapeEdge edgeS,
            Set<RuleEdge> edgesR) {
        Set<EdgeMultDir> result = EnumSet.noneOf(EdgeMultDir.class);
        for (RuleEdge edgeR : edgesR) {
            if (this.isSrcInconsistent(edgeR, edgeS)
                && !this.isTgtInconsistent(edgeR, edgeS)) {
                result.add(EdgeMultDir.INCOMING);
            }
            if (!this.isSrcInconsistent(edgeR, edgeS)
                && this.isTgtInconsistent(edgeR, edgeS)) {
                result.add(EdgeMultDir.OUTGOING);
            }
            // If both directions are inconsistent, then we can't do much now.
            // This case is handled later in the materialisation process.
        }
        return result;
    }

    private boolean isNodeInconsitent(RuleNode nodeR, ShapeNode expectedImage) {
        return !this.getNode(nodeR).equals(expectedImage);
    }

    private boolean isSrcInconsistent(RuleEdge edgeR, ShapeEdge expectedImage) {
        return this.isNodeInconsitent(edgeR.source(), expectedImage.source());
    }

    private boolean isTgtInconsistent(RuleEdge edgeR, ShapeEdge expectedImage) {
        return this.isNodeInconsitent(edgeR.target(), expectedImage.target());
    }

    private <K extends Object,V extends Object> Map<V,Set<K>> computeInverse(
            Map<K,V> map) {
        Map<V,Set<K>> result = new THashMap<V,Set<K>>();
        for (Map.Entry<K,V> entry : map.entrySet()) {
            V value = entry.getValue();
            Set<K> keys = result.get(value);
            if (keys == null) {
                result.put(value, keys = new THashSet<K>());
            }
            keys.add(entry.getKey());
        }
        return result;
    }

    private Map<ShapeNode,Set<RuleNode>> inverseNodeMap;
    private Map<ShapeEdge,Set<RuleEdge>> inverseEdgeMap;
}
