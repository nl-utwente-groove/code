package groove.abstraction.neigh.shape;

import groove.abstraction.neigh.equiv.EquivClass;
import groove.graph.ElementMap;
import groove.graph.Node;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.util.Fixable;
import groove.view.FormatException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Element map from a host graph to a shape.
 * @author Eduardo Zambon
 */
public class HostToShapeMap extends
        ElementMap<HostNode,HostEdge,HostNode,HostEdge> implements Fixable {

    private Map<ShapeNode,Set<HostNode>> inverseNodeMap;
    private Map<ShapeEdge,Set<HostEdge>> inverseEdgeMap;

    /** Default constructor. */
    public HostToShapeMap() {
        super(ShapeFactory.instance());
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<HostEdge,ShapeEdge> edgeMap() {
        return (Map<HostEdge,ShapeEdge>) super.edgeMap();
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<HostNode,ShapeNode> nodeMap() {
        return (Map<HostNode,ShapeNode>) super.nodeMap();
    }

    @Override
    public ShapeNode putNode(HostNode key, HostNode value) {
        assert value instanceof ShapeNode;
        return (ShapeNode) super.putNode(key, value);
    }

    @Override
    public ShapeEdge putEdge(HostEdge key, HostEdge value) {
        assert value instanceof ShapeEdge;
        return (ShapeEdge) super.putEdge(key, value);
    }

    @Override
    public ShapeNode getNode(Node key) {
        return (ShapeNode) super.getNode(key);
    }

    @Override
    public ShapeEdge getEdge(HostEdge key) {
        return (ShapeEdge) super.getEdge(key);
    }

    @Override
    public void setFixed() throws FormatException {
        // Fixing is the same as computing the inverse map.
        this.getInverseNodeMap();
        this.getInverseEdgeMap();
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
    public Map<ShapeNode,Set<HostNode>> getInverseNodeMap() {
        if (this.inverseNodeMap == null) {
            this.inverseNodeMap = this.computeInverse(this.nodeMap());
            this.inverseEdgeMap = this.computeInverse(this.edgeMap());
        }
        return this.inverseNodeMap;
    }

    /** Returns the inverse mapping, from shape nodes to their 
     * sets of pre-images.
     */
    public Map<ShapeEdge,Set<HostEdge>> getInverseEdgeMap() {
        if (this.inverseEdgeMap == null) {
            this.inverseNodeMap = this.computeInverse(this.nodeMap());
            this.inverseEdgeMap = this.computeInverse(this.edgeMap());
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

    /** Returns the set of host nodes mapped to a given shape node. */
    public Set<HostNode> getPreImages(ShapeNode node) {
        Set<HostNode> result = this.getInverseNodeMap().get(node);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    /** Returns the set of host edges mapped to a given shape edge. */
    public Set<HostEdge> getPreImages(ShapeEdge edge) {
        Set<HostEdge> result = getInverseEdgeMap().get(edge);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    /**
     * Returns the set of nodes that maps to values occurring in the given
     * equivalence class. 
     */
    public EquivClass<HostNode> getPreImages(EquivClass<ShapeNode> ecS) {
        EquivClass<HostNode> nodesG = new EquivClass<HostNode>();
        for (ShapeNode nodeS : ecS) {
            nodesG.addAll(this.getPreImages(nodeS));
        }
        return nodesG;
    }

}