package groove.abstraction.neigh.shape;

import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.graph.InversableElementMap;
import groove.graph.Node;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.util.Fixable;

import java.util.Map;
import java.util.Set;

/**
 * Element map from a host graph to a shape.
 * 
 * @author Eduardo Zambon
 */
public final class HostToShapeMap extends
        InversableElementMap<HostNode,HostEdge,HostNode,HostEdge> implements
        Fixable {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Cache used to speed-up some morphism look-ups. */
    private Map<EquivClass<ShapeNode>,EquivClass<HostNode>> ecMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public HostToShapeMap(ShapeFactory factory) {
        super(factory);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

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

    @SuppressWarnings("unchecked")
    @Override
    public Set<HostNode> getPreImages(HostNode node) {
        assert node instanceof ShapeNode;
        return (Set<HostNode>) super.getPreImages(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<HostEdge> getPreImages(HostEdge edge) {
        assert edge instanceof ShapeEdge;
        return (Set<HostEdge>) super.getPreImages(edge);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the set of nodes that maps to values occurring in the given
     * equivalence class. 
     */
    public EquivClass<HostNode> getPreImages(EquivClass<ShapeNode> ecS) {
        if (this.ecMap == null) {
            this.ecMap =
                new MyHashMap<EquivClass<ShapeNode>,EquivClass<HostNode>>();
        }
        EquivClass<HostNode> nodesG = this.ecMap.get(ecS);
        if (nodesG == null) {
            nodesG =
                new NodeEquivClass<HostNode>((HostFactory) this.getFactory());
            for (ShapeNode nodeS : ecS) {
                nodesG.addAll(this.getPreImages(nodeS));
            }
            this.ecMap.put(ecS, nodesG);
        }
        return nodesG;
    }

}