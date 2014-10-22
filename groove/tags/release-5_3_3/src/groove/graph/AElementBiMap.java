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
package groove.graph;

import groove.util.Fixable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extension of an element map that allows for efficient retrieval of the
 * reverse mapping.
 * When the inverse relation is computed, the map is fixed and can no longer
 * be altered.
 */
abstract public class AElementBiMap<SN extends Node,SE extends Edge,TN extends Node,TE extends Edge>
        extends AElementMap<SN,SE,TN,TE> implements Fixable {

    private Map<TN,Set<SN>> inverseNodeMap;
    private Map<TE,Set<SE>> inverseEdgeMap;

    /** Default constructor. */
    public AElementBiMap(ElementFactory<TN,TE> factory) {
        super(factory);
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            // Fixing is the same as computing the inverse maps.
            this.getInverseNodeMap();
            this.getInverseEdgeMap();
        }
        return result;
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

    /** Returns the inverse mapping, from shape nodes to their 
     * sets of pre-images.
     */
    @SuppressWarnings("unchecked")
    public Map<TN,Set<SN>> getInverseNodeMap() {
        if (this.inverseNodeMap == null) {
            this.inverseNodeMap =
                (Map<TN,Set<SN>>) this.computeInverse(this.nodeMap());
        }
        return this.inverseNodeMap;
    }

    /** Returns the inverse mapping, from shape nodes to their 
     * sets of pre-images.
     */
    @SuppressWarnings("unchecked")
    public Map<TE,Set<SE>> getInverseEdgeMap() {
        if (this.inverseEdgeMap == null) {
            this.inverseEdgeMap =
                (Map<TE,Set<SE>>) this.computeInverse(this.edgeMap());
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
                result.put(value, keys = new LinkedHashSet<K>());
            }
            keys.add(entry.getKey());
        }
        return result;
    }

    /** Returns the set of host nodes mapped to a given shape node. */
    public Set<? extends SN> getPreImages(Node node) {
        Set<SN> result = getInverseNodeMap().get(node);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    /** Returns the set of host edges mapped to a given shape edge. */
    public Set<? extends SE> getPreImages(Edge edge) {
        Set<SE> result = getInverseEdgeMap().get(edge);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }
}
