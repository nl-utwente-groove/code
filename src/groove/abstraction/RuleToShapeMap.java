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
package groove.abstraction;

import groove.graph.TypeLabel;
import groove.rel.LabelVar;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.Fixable;
import groove.view.FormatException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Mapping from rules to shapes, used in prematches and matches. */
public class RuleToShapeMap extends RuleToHostMap implements Fixable {
    /** Creates an empty rule-to-shape match. */
    public RuleToShapeMap() {
        // empty map
    }

    /** 
     * Copies a given rule-to-host-graph map into a rule-to-shape map.
     * This is only valid if the images of the map that is passed in
     * are actually {@link ShapeNode}s and {@link ShapeEdge}s.
     */
    public RuleToShapeMap(RuleToHostMap map) {
        // copy the node entries
        for (Map.Entry<RuleNode,? extends HostNode> nodeEntry : map.nodeMap().entrySet()) {
            RuleNode ruleNode = nodeEntry.getKey();
            ShapeNode shapeNode = (ShapeNode) nodeEntry.getValue();
            putNode(ruleNode, shapeNode);
        }
        // copy the edge entries
        for (Map.Entry<RuleEdge,? extends HostEdge> nodeEntry : map.edgeMap().entrySet()) {
            RuleEdge ruleEdge = nodeEntry.getKey();
            ShapeEdge shapeEdge = (ShapeEdge) nodeEntry.getValue();
            putEdge(ruleEdge, shapeEdge);
        }
        // copy the valuation
        putAllVar(map.getValuation());
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

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public ShapeFactory getFactory() {
        return ShapeFactory.instance();
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public RuleToShapeMap clone() {
        return (RuleToShapeMap) super.clone();
    }

    /** Tests if the map is not fixed and specialises the return type. */
    @Override
    public RuleToShapeMap newMap() {
        return new RuleToShapeMap();
    }

    @Override
    public void setFixed() throws FormatException {
        // fixing is the same as computing the inverse map.
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

    private <K extends Object,V extends Object> Map<V,Set<K>> computeInverse(
            Map<K,V> map) {
        Map<V,Set<K>> result = new HashMap<V,Set<K>>();
        for (Map.Entry<K,V> entry : map.entrySet()) {
            V value = entry.getValue();
            Set<K> keys = result.get(value);
            if (keys == null) {
                result.put(value, keys = new HashSet<K>());
            }
            keys.add(entry.getKey());
        }
        return result;
    }

    private Map<ShapeNode,Set<RuleNode>> inverseNodeMap;
    private Map<ShapeEdge,Set<RuleEdge>> inverseEdgeMap;
}
